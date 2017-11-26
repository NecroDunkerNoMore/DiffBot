package org.ndnm.diffbot;

import java.math.BigInteger;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.service.DiffResultService;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.service.HtmlSnapshotService;
import org.ndnm.diffbot.spring.SpringContext;
import org.ndnm.diffbot.util.DbDataLoader;
import org.ndnm.diffbot.util.DiffGenerator;
import org.ndnm.diffbot.util.TimeUtils;


public class PersistenceTest extends GeneratorTestBase {
    private int fauxSeconds = 1;

    @Before
    public void dBetup() {
        // Reset to clean state before tests run
        resetDb();
    }


    @After
    public void dbTeardown() {
        // Reset to clean state after tests run
        resetDb();
    }


    private static void resetDb() {
        DbDataLoader dbDataLoader = SpringContext.getBean(DbDataLoader.class);
        dbDataLoader.fireAllScripts();
    }


    @Test
    public void testDiffUrlCrud() {
        DiffUrlService diffUrlService = SpringContext.getBean(DiffUrlService.class);
        DiffUrl diffUrl = new DiffUrl("https://example.com/foo.html");

        // Test CReate
        diffUrlService.save(diffUrl);
        DiffUrl savedDiffUrl = diffUrlService.findById(BigInteger.valueOf(2));
        Assert.assertTrue("DiffUrl came back null after save!", savedDiffUrl != null);

        // Test Update
        savedDiffUrl.setActive(false);
        diffUrlService.update(savedDiffUrl);
        DiffUrl updatedDiffUrl = diffUrlService.findById(savedDiffUrl.getId());
        Assert.assertTrue("DiffUrl update did not persist!", !updatedDiffUrl.isActive());

        // Test Delete
        diffUrlService.delete(updatedDiffUrl);
        DiffUrl deletedDiffUrl = diffUrlService.findById(updatedDiffUrl.getId());
        Assert.assertTrue("DiffUrl delete did not persist!", deletedDiffUrl == null);
    }


    @SuppressWarnings("deprecation")
    @Test
    public void testDiffResultCrud() {
        DiffResultService diffResultService = SpringContext.getBean(DiffResultService.class);

        Date dateCaptured = TimeUtils.getTimeGmt();
        DiffUrl diffUrl = new DiffUrl("https://example.com/foo.html");
        DiffResult diffResult = DiffGenerator.getDiffResult(dateCaptured, diffUrl, originalFileAsString, revisedFileAsString);

        // Test CReate
        diffResultService.save(diffResult);
        DiffResult savedDiffResult = diffResultService.findById(diffResult.getId());
        Assert.assertNotNull(savedDiffResult);

        // Test Update
        Date newDate = getTruncatedDate();
        newDate.setYear(newDate.getYear() + 20);
        diffResult.getDiffPatch().setDateCreated(newDate);
        diffResultService.update(diffResult);
        DiffResult updatedDiffResult = diffResultService.findById(diffResult.getId());
        Assert.assertTrue("Update did not persist!",
                updatedDiffResult.getDiffPatch().getDateCreated().getTime()
                        == newDate.getTime());

        // Test Delete
        diffResultService.delete(diffResult);
        DiffResult deltetedDiffRestult = diffResultService.findById(diffResult.getId());
        Assert.assertNull("Deletion failed!", deltetedDiffRestult);
    }


    @SuppressWarnings("deprecation")
    @Test
    public void testHtmlSnapshot() {
        HtmlSnapshotService htmlSnapshotService = SpringContext.getBean(HtmlSnapshotService.class);
        DiffUrlService diffUrlService = SpringContext.getBean(DiffUrlService.class);

        DiffUrl diffUrl = new DiffUrl("https://example.com");
        diffUrlService.save(diffUrl);
        DiffUrl savedDiffUrl = diffUrlService.findById(BigInteger.valueOf(2));

        // Save a bunch to ensure we get the latest one next
        for (int i = 0; i < 6; i++) {
            CaptureType captureType = i% 2 == 0 ? CaptureType.POST_EVENT : CaptureType.PRE_EVENT;
            HtmlSnapshot htmlSnapshot = getRandomHtmlSnapshot(savedDiffUrl, captureType);
            htmlSnapshotService.save(htmlSnapshot);
        }

        // Create a distinct snapshot that's easily recognized.
        String rawHtml = "Latest snapshot here.";
        CaptureType captureType = CaptureType.POST_EVENT;
        Date dateCaptured = TimeUtils.getTimeGmt();
        dateCaptured.setSeconds(dateCaptured.getSeconds() + fauxSeconds++);
        dateCaptured.setYear(dateCaptured.getYear()+10);
        HtmlSnapshot htmlSnapshot = new HtmlSnapshot(savedDiffUrl, rawHtml, captureType, dateCaptured);
        htmlSnapshotService.save(htmlSnapshot);

        HtmlSnapshot latestHtmlSnapshot = htmlSnapshotService.findLatest(savedDiffUrl);

        Assert.assertNotNull("Latest HtmlSnapshot came back null!", latestHtmlSnapshot);
        Assert.assertTrue("Failed to fetch latest HtmlSnapshot!", latestHtmlSnapshot.getRawHtml().equals(htmlSnapshot.getRawHtml()));
    }


    @SuppressWarnings("deprecation")
    private HtmlSnapshot getRandomHtmlSnapshot(DiffUrl diffUrl, CaptureType captureType) {
        String rawHtml = generateRandomString(100);
        Date dateCaptured = TimeUtils.getTimeGmt();
        dateCaptured.setSeconds(dateCaptured.getSeconds() + fauxSeconds++);

        return new HtmlSnapshot(diffUrl, rawHtml, captureType, dateCaptured);
    }
}
