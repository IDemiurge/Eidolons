package main.libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import main.libgdx.bf.GridConst;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.screens.map.MapScreen;

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
         * GridConst.CELL_H;
    }


    @Override
    protected float getWidth() {
        return getScreen().getGridPanel().getCols()
         * GridConst.CELL_W;
    }
}
