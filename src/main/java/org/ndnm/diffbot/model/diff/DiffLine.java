package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;

public class DiffLine implements Serializable {
    private static final long serialVersionUID = 9204315634738276939L;

    private BigInteger id;
    private String line;
    private LineType lineType;


    public DiffLine() {
        //for orm
    }


    public DiffLine(String line, LineType lineType) {
        this.line = line;
        this.lineType = lineType;
    }


    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    public String getLine() {
        return line;
    }


    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return line;
    }


    public LineType getLineType() {
        return lineType;
    }


    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

}
