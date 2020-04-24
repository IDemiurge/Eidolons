package main.content.values;

import main.content.VALUE;

public class ValuePair {
    protected VALUE constant;
    protected String value;

    public ValuePair(VALUE constant, String value) {
        this.constant = constant;
        this.value = value;
    }

    public VALUE getConstant() {
        return constant;
    }

    public String getValue() {
        return value;
    }
}
