package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.dialog.EnumChooser;
import main.level_editor.gui.palette.HybridPalette;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.control.ControlPanelHolder;
import main.level_editor.gui.panels.control.TabbedControlPanel;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.LE_TreePanel;

public class LE_GuiStage extends GenericGuiStage {

    private TablePanelX palettePanel;
    private TopPanel topPanel;
    private final ClosablePanel toolPanel;

    LE_TreePanel treePanel;
    BlockTemplateChooser templateChooser;
    EnumChooser enumChooser;
    TablePanelX innerTable;


    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        TablePanelX toolHolder = new ControlPanelHolder();

        TabbedControlPanel tabs = new TabbedControlPanel(toolHolder);
        toolPanel = new ClosablePanel();
        toolPanel.add(tabs.getTabsPane()).row();
        toolPanel.add(toolHolder);

        ClosablePanel treeHolderPanel = new ClosablePanel();
        treeHolderPanel.add(treePanel = new LE_TreePanel());
        if (isTableMode()) {
            initTable();
        } else {
            addActor(palettePanel = new HybridPalette());
            addActor(topPanel = new TopPanel());

            addActor(treeHolderPanel);
            addActor((toolPanel));
        }


        //what about info panel?

        //popups, draggable, closable? closable is just about hiding...

        //separate table?
        addActor(templateChooser = new BlockTemplateChooser());
        addActor(enumChooser = new EnumChooser());

    }

    private void initTable() {
        clear();
        addActor(innerTable = new TablePanelX(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
            @Override
            public void layout() {
                super.layout();
                return;
            }
        });
        innerTable.debugAll();
        innerTable.add(topPanel = new TopPanel()).left().top().expandX().height(100).row();
//        innerTable.add(treePanel = new LE_TreePanel()).left();
//        innerTable.add(toolPanel).colspan(4).center().top();
        //could have 2 - one top and one bottom?
        innerTable.add(palettePanel = new HybridPalette()).size(400, 900).right();
    }


    private boolean isTableMode() {
        return true;
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void act(float delta) {
        if (!isTableMode()) {
            topPanel.setY(Gdx.graphics.getHeight() - 50);
            topPanel.setX(200);
            GdxMaster.center(palettePanel);
            palettePanel.setY(800);

            GdxMaster.center(templateChooser);
            GdxMaster.center(enumChooser);
            toolPanel.setX(99);
            toolPanel.setY(Gdx.graphics.getHeight() - toolPanel.getHeight() - 199);
            treePanel.setX(Gdx.graphics.getWidth() - treePanel.getWidth());
            treePanel.setY(Gdx.graphics.getHeight() - treePanel.getHeight());
        }
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            initTable();
        }
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
