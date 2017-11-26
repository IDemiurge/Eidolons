package main.system.controls;

import com.badlogic.gdx.Input.Keys;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.bf.GridCellContainer;
import main.libgdx.bf.GridUnitView;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.stage.BattleGuiStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.text.SpecialLogger;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

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
            case Keys.ESCAPE:
                escape();
                break;
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
        if (cell.getUnitViews().size() == 1)
            return; // or do something else
        int index = cell.getUnitViews().indexOf(hovered);
        index++;
        if (cell.getUnitViews().size() <= index  )
            index = 0;

        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_OFF, hovered);
        GridUnitView newFocus = cell.getUnitViews().get(index);
        cell.popupUnitView(newFocus);
        GuiEventManager.trigger(GuiEventType.GRID_OBJ_HOVER_ON, newFocus);
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, newFocus.getTooltip()  );


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
