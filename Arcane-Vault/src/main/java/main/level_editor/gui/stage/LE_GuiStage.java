package main.level_editor.gui.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterData;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.decor.DecorData;
import eidolons.libgdx.bf.grid.moving.PlatformData;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.ToolTipManager;
import eidolons.libgdx.stage.GenericGuiStage;
import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.advanced.DecorInputHelper;
import main.level_editor.backend.functions.advanced.ScriptInputHelper;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.dialog.AiEditDialog;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.dialog.ChooserDialog;
import main.level_editor.gui.dialog.EnumChooser;
import main.level_editor.gui.dialog.entity.EncounterEditDialog;
import main.level_editor.gui.dialog.struct.*;
import main.level_editor.gui.panels.ClosablePanel;
import main.level_editor.gui.panels.control.ControlPanelHolder;
import main.level_editor.gui.panels.control.TabbedControlPanel;
import main.level_editor.gui.panels.palette.HybridPalette;
import main.level_editor.gui.top.LE_ButtonStripe;
import main.level_editor.gui.top.LE_StatusBar;
import main.level_editor.gui.top.TopPanel;
import main.level_editor.gui.tree.FloorTabs;
import main.level_editor.gui.tree.LE_TreeHolder;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.data.DataUnit;

public class LE_GuiStage extends GenericGuiStage {

    public static boolean dialogActive;
    private final SmartButton open;
    private final TablePanelX<Actor> floorsTable;
    private TopPanel topPanel;
    private LE_ButtonStripe buttons;
    private final ClosablePanel controlPanel;
    private final TabbedControlPanel controlTabs;
    private final HybridPalette palettePanel;

    private final LE_TreeHolder treePanel;
    private final BlockTemplateChooser templateChooser;
    private final EnumChooser enumChooser;
    private final DataTable editTable;

    private TablePanelX innerTable;
    private ChooserDialog dialog;
    private final BlockEditDialog blockEditor;
    private final ModuleDialog moduleEditor;
    private final ZoneEditDialog zoneEditor;
    private final FloorEditDialog floorDialog;
    private final EncounterEditDialog encounterEditor;
    private final PlatformEditDialog platformEditDialog;
    private final AiEditDialog aiEditor;
    private final CellDataEditor cellDataEditor;
    private final DecorEditor decorEditor;

    LE_StatusBar statusBar;
    FloorTabs tabs = new FloorTabs();
    private boolean positionsAdjusted;

    public LE_GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        TablePanelX toolHolder = new ControlPanelHolder();
        controlTabs = new TabbedControlPanel(toolHolder);
        controlPanel = new ClosablePanel();
        controlPanel.add(controlTabs.getTabsPane()).width(600).row();
        controlPanel.add(toolHolder);
        controlTabs.switchTab(0);


        treePanel = new LE_TreeHolder();
        palettePanel = new HybridPalette();
        topPanel = new TopPanel();
        initButtons();
        initTable();

        open = new SmartButton(ButtonStyled.STD_BUTTON.UP, () -> Eidolons.onNonGdxThread(() -> FloorManager.addFloor()));

        floorsTable = new TablePanelX<>(300, 40);
        //        floorsTable.add(open);
        floorsTable.add(tabs.getTabsPane()).width(600).minWidth(300).padLeft(110);
        floorsTable.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        addActor(floorsTable);
        GdxMaster.top(floorsTable);
        floorsTable.setY(floorsTable.getY() - 63);
        addActor(open);
        addActor(templateChooser = new BlockTemplateChooser());
        addActor(enumChooser = new EnumChooser());
        addActor(floorDialog = new FloorEditDialog());
        addActor(blockEditor = new BlockEditDialog());
        addActor(moduleEditor = new ModuleDialog());
        addActor(zoneEditor = new ZoneEditDialog());
        addActor(aiEditor = new AiEditDialog());
        addActor(encounterEditor = new EncounterEditDialog());
        addActor(cellDataEditor = new CellDataEditor());
        addActor(platformEditDialog = new PlatformEditDialog());
        addActor(decorEditor = new DecorEditor());
        addActor(editTable = new DataTable(2, 50));

        addActor(statusBar = new LE_StatusBar());
        GuiEventManager.bind(GuiEventType.LE_GUI_TOGGLE, p -> {
            toggleUiVisible();
        });
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
        //        innerTable.padTop(30);
        TablePanelX upperTable;
        innerTable.add(upperTable = new TablePanelX() {
            @Override
            public void layout() {
                super.layout();
                buttons.setY(buttons.getY() - 20);
                buttons.setX(buttons.getX() + 226);
                topPanel.setX(222);
                topPanel.setY(topPanel.getY() + 14);
                controlPanel.setX(controlPanel.getX() - 215);
                controlPanel.setY(controlPanel.getY() - 50);
                treePanel.setY(treePanel.getY() - 50);
            }
        }).maxHeight(120);
        upperTable.add(topPanel = new TopPanel()).left();
        upperTable.add(buttons).center();
        //        innerTable.row();
        upperTable.add(controlPanel).center().top();

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
        LE_GuiStage.dialogActive = false;
        GdxMaster.top(open);
        open.setY(open.getY() - 50);
        open.setX(1);
        floorsTable.setX(35);

