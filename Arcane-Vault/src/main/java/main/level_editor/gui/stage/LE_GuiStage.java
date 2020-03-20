package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.LevelEditor;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.dialog.EnumChooser;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.control.ControlPanelHolder;
import main.level_editor.gui.panels.control.TabbedControlPanel;
import main.level_editor.gui.panels.palette.HybridPalette;
import main.level_editor.gui.top.LE_ButtonStripe;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.LE_TreeHolder;

public class LE_GuiStage extends GenericGuiStage {

    private   ClosablePanel dialogueTable;
    private ClosablePanel treeHolderPanel;
    private TopPanel topPanel;
    private LE_ButtonStripe buttons;
    private ClosablePanel controlPanel;
    private ClosablePanel palettePanel;

    LE_TreeHolder treePanel;
    BlockTemplateChooser templateChooser;
    EnumChooser enumChooser;
    DataTable editTable;

    TablePanelX innerTable;


    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        TablePanelX toolHolder = new ControlPanelHolder();
        TabbedControlPanel tabs = new TabbedControlPanel(toolHolder);
        controlPanel = new ClosablePanel();
        controlPanel.add(tabs.getTabsPane()).width(600).row();
        controlPanel.add(toolHolder);
        tabs.switchTab(0);

        treeHolderPanel = new ClosablePanel();
        treeHolderPanel.add(treePanel = new LE_TreeHolder());
        palettePanel = new HybridPalette();
        topPanel = new TopPanel();
        initButtons();
        if (isTableMode()) {
            initTable();
        } else {
            addActor(palettePanel);
            addActor(topPanel);
            addActor(treeHolderPanel);
            addActor((controlPanel));
        }

        //separate table?
        addActor(dialogueTable = new ClosablePanel());

        dialogueTable.add(  templateChooser = new BlockTemplateChooser());
        dialogueTable.add(enumChooser = new EnumChooser());
        dialogueTable.add(editTable = new DataTable(2, 50));
    }

    private void initButtons() {
        buttons = new LE_ButtonStripe();
        treeHolderPanel.setLinkedButton(buttons.getStructurePanel());
        controlPanel.setLinkedButton(buttons.getControlPanel());
        palettePanel.setLinkedButton(buttons.getPalettePanel());
//treeHolderPanel.setLinkedButton(buttons.getBrushes());
//treeHolderPanel.setLinkedButton(buttons.getViewModes());
    }

    private void initTable() {
        clear();
        addActor(innerTable = new TablePanelX(Gdx.graphics.getWidth()
                * ScreenMaster.WIDTH_WINDOWED /100, Gdx.graphics.getHeight()* ScreenMaster.HEIGHT_WINDOWED/100) {
            @Override
            public void layout() {
                super.layout();
                return;
            }
        });
        innerTable.debugAll();
        Cell cell = innerTable.add(topPanel = new TopPanel()).expandX().height(100);

        cell = innerTable.add(buttons).left().right().expandX().height(100);
        innerTable.row();
        cell =  innerTable.add(controlPanel).colspan(2).center().top().height(200);
        innerTable.row();
        cell =   innerTable.add(treePanel = new LE_TreeHolder()).left();
        //could have 2 - one top and one bottom?
        cell = innerTable.add(palettePanel = new HybridPalette()).size(400, 900).center();

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
//        if (treePanel.getUserObject() == null) {
//           GuiEventManager.trigger(GuiEventType.LE_TREE_RESET, LevelEditor.getCurrent());
//        }
        if (!isTableMode()) {
            topPanel.setY(Gdx.graphics.getHeight() - 50);
            topPanel.setX(200);
            GdxMaster.center(palettePanel);
            palettePanel.setY(800);

            controlPanel.setX(99);
            controlPanel.setY(Gdx.graphics.getHeight() - controlPanel.getHeight() - 199);
            treePanel.setX(Gdx.graphics.getWidth() - treePanel.getWidth());
            treePanel.setY(Gdx.graphics.getHeight() - treePanel.getHeight());
        }
        GdxMaster.center(templateChooser);
        GdxMaster.center(enumChooser);
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
    public boolean keyDown(int keyCode) {
        Eidolons.onNonGdxThread(()->{
            LevelEditor.getCurrent().getGame().getKeyManager().handleKeyDown(keyCode);
        });
        return super.keyDown(keyCode);
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
