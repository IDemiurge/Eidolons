package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;
import main.libgdx.bf.mouse.InputController;

/**
 * Created by JustMe on 1/29/2017.
 */
public class GridMaster {

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 =  getVectorForCoordinateWithOffset(coordinates);
        Vector2 v2 =  getVectorForCoordinateWithOffset(coordinates2);
        float xDiff = v1.x-v2.x;
        float yDiff = v1.y-v2.y;
        return (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
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
    private static GridPanel getGrid() {return GameScreen.getInstance().getGridPanel();
    }
    private static Stage getStage() {return GameScreen.getInstance().getGridStage();
    }

    public static Vector2 getMouseCoordinates() {
       return getStage().screenToStageCoordinates(
        new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }
}
