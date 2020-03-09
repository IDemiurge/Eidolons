package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.gui.palette.PaletteHolder;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.LE_TreePanel;

public class LE_GuiStage extends GenericGuiStage {

    private final TablePanelX palettePanel;
    private final TopPanel topPanel;
    LE_TreePanel treePanel;

    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        addActor(palettePanel= new PaletteHolder());
        addActor(topPanel= new TopPanel());
        addActor(treePanel=new LE_TreePanel());

    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void act(float delta) {
        topPanel.setY(Gdx.graphics.getHeight() - 50);
        topPanel.setX(200);
        GdxMaster.center(palettePanel);
        palettePanel.setY(100);

        treePanel.setX(Gdx.graphics.getWidth() - treePanel.getWidth() );
        treePanel.setY(Gdx.graphics.getHeight() - treePanel.getHeight() );
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            return super.touchUp(screenX, screenY, pointer, button);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return true;
    }


}
