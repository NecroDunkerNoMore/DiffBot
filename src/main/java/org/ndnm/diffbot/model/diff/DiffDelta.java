package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import difflib.Delta;

@Entity
@Table(name = "diff_delta_t")
public class DiffDelta {
    private BigInteger id;
    private DeltaType type;
    private List<DiffLine> originalLines;
    private List<DiffLine> revisedLines;
    private int startPosition;
    private int endPosition;


    public DiffDelta() {
        //for orm
    }


    public DiffDelta(Delta delta) {
        this.type = initType(delta.getType());
        this.startPosition = initStartPositionByType(delta);
        this.endPosition = initEndPositionByType(delta);

        this.originalLines = new ArrayList<>();
        initLines(delta, originalLines, LineType.ORIGINAL);

        this.revisedLines = new ArrayList<>();
        initLines(delta, revisedLines, LineType.REVISED);

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
        switch (this.type) {
            case CHANGE:
                return delta.getOriginal().getPosition();
            case INSERT:
                return delta.getRevised().getPosition();
            case DELETE:
                return delta.getOriginal().getPosition();
            default:
                throw new RuntimeException("Unrecognized enum value: " + this.type);
        }
    }


    private int initEndPositionByType(Delta delta) {
        switch (this.type) {
            case CHANGE:
                return delta.getOriginal().last();
            case INSERT:
                return delta.getRevised().last();
            case DELETE:
                return delta.getOriginal().last();
            default:
                throw new RuntimeException("Unrecognized enum value: " + this.type);
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


    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    public DeltaType getType() {
        return type;
    }


    public void setType(DeltaType type) {
        this.type = type;
    }


    public int getStartPosition() {
        return startPosition;
    }


    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }


    public int getEndPosition() {
        return endPosition;
    }


    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }


    public List<DiffLine> getOriginalLines() {
        return originalLines;
    }


    public List<DiffLine> getRevisedLines() {
        return revisedLines;
    }

}
