package libgdx.bf.decor.pillar;

import com.badlogic.gdx.math.Vector2;
import eidolons.content.consts.VisualEnums;
import main.game.bf.directions.DIRECTION;

import static eidolons.content.consts.VisualEnums.PILLAR.*;
import static main.game.bf.directions.DIRECTION.*;
import static main.game.bf.directions.DIRECTION.DOWN;
import static main.game.bf.directions.DIRECTION.RIGHT;

public class Pillars {
    public static final DIRECTION prefHor = RIGHT;
    public static final DIRECTION prefVert = DOWN;
    public static final int size = 40;

    public static VisualEnums.PILLAR getPillar(Object o) {
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
                return VisualEnums.PILLAR.UP;
            case DOWN_RIGHT:
                return VisualEnums.PILLAR.DOWN;
            case UP_RIGHT:
                return VisualEnums.PILLAR.LEFT;
            case UP_LEFT:
                return VisualEnums.PILLAR.RIGHT;
        }
        return null;
    }

    public static DIRECTION[] getAdjacent(VisualEnums.PILLAR type, boolean wall) {
        if (wall) {
            switch (type) {
                case HOR:
                case SINGLE:
                    return new DIRECTION[]{DOWN};
                case VERT:
                    return new DIRECTION[]{RIGHT};
                case RIGHT:
                    return new DIRECTION[]{DOWN_RIGHT, DOWN};
                case UP:
                    return new DIRECTION[]{RIGHT, UP_RIGHT};
                case LEFT:
                    return new DIRECTION[]{DOWN_LEFT, DOWN};
                case CORNER:
                case SKEWED_CORNER:
                case SKEWED_CORNER_UP:
                case SKEWED_CORNER_LEFT:
                    return new DIRECTION[]{DOWN_RIGHT, DOWN, RIGHT}; //TODO const
                case DOWN:
                    return new DIRECTION[]{RIGHT, DOWN_RIGHT};
            }
        }
        switch (type) {
            case HOR:
            case SINGLE:
            case LEFT:
            case RIGHT:
                return new DIRECTION[]{null, DOWN};
            case VERT:
            case UP:
            case DOWN:
                return new DIRECTION[]{null, RIGHT};
            case CORNER:
            case SKEWED_CORNER:
            case SKEWED_CORNER_LEFT:
            case SKEWED_CORNER_UP:
                return new DIRECTION[]{null, DOWN_RIGHT, DOWN, RIGHT};
        }
        return null;
    }

    static VisualEnums.PILLAR getDefault(boolean wall) {
        if (wall) {
            return SKEWED_CORNER;
        }
        return SINGLE;
    }

    public static VisualEnums.PILLAR getCorner(boolean skewed) {
        return skewed ? SKEWED_CORNER : CORNER;
    }

    public static Vector2 getOffset(VisualEnums.PILLAR pillar) {
        switch (pillar) {
            case VERT:
            case UP:
            case DOWN:
                return new Vector2(prefHor == RIGHT ? 128 : -128, 0);
        }
        return new Vector2(0, -size);
    }

    //VERT == DOWN, HOR == UP;  skew is normal for HOR, RIGHT==UP, LEFT==DOWN for VERT
    public static VisualEnums.PILLAR getPillarDIRECTION(Boolean vert, Boolean skewRightLeftNone) {
        if (vert == null) {
            // return prefHor ==  ?PILLAR.RIGHT  : PILLAR.LEFT ;
        }
        if (skewRightLeftNone == null) {
            return vert ? VERT : HOR;
        }
        return vert ? (!skewRightLeftNone ? VisualEnums.PILLAR.UP : VisualEnums.PILLAR.DOWN)
                : (!skewRightLeftNone ? VisualEnums.PILLAR.RIGHT : VisualEnums.PILLAR.LEFT);
    }


}
