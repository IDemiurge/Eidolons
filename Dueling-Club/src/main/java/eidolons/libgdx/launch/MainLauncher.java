package eidolons.libgdx.launch;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
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
    private static final String LAST_CHOICE_FILE ="xml\\last dc.xml" ;
    private static Stack<String> lastChoiceStack;

    public static void main(String[] args) {
        if (args.length > 0) {
            args =args[0].split(",");
        }
        CoreEngine.setFastMode(args.length > 1);

        new MainLauncher().start();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
//        if (CoreEngine.isFastMode()) {
//            CoreEngine.setJar(true);
//        }
        if (args.length > 0) {
            String[] commands = args;
            if (commands.length == 1) {
                CoreEngine.setJarlike(true);
                return;
            }
            for (String command : commands) {
                command = command.trim();
                MAIN_MENU_ITEM item =
                 new EnumMaster<MAIN_MENU_ITEM>().retrieveEnumConst(MAIN_MENU_ITEM.class, command);
                if (item != null)
                    MainMenu.getInstance().getHandler().handle(item);
                else {
                    if (StringMaster.isInteger(command)) {
                    int i =StringMaster.getInteger(command);
                        if (i<0){
                            i =getLast();
                        }
                        presetNumbers.add(0, i);
                    }

                }
            }
        }
    }

    private static int getLast() {
if (lastChoiceStack == null ){
    lastChoiceStack = new Stack<>();
    lastChoiceStack.addAll(StringMaster.openContainer(
     FileManager.readFile(LAST_CHOICE_FILE)));
}
        return StringMaster.getInteger(lastChoiceStack.remove(0));
    }

    @Override
    protected void engineInit() {
        super.engineInit();
        DC_Engine.dataInit();
    }
}
