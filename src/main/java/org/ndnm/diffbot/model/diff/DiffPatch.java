package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import difflib.Delta;
import difflib.Patch;

@Entity
@Table(name = "diff_patch_t")
public class DiffPatch implements Serializable {
    private static final long serialVersionUID = 1180167484368589988L;

    private BigInteger id;
    private DiffResult diffResult;//ORM parent
    private List<DiffDelta> diffDeltas;//ORM children
    private Date dateCreated;

    // Convenience lists built from diffDeltas
    @Transient
    private List<DiffDelta> changeDeltas;
    @Transient
    private List<DiffDelta> insertDeltas;
    @Transient
    private List<DiffDelta> deleteDeltas;
    @Transient
    private int totalLinesAffected;


    public DiffPatch() {
        //For ORM
    }


    @SuppressWarnings("unchecked")//patch.getDeltas()
    public DiffPatch(Patch patch, Date dateCreated) {
        this.dateCreated = dateCreated;
        totalLinesAffected = 0;
        initLists(patch.getDeltas());
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


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_captured")
    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCaptured) {
        this.dateCreated = dateCaptured;
    }


    @OneToMany(targetEntity = DiffDelta.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diffPatch")
    @Fetch(value = FetchMode.SUBSELECT)
    public List<DiffDelta> getDiffDeltas() {
        return diffDeltas;
    }


    public void setDiffDeltas(List<DiffDelta> diffDeltas) {
        this.diffDeltas = diffDeltas;
    }


    // Used for manual object creation, needed by hibernate to persist associations
    private void addDiffDeltas(List<DiffDelta> diffDeltas) {
        this.diffDeltas = diffDeltas;
        for (DiffDelta diffDelta : diffDeltas) {
            diffDelta.setDiffPatch(this);
            totalLinesAffected += diffDelta.getTotalLinesAffected();
        }
    }


    @Transient
    public List<DiffDelta> getChangeDeltas() {
        if (changeDeltas == null) {
            initDeltaListsByType(getDiffDeltas());
        }
        return changeDeltas;
    }


    public void setChangeDeltas(List<DiffDelta> changeDeltas) {
        this.changeDeltas = changeDeltas;
    }


    @Transient
    public List<DiffDelta> getInsertDeltas() {
        if (insertDeltas == null) {
            initDeltaListsByType(getDiffDeltas());
        }
        return insertDeltas;
    }


    public void setInsertDeltas(List<DiffDelta> insertDeltas) {
        this.insertDeltas = insertDeltas;
    }


    @Transient
    public List<DiffDelta> getDeleteDeltas() {
        if (deleteDeltas == null) {
            initDeltaListsByType(diffDeltas);
        }
        return deleteDeltas;
    }


    public void setDeleteDeltas(List<DiffDelta> deleteDeltas) {
        this.deleteDeltas = deleteDeltas;
    }


    @OneToOne(targetEntity = DiffResult.class)
    @JoinColumn(name = "id")
    public DiffResult getDiffResult() {
        return diffResult;
    }


    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }


    @Transient
    public int getTotalLinesAffected() {
        return totalLinesAffected;
    }


    private void initLists(List<Delta> deltas) {
        List<DiffDelta> allDiffDeltas = convertDeltasToDiffDeltas(deltas);
        addDiffDeltas(allDiffDeltas);//Register for ORM
        initDeltaListsByType(allDiffDeltas);
    }


    @SuppressWarnings("unchecked")//diffDeltas.add(diffDelta)
    private List<DiffDelta> convertDeltasToDiffDeltas(List deltas) {
        List<DiffDelta> diffDeltas = new ArrayList<>();
        List<Delta> castDeltas = new ArrayList<>();

        castDeltas.addAll(deltas);
        for (Delta delta : castDeltas) {
            DiffDelta diffDelta = new DiffDelta(delta, dateCreated);
            diffDeltas.add(diffDelta);
        }

        return diffDeltas;
    }


    @Transient
    public boolean hasDeltas() {
        return getDiffDeltas() != null && !getDiffDeltas().isEmpty();
    }


    private void initDeltaListsByType(List<DiffDelta> diffDeltas) {
        this.changeDeltas = new ArrayList<>();
        this.insertDeltas = new ArrayList<>();
        this.deleteDeltas = new ArrayList<>();

        for (DiffDelta diffDelta : diffDeltas) {
            switch (diffDelta.getDeltaType()) {
                case CHANGE:
                    this.changeDeltas.add(diffDelta);
                    break;
                case INSERT:
                    this.insertDeltas.add(diffDelta);
                    break;
                case DELETE:
                    this.deleteDeltas.add(diffDelta);
                    break;
                default:
                    throw new RuntimeException("Unrecognized enum type: " + diffDelta.getDeltaType());
            }//switch
        }//for
    }

}
