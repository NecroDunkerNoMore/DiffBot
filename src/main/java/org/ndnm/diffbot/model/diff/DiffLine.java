package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "diff_line_t")
public class DiffLine implements Serializable {
    private static final long serialVersionUID = 9204315634738276939L;

    private BigInteger id;
    private String line;
    private LineType lineType;
    private DiffDelta diffDelta;//ORM parent


    public DiffLine() {
        //for orm
    }


    public DiffLine(String line, LineType lineType) {
        this.line = line;
        this.lineType = lineType;
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


    @Column(name = "line")
    public String getLine() {
        return line;
    }


    public void setLine(String line) {
        this.line = line;
    }


    @Enumerated(EnumType.ORDINAL)
    public LineType getLineType() {
        return lineType;
    }


    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }


    public DiffDelta getDiffDelta() {
        return diffDelta;
    }


    public void setDiffDelta(DiffDelta diffDelta) {
        this.diffDelta = diffDelta;
    }


    @Override
    public String toString() {
        return line;
    }
}
