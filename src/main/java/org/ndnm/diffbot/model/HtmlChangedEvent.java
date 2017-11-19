package org.ndnm.diffbot.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class HtmlChangedEvent {
    private BigInteger id;
    private DiffUrl diffUrl;
    private Date dateCaptured;
    private HtmlCapture preEventHtmlCapture;
    private HtmlCapture postEventHtmlCapture;
    private List<DiffResult> diffResults;


    public HtmlChangedEvent() {
        //For ORM
    }


    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    public DiffUrl getDiffUrl() {
        return diffUrl;
    }


    public void setDiffUrl(DiffUrl diffUrl) {
        this.diffUrl = diffUrl;
    }


    public Date getDateCaptured() {
        return dateCaptured;
    }


    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
    }


    public HtmlCapture getPreEventHtmlCapture() {
        return preEventHtmlCapture;
    }


    public void setPreEventHtmlCapture(HtmlCapture preEventHtmlCapture) {
        this.preEventHtmlCapture = preEventHtmlCapture;
    }


    public HtmlCapture getPostEventHtmlCapture() {
        return postEventHtmlCapture;
    }


    public void setPostEventHtmlCapture(HtmlCapture postEventHtmlCapture) {
        this.postEventHtmlCapture = postEventHtmlCapture;
    }


    public List<DiffResult> getDiffResults() {
        return diffResults;
    }


    public void setDiffResults(List<DiffResult> diffResults) {
        this.diffResults = diffResults;
    }
}
