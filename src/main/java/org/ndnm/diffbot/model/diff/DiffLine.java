package org.ndnm.diffbot.model.diff;

import java.io.Serializable;
import java.math.BigInteger;

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
import javax.persistence.Table;


@Entity
@Table(name = "diff_line_t")
public class DiffLine implements Serializable {
    private static final long serialVersionUID = 9204315634738276939L;

    private BigInteger id;
    private DiffDelta diffDelta;//ORM parent
    private String line;
    private LineType lineType;


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


    @Enumerated(EnumType.STRING)
    @Column(name = "line_type")
    public LineType getLineType() {
        return lineType;
    }


    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }


    @ManyToOne(targetEntity = DiffDelta.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "diff_delta_id", nullable = false)
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
