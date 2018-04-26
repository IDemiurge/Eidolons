package eidolons.system.controls;

import com.badlogic.gdx.Input.Keys;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.bf.grid.GenericGridView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.stage.BattleGuiStage;
import eidolons.libgdx.stage.Blocking;
import eidolons.system.options.OptionsMaster;
import eidolons.test.debug.DebugMaster.DEBUG_FUNCTIONS;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.log.SpecialLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/2/2017.
 */
public class GlobalController implements Controller {
    private boolean active;

    /*
        toggle dummy?

         */

    @Override
    public void keyDown(int keyCode) {
        switch (keyCode) {
            case Keys.F1:
                HqMaster.toggleHqPanel();
                break;
            case Keys.ESCAPE:
                escape();
                break;
            case ' ':
                if (Eidolons.getScreen().getGuiStage().getDisplayedClosable()
                 instanceof Blocking)
                    return;
                Eidolons.game.getLoop().togglePaused();
                break;
//                GuiEventManager.trigger(GuiEventType.GAME_PAUSE_TOGGLE);
            case Keys.TAB:
                try {
                    tab();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                break;
        }
    }

    private void tab() {
        GridUnitView hovered = DungeonScreen.getInstance().getGridPanel().getHoverObj();
        GridCellContainer cell = (GridCellContainer) hovered.getParent();

        List<GenericGridView> list = new ArrayList<>(cell.getUnitViewsVisible());
        if (list.size() == 1)
            return; // or do something else
        SortMaster.sortByExpression(list, view -> view.hashCode());
        int index = list.indexOf(hovered);
        index++;
        if (list.size() <= index)
            index = 0;

        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, hovered);
        GenericGridView newFocus = list.get(index);
        cell.popupUnitView(newFocus);
        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, newFocus);
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, newFocus.getTooltip());


    }

    private void escape() {
        BattleGuiStage gui = DungeonScreen.getInstance().getGuiStage();
        if (gui.closeDisplayed())
            return;
        gui.getGameMenu().open();
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
                    GuiEventManager.trigger(GuiEventType.SHOW_INVENTORY,
                     new InventoryDataSource(DC_Game.game.getLoop().getActiveUnit()));
                }
                break;
            case ' ':
                if (DungeonScreen.getInstance() == null)
                    return false;
                if (!DungeonScreen.getInstance().isWaitingForInput())
                    Eidolons.game.getDebugMaster().executeDebugFunction(DEBUG_FUNCTIONS.PAUSE);
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
