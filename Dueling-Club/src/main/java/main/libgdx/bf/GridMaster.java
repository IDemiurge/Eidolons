package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import main.game.bf.Coordinates;
import main.libgdx.gui.CursorPosVector2;
import main.libgdx.screens.DungeonScreen;

/**
 * Created by JustMe on 1/29/2017.
 */
public class GridMaster {

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 = getCenteredPos(coordinates);
        Vector2 v2 = getCenteredPos(coordinates2);
        return v1.dst(v2);
    }

    public static Vector2 getPosWithOffset(Coordinates sourceCoordinates) {
        return getVectorForCoordinate(sourceCoordinates, false, true);
    }
        public static Vector2 getCenteredPos(Coordinates sourceCoordinates) {
        return getVectorForCoordinate(sourceCoordinates, true, false);
    }


    public static Vector2 getVectorForCoordinate(Coordinates sourceCoordinates,
                                                 boolean center,
                                                 boolean camera) {
//        InputController controller = DungeonScreen.getInstance().getController();
        float x = sourceCoordinates.getX() * GridConst.CELL_W  ;
        float y = (DungeonScreen.getInstance().getGridPanel().getRows()
         - sourceCoordinates.getY()) * GridConst.CELL_H  ;
        if (camera) {
//            x -= controller.getXCamPos();
//            y -= controller.getYCamPos();
            x -= Gdx.graphics.getWidth()/2;
            y -= Gdx.graphics.getHeight()/2;
        }
        if (center) {
            x += GridConst.CELL_W  / 2;
            y -= GridConst.CELL_H  / 2;
        }
        else {

        }
        return new Vector2(x, y);
    }

    public static Vector2 getMouseCoordinates() {
        return DungeonScreen.getInstance().getGridStage().screenToStageCoordinates(new CursorPosVector2());
    }

    public static void offset(Vector2 orig, Vector2 dest, int additionalDistance) {
        Vector2 v = new Vector2(dest.x - orig.x, dest.y - orig.y);

// similar triangles solution!

        double hypotenuse = dest.dst(orig);

        double ratio = (hypotenuse + additionalDistance) / hypotenuse;
        float xDiff = (float) (ratio * v.x) - v.x;
        float yDiff = (float) (ratio * v.y) - v.y;
        v.add(xDiff, yDiff);
    }
}
