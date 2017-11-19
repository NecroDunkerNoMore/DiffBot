package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;
import java.util.List;

import difflib.Delta;

/*
 * Wrapper class for difflib.Delta
 */
public class DiffDelta {
    private BigInteger id;
    private Delta delta;
    private DeltaType type;
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

    @SuppressWarnings("unchecked")
    public List<String> getOriginalLines() {
        return delta.getOriginal().getLines();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRevisedLines() {
        return delta.getRevised().getLines();
    }

}
