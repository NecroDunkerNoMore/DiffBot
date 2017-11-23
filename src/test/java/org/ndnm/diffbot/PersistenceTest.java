package org.ndnm.diffbot;

import org.junit.Assert;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.spring.SpringContext;

public class PersistenceTest {

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

}
