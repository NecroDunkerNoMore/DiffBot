package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import difflib.Delta;

@Entity
@Table(name = "diff_delta_t")
public class DiffDelta implements Serializable {
    private static final long serialVersionUID = -2395860567963108268L;

    private BigInteger id;
    private DiffPatch diffPatch;//ORM parent
    private List<DiffLine> diffLines;//ORM children
    private DeltaType deltaType;
    private Date dateCreated;
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


    public DiffDelta(Delta delta, Date dateCreated) {
        this.deltaType = initType(delta.getType());
        this.startPosition = initStartPositionByType(delta);
        this.endPosition = initEndPositionByType(delta);
        this.dateCreated = dateCreated;
        initLines(delta);
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
    @Fetch(value = FetchMode.SUBSELECT)
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
    @Column(name = "delta_type")
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


    @ManyToOne(targetEntity = DiffPatch.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "diff_patch_id", nullable = false)
    public DiffPatch getDiffPatch() {
        return diffPatch;
    }


    public void setDiffPatch(DiffPatch diffPatch) {
        this.diffPatch = diffPatch;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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
    private void initLines(Delta delta) {
        this.diffLines = new ArrayList<>();

        this.originalLines = convertLinesByType(delta, LineType.ORIGINAL);
        this.diffLines.addAll(originalLines);

        this.revisedLines = convertLinesByType(delta, LineType.REVISED);
        diffLines.addAll(revisedLines);

        for (DiffLine diffLine : diffLines) {
            diffLine.setDiffDelta(this);
        }
    }


    @SuppressWarnings("unchecked")
    private List<DiffLine> convertLinesByType(Delta delta, LineType type) {
        List<DiffLine> convertedDiffLines = new ArrayList<>();

        List<String> linesToConvert;
        if (type == LineType.ORIGINAL) {
            linesToConvert = delta.getOriginal().getLines();
        } else {
            linesToConvert = delta.getRevised().getLines();
        }

        for (String line : linesToConvert) {
            DiffLine diffLine = new DiffLine(line, type);
            convertedDiffLines.add(diffLine);
        }

        return convertedDiffLines;
    }


    private int initStartPositionByType(Delta delta) {
        switch (this.deltaType) {
            case INSERT:
                return delta.getRevised().getPosition();
            case CHANGE:
                return delta.getOriginal().getPosition();
            case DELETE:
                return delta.getOriginal().getPosition();
            default:
                throw new RuntimeException("Unrecognized enum value: " + this.deltaType);
        }
    }


    private int initEndPositionByType(Delta delta) {
        switch (this.deltaType) {
            case INSERT:
                return delta.getRevised().last();
            case CHANGE:
                return delta.getOriginal().last();
            case DELETE:
                return delta.getOriginal().last();
            default:
                throw new RuntimeException("Unrecognized enum value: " + this.deltaType);
        }
    }


    private DeltaType initType(Delta.TYPE internalDeltaType) {
        switch (internalDeltaType) {
            case INSERT:
                return DeltaType.INSERT;
            case CHANGE:
                return DeltaType.CHANGE;
            case DELETE:
                return DeltaType.DELETE;
            default:
                throw new RuntimeException("Unrecognized enum value: " + internalDeltaType);
        }
    }

}
