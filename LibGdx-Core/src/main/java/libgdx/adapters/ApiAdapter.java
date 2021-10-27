package libgdx.adapters;

import com.badlogic.gdx.InputAdapter;
import eidolons.content.consts.VisualEnums;
import eidolons.system.libgdx.api.GdxApi;
import eidolons.system.libgdx.datasource.ScreenData;
import libgdx.GdxMaster;
import libgdx.bf.menu.GameMenu;
import libgdx.gui.dungeon.panels.headquarters.town.TownPanel;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.screens.handlers.ScreenLoader;
import libgdx.screens.menu.MainMenu;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ApiAdapter implements GdxApi {

    @Override
    public void exited() {
        ScreenMaster.getScreen().reset();
        ScreenLoader.setFirstInitDone(false);
        MainMenu.getInstance().setCurrentItem(null);
        GameMenu.menuOpen = false;
        TownPanel.setActiveInstance(null);
        GdxMaster.setInputProcessor(new InputAdapter());
    }

    @Override
    public void showMainMenu() {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                new ScreenData(VisualEnums.SCREEN_TYPE.MAIN_MENU, "Loading..."));
    }

    @Override
    public void setDefaultCursor() {
        GdxMaster.setDefaultCursor();
    }

    @Override
    public void setTargetingCursor() {
        GdxMaster.setTargetingCursor();
    }
}
