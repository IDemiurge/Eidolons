package eidolons.libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.screens.DungeonScreen;

/**
 * Created by JustMe on 2/7/2018.
 */
public class DungeonInputController extends InputController {
    public DungeonInputController(OrthographicCamera camera) {
        super(camera);
    }

    protected void outsideClick() {
        getScreen().getGuiStage().outsideClick();
    }

    @Override
    protected DungeonScreen getScreen() {
        return DungeonScreen.getInstance();
    }

    protected float getHeight() {
        return getScreen().getGridPanel().getRows()
         * GridMaster.CELL_H;
    }

    @Override
    protected float getMargin() {
        return 600;
    }

    @Override
    protected float getWidth() {
        return getScreen().getGridPanel().getCols()
         * GridMaster.CELL_W;
    }
}
