package main.libgdx.launch;

import main.game.battlecraft.DC_Engine;
import main.libgdx.screens.menu.MainMenu;
import main.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher {
    public static void main(String[] args) {
        new MainLauncher().start();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        if (args.length > 0) {
            String[] commands = args[0].split(",");
            if (commands.length == 1) {
                CoreEngine.setJarlike(true);
                return;
            }
            CoreEngine.setFastMode(true);
            for (String command : commands) {
                MAIN_MENU_ITEM item =
                 new EnumMaster<MAIN_MENU_ITEM>().retrieveEnumConst(MAIN_MENU_ITEM.class, command);

                if (item != null)
                    MainMenu.getInstance().getHandler().handle(item);
            }
        }
    }

    @Override
    protected void engineInit() {
        super.engineInit();
        DC_Engine.dataInit();
    }
}
