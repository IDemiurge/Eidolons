package logic.lane;

import gdx.visuals.lanes.LaneConsts;

import java.util.Objects;

public class HeroPos {
    public static int MAX_INDEX = 6;
    private final int cell;
    private final boolean leftSide;
    private final int lane;

    public HeroPos(int cell, boolean leftSide) {
        this.cell = cell;
        this.leftSide = leftSide;
        lane = cell / 2 + (leftSide ? 0 : LaneConsts.LANES_PER_SIDE);
    }

    public int getCell() {
        return cell;
    }

    public boolean isLeftSide() {
        return leftSide;
    }

    public int getLane() {
        return lane;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeroPos heroPos = (HeroPos) o;
        return cell == heroPos.cell &&
                leftSide == heroPos.leftSide;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cell, leftSide);
    }

    public boolean isFront() {
        return getCell() % 2 == 1;
    }

    public int dst(HeroPos pos) {
        int cell = pos.getCell();
        if (pos.isLeftSide() != isLeftSide()) {
            //dst to closest edge + 1
            int toEdge1 = getCell() + cell; //via mid, negative
            int toEdge2 = MAX_INDEX - getCell() + MAX_INDEX - cell;

            if (toEdge1 < toEdge2)
                return -toEdge1 - 1;
            return toEdge2 + 1;
        }
        return cell - getCell();
    }

}
