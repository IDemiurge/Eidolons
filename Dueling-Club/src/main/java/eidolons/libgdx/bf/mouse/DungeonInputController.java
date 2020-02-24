package eidolons.libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.GenericDungeonScreen;

/**
 * Created by JustMe on 2/7/2018.
 */
public class DungeonInputController extends InputController {
//    float cameraMaxX;
//    float cameraMaxX;
//    float cameraMaxX;
//    float cameraMaxX;

    public DungeonInputController(OrthographicCamera camera) {
        super(camera);
    }

    protected void outsideClick() {
        getScreen().getGuiStage().outsideClick();
    }

    @Override
    protected GenericDungeonScreen getScreen() {
        return DungeonScreen.getInstance();
    }

    protected float getHeight() {
        return getScreen().getGridPanel().getRows()
         * GridMaster.CELL_H;
    }

    @Override
    public boolean isWithinCamera(float x, float y, float width, float height) {
        return super.isWithinCamera(x, y, width, height);
    }

    @Override
    protected float getWidth() {
        return getScreen().getGridPanel().getCols()
                * GridMaster.CELL_W;
    }
    @Override
    protected float getMargin() {
        return 1100;
    }

}
