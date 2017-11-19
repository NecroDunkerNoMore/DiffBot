package org.ndnm.diffbot.model;


import java.io.Serializable;
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
@Table(name = "reddit_user_t")
public class RedditUser implements Serializable {
    private static final long serialVersionUID = -7312855810951382049L;

    private String id;
    private String username;
    private Date dateCreated;
    private String blacklistReason;
    private boolean isBlacklisted;


    public RedditUser() {
        // Needed for ORM
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    @Column(name = "username")
    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Column(name = "blacklist_reason")
    public String getBlacklistReason() {
        return blacklistReason;
    }


    public void setBlacklistReason(String blacklistReason) {
        this.blacklistReason = blacklistReason;
    }


    @Column(name = "is_blacklisted")
    public boolean isBlacklisted() {
        return isBlacklisted;
    }


    public void setBlacklisted(boolean blacklisted) {
        isBlacklisted = blacklisted;
    }
}
