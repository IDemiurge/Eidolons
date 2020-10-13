package eidolons.libgdx.bf.decor.pillar;

import com.badlogic.gdx.math.Vector2;
import main.game.bf.directions.DIRECTION;

import static eidolons.libgdx.bf.decor.pillar.Pillars.PILLAR.*;
import static main.game.bf.directions.DIRECTION.*;

public class Pillars {
    public static final DIRECTION prefHor = DIRECTION.RIGHT;
    public static final DIRECTION prefVert = DIRECTION.DOWN;
    public static final int size = 40;

    public static PILLAR getPillar(Object o) {
        if (o == getCorner(false)) {
            return CORNER;
        }
        DIRECTION d = (DIRECTION) o;
        switch (d) {
            case UP:
                return HOR;
            case DOWN:
                return VERT;
            case LEFT:
                return SKEWED_CORNER;
            case RIGHT:
                return SINGLE;
            case DOWN_LEFT:
                return PILLAR.UP;
            case DOWN_RIGHT:
                return PILLAR.DOWN;
            case UP_RIGHT:
                return PILLAR.LEFT;
            case UP_LEFT:
                return PILLAR.RIGHT;
        }
        return null;
    }

    public static DIRECTION[] getAdjacent(PILLAR type, boolean wall) {
        if (wall) {
            switch (type) {
                case HOR:
                case SINGLE:
                    return new DIRECTION[]{DIRECTION.DOWN};
                case VERT:
                    return new DIRECTION[]{DIRECTION.RIGHT};
                case RIGHT:
                    return new DIRECTION[]{DOWN_RIGHT, DIRECTION.DOWN};
                case UP:
                    return new DIRECTION[]{DIRECTION.RIGHT, UP_RIGHT};
                case LEFT:
                    return new DIRECTION[]{DOWN_LEFT, DIRECTION.DOWN};
                case CORNER:
                case SKEWED_CORNER:
                case SKEWED_CORNER_UP:
                case SKEWED_CORNER_LEFT:
                    return new DIRECTION[]{DOWN_RIGHT, DIRECTION.DOWN, DIRECTION.RIGHT}; //TODO const
                case DOWN:
                    return new DIRECTION[]{DIRECTION.RIGHT, DOWN_RIGHT};
            }
        }
        switch (type) {
            case HOR:
            case SINGLE:
            case LEFT:
            case RIGHT:
                return new DIRECTION[]{null, DIRECTION.DOWN};
            case VERT:
            case UP:
            case DOWN:
                return new DIRECTION[]{null, DIRECTION.RIGHT};
            case CORNER:
            case SKEWED_CORNER:
            case SKEWED_CORNER_LEFT:
            case SKEWED_CORNER_UP:
                return new DIRECTION[]{null, DOWN_RIGHT, DIRECTION.DOWN, DIRECTION.RIGHT};
        }
        return null;
    }

    public enum PILLAR {
        HOR, VERT,
        RIGHT, UP,
        LEFT, DOWN,
        SINGLE {
            @Override
            public String toString() {
                return "bare";
            }
        },
        CORNER,
        SKEWED_CORNER {
            @Override
            public String toString() {
                return "skewed_corner";
            }
        },
        SKEWED_CORNER_UP {
            @Override
            public String toString() {
                return "skewed_corner_up";
            }
        }, SKEWED_CORNER_LEFT {
            @Override
            public String toString() {
                return "skewed_corner_left";
            }
        };

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

    }

    static PILLAR getDefault(boolean wall) {
        if (wall) {
            return SKEWED_CORNER;
        }
        return SINGLE;
    }

    public static PILLAR getCorner(boolean skewed) {
        return skewed ? SKEWED_CORNER : CORNER;
    }

    public static Vector2 getOffset(PILLAR pillar) {
        switch (pillar) {
            case VERT:
            case UP:
            case DOWN:
                return new Vector2(prefHor == DIRECTION.RIGHT ? 128 : -128, 0);
        }
        return new Vector2(0, -size);
    }

    //VERT == DOWN, HOR == UP;  skew is normal for HOR, RIGHT==UP, LEFT==DOWN for VERT
    public static PILLAR getPillarDIRECTION(Boolean vert, Boolean skewRightLeftNone) {
        if (vert == null) {
            // return prefHor ==  ?PILLAR.RIGHT  : PILLAR.LEFT ;
        }
        if (skewRightLeftNone == null) {
            return vert ? VERT : HOR;
        }
        return vert ? (!skewRightLeftNone ? PILLAR.UP : PILLAR.DOWN)
                : (!skewRightLeftNone ? PILLAR.RIGHT : PILLAR.LEFT);
    }


}
