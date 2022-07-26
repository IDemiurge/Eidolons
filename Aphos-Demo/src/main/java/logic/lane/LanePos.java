package logic.lane;

import gdx.visuals.lanes.LaneConsts;
import main.system.math.MathMaster;

import java.util.Objects;

public class LanePos {
    public final int lane;
    public final int cell;
    public boolean leftSide=false;

    public LanePos(int lane, int cell) {
        this.lane = lane;
        this.cell = cell;
        if (lane < LaneConsts.LANES_PER_SIDE)
            leftSide=true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanePos pos1 = (LanePos) o;
        return lane == pos1.lane &&
                cell == pos1.cell;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lane, cell);
    }

    @Override
    public String toString() {
        return "|lane " + lane +
                ", pos " + cell + "|";
    }

    public int dst(LanePos pos) {
        //TODO precise position pls!
        return  Math.abs(pos.lane - lane) + Math.abs(pos.cell - cell);
    }
    public int dst(HeroPos pos) {
        return Math.abs(pos.getLane() - lane) + cell;
    }
}