        //        if (treePanel.getUserObject() == null) {
        //           GuiEventManager.trigger(GuiEventType.LE_TREE_RESET, LevelEditor.getCurrent());
        //        }
        statusBar.setY(20);
        statusBar.setX(GdxMaster.getWidth() / 2 - 340);
        //        statusBar.setX(550);
        //        statusBar.setDebug(true);
        super.act(delta);

        if (dialog != null) {
            GdxMaster.center(dialog);
            dialog.setZIndex(Integer.MAX_VALUE);
        }
        enumChooser.setZIndex(Integer.MAX_VALUE);
        if (textInputPanel != null) {
            textInputPanel.setZIndex(Integer.MAX_VALUE);
        }
        confirmationPanel.setZIndex(Integer.MAX_VALUE);
        if (getFileChooser() != null) {
            getFileChooser().setZIndex(Integer.MAX_VALUE);
        }
        tooltips.setZIndex(Integer.MAX_VALUE);
    }

    public void textInput(boolean script, Input.TextInputListener textInputListener, String title, String text, String hint) {
        if (script) {
            textInputPanel = new ScriptInputHelper(LevelEditor.getManager(),
                    title, text, hint, textInputListener);
        } else if (getDialog() != null) {
            if (getDialog() == decorEditor) {
                textInputPanel = new DecorInputHelper(LevelEditor.getManager(),
                        title, text, hint, textInputListener);
            }
        }
        if (textInputPanel == null) {
            super.textInput(script, textInputListener, title, text, hint);
            return;
        }
        addActor(textInputPanel);
        textInputPanel.setPosition(GdxMaster.centerWidth(textInputPanel),
                GdxMaster.centerHeight(textInputPanel));
        textInputPanel.open();
        setKeyboardFocus(textInputPanel);

    }

    @Override
    public boolean setKeyboardFocus(Actor actor) {
        if (actor == null) {
            if (GdxMaster.isVisibleEffectively(textInputPanel)) {
                return false;
            }
        }
        return super.setKeyboardFocus(actor);
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
    public boolean keyUp(int keyCode) {
        boolean r = super.keyUp(keyCode);
        Eidolons.onNonGdxThread(() -> {
            LevelEditor.getManager().getKeyHandler().keyUp(keyCode);
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

    public AiEditDialog getAiEditorDialog() {
        dialog = aiEditor;
        return aiEditor;
    }

    public EncounterEditDialog getEncounterEditor() {
        dialog = encounterEditor;
        return encounterEditor;
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
        if (LevelEditor.getManager().getDisplayHandler().getDisplayMode().isGameView()) {
            statusBar.toggleFade();
            topPanel.toggleFade();
            boolean v = tabs.getTabsPane().isVisible();
            tabs.getTabsPane().setVisible(!v);
            v = buttons.isVisible();
            buttons.setVisible(!v);
            floorsTable.toggleFade();
            v = open.isVisible();
            open.setVisible(!v);
        }
        controlPanel.toggleFade();
        treePanel.toggleFade();
        palettePanel.toggleFade();
    }

    public HybridPalette getPalettePanel() {
        return palettePanel;
    }

    public CellDataEditor getCellDataEditor() {
        dialog = cellDataEditor;
        return cellDataEditor;
    }

    public DecorEditor getDecorEditor() {
        dialog = decorEditor;
        return decorEditor;
    }

    public PlatformEditDialog getPlatformDialog() {
        dialog = platformEditDialog;
        return platformEditDialog;
    }

    public FloorEditDialog getFloorDialog() {
        dialog = floorDialog;
        return floorDialog;
    }

    public TabbedControlPanel getControlTabs() {
        return controlTabs;
    }

    public <S extends Enum<S>, T extends DataUnit<S>>
    DataEditDialog<S, T> getEditDialog(DataUnit<S> dataUnit) {
        if (dataUnit instanceof PlatformData) {
            return (DataEditDialog<S, T>) getPlatformDialog();
        }
        if (dataUnit instanceof DecorData) {
            return (DataEditDialog<S, T>) getDecorEditor();
        }
        if (dataUnit instanceof FloorData) {
            return (DataEditDialog<S, T>) getFloorDialog();
        }
        if (dataUnit instanceof BlockData) {
            return (DataEditDialog<S, T>) getBlockEditor();
        }
        if (dataUnit instanceof ZoneData) {
            return (DataEditDialog<S, T>) getZoneEditor();
        }
        if (dataUnit instanceof ModuleData) {

            return (DataEditDialog<S, T>) getModuleDialog();
        }
        if (dataUnit instanceof AiData) {
            return (DataEditDialog<S, T>) getAiEditorDialog();
        }
        if (dataUnit instanceof EncounterData) {
            return (DataEditDialog<S, T>) getEncounterEditor();
        }
        if (dataUnit instanceof CellScriptData) {
            return (DataEditDialog<S, T>) getCellDataEditor();
        }
        return null;
    }

    @Override
    protected ToolTipManager createToolTipManager() {
        return new LE_TooltipManager(this);
    }

    public FloorTabs getFloorTabs() {
        return tabs;
    }
}
