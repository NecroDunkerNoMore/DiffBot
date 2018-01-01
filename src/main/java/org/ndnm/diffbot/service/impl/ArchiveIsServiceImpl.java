package org.ndnm.diffbot.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.ndnm.diffbot.model.ArchivedUrl;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.service.ArchiveService;
import org.ndnm.diffbot.util.TimeUtils;
import org.ndnm.diffbot.util.UrlMatcher;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Service
public class ArchiveIsServiceImpl extends HttpConnectionCloser implements ArchiveService {
    private static final Logger LOG = LogManager.getLogger(ArchiveIsServiceImpl.class);
    private static final String XPATH_TO_SUBMIT_ID = "//*[@id=\"submiturl\"]/input/@value";
    private static final String HEADER_LOCATION_KEY = "Location";
    private static final String HEADER_REFRESH_KEY = "Refresh";
    private static final String GET_REQUEST_URL = "https://archive.is";
    private static final String POST_REQUEST_URL = "http://archive.is/submit/";
    private static final String FAIL_LINK_FORMAT = "https://archive.today/?run=1&url=%s";
    private static final String URL_FORM_KEY = "url";
    private static final String SUBMIT_ID_FORM_KEY = "submitid";


    @Override
    public ArchivedUrl archive(DiffUrl diffUrl) {

        LOG.info("Attempting to archive link: %s", diffUrl.getSourceUrl());
        CloseableHttpClient client = HttpClientBuilder.create().build();

        ArchivedUrl archivedUrl;
        try {
            String submitId = getSubmitIdToken(client);

            if (StringUtils.isBlank(submitId)) {
                archivedUrl = createArchivedUrl(null, diffUrl);
            } else {
                String archivedLink = getArchivedLink(client, diffUrl, submitId);
                archivedUrl = createArchivedUrl(archivedLink, diffUrl);
            }

            archivedUrl.setDiffUrl(diffUrl);

        } finally {
            closeHttpObjects(client);
        }

        return archivedUrl;
    }


    /*
     * The post method on archive.is needs a hidden form field, which is
     * on the landing page of the site. This method goes and pulls the
     * page, and extracts/returns this token
     */
    private String getSubmitIdToken(CloseableHttpClient httpClient) {
        HttpGet getMethod = new HttpGet(GET_REQUEST_URL);

        CloseableHttpResponse response = null;
        String submitId = null;
        try {
            response = httpClient.execute(getMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOG.warn("Could not get submit token, got response code: %d!", statusCode);
                return null;
            }

            String htmlContents = EntityUtils.toString(response.getEntity());
            TagNode tagNode = new HtmlCleaner().clean(htmlContents);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPath xpath = XPathFactory.newInstance().newXPath();
            submitId = (String) xpath.evaluate(XPATH_TO_SUBMIT_ID, doc, XPathConstants.STRING);

        } catch (IOException e) {
            LOG.error("Error encountered when trying to archive link!: %s", e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.error("Could not parse html for submitId token: %s", e.getMessage());
        } catch (XPathExpressionException e) {
            LOG.error("Xpath failed when trying to extract submitId token: %s", e.getMessage());
        } catch (Exception e) {
            LOG.error("Unkown exception when trying to extract submitId token: %s", e.getMessage());
        } finally {
            closeHttpObjects(response);
        }

        return submitId;
    }


    private String getArchivedLink(CloseableHttpClient httpClient, DiffUrl diffUrl, String submitId) {
        HttpPost postMethod = getPostMethod(diffUrl.getSourceUrl(), submitId);

        String archivedLink = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(postMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            String headerKey;
            if (statusCode == HttpStatus.SC_OK) {
                // For links that are saved already (and haven't changed?)
                headerKey = HEADER_REFRESH_KEY;
                LOG.info("Remote service detected this has been saved recently.");
            } else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                // For links the service regards as never saved before
                headerKey = HEADER_LOCATION_KEY;
                LOG.info("Remote service detected this has never been saved.");
            } else {
                LOG.error("Unknown response code: %d", statusCode);
                return null;
            }

            Header[] headers = response.getHeaders(headerKey);
            if (headers != null && headers.length > 0) {
                Header archivePathHeader = headers[0];
                if (archivePathHeader != null) {
                    // The 'Refresh' header has extra cruft, but there will only
                    // ever be one url. Match w/ regex groups, and use it
                    String headerValue = archivePathHeader.getValue();
                    List<String> urls = UrlMatcher.extractUrls(headerValue);
                    archivedLink = urls.get(0);
                }
            }

        } catch (Exception e) {
            LOG.error("Error encountered when trying to archive link!: " + e.getMessage());
        } finally {
            closeHttpObjects(response);
        }

        return archivedLink;
    }


    @Override
    public boolean isHealthy() {
        HttpGet getMethod = new HttpGet(GET_REQUEST_URL);

        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            response = httpClient.execute(getMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOG.info("Health check failed, got response code: %d", statusCode);
                return false;
            }

            String htmlContents = EntityUtils.toString(response.getEntity());
            TagNode tagNode = new HtmlCleaner().clean(htmlContents);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPath xpath = XPathFactory.newInstance().newXPath();
            String submitId = (String) xpath.evaluate(XPATH_TO_SUBMIT_ID, doc, XPathConstants.STRING);

            if (StringUtils.isBlank(submitId)) {
                LOG.info("Health check failed, submitId token was null or empty.");
                return false;
            }

        } catch (Throwable t) {
            LOG.info("Health check failed, exception thrown: %s", t.getMessage());
        } finally {
            closeHttpObjects(response, httpClient);
        }

        return true;
    }


    private ArchivedUrl createArchivedUrl(String archivedLink, DiffUrl diffUrl) {
        ArchivedUrl archivedUrl = new ArchivedUrl();
        archivedUrl.setDateArchived(TimeUtils.getTimeGmt());

        if (StringUtils.isNotBlank(archivedLink)) {
            archivedUrl.setArchivedLink(archivedLink);
            LOG.info("Archive link successful: " + archivedUrl.getArchivedLink());
        } else {
            // Set clickable archive.is submission link that failed; we WON'T set
            // dateArchived, which is how we detect if save worked or not
            String failLink = String.format(FAIL_LINK_FORMAT, diffUrl.getSourceUrl());
            diffUrl.setSourceUrl(failLink);
            LOG.warn("Couldn't obtain archive for URL: " + diffUrl.getSourceUrl());
        }

        return archivedUrl;
    }


    /*
     * Currently the archive.is form for submitting has one visible form field,
     * and one hidden. We need to submit both, which we build here.
     */
    private HttpPost getPostMethod(String urlToSave, String submitId) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(URL_FORM_KEY, urlToSave));
        params.add(new BasicNameValuePair(SUBMIT_ID_FORM_KEY, submitId));

        HttpPost httpPost = new HttpPost(POST_REQUEST_URL);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            // If this ever does happen, we want the app to tank, as something really weird is going on
            throw new RuntimeException(e);
        }

        return httpPost;
    }


    @Override
    protected Logger getLog() {
        return LOG;
    }
}
