package main.level_editor.gui.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.gui.palette.PaletteHolder;
import main.level_editor.gui.top.TopPanel;

public class LE_GuiStage extends GenericGuiStage {

    private final PaletteHolder palettePanel;
    private final TopPanel topPanel;

    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        palettePanel = new PaletteHolder();
        addActor(palettePanel);

        topPanel = new TopPanel() ;
        addActor(topPanel);


        palettePanel.debugAll();
        topPanel.debugAll();
        setDebugAll(true);
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void act(float delta) {
        GdxMaster.top(topPanel);
        GdxMaster.center(palettePanel);
        palettePanel.setY(100);
        super.act(delta);
    }
}
