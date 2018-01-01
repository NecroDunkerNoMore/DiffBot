package org.ndnm.diffbot.model;


import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.ndnm.diffbot.model.diff.DiffResult;



@Entity
@Table(name = "archived_url_t")
public class ArchivedUrl {
    private BigInteger id;
    private DiffResult diffResult;
    private String archivedLink;
    private Date dateArchived;


    public ArchivedUrl() {
        //ORM
    }


    public ArchivedUrl(DiffResult diffResult) {
        this.diffResult = diffResult;
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


    @OneToOne(targetEntity = DiffResult.class)
    @JoinColumn(name = "diff_result_id", nullable = false)
    public DiffResult getDiffResult() {
        return diffResult;
    }


    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
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
        return diffResult != null ? diffResult.getDiffUrl().getSourceUrl() : null;
    }


    public void addDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }
}
