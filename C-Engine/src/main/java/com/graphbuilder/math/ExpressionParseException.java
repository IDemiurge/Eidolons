package com.graphbuilder.math;

/**
 * Exception thrown if expression cannot be parsed correctly.
 *
 * @see com.graphbuilder.math.ExpressionTree
 */
public class ExpressionParseException extends RuntimeException {

    private String descrip = null;
    private int index = 0;

    public ExpressionParseException(String descrip, int index) {
        this.descrip = descrip;
        this.index = index;
    }

    public String getDescription() {
        return descrip;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return "(" + index + ") " + descrip;
    }
}