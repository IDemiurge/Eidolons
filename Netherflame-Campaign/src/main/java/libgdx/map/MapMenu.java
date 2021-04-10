package libgdx.map;

import eidolons.game.core.GameLoop;
import eidolons.macro.MacroGame;
import libgdx.bf.menu.GameMenu;
import libgdx.screens.menu.MenuItem;

import java.util.List;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapMenu extends GameMenu {

    @Override
    protected List<MenuItem<GAME_MENU_ITEM>> getDefaultItems() {
        return super.getDefaultItems();
    }

    @Override
    protected GameLoop getLoop() {
        return (MacroGame.getGame().getLoop());
    }

    @Override
    protected boolean isHidden(GAME_MENU_ITEM item) {
        switch (item) {
//            case QUICK_HELP:
            case MANUAL:
            case OPTIONS:
            case SAVE:
//            case LOAD:
            case RESUME:
            case EXIT:
                return false;
        }
        return true;
    }
}
