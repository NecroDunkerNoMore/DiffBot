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
 * Copyright (C) 2017  github.com/NecroDunkerNoMore
 */

package org.ndnm.diffbot.app;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.model.AuthPollingTime;
import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.service.AuthTimeService;
import org.ndnm.diffbot.service.DiffResultService;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.service.HealthCheckableService;
import org.ndnm.diffbot.service.HtmlFetchingService;
import org.ndnm.diffbot.service.HtmlSnapshotService;
import org.ndnm.diffbot.service.RedditService;
import org.ndnm.diffbot.service.RedditTimeService;
import org.ndnm.diffbot.service.RedditUserService;
import org.ndnm.diffbot.spring.SpringContext;
import org.ndnm.diffbot.util.DiffGenerator;
import org.ndnm.diffbot.util.RedditPostFormatter;
import org.ndnm.diffbot.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DiffBot implements HealthCheckableService {
    private static final Logger LOG = LogManager.getLogger(DiffBot.class);
    private static final long AUTH_SLEEP_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final long DIFF_POLLING_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final long OAUTH_REFRESH_INTERVAL = 50 * 60 * 1000; // 50 minutes in millis
    private static final int MAX_AUTH_ATTEMPTS = 3;

    @Resource(name = "botsRedditUsername")
    private String botsRedditUsername;
    private final RedditService redditService;
    private final DiffResultService diffResultService;
    private final RedditUserService redditUserService;
    private final RedditTimeService redditTimeService;
    private final AuthTimeService authTimeService;
    private final HtmlFetchingService htmlFetchingService;
    private final DiffUrlService diffUrlService;
    private final HtmlSnapshotService htmlSnapshotService;
    private final RedditPostFormatter redditPostFormatter;
    private boolean killSwitchClick;


    @Autowired
    public DiffBot(RedditService redditService, DiffResultService diffResultService, RedditUserService redditUserService,
                   RedditTimeService redditTimeService, AuthTimeService authTimeService, HtmlFetchingService htmlFetchingService,
                   DiffUrlService diffUrlService, HtmlSnapshotService htmlSnapshotService, RedditPostFormatter redditPostFormatter) {
        this.redditService = redditService;
        this.diffResultService = diffResultService;
        this.redditUserService = redditUserService;
        this.redditTimeService = redditTimeService;
        this.authTimeService = authTimeService;
        this.htmlFetchingService =  htmlFetchingService;
        this.diffUrlService = diffUrlService;
        this.htmlSnapshotService = htmlSnapshotService;
        this.redditPostFormatter = redditPostFormatter;
        this.killSwitchClick = false;
    }


    private void run() {
        if (!performAuth()) {
            LOG.fatal("Failed initial authentication, exiting!");
            System.exit(1);
        }

        while (!killSwitchClick) {
            LOG.info("--------------------------------------------------------------------------------");
            LOG.info("Pulling all DiffUrls to iterate over...");
            List<DiffUrl> diffUrls = getDiffUrlService().findAll();
            LOG.info("Found %d DiffUrl(s).", diffUrls.size());

            for (DiffUrl diffUrl : diffUrls) {
                LOG.info("----------------------------------------");
                LOG.info("Processing DiffUrl: %s", diffUrl.getSourceUrl());

                HtmlSnapshot lastHtmlSnapshot  = getHtmlSnapshotService().findLatest(diffUrl);
                if (lastHtmlSnapshot == null) {
                    LOG.info("Saving initial HtmlSnapshot for DiffUrl: '%s': ", diffUrl.getSourceUrl());
                    processFirstTimeHtmlSnapshot(diffUrl);
                    continue;
                }

                Date dateCaptured = Calendar.getInstance().getTime();
                String oldHtml = lastHtmlSnapshot.getRawHtml();
                String newHtml = getHtmlFetchingService().fetchHtml(diffUrl);

                DiffResult diffResult = DiffGenerator.getDiffResult(dateCaptured, diffUrl, oldHtml, newHtml);
                if (!diffResult.hasDeltas()) {
                    LOG.info("Found no changes, continuing.");
                    continue;
                }

                LOG.info("********************************************************************************");
                LOG.info("Saving DiffResult w/ %d deltas from DiffUrl: '%s'", diffResult.getNumDeltas(), diffUrl.getSourceUrl());
                getDiffResultService().save(diffResult);
                LOG.info("Save complete.");

                LOG.info("********************************************************************************");
                LOG.info("Posting DiffResult to reddit...");
                getRedditService().postDiffResult(diffResult);
                LOG.info("Posting complete.");
                //notifySubscribers(diffResult);

            }

            LOG.info("Finshed processing %d DiffUrls.", diffUrls.size());

            // OAuth token needs refreshing every 60 minutes
            if (authNeedsRefreshing()) {
                retryAuthTillSuccess();
            }

            try {
                LOG.info("Sleeping for %d seconds...", DIFF_POLLING_INTERVAL/1000);
                Thread.sleep(DIFF_POLLING_INTERVAL);
                LOG.info("Awake now.");
            } catch (InterruptedException e) {
                LOG.warn("Unexpectedly woken from sleep!: " + e.getMessage());
            }

        }

    }



    private void processFirstTimeHtmlSnapshot(DiffUrl diffUrl) {
        String rawHtml = getHtmlFetchingService().fetchHtml(diffUrl);
        HtmlSnapshot newHtmlSnapshot = new HtmlSnapshot(diffUrl, rawHtml, CaptureType.POST_EVENT, Calendar.getInstance().getTime());
        getHtmlSnapshotService().save(newHtmlSnapshot);
    }


    private void retryAuthTillSuccess() {
        int attempts = 0;

        boolean success = performAuth();
        attempts++;

        while (!success) {
            if (attempts >= MAX_AUTH_ATTEMPTS) {
                LOG.fatal("Could not authenticate before exhausting %d attempts, exiting.", MAX_AUTH_ATTEMPTS);
                killSwitchClick = true;
                return;
            }

            try {
                Thread.sleep(AUTH_SLEEP_INTERVAL);
                success = performAuth();
                attempts++;
            } catch (InterruptedException e) {
                LOG.warn("Woken up from sleep unexpectedly!");
            }
        }
    }


    private boolean authNeedsRefreshing() {
        AuthPollingTime lastAuthTime = getAuthTimeService().getLastSuccessfulAuth();
        long now = TimeUtils.getTimeGmt().getTime();
        long lastAuth = lastAuthTime.getDate().getTime();

        return (now - lastAuth) >= OAUTH_REFRESH_INTERVAL;
    }


    private boolean isUserBlacklisted(String authorUsername) {
        boolean isBlacklisted = StringUtils.isBlank(authorUsername) || getRedditUserService().isUserBlacklisted(authorUsername);
        if (isBlacklisted) {
            LOG.info("User '%s' is blacklisted.");
        }

        return isBlacklisted;
    }


    private void deliverDiffResult(List<DiffResult> diffResults) {
        LOG.info("Making reddit post for %d events...", diffResults.size());
        //getRedditService().postDiffResults(diffResults);
        LOG.info("Completed reddit posting events.");
    }


    private boolean performAuth() {
        LOG.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        LOG.info("Attempting reddit authentication...");
        AuthPollingTime time = new AuthPollingTime();
        time.setDate(TimeUtils.getTimeGmt());

        boolean success = getRedditService().performAuth();
        success &= isAuthenticated();

        time.setSuccess(success);
        getAuthTimeService().save(time);

        LOG.info("Authentication attempt was successful: %s", success);
        return success;
    }


    @Override
    public boolean isHealthy() {
        return getHtmlFetchingService().isHealthy();
    }


    private boolean isAuthenticated() {
        return getRedditService().isAuthenticated();
    }


    private DiffResultService getDiffResultService() {
        return diffResultService;
    }


    private RedditUserService getRedditUserService() {
        return redditUserService;
    }


    public RedditTimeService getRedditTimeService() {
        return redditTimeService;
    }


    private AuthTimeService getAuthTimeService() {
        return authTimeService;
    }


    private RedditService getRedditService() {
        return redditService;
    }


    private HtmlFetchingService getHtmlFetchingService() {
        return htmlFetchingService;
    }


    private DiffUrlService getDiffUrlService() {
        return diffUrlService;
    }


    private HtmlSnapshotService getHtmlSnapshotService() {
        return htmlSnapshotService;
    }


    private void resetKillSwitch() {
        this.killSwitchClick = false;
    }


    public static void main(String... args) {
        LOG.info("################################################################################");
        LOG.info("Intializing bot...");
        DiffBot diffbot = SpringContext.getBean(DiffBot.class);

        final int baseBackoffTime = 10 * 1000; // 10 seconds
        int currentBackoffTime = baseBackoffTime;
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                LOG.info("Starting main loop.");
                diffbot.run();
            } catch (Throwable t) {
                LOG.fatal("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                LOG.fatal("Main loop has tanked: %s", t.getMessage());
            }

            LOG.fatal("Sleeping for %d seconds...", currentBackoffTime / 1000);
            try {
                Thread.sleep(currentBackoffTime);
            } catch (InterruptedException e) {
                LOG.fatal("Backoff sleep interupted!: %s", e.getMessage());
            }

            // Set backoff based on if remote services are healthy (this health check cascades)
            if (diffbot.isHealthy()) {
                currentBackoffTime = baseBackoffTime;
            } else {
                // Exponential backoff
                currentBackoffTime *= 2;
            }

            diffbot.resetKillSwitch();
        }//while
    }

}
