package tests.macro;

import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.macro.MacroGame;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;
import tests.EidolonsTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 6/10/2018.
 */
public class JUnitMacroInit extends EidolonsTest {

    @Override
    protected boolean isScenario() {
        return true;
    }

    @Test
    public void test() {
        if (isManual())
            manualInit();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_LOOP_STARTED);
        assertTrue(MacroGame.getGame().getWorld() != null);
    }

    protected boolean isManual() {
        return false;
    }


    public void manualInit() {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.MAP));
    }

    @Override
    protected String getLaunchArgString() {
        if (isManual())
            return "";
        return MAIN_MENU_ITEM.MAP_PREVIEW.toString();
    }
}
