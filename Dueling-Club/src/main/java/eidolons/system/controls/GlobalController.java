package eidolons.system.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.GuiStage;
import eidolons.system.options.OptionsMaster;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
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
            case Keys.F1:
                if (Eidolons.getGame().isBossFight()) {
                    EUtils.showInfoText("There is no escape...");
                    return false;
                }
                HqMaster.toggleHqPanel();
                return true;
            case Keys.F4:
                if (Eidolons.getScope() != SCOPE.MENU) {
                    Eidolons.exitToMenu();
                    return true;
                }
                return false;
            case Keys.ESCAPE:
                return escape();

            case Keys.SPACE:
                return space();

            case Keys.TAB:
                try {
                    return tab();
                } catch (Exception e) {
                    ExceptionMaster.printStackTrace(e);
                }
                return false;
            case Keys.ENTER:
                return enter();

        }
        return false;
    }

    private boolean doTest(int keyCode) {
        switch (keyCode) {
            case Keys.F2:
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
            //            case Keys.F4: already implemented?
            //                Eidolons.exitToMenu();
            //                break;
        }
        return false;
    }

    private boolean space() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActorMaster.click(activeButton);
            activeButton = null;
            return true;
        }
        return false;
    }

    private boolean enter() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActorMaster.click(activeButton);
            activeButton = null;
            return true;
        }

        if (TownPanel.getActiveInstance() != null) {
            TownPanel.getActiveInstance().done();
        }
        if (Eidolons.getScope() == SCOPE.MENU) {
            MainMenu.getInstance().getHandler().handle(MAIN_MENU_ITEM.PLAY);
            return true;
        }
        return false;
    }

    static SmartButton activeButton;

    public static void setActiveButton(SmartButton activeButton) {
        GlobalController.activeButton = activeButton;
    }

    private boolean tab() {
        GridUnitView hovered = DungeonScreen.getInstance().getGridPanel().getHoverObj();
        GridCellContainer cell = (GridCellContainer) hovered.getParent();

        List<GenericGridView> list = new ArrayList<>(cell.getUnitViewsVisible());
        if (list.size() == 1)
            return false;
        SortMaster.sortByExpression(list, view -> view.hashCode());
        int index = list.indexOf(hovered);
        index++;
        if (list.size() <= index)
            index = 0;
        int finalIndex = index;
        Eidolons.onNonGdxThread(() -> {
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, hovered);
            GenericGridView newFocus = list.get(finalIndex);
            WaitMaster.WAIT(100);
            cell.popupUnitView(newFocus);
            WaitMaster.WAIT(100);
            GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, newFocus);
            WaitMaster.WAIT(100);
            GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, newFocus.getTooltip());
        });


        return true;
    }

    private boolean escape() {
        if (activeButton != null) {
            if (!activeButton.isChecked())
                ActorMaster.click(activeButton);
            activeButton = null;
            return true;
        }
        if (DC_Game.game.getManager().isSelecting()
            //         DungeonScreen.getInstance().getGridPanel().isSelecting()
        ) {
            DungeonScreen.getInstance().getGridPanel().clearSelection();
            return true;
        }
        GuiStage guiStage = Eidolons.getScreen().getGuiStage();
        if (guiStage.getDraggedEntity() != null) {
            guiStage.setDraggedEntity(null);
            return true;
        }
        if (guiStage.closeDisplayed())
            return true;

        guiStage.getTooltips().getStackMaster().stackOff();

        guiStage.getGameMenu().open();
        return true;
    }

    @Override
    public boolean charTyped(char c) {
        if (active) {
            active = true;
            return true;
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
            case ' ':
                if (DungeonScreen.getInstance() == null)
                    return false;
                if (DungeonScreen.getInstance().isWaitingForInput())
                    return true;
                if (Eidolons.getScreen().getGuiStage().getDisplayedClosable()
                        instanceof Blocking)
                    return false;
                Eidolons.game.getLoop().togglePaused();
                //                Eidolons.game.getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.PAUSE);
                return true;
            case 'D':
                Eidolons.game.getDebugMaster().showDebugWindow();
                return true;
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
