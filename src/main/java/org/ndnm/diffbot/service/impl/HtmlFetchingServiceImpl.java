package org.ndnm.diffbot.service.impl;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.service.HtmlFetchingService;
import org.springframework.stereotype.Service;


@Service
public class HtmlFetchingServiceImpl extends HttpConnectionCloser implements HtmlFetchingService {
    private static Logger LOG = LogManager.getLogger(HtmlFetchingServiceImpl.class);

    @Resource(name = "userAgentString")
    private String userAgentString;


    public HtmlFetchingServiceImpl(String userAgentString) {
        this.userAgentString = userAgentString;
    }


    @Override
    public String fetchHtml(DiffUrl diffUrl) {
        return fetchHtml(diffUrl.getSourceUrl());
    }


    private String fetchHtml(String url) {
        HttpGet getMethod = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setUserAgent(getUserAgentString())
                .build();

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(getMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                String message = String.format("Could not get page, got response code: %d!", statusCode);
                throw new HttpResponseException(statusCode, message);
            }

            String html = EntityUtils.toString(response.getEntity());

            // Takes care of invisible unicode (like \\uEA09) and  reserved characters (like \u200B);
            // these have been seen in the wild, and confuse the differ
            html = html.replaceAll("[\\p{Co}\\u200B]", "");

            return html;

        } catch (HttpResponseException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error("Error encountered when trying to pull HTML!: %s", e.getMessage());
        } catch (Exception e) {
            LOG.error("Unknown exception when trying to to pull HTML!: %s", e.getMessage());
        } finally {
            closeHttpObjects(response);
        }

        return null;
    }


    @Override
    public boolean isHealthy() {
        String html = fetchHtml("https://google.com");
        return html != null;
    }


    @Override
    protected Logger getLog() {
        return LOG;
    }


    private String getUserAgentString() {
        return userAgentString;
    }

}
