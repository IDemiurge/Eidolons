package logic.lane;

import java.util.Objects;

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
        return getCell() % 2==1;
    }
}
