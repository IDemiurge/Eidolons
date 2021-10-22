package libgdx.controls;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.Core.APPLICATION_SCOPE;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.story.cinematic.CinematicLib;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.game.exploration.handlers.ExplorationMaster;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.grid.cell.GenericGridView;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.gui.generic.btn.SmartButton;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.menu.OptionsWindow;
import libgdx.gui.overlay.choice.VC_DataSource;
import libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import libgdx.gui.panels.headquarters.HqMaster;
import libgdx.gui.panels.headquarters.town.TownPanel;
import libgdx.screens.AtlasGenSpriteBatch;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.screens.menu.MainMenu;
import libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import libgdx.stage.Blocking;
import libgdx.stage.ConfirmationPanel;
import libgdx.stage.GuiStage;
import libgdx.texture.TextureCache;
import libgdx.utils.GdxDialogMaster;
import eidolons.system.test.TestDialogMaster;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/2/2017.
 */
public class GlobalController implements Controller {
    private static final boolean TEST_MODE = true;
    private static boolean controlPause;
    private boolean active;
    private String lastScript;

    public static void setControlPause(boolean controlPause) {
        GlobalController.controlPause = controlPause;
    }

    public static boolean getControlPause() {
        return controlPause;
    }

    public static void cellClicked(InputEvent event, float x, float y) {
        if (controlPause) {
            //            if (Gdx.input.isCatchMenuKey()) TODO how to disable on drag?
            Core.getGame().getLoop().setPaused(false);
            setControlPause(false);
        }
    }
    /*
        toggle dummy?

         */

