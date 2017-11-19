package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.ndnm.diffbot.model.HtmlCapture;

import difflib.Patch;

@Entity
@Table(name = "diff_result_t")
public class DiffResult implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

    private BigInteger id;
    private DiffUrl diffUrl;
    private Date dateCaptured;
    private HtmlCapture preEventHtmlCapture;
    private HtmlCapture postEventHtmlCapture;
    private DiffPatch diffPatch;


    public DiffResult() {
        // Necessary for ORM
    }


    public DiffResult(DiffUrl diffUrl, Patch patch, Date dateCaptured) {
        this(diffUrl, new DiffPatch(patch, dateCaptured), dateCaptured);
    }


    public DiffResult(DiffUrl diffUrl, DiffPatch diffPatch, Date dateCaptured) {
        this.diffUrl = diffUrl;
        this.diffPatch = diffPatch;
        this.dateCaptured = dateCaptured;
    }


    public List<DiffDelta> getChangeDeltas() {
        return diffPatch.getChangeDeltas();
    }


    public List<DiffDelta> getInsertDeltas() {
        return diffPatch.getInsertDeltas();
    }


    public List<DiffDelta> getDeleteDeltas() {
        return diffPatch.getDeleteDeltas();
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


    public DiffPatch getDiffPatch() {
        return diffPatch;
    }


    public void setDiffPatch(DiffPatch diffPatch) {
        this.diffPatch = diffPatch;
    }


    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }

}
