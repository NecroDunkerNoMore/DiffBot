package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;

public enum LineType {
    ORIGINAL(1),
    REVISED(2);

    private BigInteger id;

    LineType(long id) {
        this.id = BigInteger.valueOf(id);
    }


    public BigInteger getId() {
        return id;
    }
}
