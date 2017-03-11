package main.libgdx.gui;

/**
 * util object for functional table creation
 * <p>
 * ATTENTION - not working correctly
 */
public class RowColCounter {
    private int maxRow;
    private int maxCol;

    private int col = 0;
    private int row = 0;

    public RowColCounter(int maxRow, int maxCol) {
        this.maxRow = maxRow;
        this.maxCol = maxCol;
    }

    public boolean isNextRow() {
        return row < maxRow;
    }

    public boolean isNexCol() {
        return col < maxCol;
    }

    public int nextRow() {
        return row++;
    }

    public int nextCol() {
        row = 0;
        return col++;
    }

    public void reset() {
        row = 0;
        col = 0;
    }
}
