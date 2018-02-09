package main.libgdx.screens.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.libgdx.bf.menu.GameMenu;
import main.libgdx.stage.GuiStage;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapGuiStage extends GuiStage {
    public MapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        init();
    }
    @Override
    public boolean keyDown(int keyCode) {
        return true;
    }
    @Override
    protected GameMenu createGameMenu() {
        return new MapMenu();//TODO loop
    }

    @Override
    protected boolean handleKeyTyped(char character) {
        return true; //TODO
    }
}
