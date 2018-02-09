package main.libgdx.bf.mouse;

import com.badlogic.gdx.graphics.OrthographicCamera;
import main.libgdx.bf.GridConst;
import main.libgdx.screens.DungeonScreen;

/**
 * Created by JustMe on 2/7/2018.
 */
public class DungeonInputController extends InputController{
    public DungeonInputController(OrthographicCamera camera) {
        super(camera);
    }
    protected void outsideClick() {
        DungeonScreen.getInstance().getGuiStage().outsideClick();
    }
    protected float getHeight() {
        return   DungeonScreen.getInstance().getGridPanel().getRows()
         * GridConst.CELL_H;
    }
    @Override
    protected void cameraStop() {
        DungeonScreen.getInstance() .cameraStop();
    }

    @Override
    protected float getWidth() {
     return DungeonScreen.getInstance().getGridPanel().getCols()
         * GridConst.CELL_W;
    }
}
