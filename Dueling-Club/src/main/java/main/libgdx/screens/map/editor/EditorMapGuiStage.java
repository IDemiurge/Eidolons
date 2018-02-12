package main.libgdx.screens.map.editor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.libgdx.GdxMaster;
import main.libgdx.screens.map.MapGuiStage;

/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorMapGuiStage extends MapGuiStage {
    private EditorControlPanel controlPanel;
    private EditorPalette palette;

    public EditorMapGuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    protected boolean isVignetteOn() {
        return false;
    }
    @Override
    protected void init() {
         palette = new EditorPalette();
        addActor(palette);
        controlPanel = new EditorControlPanel();
        controlPanel.setPosition(0, GdxMaster.top(controlPanel)-125);
        addActor(controlPanel);
    }

    public EditorPalette getPalette() {
        return palette;
    }
}
