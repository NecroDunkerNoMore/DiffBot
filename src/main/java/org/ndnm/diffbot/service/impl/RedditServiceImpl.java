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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.service.AuthService;
import org.ndnm.diffbot.service.RedditService;
import org.ndnm.diffbot.util.RedditPostFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.AuthenticatedUserReference;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.fluent.InboxReference;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.paginators.InboxPaginator;



@Service
public class RedditServiceImpl implements RedditService {
    private static final Logger LOG = LogManager.getLogger(RedditServiceImpl.class);

    private final AuthService authService;
    private final RedditClient redditClient;
    private final RedditPostFormatter redditPostFormatter;


    @Autowired
    public RedditServiceImpl(AuthService authService, RedditClient redditClient, RedditPostFormatter redditPostFormatter) {
        this.authService = authService;
        this.redditClient = redditClient;
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
    public void postDiffResults(List<DiffResult> diffResults) {
        AccountManager accountManager = new AccountManager(redditClient);
        try {

        } catch (Exception e) {
        }
    }


    @Override
    public Listing<Message> getUnreadMessages() {

        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not instantiate fluent client to check mail: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get reference to inbox: %s", e.getMessage());
            return new Listing<>(Message.class);
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
        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not get fluent client to mark message with id(%s) as read: %s",
                    message.getId(), e.getMessage());
            return;
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref while marking message with id(%s) as read: %s",
                    message.getId(), e.getMessage());
            return;
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get inbox reference while marking message with id(%s) as read: %s",
                    message.getId(), e.getMessage());
            return;
        }

        try {
            inbox.readMessage(true, message);
        } catch (Exception e) {
            LOG.error("Could not mark message with id(%s) as read: %s", message.getId(), e.getMessage());
        }
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

}
