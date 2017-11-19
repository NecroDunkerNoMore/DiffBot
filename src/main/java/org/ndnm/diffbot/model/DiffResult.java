package org.ndnm.diffbot.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.tuple.Pair;

@Entity
@Table(name = "diff_result_t")
public class DiffResult implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

    private BigInteger id;
    private List<Pair<String, String>> diffPairs;


    public DiffResult() {
        // Necessary for ORM
    }


    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    public List<Pair<String, String>> getDiffPairs() {
        return diffPairs;
    }


    public void setDiffPairs(List<Pair<String, String>> diffPairs) {
        this.diffPairs = diffPairs;
    }
}
