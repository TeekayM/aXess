package net.teekay.axess.access;

import java.io.Serializable;

public enum AccessCompareMode {

    SPECIFIC("SPECIFIC"),
    LESSER_THAN_OR_EQUAL("LESSER_THAN_OR_EQUAL"),
    BIGGER_THAN_OR_EQUAL("BIGGER_THAN_OR_EQUAL");

    public String id;

    AccessCompareMode(String id) {
        this.id = id;
    }

    public String toString() {
        return this.id;
    }
}
