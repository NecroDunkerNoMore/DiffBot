package org.ndnm.diffbot.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ndnm.diffbot.model.diff.DiffResult;

@Entity
@Table(name = "html_capture_t")
public class HtmlCapture implements Serializable {
    private static final long serialVersionUID = 8530872380652605568L;

    private BigDecimal id;
    private Date dateCaptured;
    private String rawHtml;
    private DiffResult diffResult;//ORM parent
    private CaptureType captureType;


    public HtmlCapture() {
        //For Orm
    }


    public HtmlCapture(String rawHtml, CaptureType captureType) {
        this.rawHtml = rawHtml;
        this.dateCaptured = Calendar.getInstance().getTime();
        this.captureType = captureType;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public BigDecimal getId() {
        return id;
    }


    public void setId(BigDecimal id) {
        this.id = id;
    }


    public DiffResult getDiffResult() {
        return diffResult;
    }


    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }


    public Date getDateCaptured() {
        return dateCaptured;
    }


    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
    }


    public String getRawHtml() {
        return rawHtml;
    }


    public void setRawHtml(String rawHtml) {
        this.rawHtml = rawHtml;
    }


    @Enumerated(EnumType.ORDINAL)
    public CaptureType getCaptureType() {
        return captureType;
    }


    public void setCaptureType(CaptureType captureType) {
        this.captureType = captureType;
    }
}
