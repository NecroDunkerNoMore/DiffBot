package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import difflib.Delta;

@Entity
@Table(name = "diff_delta_t")
public class DiffDelta implements Serializable {
    private static final long serialVersionUID = -2395860567963108268L;

    private BigInteger id;
    private DiffPatch diffPatch;//ORM parent
    private List<DiffLine> diffLines;//ORM children
    private DeltaType deltaType;
    private int startPosition;
    private int endPosition;

    // Convenience lists built from diffLines
    @Transient
    private List<DiffLine> originalLines;
    @Transient
    private List<DiffLine> revisedLines;


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


    @OneToMany(targetEntity = DiffLine.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diffDelta")
    public List<DiffLine> getDiffLines() {
        return diffLines;
    }


    public void setDiffLines(List<DiffLine> diffLines) {
        this.diffLines = diffLines;
    }


    public void addDiffLines(List<DiffLine> diffLines) {
        this.diffLines = diffLines;
        for (DiffLine diffLine : diffLines) {
            diffLine.setDiffDelta(this);
        }
    }


    @Enumerated(EnumType.STRING)
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


    @Transient
    public List<DiffLine> getOriginalLines() {
        return originalLines;
    }


    @Transient
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
