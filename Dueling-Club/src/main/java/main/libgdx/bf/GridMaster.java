package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.battlefield.Coordinates;
import main.game.battlefield.vision.VisionManager;
import main.libgdx.GameScreen;
import main.libgdx.bf.mouse.InputController;

/**
 * Created by JustMe on 1/29/2017.
 */
public class GridMaster {

    private static boolean gammaOn = true;

    public static boolean isCoordinateVisible(Coordinates c) {
        Vector2 v = GridMaster.getVectorForCoordinateWithOffset(c);
        InputController controller = GameScreen.getInstance().getController();
        return controller.getCamera().frustum.pointInFrustum(new Vector3(v.x, v.y, 0));
    }

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 = getVectorForCoordinateWithOffset(coordinates);
        Vector2 v2 = getVectorForCoordinateWithOffset(coordinates2);
        float xDiff = v1.x - v2.x;
        float yDiff = v1.y - v2.y;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public static Vector2 getVectorForCoordinateWithOffset(Coordinates sourceCoordinates) {
        InputController controller = GameScreen.getInstance().getController();
        float x = sourceCoordinates.getX() * GridConst.CELL_W / controller.getZoom();
        float y = (getGrid().getRows() - sourceCoordinates.getY()) * GridConst.CELL_H / controller.getZoom();
        if (true) {
            x += GridConst.CELL_W / 2;
            y -= GridConst.CELL_H / 2;
        }
        return new Vector2(x, y);
    }

    public static GridPanel getGrid() {
        return GameScreen.getInstance().getGridPanel();
    }

    private static Stage getStage() {
        return GameScreen.getInstance().getGridStage();
    }

    public static Vector2 getMouseCoordinates() {
        return getStage().screenToStageCoordinates(
                new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }

    public static int getCellWidth() {
        return GridConst.CELL_W;
    }

    public static int getCellHeight() {
        return GridConst.CELL_H;
    }

    public static void offset(Vector2 orig, Vector2 dest, int additionalDistance, boolean xPositive, boolean yPositive) {
        Vector2 v = new Vector2(dest.x - orig.x, dest.y - orig.y);

// similar triangles solution!

        double hypotenuse = Math.sqrt(v.x * v.x + v.y * v.y);

        double ratio = (hypotenuse + additionalDistance) / hypotenuse;
        float xDiff = (float) (ratio * v.x) - v.x;
        float yDiff = (float) (ratio * v.y) - v.y;
        v.set(v.x + xDiff, v.y +
                yDiff
        );

        /*



         */
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
