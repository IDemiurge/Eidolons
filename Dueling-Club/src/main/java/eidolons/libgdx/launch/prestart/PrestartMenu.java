package eidolons.libgdx.launch.prestart;

import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.bf.menu.GameMenuHandler;
import eidolons.libgdx.screens.menu.MenuItem;

import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class PrestartMenu extends GameMenu {

    public PrestartMenu() {
        super();
        setVisible(true);
    }

    @Override
    protected List<MenuItem<GAME_MENU_ITEM>> getDefaultItems() {
        return super.getDefaultItems();
    }

    @Override
    public void close() {
        setCurrentItem(null);
        setPreviousItem(null);
        setVisible(false);
    }

    @Override
    protected GameMenuHandler initHandler() {
        return new GameMenuHandler() {
            @Override
            public Boolean clicked(GAME_MENU_ITEM sub) {
                switch (sub) {
                    case LAUNCH_GAME:
                        PreLauncher.gameLaunched();

                        break;
                }
                return super.clicked(sub);
            }
        };
    }

    @Override
    protected boolean isHidden(GAME_MENU_ITEM item) {
        switch (item) {
            case WEBSITE:
            case ABOUT:
            case LAUNCH_GAME:
                return false;
            case INFO:
            case RESTART:
            case RESUME:
            case PASS_TIME:
                return true;
        }
        return super.isHidden(item);
    }

}
