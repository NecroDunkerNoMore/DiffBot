package org.ndnm.diffbot;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ndnm.diffbot.model.UrlPollingTime;
import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.service.DiffResultService;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.service.HtmlSnapshotService;
import org.ndnm.diffbot.service.UrlPollingTimeService;
import org.ndnm.diffbot.spring.SpringContext;
import org.ndnm.diffbot.util.DbDataLoader;
import org.ndnm.diffbot.util.DiffGenerator;
import org.ndnm.diffbot.util.TimeUtils;


public class PersistenceTest extends GeneratorTestBase {
    private int fauxSeconds = 1;


    @Before
    public void dBsetup() {
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

        DiffUrl diffUrl1 = new DiffUrl("https://example.com/1/");
        diffUrlService.save(diffUrl1);
        DiffUrl savedDiffUrl1 = diffUrlService.findById(BigInteger.valueOf(1));

        // Save a bunch to ensure we get the latest one next
        for (int i = 0; i < 6; i++) {
            CaptureType captureType = i % 2 == 0 ? CaptureType.POST_EVENT : CaptureType.PRE_EVENT;
            HtmlSnapshot htmlSnapshot = getRandomHtmlSnapshot(savedDiffUrl1, captureType);
            htmlSnapshotService.save(htmlSnapshot);
        }


        DiffUrl diffUrl2 = new DiffUrl("https://example.com/2/");
        diffUrlService.save(diffUrl2);
        DiffUrl savedDiffUrl2 = diffUrlService.findById(BigInteger.valueOf(2));

        // Save a bunch to ensure we get the latest one next
        for (int i = 0; i < 6; i++) {
            CaptureType captureType = i % 2 == 0 ? CaptureType.POST_EVENT : CaptureType.PRE_EVENT;
            HtmlSnapshot htmlSnapshot = getRandomHtmlSnapshot(savedDiffUrl2, captureType);
            htmlSnapshotService.save(htmlSnapshot);
        }


        // Create a distinct snapshot that's easily recognized.
        String rawHtml = "Latest snapshot here.";
        CaptureType captureType = CaptureType.POST_EVENT;
        Date dateCaptured = TimeUtils.getTimeGmt();
        dateCaptured.setSeconds(dateCaptured.getSeconds() + fauxSeconds++);
        dateCaptured.setYear(dateCaptured.getYear() + 10);
        HtmlSnapshot htmlSnapshot = new HtmlSnapshot(savedDiffUrl2, rawHtml, captureType, dateCaptured);
        htmlSnapshotService.save(htmlSnapshot);

        HtmlSnapshot latestHtmlSnapshot = htmlSnapshotService.findLatest(savedDiffUrl2);

        Assert.assertNotNull("Latest HtmlSnapshot came back null!", latestHtmlSnapshot);
        Assert.assertTrue("Failed to fetch latest HtmlSnapshot!", latestHtmlSnapshot.getRawHtml().equals(htmlSnapshot.getRawHtml()));
    }


    @SuppressWarnings("deprecation")
    @Test
    public void testTwoHtmlSnapshotBug() {
        // Bug manifests when snapshots are sub-second w/ eachother, so use same date for both
        Date now = TimeUtils.getTimeGmt();

        HtmlSnapshotService htmlSnapshotService = SpringContext.getBean(HtmlSnapshotService.class);
        DiffUrlService diffUrlService = SpringContext.getBean(DiffUrlService.class);

        DiffUrl diffUrl1 = new DiffUrl("https://example.com/1/");
        diffUrlService.save(diffUrl1);
        DiffUrl savedDiffUrl1 = diffUrlService.findById(BigInteger.valueOf(1));

        CaptureType captureType1 = CaptureType.POST_EVENT;
        HtmlSnapshot htmlSnapshot1 = getRandomHtmlSnapshot(savedDiffUrl1, captureType1);
        htmlSnapshot1.setDateCaptured(now);
        htmlSnapshotService.save(htmlSnapshot1);


        DiffUrl diffUrl2 = new DiffUrl("https://example.com/2/");
        diffUrlService.save(diffUrl2);
        DiffUrl savedDiffUrl2 = diffUrlService.findById(BigInteger.valueOf(2));

        CaptureType captureType2 = CaptureType.POST_EVENT;
        HtmlSnapshot htmlSnapshot2 = getRandomHtmlSnapshot(savedDiffUrl2, captureType2);
        htmlSnapshot2.setRawHtml("This is latest for DiffUrl2");
        htmlSnapshot2.setDateCaptured(now);
        htmlSnapshotService.save(htmlSnapshot2);

        HtmlSnapshot latestHtmlSnapshot = htmlSnapshotService.findLatest(savedDiffUrl2);

        Assert.assertNotNull("Latest HtmlSnapshot came back null!", latestHtmlSnapshot);
        Assert.assertTrue("Failed to fetch latest HtmlSnapshot!", latestHtmlSnapshot.getRawHtml().equals(htmlSnapshot2.getRawHtml()));
    }


    @Test
    public void testUrlPollingTime() {
        UrlPollingTimeService urlPollingTimeService = SpringContext.getBean(UrlPollingTimeService.class);

        UrlPollingTime urlPollingTime = null;
        for (int i = 0; i < 6; i++) {
            urlPollingTime = new UrlPollingTime();
            urlPollingTime.setDate(getAnotherFauxDate());
            urlPollingTime.setSuccess(true);
            urlPollingTimeService.save(urlPollingTime);
        }

        UrlPollingTime latest = urlPollingTimeService.getLastPollingTime();
        Assert.assertNotNull("Polling time came back null!", latest);

        Date latestDate = urlPollingTime.getDate();
        Assert.assertTrue("Did not pull latest date!", urlPollingTime.getDate().getTime() == latestDate.getTime());

    }


    @Test
    public void testTextEncoding() {
        Set<String> utf8Set = new HashSet<>();
        // Seem in the wild, comes back as '?' unknown markers from DB, throws DiffUtils off
        utf8Set.add("\uEA09");//PrivateUseArea Unicode
        utf8Set.add("\u200B");//Non-visible empty space Unicode

        String utf8String = "\uEA09 \u200B";

        for (String uchar : utf8Set) {
            Assert.assertTrue("Didn't find char that should be present!", utf8String.contains(uchar));
        }

        utf8String = utf8String.replaceAll("[\\p{Co}\\u200B]", "");

        for (String uchar : utf8Set) {
            Assert.assertTrue("Found chars that should be gone!", !utf8String.contains(uchar));
        }


    }


    @SuppressWarnings("deprecation")
    private HtmlSnapshot getRandomHtmlSnapshot(DiffUrl diffUrl, CaptureType captureType) {
        String rawHtml = generateRandomString(100);
        Date dateCaptured = getAnotherFauxDate();

        return new HtmlSnapshot(diffUrl, rawHtml, captureType, dateCaptured);
    }


    @SuppressWarnings("deprecation")
    private Date getAnotherFauxDate() {
        Date date = TimeUtils.getTimeGmt();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        date.setTime(calendar.getTimeInMillis());
        date.setSeconds(date.getSeconds() + fauxSeconds++);

        return date;
    }
}
