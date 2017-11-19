package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import difflib.Delta;

/*
 * Wrapper class for difflib.Delta
 */
public class DiffDelta {
    private BigInteger id;
    private Delta delta;
    private DeltaType type;
    private List<DiffLine> originalLines;
    private List<DiffLine> revisedLines;
    private int startPosition;
    private int endPosition;


    public DiffDelta() {
        //for orm
    }

    public DiffDelta(Delta delta) {
        this.delta = delta;
        this.type = getTypeFrom(delta);
        this.startPosition = getStartPositionByType();
        this.endPosition = getEndPositionByType();

        this.originalLines = new ArrayList<>();
        initLines(originalLines, LineType.ORIGINAL);

        this.revisedLines = new ArrayList<>();
        initLines(revisedLines, LineType.REVISED);

    }


    @SuppressWarnings("unchecked")
    private void initLines(List<DiffLine> diffLines, LineType type) {
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


    private int getStartPositionByType() {
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

    private int getEndPositionByType() {
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

    private DeltaType getTypeFrom(Delta delta) {
        switch (delta.getType()){
            case CHANGE:
                return DeltaType.CHANGE;
            case INSERT:
                return DeltaType.INSERT;
            case DELETE:
                return DeltaType.DELETE;
            default:
                throw new RuntimeException("Unrecognized enum value: " + delta.getType());
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
