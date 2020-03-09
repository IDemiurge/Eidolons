package eidolons.system.controls;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Launcher;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.LordPanel;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.libgdx.stage.GuiStage;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import eidolons.system.test.TestDialogMaster;
import main.content.enums.GenericEnums;
import main.entity.Ref;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
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
            Eidolons.getGame().getLoop().setPaused(false);
            setControlPause(false);
        }
    }
    /*
        toggle dummy?

         */

    @Override
    public boolean keyDown(int keyCode) {

        if (controlPause) {
            Eidolons.getGame().getLoop().setPaused(false);
            setControlPause(false);
            return true;
        }
        if (TEST_MODE)
            if (CoreEngine.isIDE()) {
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
                if (CoreEngine.isIDE()) {
                    EidolonLord.lord.soulforceLost(50);
                    return false;
                }
                if (CoreEngine.isIDE())
                    LordPanel.getInstance().init();
                GuiEventManager.trigger(GuiEventType.TOGGLE_LORD_PANEL);
                return true;
            case Keys.F1:
                if (!CoreEngine.isIDE())
                    if (!EidolonsGame.isHqEnabled()) {
                        return false;
                    }
                if (ShadowMaster.isShadowAlive()) {
                    EUtils.showInfoText("Cannot do this now");
                    return false;
                }
                if (Eidolons.getGame().isBossFight()) {
                    EUtils.showInfoText("There is no time...");
                    return false;
                }
                HqMaster.toggleHqPanel();
                return true;
            case Keys.F4:
                if (CoreEngine.isIDE())
                    if (Eidolons.getScope() != SCOPE.MENU) {
                        Eidolons.exitToMenu();
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
            case Keys.ENTER:
                GuiEventManager.trigger(GuiEventType.DISPOSE_SCOPE, "DIALOGUE");
                break;
            case Keys.END:
//                GuiEventManager.trigger(GuiEventType.LOG_DIAGNOSTICS);
                break;
            case Keys.F9:
                Eidolons.onNonGdxThread(() -> {
                    String text = DialogMaster.inputText("Your script...", lastScript);
                    if (!text.contains("=")) {
                        text = "script=" + text;
                    }
                    if (!StringMaster.isEmpty(text)) {
                        lastScript = text;
                        doScript(text);
                    }
                });
                return true;
            case Keys.F8:
                if (EidolonsGame.DUEL) {
                    return false;
                }
                if (CoreEngine.isIDE()) {
                    EidolonLord.lord.soulforceGained(110);
                }
                PaleAspect.togglePale();
                return true;
            case Keys.F6:
                if (EidolonsGame.DUEL) {
                    return false;
                }
                new Thread(() -> IGG_Launcher.introBriefing(), " thread").start();

                return true;
            case Keys.F7:
                if (EidolonsGame.DUEL) {
                    return false;
                }
                DC_Game.game.getMetaMaster().getDialogueManager().test();

                return true;
            case Keys.F3:
                new Thread(() -> {
                    DC_Game.game.getMetaMaster().getDefeatHandler().isEnded(true, true);
                }, " thread").start();

                return true;
            case Keys.F4:
                Eidolons.getMainHero().kill(Eidolons.getMainHero(), true, true);
//                WeaveMaster.openWeave();
                return true;
            case Keys.F5:
                new DealDamageEffect(new Formula("1000"), GenericEnums.DAMAGE_TYPE.PURE)
                        .apply(Ref.getSelfTargetingRefCopy(Eidolons.getMainHero()));

//                Eidolons.getMainHero().setParam(PARAMS.C_TOUGHNESS, 0);
                Eidolons.getMainHero().getGame().getManager().reset();
//                WeaveMaster.openWeave();
                return true;
            //            case Keys.F4: already implemented?
            //                Eidolons.exitToMenu();
            //                break;
        }
        return false;
    }

    private void doScript(String text) {
        try {
            Eidolons.getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(text);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private boolean space() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMaster.click(activeButton);
            activeButton = null;
            return true;
        }
        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().ok();
            return true;
        }
        if (ScreenMaster.getScreen().getController() != null)
            if (ScreenMaster.getScreen().getController().space()) {
                main.system.auxiliary.log.LogMaster.dev("  *******SPACE CONSUMED ");
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
        Eidolons.game.getLoop().togglePaused();
        return true;
    }

    private boolean enter() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActionMaster.click(activeButton);
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
        if (Eidolons.getScope() == SCOPE.MENU) {
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

    public static void setActiveButton(SmartButton activeButton) {
        GlobalController.activeButton = activeButton;
    }

    private boolean tab() {
        List<GenericGridView> list = new ArrayList<>();
        GridUnitView hovered = DungeonScreen.getInstance().getGridPanel().getHoverObj();
        GridCellContainer cell = null;
        if (hovered != null) {
            cell = (GridCellContainer) hovered.getParent();
            list.addAll(cell.getUnitViewsVisible());
        }
        if (list.size() <= 1) {
            GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero());
            if (ScreenMaster.getScreen().getController().inputPass()) {

            }
            return true;
        }
        SortMaster.sortByExpression(list, view -> view.hashCode());
        int index = list.indexOf(hovered);
        index++;
        if (list.size() <= index)
            index = 0;
        int finalIndex = index;
        GridCellContainer finalCell = cell;
        Eidolons.onNonGdxThread(() -> {
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
                ActionMaster.click(activeButton);
            activeButton = null;
            return true;
        }

        if (ConfirmationPanel.getInstance().isVisible()) {
            ConfirmationPanel.getInstance().cancel();
            return true;
        }

        if (DC_Game.game.getManager().isSelecting()
            //         DungeonScreen.getInstance().getGridPanel().isSelecting()
        ) {
            DungeonScreen.getInstance().getGridPanel().clearSelection();
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
        if (CoreEngine.isContentTestMode()) {
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
                Eidolons.game.getDungeonMaster().getExplorationMaster().getTimeMaster()
                        .playerWaits();
                return true;
            case 'O': {
                OptionsMaster.openMenu();
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
