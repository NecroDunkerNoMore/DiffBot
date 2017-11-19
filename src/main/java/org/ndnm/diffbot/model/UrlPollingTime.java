package org.ndnm.diffbot.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "url_polling_time_t")
public class UrlPollingTime implements Serializable {
    private static final long serialVersionUID = 5415645255588950985L;

    private BigInteger id;
    private String url;
    private Date date;
    private boolean success;


    public UrlPollingTime() {
        // Need by ORM
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


    @Column(name = "url")
    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    @Column(name = "success")
    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

}
