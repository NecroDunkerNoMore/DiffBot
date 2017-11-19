package org.ndnm.diffbot.model.diff;

import java.math.BigInteger;

public enum DeltaType {
    CHANGE(1),
    INSERT(2),
    DELETE(3);

    private BigInteger id;

    DeltaType(long id) {
        this.id = BigInteger.valueOf(id);
    }


    public BigInteger getId() {
        return id;
    }
}
