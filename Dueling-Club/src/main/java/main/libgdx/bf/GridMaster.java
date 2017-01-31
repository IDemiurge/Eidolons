package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.battlefield.Coordinates;
import main.libgdx.GameScreen;

/**
 * Created by JustMe on 1/29/2017.
 */
public class GridMaster {

    public static float getDistance(Coordinates coordinates, Coordinates coordinates2) {
        Vector2 v1 = getGrid().getVectorForCoordinateWithOffset(coordinates);
        Vector2 v2 = getGrid().getVectorForCoordinateWithOffset(coordinates2);
        float xDiff = v1.x-v2.x;
        float yDiff = v1.y-v2.y;
        return (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
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
