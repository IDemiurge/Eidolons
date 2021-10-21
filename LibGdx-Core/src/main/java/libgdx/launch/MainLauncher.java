package libgdx.launch;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.core.launch.TestLaunch;
import libgdx.screens.menu.MainMenu;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.launch.Launch;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Stack;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher {
    public static final Stack<Integer> presetNumbers = new Stack<>();
    private static final String LAST_CHOICE_FILE = "xml/last dc.xml";

    public static Integer HERO_INDEX = -1;
    public static String levelPath;
    public static boolean presetNumbersOn;
    private static Stack<String> lastChoiceStack;
    private static EngineLauncher engineLauncher;

    public static void main(String[] args) {
        LaunchFlags    .initFlags();
        Launch.START(Launch.LaunchPhase._1_dc_setup);
        engineLauncher = createEngineLauncher();
        args=LaunchFlags.processArgs(args, engineLauncher);

        Launch.END(Launch.LaunchPhase._1_dc_setup);

        new MainLauncher().start();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        Launch.END(Launch.LaunchPhase._7_menu_show);
        String[] commands = args;
        if (commands.length > 1) {
            processCommands(commands);
        }
    }

    private static void processCommands(String[] commands) {

        for (String command : commands) {
            command = command.trim();
            MainMenu.MAIN_MENU_ITEM item = null;
            try {
                item = MainMenu.MAIN_MENU_ITEM.valueOf(command.toUpperCase());
            } catch (Exception e) {
            }
            if (item != null) {
                MainMenu.getInstance().getHandler().handle(item);
            } else {
                if (NumberUtils.isInteger(command)) {
                    int i = NumberUtils.getIntParse(command);
                    if (i < 0) {
                        i = getLast();
                    }
                    presetNumbersOn = true;
                    presetNumbers.add(0, i);
                } else {
                    String[] p = command.split("=");
                    if (p.length > 1) {
                        EidolonsGame.setVar(p[0], Boolean.valueOf(p[1]));
                    } else {
                        if (command.contains(".") || command.contains("::")) {
                            String replace = command.replace("_", " ");
                            levelPath=replace;
                            engineLauncher.setTestLaunch(new TestLaunch(replace));
                        }
                    }
                }
            }
        }

    }


    private static EngineLauncher createEngineLauncher() {
        return EngineLauncher.getInstance();
    }

    private static int getLast() {
        if (lastChoiceStack == null) {
            lastChoiceStack = new Stack<>();
            lastChoiceStack.addAll(ContainerUtils.openContainer(
                    FileManager.readFile(LAST_CHOICE_FILE)));
        }
        return NumberUtils.getIntParse(lastChoiceStack.remove(0));
    }



    @Override
    public String getOptionsPath() {
        return null;
    }

}
