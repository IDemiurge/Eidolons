package eidolons.libgdx.bf.decor.pillar;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.PILLAR_TYPE;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.EnumMaster;

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
                return getDefault(false);
            case CORNER_SKEWED:
                return DIRECTION.LEFT;
        }
        return null;
    }

    public static PILLAR_TYPE getType(boolean wall, Coordinates c) {
        String name;
        if (wall) {
            // name = DC_Game.game.getObjectByCoordinate(c).getProperty(prop);
            //for struct?
            // return PILLAR_TYPE.bone;
            // name = DC_Game.game.getCellByCoordinate(c).getCellType().name();
        }
        // else
            name = DC_Game.game.getCellByCoordinate(c).getCellType().name();
        PILLAR_TYPE type = new EnumMaster<PILLAR_TYPE>().retrieveEnumConst(PILLAR_TYPE.class, name);
        if (type == null) {
            LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(c);
            name = struct.getPropagatedValue("pillar type");
            if (name.isEmpty()) {
                return getDefaultType();
            }
            return new EnumMaster<PILLAR_TYPE>().retrieveEnumConst(PILLAR_TYPE.class, name);
        }
        return type;

    }

    private static PILLAR_TYPE getDefaultType() {
        return PILLAR_TYPE.crypt;
    }

    public static PILLAR getPillar(Object o) {
        if (o==getCorner(false)) {
            return PILLAR.CORNER;
        }
        DIRECTION d = (DIRECTION) o;
        switch (d) {
            case UP:
                return PILLAR.HOR;
            case DOWN:
                return PILLAR.VERT;
            case LEFT:
                return PILLAR.CORNER_SKEWED;
            case RIGHT:
                return PILLAR.SINGLE;
            case UP_LEFT:
                return PILLAR.VERT_DOWN;
            case UP_RIGHT:
                return PILLAR.VERT_UP;
            case DOWN_RIGHT:
                return PILLAR.HOR_RIGHT;
            case DOWN_LEFT:
                return PILLAR.HOR_LEFT;
        }
        return null;
    }

    public static DIRECTION getAdjacent(PILLAR type, boolean wall) {
        switch (type) {
            case HOR:
            case SINGLE:
                return DIRECTION.DOWN;
            case VERT:
                return DIRECTION.RIGHT;
            case HOR_RIGHT:
                return DIRECTION.DOWN_LEFT;
            case VERT_UP:
            case HOR_LEFT:
            case CORNER:
            case CORNER_SKEWED:
                return DIRECTION.DOWN_RIGHT;
            case VERT_DOWN:
                return DIRECTION.UP_RIGHT;
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

    static DIRECTION getDefault(boolean wall) {
        if (wall) {
            return getCorner(true);
        }
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
        if (direction == getDefault(false)) {
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

    public static String getPillarPath(DungeonEnums.PILLAR_TYPE cell, DIRECTION direction) {
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
            return prefix + "skewed corner.png";
        }
        if (direction == getDefault(false)) {
            return prefix + "bare.png";
        }
        boolean vert = Boolean.TRUE == direction.isGrowY();
        if (direction.isDiagonal())
            return prefix + (vert ? (direction.isGrowX() ? "down" : "up") : (direction.isGrowX() ? "left" : "right")) + ".png";
        else {
            return prefix + (vert ? "vert" : "hor") + ".png";
        }
    }

    public static String getPillarPathAlt(DungeonEnums.PILLAR_TYPE cell, DIRECTION direction) {
        if (direction == getCorner(false)) {
            return PathFinder.getPillarsPath() + "double/corner_" + cell.toString() + ".png";
        }
        if (direction == getCorner(true)) {
            return PathFinder.getPillarsPath() + "double/skewed_corner_" + cell.toString() + ".png";
        }
        if (direction == getDefault(false)) {
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
