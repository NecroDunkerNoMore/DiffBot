package org.ndnm.diffbot;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.service.DiffResultService;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.spring.SpringContext;
import org.ndnm.diffbot.util.DiffGenerator;

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
    public void testDiffResultCrud() {
        DiffResultService diffResultService = SpringContext.getBean(DiffResultService.class);

        Date dateCaptured = Calendar.getInstance().getTime();
        DiffUrl diffUrl = new DiffUrl("https://example.com/foo.html");

        DiffResult diffResult = DiffGenerator.getDiffResult(dateCaptured, diffUrl, originalFileAsString, revisedFileAsString);
        diffResultService.save(diffResult);
        DiffResult savedDiffResult = diffResultService.findById(diffResult.getId());
        Assert.assertNotNull(savedDiffResult);
    }

}
