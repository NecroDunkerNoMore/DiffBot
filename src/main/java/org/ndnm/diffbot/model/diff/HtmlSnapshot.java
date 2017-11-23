package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "html_snapshot_t")
public class HtmlSnapshot implements Serializable {
    private static final long serialVersionUID = 8530872380652605568L;

    private BigInteger id;
    private DiffResult diffResult;//ORM parent
    private DiffUrl diffUrl;
    private CaptureType captureType;
    private Date dateCaptured;
    private String rawHtml;


    public HtmlSnapshot() {
        //For ORM
    }


    public HtmlSnapshot(DiffUrl diffUrl, String rawHtml, CaptureType captureType, Date dateCaptured) {
        this.dateCaptured = dateCaptured;
        this.captureType = captureType;
        this.diffUrl = diffUrl;
        this.rawHtml = rawHtml;
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


    @OneToOne(targetEntity = DiffUrl.class, fetch = FetchType.EAGER)
    public DiffUrl getDiffUrl() {
        return diffUrl;
    }


    public void setDiffUrl(DiffUrl diffUrl) {
        this.diffUrl = diffUrl;
    }


    @ManyToOne(targetEntity = DiffResult.class)
    @JoinColumn(name = "diff_result_id", nullable = false)
    public DiffResult getDiffResult() {
        return diffResult;
    }


    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_captured")
    public Date getDateCaptured() {
        return dateCaptured;
    }


    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
    }


    @Column(name = "raw_html")
    public String getRawHtml() {
        return rawHtml;
    }


    public void setRawHtml(String rawHtml) {
        this.rawHtml = rawHtml;
    }


    @Enumerated(EnumType.STRING)
    public CaptureType getCaptureType() {
        return captureType;
    }


    public void setCaptureType(CaptureType captureType) {
        this.captureType = captureType;
    }
}
