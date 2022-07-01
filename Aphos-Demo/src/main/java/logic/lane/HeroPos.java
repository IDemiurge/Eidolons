package logic.lane;

public class HeroPos {
    private final int cell;
    private final boolean leftSide;

    public HeroPos(int cell, boolean leftSide) {
        this.cell = cell;
        this.leftSide = leftSide;
    }

    public int getCell() {
        return cell;
    }

    public boolean isLeftSide() {
        return leftSide;
    }
}
