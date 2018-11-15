package eidolons.libgdx.launch;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.TestMasterContent;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Stack;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher {
    public static final Stack<Integer> presetNumbers = new Stack<>();
    private static final String LAST_CHOICE_FILE = "xml/last dc.xml";
    private static Stack<String> lastChoiceStack;

    public static void main(String[] args) {
        CoreEngine.setSwingOn(false);
        if (args.length > 0) {
            args = args[0].split(",");
        }
        CoreEngine.setFastMode(args.length > 1);
        CoreEngine.setFullFastMode(args.length > 3);
        if (CoreEngine.isIDE())
        {
            CoreEngine.setJarlike(!CoreEngine.isFastMode());
            if (CoreEngine.isFastMode())
                TestMasterContent.setAddSpells(true);
            if (CoreEngine.isFullFastMode()) {
                TestMasterContent.setAddAllSpells(true);
                OptionsMaster.setOptionsPath("C:\\Users\\JustMe\\Eidolons\\fast options.xml");
            }
        }

        String[] commands = args;
        if (commands.length == 1) {
            if (PathUtils.splitPath(commands[0]).size() > 1)
                OptionsMaster.setOptionsPath(commands[0]);
            else
                CoreEngine.setJarlike(true);
        }
        new MainLauncher().start();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        //        if (CoreEngine.isFastMode()) {
        //            CoreEngine.setJar(true);
        //        }
        if (commands.length > 1) {

            for (String command : commands) {
                command = command.trim();
                MAIN_MENU_ITEM item =
                 new EnumMaster<MAIN_MENU_ITEM>().retrieveEnumConst(MAIN_MENU_ITEM.class, command);
                if (item != null)
                    MainMenu.getInstance().getHandler().handle(item);
                else {
                    if (NumberUtils.isInteger(command)) {
                        int i = NumberUtils.getInteger(command);
                        if (i < 0) {
                            i = getLast();
                        }
                        presetNumbers.add(0, i);
                    }

                }
            }
        }
    }

    private static int getLast() {
        if (lastChoiceStack == null) {
            lastChoiceStack = new Stack<>();
            lastChoiceStack.addAll(ContainerUtils.openContainer(
             FileManager.readFile(LAST_CHOICE_FILE)));
        }
        return NumberUtils.getInteger(lastChoiceStack.remove(0));
    }

    @Override
    protected void engineInit() {
        super.engineInit();
        DC_Engine.dataInit();
    }
}
