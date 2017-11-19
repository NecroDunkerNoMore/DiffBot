package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import difflib.Delta;
import difflib.Patch;

public class DiffPatch {
    private BigInteger id;
    private Patch patch;
    private Date dateCaptured;
    private List<DiffDelta> allDiffDeltas;
    private List<DiffDelta> changeDeltas;
    private List<DiffDelta> insertDeltas;
    private List<DiffDelta> deleteDeltas;


    public DiffPatch() {
        //for orm
    }


    public DiffPatch(Patch patch, Date dateCaptured) {
        this.patch = patch;
        this.dateCaptured = dateCaptured;
        initLists();
        convertDeltasToDiffDeltas();
        separateDeltasByType();
    }


    private void separateDeltasByType() {
        for (DiffDelta diffDelta : allDiffDeltas) {
            switch (diffDelta.getType()) {
                case CHANGE:
                    changeDeltas.add(diffDelta);
                    break;
                case INSERT:
                    insertDeltas.add(diffDelta);
                    break;
                case DELETE:
                    deleteDeltas.add(diffDelta);
                    break;
                default:
                    throw new RuntimeException("Unrecognized enum type: " + diffDelta.getType());
            }
        }
    }


    @SuppressWarnings("unchecked")//patch.getDeltas()
    private void convertDeltasToDiffDeltas() {
        List<Delta> allDeltas = new ArrayList<>();
        allDeltas.addAll(patch.getDeltas());
        for (Delta delta : allDeltas) {
            DiffDelta diffDelta = new DiffDelta(delta);
            allDiffDeltas.add(diffDelta);
        }
    }


    private void initLists() {
        allDiffDeltas = new ArrayList<>();
        changeDeltas = new ArrayList<>();
        insertDeltas = new ArrayList<>();
        deleteDeltas = new ArrayList<>();
    }


    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    public List<DiffDelta> getAllDiffDeltas() {
        return allDiffDeltas;
    }


    public void setAllDiffDeltas(List<DiffDelta> allDiffDeltas) {
        this.allDiffDeltas = allDiffDeltas;
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


    public Date getDateCaptured() {
        return dateCaptured;
    }


    public void setDateCaptured(Date dateCaptured) {
        this.dateCaptured = dateCaptured;
    }
}
