package org.ndnm.diffbot.model;


import java.math.BigInteger;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.ndnm.diffbot.model.diff.DiffUrl;


@Entity
@Table(name = "archived_url_t")
public class ArchivedUrl {
    private BigInteger id;
    private DiffUrl diffUrl;
    private String archivedLink;
    private Date dateArchived;


    public ArchivedUrl() {
        //ORM
    }


    public ArchivedUrl(DiffUrl diffUrl) {
        this.diffUrl = diffUrl;
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


    @ManyToOne(targetEntity = DiffUrl.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "diff_url_id", nullable = false)
    public DiffUrl getDiffUrl() {
        return diffUrl;
    }


    public void setDiffUrl(DiffUrl diffUrl) {
        this.diffUrl = diffUrl;
    }


    @Column(name = "archived_link")
    public String getArchivedLink() {
        return archivedLink;
    }


    public void setArchivedLink(String archiveAddress) {
        this.archivedLink = archiveAddress;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_archived")
    public Date getDateArchived() {
        return dateArchived;
    }


    public void setDateArchived(Date dateArchived) {
        this.dateArchived = dateArchived;
    }


    @Transient
    public String getSourceUrl() {
        return diffUrl != null ? diffUrl.getSourceUrl() : null;
    }


    public void addDiffUrl(DiffUrl diffUrl) {
        this.diffUrl = diffUrl;
    }
}
