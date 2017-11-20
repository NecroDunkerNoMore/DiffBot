package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import difflib.Delta;

@Entity
@Table(name = "diff_delta_t")
public class DiffDelta implements Serializable {
    private static final long serialVersionUID = -2395860567963108268L;

    private BigInteger id;
    private DeltaType deltaType;
    private int startPosition;
    private int endPosition;
    private List<DiffLine> diffLines;
    private List<DiffLine> originalLines;
    private List<DiffLine> revisedLines;
    private DiffPatch diffPatch;//parent for orm


    public DiffDelta() {
        //for orm
    }


    public DiffDelta(Delta delta) {
        this.deltaType = initType(delta.getType());
        this.startPosition = initStartPositionByType(delta);
        this.endPosition = initEndPositionByType(delta);

        this.originalLines = new ArrayList<>();
        initLines(delta, originalLines, LineType.ORIGINAL);

        this.revisedLines = new ArrayList<>();
        initLines(delta, revisedLines, LineType.REVISED);

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


    public List<DiffLine> getDiffLines() {
        return diffLines;
    }


    public void setDiffLines(List<DiffLine> diffLines) {
        this.diffLines = diffLines;
    }


    public void addDiffLines(List<DiffLine> newDiffLines) {
        if (this.diffLines == null) {
            this.diffLines = new ArrayList<>();
        }

        for (DiffLine diffLine : newDiffLines) {
            diffLine.setDiffDelta(this);
            diffLines.add(diffLine);
        }

    }


    @Enumerated(EnumType.ORDINAL)
    public DeltaType getDeltaType() {
        return deltaType;
    }


    public void setDeltaType(DeltaType deltaType) {
        this.deltaType = deltaType;
    }


    @Column(name = "start_position")
    public int getStartPosition() {
        return startPosition;
    }


    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }


    @Column(name = "end_position")
    public int getEndPosition() {
        return endPosition;
    }


    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }


    @ManyToOne(targetEntity = DiffPatch.class)
    @JoinColumn(name = "diff_patch_id", nullable = false)
    public DiffPatch getDiffPatch() {
        return diffPatch;
    }


    public void setDiffPatch(DiffPatch diffPatch) {
        this.diffPatch = diffPatch;
    }


    public List<DiffLine> getOriginalLines() {
        return originalLines;
    }


    public List<DiffLine> getRevisedLines() {
        return revisedLines;
    }


    public void setOriginalLines(List<DiffLine> originalLines) {
        this.originalLines = originalLines;
    }


    public void setRevisedLines(List<DiffLine> revisedLines) {
        this.revisedLines = revisedLines;
    }


    @SuppressWarnings("unchecked")
    private void initLines(Delta delta, List<DiffLine> diffLines, LineType type) {
        List<String> linesToConvert;
        if (type == LineType.ORIGINAL) {
            linesToConvert = delta.getOriginal().getLines();
        } else {
            linesToConvert = delta.getRevised().getLines();
        }

        for (String line : linesToConvert) {
            DiffLine diffLine = new DiffLine(line, type);
            diffLines.add(diffLine);
        }
    }


    private int initStartPositionByType(Delta delta) {
        switch (this.deltaType) {
            case CHANGE:
                return delta.getOriginal().getPosition();
            case INSERT:
                return delta.getRevised().getPosition();
            case DELETE:
                return delta.getOriginal().getPosition();
            default:
                throw new RuntimeException("Unrecognized enum value: " + this.deltaType);
        }
    }


    private int initEndPositionByType(Delta delta) {
        switch (this.deltaType) {
            case CHANGE:
                return delta.getOriginal().last();
            case INSERT:
                return delta.getRevised().last();
            case DELETE:
                return delta.getOriginal().last();
            default:
                throw new RuntimeException("Unrecognized enum value: " + this.deltaType);
        }
    }


    private DeltaType initType(Delta.TYPE internalDeltaType) {
        switch (internalDeltaType) {
            case CHANGE:
                return DeltaType.CHANGE;
            case INSERT:
                return DeltaType.INSERT;
            case DELETE:
                return DeltaType.DELETE;
            default:
                throw new RuntimeException("Unrecognized enum value: " + internalDeltaType);
        }
    }

}
