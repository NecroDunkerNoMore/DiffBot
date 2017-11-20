package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ndnm.diffbot.model.CaptureType;
import org.ndnm.diffbot.model.HtmlCapture;

import difflib.Patch;

@Entity
@Table(name = "diff_result_t")
public class DiffResult implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

    private BigInteger id;
    private DiffUrl diffUrl;
    private Date dateCaptured;
    private List<HtmlCapture> htmlCaptures;
    private HtmlCapture preEventHtmlCapture;
    private HtmlCapture postEventHtmlCapture;
    private DiffPatch diffPatch;


    public DiffResult() {
        // Necessary for ORM
    }


    public DiffResult(DiffUrl diffUrl, Patch patch, List<HtmlCapture> htmlCaptures, Date dateCaptured) {
        this(diffUrl, new DiffPatch(patch, dateCaptured), htmlCaptures, dateCaptured);
    }


    public DiffResult(DiffUrl diffUrl, DiffPatch diffPatch, List<HtmlCapture> htmlCaptures, Date dateCaptured) {
        this.diffUrl = diffUrl;
        this.diffPatch = diffPatch;
        this.diffPatch.setDiffResult(this);
        this.dateCaptured = dateCaptured;
        this.htmlCaptures = htmlCaptures;
        for (HtmlCapture htmlCapture : htmlCaptures) {
            htmlCapture.setDiffResult(this);
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    @Column(name = "date_captured")
    public Date getDateCaptured() {
        return dateCaptured;
    }


    public void setDateCaptured(Date dateCaptured) {
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


    public HtmlCapture getPreEventHtmlCapture() {
        if (preEventHtmlCapture == null) {
            preEventHtmlCapture = getHtmlCaptureByType(CaptureType.PRE_EVENT);
        }
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


    private HtmlCapture getHtmlCaptureByType(CaptureType captureType) {
        for (HtmlCapture htmlCapture : getHtmlCaptures()) {
            if (htmlCapture.getCaptureType() == captureType) {
                return htmlCapture;
            }
        }

        return null;
    }


    public List<HtmlCapture> getHtmlCaptures() {
        return htmlCaptures;
    }


    public void setHtmlCaptures(List<HtmlCapture> htmlCaptures) {
        this.htmlCaptures = htmlCaptures;
    }


    public DiffPatch getDiffPatch() {
        return diffPatch;
    }


    public void setDiffPatch(DiffPatch diffPatch) {
        this.diffPatch = diffPatch;
    }

}
