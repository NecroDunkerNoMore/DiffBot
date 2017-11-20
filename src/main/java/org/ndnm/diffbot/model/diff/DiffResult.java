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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.ndnm.diffbot.model.CaptureType;
import org.ndnm.diffbot.model.HtmlSnapshot;

import difflib.Patch;

@Entity
@Table(name = "diff_result_t")
public class DiffResult implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

    private BigInteger id;
    private Date dateCaptured;
    private DiffPatch diffPatch;
    private List<HtmlSnapshot> htmlSnapshots;

    @Transient
    private HtmlSnapshot preEventHtmlSnapshot;
    @Transient
    private HtmlSnapshot postEventHtmlSnapshot;


    public DiffResult() {
        // Necessary for ORM
    }


    public DiffResult(Patch patch, List<HtmlSnapshot> htmlSnapshots, Date dateCaptured) {
        this(new DiffPatch(patch, dateCaptured), htmlSnapshots, dateCaptured);
    }


    public DiffResult(DiffPatch diffPatch, List<HtmlSnapshot> htmlSnapshots, Date dateCaptured) {
        this.diffPatch = diffPatch;
        this.diffPatch.setDiffResult(this);
        this.dateCaptured = dateCaptured;
        this.htmlSnapshots = htmlSnapshots;
        for (HtmlSnapshot htmlSnapshot : htmlSnapshots) {
            htmlSnapshot.setDiffResult(this);
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


    @Temporal(TemporalType.TIMESTAMP)
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


    public HtmlSnapshot getPreEventHtmlSnapshot() {
        if (preEventHtmlSnapshot == null) {
            preEventHtmlSnapshot = getHtmlCaptureByType(CaptureType.PRE_EVENT);
        }
        return preEventHtmlSnapshot;
    }


    public void setPreEventHtmlSnapshot(HtmlSnapshot preEventHtmlSnapshot) {
        this.preEventHtmlSnapshot = preEventHtmlSnapshot;
    }


    public HtmlSnapshot getPostEventHtmlSnapshot() {
        if (postEventHtmlSnapshot == null) {
            postEventHtmlSnapshot = getHtmlCaptureByType(CaptureType.POST_EVENT);
        }
        return postEventHtmlSnapshot;
    }


    public void setPostEventHtmlSnapshot(HtmlSnapshot postEventHtmlSnapshot) {
        this.postEventHtmlSnapshot = postEventHtmlSnapshot;
    }


    private HtmlSnapshot getHtmlCaptureByType(CaptureType captureType) {
        for (HtmlSnapshot htmlSnapshot : getHtmlSnapshots()) {
            if (htmlSnapshot.getCaptureType() == captureType) {
                return htmlSnapshot;
            }
        }

        return null;
    }


    public List<HtmlSnapshot> getHtmlSnapshots() {
        return htmlSnapshots;
    }


    public void setHtmlSnapshots(List<HtmlSnapshot> htmlSnapshots) {
        this.htmlSnapshots = htmlSnapshots;
    }


    public DiffPatch getDiffPatch() {
        return diffPatch;
    }


    public void setDiffPatch(DiffPatch diffPatch) {
        this.diffPatch = diffPatch;
    }

    public DiffUrl getDiffUrl() {
        for (HtmlSnapshot htmlSnapshot : getHtmlSnapshots()) {
            if (htmlSnapshot.getDiffUrl() != null) {
                return htmlSnapshot.getDiffUrl();
            }
        }

        return null;
    }

}
