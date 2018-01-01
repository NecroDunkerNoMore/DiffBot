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


import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.service.ArchiveService;
import org.ndnm.diffbot.service.ArchivedUrlService;
import org.ndnm.diffbot.service.DiffResultService;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.service.HealthCheckableService;
import org.ndnm.diffbot.service.HtmlFetchingService;
import org.ndnm.diffbot.service.HtmlSnapshotService;
import org.ndnm.diffbot.service.RedditService;
import org.ndnm.diffbot.service.TimingService;
import org.ndnm.diffbot.spring.SpringContext;
import org.ndnm.diffbot.util.DiffGenerator;
import org.ndnm.diffbot.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DiffBot implements HealthCheckableService {
    private static final Logger LOG = LogManager.getLogger(DiffBot.class);

    private final RedditService redditService;
    private final DiffResultService diffResultService;
    private final HtmlFetchingService htmlFetchingService;
    private final DiffUrlService diffUrlService;
    private final HtmlSnapshotService htmlSnapshotService;
    private final TimingService timingService;
    private final ArchiveService archiveService;
    private final ArchivedUrlService archivedUrlService;
    private boolean killSwitchClick;


    @Autowired
    public DiffBot(RedditService redditService, DiffResultService diffResultService,
                   HtmlFetchingService htmlFetchingService, DiffUrlService diffUrlService,
                   HtmlSnapshotService htmlSnapshotService, TimingService timingService,
                   ArchiveService archiveService, ArchivedUrlService archivedUrlService) {
        this.redditService = redditService;
        this.diffResultService = diffResultService;
        this.htmlFetchingService = htmlFetchingService;
        this.diffUrlService = diffUrlService;
        this.htmlSnapshotService = htmlSnapshotService;
        this.timingService = timingService;
        this.archiveService = archiveService;
        this.archivedUrlService = archivedUrlService;
        this.killSwitchClick = false;
    }


    private void run() {
        if (!performAuth()) {
            LOG.fatal("Failed initial authentication, exiting!");
            System.exit(1);
        }

        while (!killSwitchClick) {

            if (getTimingService().isTimeToProcessDiffUrls()) {
                boolean success = processDiffUrls();
                getTimingService().saveNewUrlPollingTime(success);
            }

            if (getTimingService().isTimeToCheckRedditMail()) {
                boolean success = getRedditService().processMail();
                getTimingService().saveNewRedditPollingTime(success);
            }

            // OAuth token needs refreshing periodically
            if (getTimingService().isTimeToRefreshAuth()) {
                retryAuthTillSuccess();
            }

            try {
                LOG.info("Sleeping for %d seconds...", getTimingService().getMainLoopIntervalInMillis() / 1000);
                Thread.sleep(getTimingService().getMainLoopIntervalInMillis());
                LOG.info("Awake now.");
            } catch (InterruptedException e) {
                LOG.warn("Unexpectedly woken from sleep!: " + e.getMessage());
            }

        }

    }


    private boolean processDiffUrls() {
        LOG.info("--------------------------------------------------------------------------------");
        LOG.info("Pulling all DiffUrls to iterate over...");
        List<DiffUrl> diffUrls = getDiffUrlService().findAll();
        LOG.info("Found %d DiffUrl(s).", diffUrls.size());

        for (DiffUrl diffUrl : diffUrls) {
            LOG.info("----------------------------------------");
            LOG.info("Processing DiffUrl: %s", diffUrl.getSourceUrl());

            HtmlSnapshot lastHtmlSnapshot = getHtmlSnapshotService().findLatest(diffUrl);
            if (lastHtmlSnapshot == null) {
                LOG.info("Saving initial HtmlSnapshot for DiffUrl: '%s': ", diffUrl.getSourceUrl());
                processFirstTimeHtmlSnapshot(diffUrl);
                continue;
            }

            Date dateCaptured = TimeUtils.getTimeGmt();
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
            LOG.info("Archiving DiffResult w/ with DiffUrl: '%s'", diffUrl.getSourceUrl());
            getArchiveService().archive(diffResult);
            LOG.info("Archiving complete.");


            LOG.info("********************************************************************************");
            LOG.info("Posting DiffResult to reddit...");
            String postUrl = getRedditService().postDiffResult(diffResult);
            LOG.info("Posting complete.");


            LOG.info("********************************************************************************");
            LOG.info("Notifying reddit subscribers...");
            int count = notifySubscribers(postUrl);
            LOG.info("%d notification sent.", count);

        }//for

        LOG.info("Finshed processing %d DiffUrls.", diffUrls.size());
        return true;
    }


    private void processFirstTimeHtmlSnapshot(DiffUrl diffUrl) {
        String rawHtml = getHtmlFetchingService().fetchHtml(diffUrl);
        HtmlSnapshot newHtmlSnapshot = new HtmlSnapshot(diffUrl, rawHtml, CaptureType.POST_EVENT, TimeUtils.getTimeGmt());
        getHtmlSnapshotService().save(newHtmlSnapshot);
    }


    private void retryAuthTillSuccess() {
        int attempts = 0;

        boolean success = performAuth();
        attempts++;

        while (!success) {
            if (attempts >= getRedditService().getMaxAuthAttempts()) {
                LOG.fatal("Could not authenticate before exhausting %d attempts, exiting.", getRedditService().getMaxAuthAttempts());
                killSwitchClick = true;
                return;
            }

            try {
                Thread.sleep(getTimingService().getAuthSleepIntervalInMillis());
                success = performAuth();
                attempts++;
            } catch (InterruptedException e) {
                LOG.warn("Woken up from sleep unexpectedly!");
            }
        }
    }


    private int notifySubscribers(String postUrl) {
        LOG.info("Notifying subscribers about DiffResult...");
        int numNotified = getRedditService().notifySubscribersOfPost(postUrl);
        LOG.info("Completed notifying %d subscribers.", numNotified);

        return numNotified;
    }


    private boolean performAuth() {
        LOG.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        LOG.info("Attempting reddit authentication...");

        boolean success = getRedditService().performAuth();
        success &= isAuthenticated();

        getTimingService().saveNewAuthPollingTime(success);

        LOG.info("Authentication attempt was successful: %s", success);
        return success;
    }


    @Override
    public boolean isHealthy() {
        return getRedditService().isHealthy() && getHtmlFetchingService().isHealthy();
    }


    private boolean isAuthenticated() {
        return getRedditService().isAuthenticated();
    }


    private DiffResultService getDiffResultService() {
        return diffResultService;
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


    private TimingService getTimingService() {
        return timingService;
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


    public ArchivedUrlService getArchivedUrlService() {
        return archivedUrlService;
    }


    public ArchiveService getArchiveService() {
        return archiveService;
    }
}
