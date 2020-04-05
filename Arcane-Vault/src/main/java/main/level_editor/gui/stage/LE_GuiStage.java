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
import main.level_editor.LevelEditor;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.dialog.ChooserDialog;
import main.level_editor.gui.dialog.EnumChooser;
import main.level_editor.gui.dialog.struct.BlockEditDialog;
import main.level_editor.gui.dialog.struct.ModuleDialog;
import main.level_editor.gui.dialog.struct.ZoneEditDialog;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.control.ControlPanelHolder;
import main.level_editor.gui.panels.control.TabbedControlPanel;
import main.level_editor.gui.panels.palette.HybridPalette;
import main.level_editor.gui.top.LE_ButtonStripe;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.LE_TreeHolder;
import main.system.ExceptionMaster;
import main.system.auxiliary.log.LogMaster;

public class LE_GuiStage extends GenericGuiStage {

    private TablePanelX dialogueTable;
    private TopPanel topPanel;
    private LE_ButtonStripe buttons;
    private ClosablePanel controlPanel;
    private ClosablePanel palettePanel;

    private LE_TreeHolder treePanel;
    private BlockTemplateChooser templateChooser;
    private EnumChooser enumChooser;
    private DataTable editTable;

    private TablePanelX innerTable;
    private ChooserDialog dialog;
    private BlockEditDialog blockEditor;
    private ModuleDialog moduleEditor;
    private ZoneEditDialog zoneEditor;


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

        addActor(templateChooser = new BlockTemplateChooser());
        addActor(enumChooser = new EnumChooser());
        addActor(blockEditor = new BlockEditDialog());
        addActor(moduleEditor = new ModuleDialog());
        addActor(zoneEditor = new ZoneEditDialog());
        addActor(editTable = new DataTable(2, 50));

        tooltips.setZIndex(Integer.MAX_VALUE);
        confirmationPanel.setZIndex(Integer.MAX_VALUE);
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
        addActor(innerTable = new TablePanelX());
        innerTable.row().maxHeight(120);
        innerTable.padTop(30);
        innerTable.add(topPanel = new TopPanel()).top();

        innerTable.add(buttons).bottom().right().expandX();
//        innerTable.row();
        innerTable.add(controlPanel).center().top();


        innerTable.row();
        TablePanelX c;
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
        if (dialog != null) {
            GdxMaster.center(dialog);
        }
        super.act(delta);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            innerTable.remove();
            initTable();
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public void unfocus(Actor actor) {
        super.unfocus(actor);
    }

    @Override
    public void unfocusAll() {
        super.unfocusAll();
    }

    @Override
    public boolean setScrollFocus(Actor actor) {
        boolean r = super.setScrollFocus(actor);
        if (!r) {
            LogMaster.log(1, "------ setScrollFocus " + actor);
        }
        return r;
    }

    @Override
    public boolean keyDown(int keyCode) {
        boolean r = super.keyDown(keyCode);
        Eidolons.onNonGdxThread(() -> {
            LevelEditor.getManager().getKeyHandler().keyDown(keyCode);
        });
        return r;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean r = super.keyTyped(character);
        Eidolons.onNonGdxThread(() -> {
            LevelEditor.getManager().getKeyHandler().keyTyped(character);
        });
        return r;
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
        dialog = enumChooser;
        return enumChooser;
    }

    public BlockTemplateChooser getTemplateChooser() {
        dialog = templateChooser;
        return templateChooser;
    }

    public ModuleDialog getModuleDialog() {
        dialog = moduleEditor;
        return moduleEditor;
    }

    public ZoneEditDialog getZoneEditor() {
        dialog = zoneEditor;
        return zoneEditor;
    }
    public ChooserDialog getDialog() {
        return dialog;
    }

    public BlockEditDialog getBlockEditor() {
        dialog = blockEditor;
        return blockEditor;
    }

    public void toggleUiVisible() {
        controlPanel.toggleFade();
        treePanel.toggleFade();
        palettePanel.toggleFade();
    }
}
