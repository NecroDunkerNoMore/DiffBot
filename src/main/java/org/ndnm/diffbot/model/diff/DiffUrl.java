package org.ndnm.diffbot.model.diff;

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

import org.ndnm.diffbot.util.TimeUtils;

@Entity
@Table(name = "diff_url_t")
public class DiffUrl implements Serializable {
    private static final long serialVersionUID = 5169553373729915231L;

    private BigInteger id;
    private String sourceUrl;
    private Date dateCreated;
    private boolean active;


    public DiffUrl() {
        // Necessary for ORM
    }


    public DiffUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        this.dateCreated = TimeUtils.getTimeGmt();
        this.active = true;
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


    @Column(name = "source_url")
    public String getSourceUrl() {
        return sourceUrl;
    }


    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }


    @Override
    public String toString() {
        return getSourceUrl();
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Column(name = "active")
    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }
}
