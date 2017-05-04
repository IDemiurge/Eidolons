package main.libgdx.bf;

import com.badlogic.gdx.math.Vector2;
import main.game.battlefield.Coordinates;
import main.game.battlefield.vision.VisionManager;
import main.libgdx.DungeonScreen;
import main.libgdx.bf.mouse.InputController;

/**
 * Created by JustMe on 1/29/2017.
 */
public class GridMaster {

    private static boolean gammaOn = true;

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 = getVectorForCoordinateWithOffset(coordinates);
        Vector2 v2 = getVectorForCoordinateWithOffset(coordinates2);
        return (float) Math.sqrt(v1.dst2(v2));
    }

    public static Vector2 getVectorForCoordinateWithOffset(Coordinates sourceCoordinates) {
        InputController controller = DungeonScreen.getInstance().getController();
        float x = sourceCoordinates.getX() * GridConst.CELL_W / controller.getZoom();
        float y = (DungeonScreen.getInstance().getGridPanel().getRows() - sourceCoordinates.getY()) * GridConst.CELL_H / controller.getZoom();
        x += GridConst.CELL_W / 2;
        y -= GridConst.CELL_H / 2;
        return new Vector2(x, y);
    }

    public static Vector2 getMouseCoordinates() {
        return DungeonScreen.getInstance().getGridStage().screenToStageCoordinates(new Vector2());
    }

    public static void offset(Vector2 orig, Vector2 dest, int additionalDistance, boolean xPositive, boolean yPositive) {
        Vector2 v = new Vector2(dest.x - orig.x, dest.y - orig.y);

// similar triangles solution!

        double hypotenuse = Math.sqrt(v.x * v.x + v.y * v.y);

        double ratio = (hypotenuse + additionalDistance) / hypotenuse;
        float xDiff = (float) (ratio * v.x) - v.x;
        float yDiff = (float) (ratio * v.y) - v.y;
        v.add(xDiff, yDiff);
    }

    public static boolean isGammaOn() {
        if (VisionManager.isVisionHacked()) {
            return false;
        }
        return gammaOn;
    }

    public static void setGammaOn(boolean gammaOn) {
        GridMaster.gammaOn = gammaOn;
    }
}
