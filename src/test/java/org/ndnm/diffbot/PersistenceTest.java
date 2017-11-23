package org.ndnm.diffbot;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.service.HtmlSnapshotService;
import org.ndnm.diffbot.spring.SpringContext;

public class PersistenceTest extends GeneratorTestBase {

    @Test
    public void testDiffUrlCrud() {
        DiffUrlService diffUrlService = SpringContext.getBean(DiffUrlService.class);
        DiffUrl diffUrl = new DiffUrl("https://example.com/foo.html");

        // Test CReate
        diffUrlService.save(diffUrl);
        DiffUrl savedDiffUrl = diffUrlService.findById(diffUrl.getId());
        Assert.assertTrue("DiffUrl came back null after save!", savedDiffUrl != null);

        // Test Update
        diffUrl.setActive(false);
        diffUrlService.update(diffUrl);
        DiffUrl updatedDiffUrl = diffUrlService.findById(diffUrl.getId());
        Assert.assertTrue("DiffUrl update did not persist!", !updatedDiffUrl.isActive());

        // Test Delete
        diffUrlService.delete(diffUrl);
        DiffUrl deletedDiffUrl = diffUrlService.findById(diffUrl.getId());
        Assert.assertTrue("DiffUrl delete did not persist!", deletedDiffUrl == null);
    }


    @Test
    public void testHtmlSnapshotCrund() {
        HtmlSnapshotService htmlSnapshotService = SpringContext.getBean(HtmlSnapshotService.class);
        DiffUrl diffUrl = new DiffUrl("https://example.com/foo.html");

        // Test CReate
        HtmlSnapshot htmlSnapshot = new HtmlSnapshot(diffUrl, originalFileAsString, CaptureType.PRE_EVENT, Calendar.getInstance().getTime());
        htmlSnapshotService.save(htmlSnapshot);
        HtmlSnapshot savedHtmlSnapshot = htmlSnapshotService.findById(htmlSnapshot.getId());
        Assert.assertTrue("HtmlSnapshot save failed!", savedHtmlSnapshot != null);

        // Test Update
        htmlSnapshot.setCaptureType(CaptureType.POST_EVENT);
        htmlSnapshotService.update(htmlSnapshot);
        HtmlSnapshot updatedSnapshot = htmlSnapshotService.findById(htmlSnapshot.getId());
        Assert.assertTrue("HtmlSnapshot Update did not persist!", updatedSnapshot.getCaptureType() == CaptureType.POST_EVENT);

        // Test Delete
        htmlSnapshotService.delete(htmlSnapshot);
        HtmlSnapshot deletedSnapshot = htmlSnapshotService.findById(htmlSnapshot.getId());
        Assert.assertTrue("HtmlSnapshot delete failed!", deletedSnapshot == null);


    }

}
