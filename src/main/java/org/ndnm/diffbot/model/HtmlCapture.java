package org.ndnm.diffbot.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class HtmlCapture implements Serializable {
    private BigDecimal id;
    private DiffUrl diffUrl;
    private Date dateCaptured;
    private String rawHtml;


    public HtmlCapture() {
        //For Orm
    }


    public DiffUrl getDiffUrl() {
        return diffUrl;
    }


    public void setDiffUrl(DiffUrl diffUrl) {
        this.diffUrl = diffUrl;
    }


    public BigDecimal getId() {
        return id;
    }


    public void setId(BigDecimal id) {
        this.id = id;
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
}
