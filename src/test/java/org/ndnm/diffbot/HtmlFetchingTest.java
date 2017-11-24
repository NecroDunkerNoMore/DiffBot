package org.ndnm.diffbot;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.service.DiffUrlService;
import org.ndnm.diffbot.service.HtmlFetchingService;
import org.ndnm.diffbot.spring.SpringContext;

public class HtmlFetchingTest {

    @Test
    public void testPullingByDiffUrl() {
        DiffUrlService diffUrlService = SpringContext.getBean(DiffUrlService.class);
        HtmlFetchingService htmlFetchingService = SpringContext.getBean(HtmlFetchingService.class);

        DiffUrl diffUrl = diffUrlService.findById(BigInteger.valueOf(1));
        String html = htmlFetchingService.fetchHtml(diffUrl);

        Assert.assertTrue(htmlFetchingService.isHealthy());
        Assert.assertNotNull(html);
        Assert.assertTrue(html.length() > 0);
    }

}
