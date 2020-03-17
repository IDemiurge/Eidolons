package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.dialog.EnumChooser;
import main.level_editor.gui.palette.PaletteHolder;
import main.level_editor.gui.panels.LE_ToolPanel;
import main.level_editor.gui.panels.control.ControlPanelHolder;
import main.level_editor.gui.panels.control.TabbedControlPanel;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.LE_TreePanel;

public class LE_GuiStage extends GenericGuiStage {

    private final TablePanelX palettePanel;
    private final TopPanel topPanel;
    private final LE_ToolPanel toolPanel;
    LE_TreePanel treePanel;

    BlockTemplateChooser templateChooser;
    EnumChooser enumChooser;

    TablePanelX innerTable;


    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        addActor(innerTable=new TablePanelX<>(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        TablePanelX toolHolder=new ControlPanelHolder();

        TabbedControlPanel tabs = new TabbedControlPanel(toolHolder);
        TablePanelX<Actor> table = new TablePanelX<>();
        table.add(tabs.getTabsPane()).row();
        table.add(toolHolder);

//        addActor(palettePanel = new PaletteHolder());
//        addActor(topPanel = new TopPanel());
//        addActor(treePanel=new LE_TreePanel());
//        addActor(toolPanel = new LE_ToolPanel(table));
        
        innerTable.add(topPanel = new TopPanel()).left().top().expandX().row();
        innerTable.add(treePanel=new LE_TreePanel()).left();
        innerTable.add(toolPanel = new LE_ToolPanel(table)).colspan(4).center().top();
        //could have 2 - one top and one bottom?
        innerTable.add(palettePanel = new PaletteHolder()).right();

        //what about info panel?

        //popups, draggable, closable? closable is just about hiding...

        //separate table?
        addActor(templateChooser = new BlockTemplateChooser());
        addActor(enumChooser = new EnumChooser());

    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void act(float delta) {
        if (innerTable == null) {
        topPanel.setY(Gdx.graphics.getHeight() - 50);
        topPanel.setX(200);
        GdxMaster.center(palettePanel);
        palettePanel.setY(800);

        GdxMaster.center(templateChooser);
        GdxMaster.center(enumChooser);
        toolPanel.setX(99);
        toolPanel.setY(Gdx.graphics.getHeight() - toolPanel.getHeight() - 199);
        treePanel.setX(Gdx.graphics.getWidth() - treePanel.getWidth() );
        treePanel.setY(Gdx.graphics.getHeight() - treePanel.getHeight() );
        }
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

    public EnumChooser getEnumChooser() {
        return enumChooser;
    }

    public BlockTemplateChooser getTemplateChooser() {
        return templateChooser;
    }
}
