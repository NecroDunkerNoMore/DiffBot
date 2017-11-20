package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ndnm.diffbot.model.HtmlSnapshot;

@Entity
@Table(name = "diff_url_t")
public class DiffUrl implements Serializable {
    private static final long serialVersionUID = 5169553373729915231L;

    private BigInteger id;
    private String sourceUrl;
    private HtmlSnapshot htmlSnapshot;//ORM parent


    public DiffUrl() {
        // Necessary for ORM
    }


    public DiffUrl(String sourceUrl, HtmlSnapshot htmlSnapshot) {
        this.sourceUrl = sourceUrl;
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


    public HtmlSnapshot getHtmlSnapshot() {
        return htmlSnapshot;
    }


    public void setHtmlSnapshot(HtmlSnapshot htmlSnapshot) {
        this.htmlSnapshot = htmlSnapshot;
    }


    @Override
    public String toString() {
        return getSourceUrl();
    }
}
