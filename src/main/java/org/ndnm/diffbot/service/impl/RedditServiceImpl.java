/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * DiffBot - Diff webpages and post to reddit when a difference is found.
 * Copyright (C) 2017  github.com/NecroDunkerNoMore
 */

package org.ndnm.diffbot.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.model.RedditUser;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.service.AuthService;
import org.ndnm.diffbot.service.RedditService;
import org.ndnm.diffbot.service.RedditUserService;
import org.ndnm.diffbot.util.RedditPostFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.AuthenticatedUserReference;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.fluent.InboxReference;
import net.dean.jraw.fluent.SubredditReference;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.InboxPaginator;


@Service
public class RedditServiceImpl implements RedditService {
    private static final Logger LOG = LogManager.getLogger(RedditServiceImpl.class);

    private final AuthService authService;
    private final RedditClient redditClient;
    private final RedditUserService redditUserService;
    private final RedditPostFormatter redditPostFormatter;


    @Autowired
    public RedditServiceImpl(AuthService authService, RedditClient redditClient, RedditUserService redditUserService,
                             RedditPostFormatter redditPostFormatter) {
        this.authService = authService;
        this.redditClient = redditClient;
        this.redditUserService = redditUserService;
        this.redditPostFormatter = redditPostFormatter;
    }


    @Override
    public boolean performAuth() {
        return getAuthService().authenticate(getRedditClient());
    }


    @Override
    public boolean isAuthenticated() {
        return getAuthService().isAuthenticated(getRedditClient());
    }


    @Override
    public String postDiffResult(DiffResult diffResult) {
        FluentRedditClient fluentClient = new FluentRedditClient(redditClient);
        SubredditReference subredditReference = fluentClient.subreddit("TheEssaysChanged");

        URL submissionLink;
        try {
            submissionLink = new URL(diffResult.getDiffUrl().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Bad url: " + e.getMessage());
        }

        String submissionTitle = redditPostFormatter.formatPostTitle(diffResult);

        Submission submission;
        try {
            submission = subredditReference.submit(submissionLink, submissionTitle);
        } catch (ApiException e) {
            throw new RuntimeException("Could not post to redddit: " + e.getExplanation());
        }

        makeCommentOnNewPost(submission, diffResult);

        return submission.getShortURL();
    }


    private void makeCommentOnNewPost(Submission submission, DiffResult diffResult) {
        String commentBody = redditPostFormatter.formatCommentBody(diffResult);
        AccountManager accountManager = new AccountManager(redditClient);
        try {
            accountManager.reply(submission, commentBody);
        } catch (Exception e) {
            LOG.error("Could not post reply to summons (url: %d): %s", submission.getUrl(), e.getMessage());
        }
    }


    @Override
    public Listing<Message> getUnreadMessages() {
        InboxReference inbox = getInbox();
        if (inbox == null) {
            // Logged error occurred in getInbox()
            return null;
        }

        InboxPaginator inboxPaginator;
        try {
            inboxPaginator = inbox.read();
        } catch (Exception e) {
            LOG.error("Could not read inbox: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        // This is where we finally get the actual listing
        Listing<Message> listing;
        try {
            listing = inboxPaginator.next(true);
        } catch (Exception e) {
            LOG.error("Could not get inbox message listing: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        return listing;
    }


    @Override
    public void markMessageRead(Message message) {
        InboxReference inbox = getInbox();
        if (inbox == null) {
            // Logged error occurred in getInbox()
            return;
        }

        try {
            inbox.readMessage(true, message);
        } catch (Exception e) {
            LOG.error("Could not mark message with id(%s) as read: %s", message.getId(), e.getMessage());
        }
    }


    @Override
    public void replyToMessage(RedditUser redditUser, boolean isSubscribed) {
        String to = redditUser.getUsername();
        String subject = isSubscribed ? "Successfully Subscribed" : "Succsessfully Unsubscribed";

        String body;
        if (isSubscribed) {
            body = "You will now recieve PMs when the watched links change, thank you! (You can unsubscribe by replying with 'unsubscribe'.)";
        } else {
            body = "You will not recieve anymore PMs from this bot. (You can re-subscribe by replying with 'subscribe'.)";
        }

        InboxReference inbox = getInbox();
        if (inbox == null) {
            throw new RuntimeException("Couldn't get inbox!");
        }

        try {
            inbox.compose(to, subject, body);
        } catch (Exception e) {
            LOG.error("Could not send PM to summoner: %s", e.getMessage());
        }
    }


    @Override
    public int notifySubscribersOfPost(String postUrl) {
        int count = 0;

        List<RedditUser> redditSubscribers = getRedditUserService().getAllNonBlacklistedUsers();
        for (RedditUser user : redditSubscribers) {
            String to = user.getUsername();
            String subject = "TSCC Essays Have Changed.";
            String body = String.format("The Essays have changed, see the [details here](%s).", postUrl);

            InboxReference inbox = getInbox();
            if (inbox == null) {
                throw new RuntimeException("Couldn't get inbox!");
            }

            try {
                inbox.compose(to, subject, body);
            } catch (Exception e) {
                LOG.error("Could not send PM to summoner: %s", e.getMessage());
            }

            count++;
        }//for

        return count;
    }


    private InboxReference getInbox() {
        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not get fluent client to mark message with id(%s) as read: %s", e.getMessage());
            return null;
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref while marking message with id(%s) as read: %s", e.getMessage());
            return null;
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get inbox reference while marking message with id(%s) as read: %s", e.getMessage());
            return null;
        }

        return inbox;
    }


    @Override
    public boolean isHealthy() {
        if (!getAuthService().authenticate(getRedditClient())) {
            return false;
        } else if (!isRedditHealthy()) {
            return false;
        }

        return true;
    }


    private boolean isRedditHealthy() {
        try {
            FluentRedditClient client = new FluentRedditClient(redditClient);
            AuthenticatedUserReference userRef = client.me();
            InboxReference inbox = userRef.inbox();
            InboxPaginator inboxPaginator = inbox.read();
            inboxPaginator.next(true);
        } catch (Throwable t) {
            LOG.info("Health check failed: %s", t.getMessage());
            return false;
        }

        return true;
    }


    private RedditClient getRedditClient() {
        return redditClient;
    }


    private AuthService getAuthService() {
        return authService;
    }


    private RedditUserService getRedditUserService() {
        return redditUserService;
    }
}
