package org.ndnm.diffbot.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.ndnm.diffbot.model.diff.DiffUrl;

public class HtmlCapture implements Serializable {
    private static final long serialVersionUID = 8530872380652605568L;

    private BigDecimal id;
    private DiffUrl diffUrl;
    private Date dateCaptured;
    private String rawHtml;


    public HtmlCapture() {
        //For Orm
    }


    public HtmlCapture(DiffUrl diffUrl, String rawHtml) {
        this.diffUrl = diffUrl;
        this.rawHtml = rawHtml;
        this.dateCaptured = Calendar.getInstance().getTime();
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
