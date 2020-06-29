package eidolons.libgdx.bf.decor.pillar;

import com.badlogic.gdx.math.Vector2;
import main.content.enums.DungeonEnums;
import main.data.filesys.PathFinder;
import main.game.bf.directions.DIRECTION;

public class Pillars {
    public static final DIRECTION prefHor = DIRECTION.RIGHT;
    public static final DIRECTION prefVert = DIRECTION.DOWN;
    public static final int size = 40;
    public static final Integer NO_PILLAR = 0;
    public static final DungeonEnums.CELL_IMAGE DEFAULT_PILLAR = DungeonEnums.CELL_IMAGE.bare;

    public static DIRECTION getPillarD(PILLAR hor) {
        switch (hor) {
            case HOR:
                return DIRECTION.UP;
            case VERT:
                return DIRECTION.DOWN;
            case HOR_RIGHT:
                return DIRECTION.DOWN_RIGHT;
            case HOR_LEFT:
                return DIRECTION.DOWN_LEFT;

            case VERT_UP:
                return DIRECTION.UP_RIGHT;
            case VERT_DOWN:
                return DIRECTION.UP_LEFT;
            case SINGLE:
                return getDefault();
            case CORNER_SKEWED:
                return DIRECTION.LEFT;
        }
        return null;
    }

    public enum PILLAR {
        HOR, VERT,
        HOR_RIGHT, VERT_UP,
        HOR_LEFT, VERT_DOWN,
        SINGLE,
        CORNER,
        CORNER_SKEWED,

    }

    static DIRECTION getDefault() {
        return DIRECTION.RIGHT;
    }

    public static DIRECTION getCorner(boolean skewed) {
        return skewed ? DIRECTION.LEFT : null;
    }

    public static final Vector2 getOffset(DIRECTION direction) {
        if (direction == null) {
            return new Vector2(0, -size);
        }
        if (direction == getCorner(true)) {
            return new Vector2(0, -size);
        }
        if (direction == getDefault()) {
            return new Vector2(0, -size);
        }
        boolean vert = direction.growY == true;
        if (vert) {
            return new Vector2(prefHor == DIRECTION.RIGHT ? 128 : -128, 0);
        } else {
            return new Vector2(0, -size);
        }
    }

    //VERT == DOWN, HOR == UP;  skew is normal for HOR, RIGHT==UP, LEFT==DOWN for VERT
    public static DIRECTION getPillarDIRECTION(Boolean vert, Boolean skewRightLeftNone) {
        if (vert == null) {
            return prefHor;
        }
        if (skewRightLeftNone == null) {
            return vert ? DIRECTION.DOWN : DIRECTION.UP;
        }
        return vert ? (skewRightLeftNone ? DIRECTION.DOWN_RIGHT : DIRECTION.DOWN_LEFT)
                : (skewRightLeftNone ? DIRECTION.UP_RIGHT : DIRECTION.UP_LEFT);
    }

    public static String getPillarPath(DungeonEnums.CELL_IMAGE cell, DIRECTION direction) {
        switch (cell) {
            case bare:
            case mossy:
            case iron:
        return getPillarPathAlt(cell, direction);
        }
        String name = "corner";
        String prefix = PathFinder.getPillarsPath() + cell.toString() + "/";
        if (direction == getCorner(false)) {
            return prefix + "corner.png";
        }
        if (direction == getCorner(true)) {
            return prefix + "corner skewed.png";
        }
        if (direction == getDefault()) {
            return prefix + "bare.png";
        }
        boolean vert = Boolean.TRUE == direction.isGrowY();
        if (direction.isDiagonal())
            return  prefix + (vert ? (direction.isGrowX() ? "down" : "up") : (direction.isGrowX() ? "left" : "right"))+".png";
        else {
        return prefix +(vert? "vert" : "hor")+".png";
        }
    }
        public static String getPillarPathAlt(DungeonEnums.CELL_IMAGE cell, DIRECTION direction) {
        if (direction == getCorner(false)) {
            return PathFinder.getPillarsPath() + "double/corner_" + cell.toString() + ".png";
        }
        if (direction == getCorner(true)) {
            return PathFinder.getPillarsPath() + "double/skewed_corner_" + cell.toString() + ".png";
        }
        if (direction == getDefault()) {
            return PathFinder.getPillarsPath() + "double/pl_" + cell.toString() + ".png";
        }
        boolean vert = Boolean.TRUE == direction.isGrowY();
        String prefix = vert ? "vert" : "hor";
        if (direction.isDiagonal())
            prefix += "/" + (vert ? (direction.isGrowX() ? "down" : "up") : (direction.isGrowX() ? "left" : "right"));

        // prefix = (direction.isVertical())
        return PathFinder.getPillarsPath() + prefix + "/pl_" + cell.toString() + ".png";
    }

}
