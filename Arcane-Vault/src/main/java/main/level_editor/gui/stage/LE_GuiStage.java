package main.level_editor.gui.stage;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.GuiStage;
import main.level_editor.gui.palette.PalettePanel;
import main.level_editor.gui.top.TopPanel;

public class LE_GuiStage extends GenericGuiStage {

    PalettePanel palettePanel;
    TopPanel topPanel;

    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addActor(palettePanel = new PalettePanel());
        addActor(topPanel = new TopPanel());

        GdxMaster.top(topPanel);
        GdxMaster.center(palettePanel);
        palettePanel.setY(0);

    }
}