    @Override
    public boolean keyDown(int keyCode) {

        if (controlPause) {
            Core.getGame().getLoop().setPaused(false);
            setControlPause(false);
            return true;
        }
        if (TEST_MODE)
            if (Flags.isIDE()) {
                try {
                    if (doTest(keyCode))
                        return false;
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
            }

        switch (keyCode) {
            case Keys.ESCAPE:
                return escape();

            case Keys.ENTER:
                return enter();

            case Keys.SPACE:
                return space();

        }
        if (isDisabled())
            return false;

        switch (keyCode) {
            case Keys.F11:
                boolean bool = !EidolonsGame.getVar("tutorial");
                EidolonsGame.setVar("tutorial", bool);
                EUtils.showInfoText("... Tutorial " + (bool ? "on" : "off"));
                break;
            case Keys.F2:
                return true;
            case Keys.F1:
                if (!Flags.isIDE())
                    if (!EidolonsGame.isHqEnabled()) {
                        return false;
                    }
                if (Core.getGame().isBossFight()) {
                    EUtils.showInfoText("There is no time...");
                    return false;
                }
                HqMaster.toggleHqPanel();
                return true;
            case Keys.F4:
                if (Flags.isIDE())
                    if (Core.getScope() != APPLICATION_SCOPE.MENU) {
                        Core.exitToMenu();
                        return true;
                    }
                return false;


            case Keys.TAB:
                try {
                    return tab();
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
                return false;

        }
        return false;
    }

    private boolean isDisabled() {
        return DialogueManager.isRunning();
    }

    private boolean doTest(int keyCode) {
        switch (keyCode) {


            case Keys.PAGE_UP:
                ScreenMaster.getDungeonGrid().
                        resetMaps();
                break;

            case Keys.END:
                TextureCache.getInstance().logDiagnostics();
                break;
            case Keys.PAGE_DOWN:
                TextureCache.getInstance().clearCache();
                break;
            case Keys.HOME:
                if (GdxMaster.getMainBatch() instanceof AtlasGenSpriteBatch) {
                    Core.onGdxThread(() -> {
                        try {
                            ((AtlasGenSpriteBatch) GdxMaster.getMainBatch()).writeAtlases();
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    });
                }
                break;
            case Keys.ENTER:
                GuiEventManager.trigger(GuiEventType.DISPOSE_SCOPE, "DIALOGUE");
                break;
            case Keys.F9:
                Core.onNonGdxThread(() -> {
                    String text = GdxDialogMaster.inputText("Your script...", lastScript);
                    if (!text.contains("=")) {
                        text = "script=" + text;
                    }
                    if (!StringMaster.isEmpty(text)) {
                        lastScript = text;
                        doScript(text);
                    }
                });
                return true;
            case Keys.F1:
                if (CinematicLib.TEST_ON) {
                    CinematicLib.doTest(1);
                    return true;
                }
                break;
            case Keys.F2:
                if (CinematicLib.TEST_ON) {
                    CinematicLib.doTest(2);
                    return true;
                }
                break;
            case Keys.F3:
                if (CinematicLib.TEST_ON) {
                    CinematicLib.doTest(3);
                    return true;
                }
                return true;
            case Keys.F4:
                if (CinematicLib.TEST_ON) {
                    CinematicLib.doTest(4);
                    return true;
                }
                Core.getMainHero().kill(Core.getMainHero(), true, true);
                //                WeaveMaster.openWeave();
                return true;
            case Keys.F5:
                Core.getMainHero().setParam(PARAMS.C_TOUGHNESS, 1, true);
                return true;
            case Keys.F6:
                // new Thread(() -> IntroLauncher.introBriefing(), " thread").start();
                GuiEventManager.trigger(GuiEventType.VISUAL_CHOICE,
                        new VC_DataSource(VC_DataSource.VC_TYPE.death));
                Core.onNonGdxThread(() -> {
                    Object o = WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.VISUAL_CHOICE);
                    GuiEventManager.trigger(GuiEventType.VISUAL_CHOICE,
                            new VC_DataSource(VC_DataSource.VC_TYPE.hero_choice, o));
                });
                return true;
            case Keys.F7:
                GuiEventManager.trigger(GuiEventType.FLIGHT_END);
                return true;
            case Keys.F8:
                GuiEventManager.trigger(GuiEventType.FLIGHT_START,
                        VisualEnums.FLIGHT_ENVIRON.voidmaze.data +
                                "angle:120;");
                return true;
        }
        return false;
    }

    private void doScript(String text) {
        try {
            Core.getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(text);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private boolean space() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMasterGdx.click(activeButton.getActor());
            activeButton = null;
            return true;
        }
        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().ok();
            return true;
        }
        if (ScreenMaster.getScreen().getController() != null)
            if (ScreenMaster.getScreen().getController().space()) {
                main.system.auxiliary.log.LogMaster.devLog("  *******SPACE CONSUMED ");
                return true;
            }
        if (Cinematics.ON) {
            return false;
        }
        if (DungeonScreen.getInstance().isBlocked())
            return false;
        if (DungeonScreen.getInstance() == null)
            return false;
        if (DungeonScreen.getInstance().isWaitingForInputNow())
            return true;
        if (ScreenMaster.getScreen().getGuiStage().getDisplayedClosable()
                instanceof Blocking)
            return false;
        Core.game.getLoop().togglePaused();
        return true;
    }

    private boolean enter() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMasterGdx.click(activeButton.getActor());
            activeButton = null;
            return true;
        }

        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().ok();
            return true;
        }
        if (TownPanel.getActiveInstance() != null) {
            TownPanel.getActiveInstance().done();
        }
        if (Core.getScope() == APPLICATION_SCOPE.MENU) {
            MainMenu.getInstance().getHandler().handle(MAIN_MENU_ITEM.PLAY);
            return true;
        }
        try {
            if (ScreenMaster.getScreen().getController().enter())
                return true;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    static SmartButton activeButton;

    public static void setActiveButton(SmartTextButton activeButton) {
        GlobalController.activeButton = activeButton;
    }

    private boolean tab() {
        List<GenericGridView> list = new ArrayList<>();
        UnitGridView hovered = ScreenMaster.getGrid().getHoverObj();
        GridCellContainer cell = null;
        if (hovered != null) {
            cell = (GridCellContainer) hovered.getParent();
            list.addAll(cell.getUnitViewsVisible());
        }
        if (list.size() <= 1) {
            if (!CoreEngine.isLevelEditor())
                GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Core.getMainHero());
            if (ScreenMaster.getScreen().getController().inputPass()) {

            }
            return true;
        }
        SortMaster.sortByExpression(list, Object::hashCode);
        int index = list.indexOf(hovered);
        index++;
        if (list.size() <= index)
            index = 0;
        int finalIndex = index;
        GridCellContainer finalCell = cell;
        Core.onNonGdxThread(() -> {
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, hovered);
            GenericGridView newFocus = list.get(finalIndex);
            WaitMaster.WAIT(100);
            finalCell.popupUnitView(newFocus);
            WaitMaster.WAIT(100);
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, newFocus);
            WaitMaster.WAIT(100);
            GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, newFocus.getTooltip());
        });


        return true;
    }

    private boolean escape() {
        //        if (!CoreEngine.isIDE())

        if (Cinematics.ON) {
            if (DungeonScreen.getInstance().getGuiStage().isDialogueMode()) {
                doScript("skip=:;");
                //                    DungeonScreen.getInstance().getGuiStage().dialogueDone();
                FileLogManager.streamMain("Dialogue escaped");
                return true;
            }
            return false;
        }

        DungeonScreen.getInstance().cameraStop(true);

        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMasterGdx.click(activeButton.getActor());
            activeButton = null;
            return true;
        }

        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().cancel();
            return true;
        }

        if (DC_Game.game.getManager().isSelecting()
            //         ScreenMaster.getDungeonGrid().isSelecting()
        ) {
            ScreenMaster.getGrid().clearSelection();
            return true;
        }
        if (ScreenMaster.getScreen().getGuiStage() instanceof GuiStage) {
            GuiStage guiStage = (GuiStage) ScreenMaster.getScreen().getGuiStage();
            if (guiStage.getDraggedEntity() != null) {
                guiStage.setDraggedEntity(null);
                return true;
            }
            if (OptionsWindow.getInstance().isVisible()) {
                OptionsWindow.getInstance().forceClose();
                return true;
            }
            if (guiStage.closeDisplayed())
                return true;
            if (ScreenMaster.getScreen().getController().escape())
                return true;

            //        if (){
            GuiEventManager.trigger(GuiEventType.CLEAR_COMMENTS);
            guiStage.getTooltips().getStackMaster().stackOff();
            guiStage.getGameMenu().open();
            ScreenMaster.getScreen().getController().resetZoom();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char c) {
        if (active) {
            active = true;
            return true;
        }
        if (Flags.isContentTestMode()) {
            if (TestDialogMaster.key(c))
                return false;
        }
        if (DungeonScreen.getInstance().isBlocked())
            return true;
        switch (c) {
            case 'i':
            case 'I':
                if (ExplorationMaster.isExplorationOn()) {
                    GuiEventManager.trigger(GuiEventType.TOGGLE_INVENTORY,
                            new InventoryDataSource(DC_Game.game.getLoop().getActiveUnit()));
                }
                break;
            //            case 'D':
            //                return true;
            case 'W': //TODO custom hotkeys
                Core.game.getDungeonMaster().getExplorationMaster().getTimeMaster()
                        .playerWaits();
                return true;
            case 'O': {
                //TODO gdx fix
                // OptionsWindow.getInstance().open(OptionsMaster.getInstance().getOptionsMap(), stage);
                break;
            }
            case 'S': {
                //                if (!Gdx.input.isKeyPressed(Keys.ALT_LEFT))
                //                    break;
                SpecialLogger.getInstance().writeLogs();
                break;
            }
        }

        return false;
    }
}
