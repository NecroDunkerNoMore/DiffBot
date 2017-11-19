package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import difflib.Delta;
import difflib.Patch;

@Entity
@Table(name = "diff_patch_t")
public class DiffPatch {
    private BigInteger id;
    private Date dateCaptured;
    private List<DiffDelta> changeDeltas;
    private List<DiffDelta> insertDeltas;
    private List<DiffDelta> deleteDeltas;


    private DiffResult parentDiffResult;


    public DiffPatch() {
        //for orm
    }


    @SuppressWarnings("unchecked")//patch.getDeltas()
    public DiffPatch(Patch patch, Date dateCaptured) {
        this.dateCaptured = dateCaptured;
        initLists(patch.getDeltas());
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_captured")
    public Date getDateCaptured() {
        return dateCaptured;
    }


    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
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


    private void separateDeltasByType(List<DiffDelta> diffDeltas) {
        this.changeDeltas = new ArrayList<>();
        this.insertDeltas = new ArrayList<>();
        this.deleteDeltas = new ArrayList<>();

        for (DiffDelta diffDelta : diffDeltas) {
            switch (diffDelta.getType()) {
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
                    throw new RuntimeException("Unrecognized enum type: " + diffDelta.getType());
            }//switch
        }//for
    }


    @SuppressWarnings("unchecked")//patch.getDeltas()
    private List<DiffDelta> convertDeltasToDiffDeltas(List deltas) {
        List<DiffDelta> diffDeltas = new ArrayList<>();

        List<Delta> castDeltas = new ArrayList<>();
        castDeltas.addAll(deltas);
        for (Delta delta : castDeltas) {
            DiffDelta diffDelta = new DiffDelta(delta);
            diffDeltas.add(diffDelta);
        }

        return diffDeltas;
    }


    private void initLists(List<Delta> deltas) {
        List<DiffDelta> allDiffDeltas = convertDeltasToDiffDeltas(deltas);
        separateDeltasByType(allDiffDeltas);

    }


    public List<DiffDelta> getChangeDeltas() {
        return changeDeltas;
    }


    public void setChangeDeltas(List<DiffDelta> changeDeltas) {
        this.changeDeltas = changeDeltas;
    }


    public List<DiffDelta> getInsertDeltas() {
        return insertDeltas;
    }


    public void setInsertDeltas(List<DiffDelta> insertDeltas) {
        this.insertDeltas = insertDeltas;
    }


    public List<DiffDelta> getDeleteDeltas() {
        return deleteDeltas;
    }


    public void setDeleteDeltas(List<DiffDelta> deleteDeltas) {
        this.deleteDeltas = deleteDeltas;
    }


    public DiffResult getParentDiffResult() {
        return parentDiffResult;
    }


    public void setParentDiffResult(DiffResult parentDiffResult) {
        this.parentDiffResult = parentDiffResult;
    }
}
