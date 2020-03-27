package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.utils.TextInputPanel;
import main.level_editor.LevelEditor;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.dialog.EnumChooser;
import main.level_editor.gui.dialog.ModuleDialog;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.control.ControlPanelHolder;
import main.level_editor.gui.panels.control.TabbedControlPanel;
import main.level_editor.gui.panels.palette.HybridPalette;
import main.level_editor.gui.top.LE_ButtonStripe;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.LE_TreeHolder;
import main.system.ExceptionMaster;

public class LE_GuiStage extends GenericGuiStage {

    private   ClosablePanel dialogueTable;
    private TopPanel topPanel;
    private LE_ButtonStripe buttons;
    private ClosablePanel controlPanel;
    private ClosablePanel palettePanel;

    private  LE_TreeHolder treePanel;
    private  BlockTemplateChooser templateChooser;
    private  EnumChooser enumChooser;
    private DataTable editTable;
    private ModuleDialog moduleDialog;

    private TablePanelX innerTable;
    private TextInputPanel textInput;


    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        TablePanelX toolHolder = new ControlPanelHolder();
        TabbedControlPanel tabs = new TabbedControlPanel(toolHolder);
        controlPanel = new ClosablePanel();
        controlPanel.add(tabs.getTabsPane()).width(600).row();
        controlPanel.add(toolHolder);
        tabs.switchTab(0);

        treePanel = new LE_TreeHolder();
        palettePanel = new HybridPalette();
        topPanel = new TopPanel();
        initButtons();
        if (isTableMode()) {
            initTable();
        } else {
            addActor(palettePanel);
            addActor(topPanel);
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
        treePanel.setLinkedButton(buttons.getStructurePanel());
        controlPanel.setLinkedButton(buttons.getControlPanel());
        palettePanel.setLinkedButton(buttons.getPalettePanel());
//treeHolderPanel.setLinkedButton(buttons.getBrushes());
//treeHolderPanel.setLinkedButton(buttons.getViewModes());
    }

    private void initTable() {
        clear();
        addActor(innerTable = new TablePanelX( ));
        innerTable.row().maxHeight(120);
        innerTable.padTop(30);
        innerTable.add(topPanel = new TopPanel());

        innerTable.add(buttons).bottom().right().expandX();
//        innerTable.row();
        innerTable.add(controlPanel).center().top();


        innerTable.row();
        TablePanelX  c;
        innerTable.add(c = new TablePanelX<>());
        c.add(treePanel).colspan(1).left();
        //could have 2 - one top and one bottom?
        c.add(new Actor()).colspan(3).width(1110);
        c.add(palettePanel).colspan(1).right().top().size(400, 900);


        innerTable.setY(0);
//        innerTable.setLayoutEnabled(false);
//        innerTable.background(NinePatchFactory.getLightDecorPanelDrawable());
        innerTable.setFillParent(true);
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
        boolean r = super.keyDown(keyCode);
        Eidolons.onNonGdxThread(()->{
            LevelEditor.getManager().getKeyHandler().keyDown(keyCode);
        });
        return  r;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean r = super.keyTyped(character);
        Eidolons.onNonGdxThread(()->{
            LevelEditor.getManager().getKeyHandler().keyTyped(character);
        });
        return r;
    }

    @Override
    public boolean keyUp(int keyCode) {
        return super.keyUp(keyCode);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            return super.touchUp(screenX, screenY, pointer, button);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public EnumChooser getEnumChooser() {
        return enumChooser;
    }

    public BlockTemplateChooser getTemplateChooser() {
        return templateChooser;
    }

    public ModuleDialog getModuleDialog() {
        return moduleDialog;
    }

}
