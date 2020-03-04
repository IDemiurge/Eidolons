package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.gui.palette.PaletteTabs;
import main.level_editor.gui.palette.PaletteTypesTable;
import main.level_editor.gui.top.TopPanel;

public class LE_GuiStage extends GenericGuiStage {

    private final TablePanelX palettePanel;
    private final TopPanel topPanel;

    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        palettePanel = new TablePanelX();
        PaletteTypesTable palette = new PaletteTypesTable(1);
        palettePanel.add(new PaletteTabs(palette).getTable()).expandX().fillX().row();
        palettePanel.add(palette).expandX().fillX().row();
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
        topPanel.setY(Gdx.graphics.getHeight()-50);
        GdxMaster.center(palettePanel);
        palettePanel.setY(100);
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }


}
