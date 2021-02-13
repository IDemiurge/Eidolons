package libgdx.launch;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.core.launch.CustomLaunch;
import libgdx.screens.menu.MainMenu;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.TestMasterContent;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.system.util.DialogMaster;

import java.util.Stack;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher {
    public static final Stack<Integer> presetNumbers = new Stack<>();
    private static final String LAST_CHOICE_FILE = "xml/last dc.xml";
    public static Integer HERO_INDEX = -1;
    public static String levelPath;
    private static Stack<String> lastChoiceStack;
    public static boolean presetNumbersOn;

    public static void main(String[] args) {
        // EidolonsGame.setVar("non_test", true);
        // EidolonsGame.setVar("tutorial", true);
        // GpuTester.test();
        CoreEngine.setSwingOn(false);
//        if (!CoreEngine.isIDE())
        Flags.setSafeMode(true);
        Flags.setIggDemo(true);
        Flags.setMainGame(true);
        Flags.setDialogueTest(true);

        EngineLauncher engineLauncher = createEngineLauncher();


        if (args.length > 0) {
            PathFinder.init();
            if (args[0].contains("town")) {
                EidolonsGame.TOWN = true;
            }
            CoreEngine.TEST_LAUNCH = args[0].contains("test;");
            System.out.println("TEST LAUNCH =" + CoreEngine.TEST_LAUNCH);

            Flags.setJarlike(args[0].contains("jarlike;"));
            System.out.println("jarlike =" + Flags.isJarlike());

            Flags.setFastMode(CoreEngine.TEST_LAUNCH);

            if (args[0].contains("selecthero")) {
                HERO_INDEX = DialogMaster.inputInt(0);
                if (HERO_INDEX == -1) {
                    EidolonsGame.SELECT_HERO = true;
                }
            }
            String[] parts = args[0].split("--");
            if (parts.length>1) {
                OptionsMaster.setOptionsMode(parts[0]);
                LogMaster.important(" Options Mode set: " + parts[0]);
                args[0] = parts[1];
            }
            EidolonsGame.BOSS_FIGHT = args[0].contains("BOSS");
            EidolonsGame.BRIDGE = args[0].contains("bridge");
            EidolonsGame.FOOTAGE = args[0].contains("footage");
            EidolonsGame.PUZZLES = args[0].contains("puzzle");


            EidolonsGame.DUEL = args[0].contains("duel");
            EidolonsGame.DUEL_TEST = args[0].contains("duel");

            if (EidolonsGame.DUEL_TEST) {
                EidolonsGame.BRIDGE = true;
            }
            Flags.setLevelTestMode(false);
            args = args[0].split(";");
        }
        Flags.setSkillTestMode(args.length > 0);
//        CoreEngine.setLiteLaunch(args.length > 0);
//        CoreEngine.setContentTestMode(args.length > 2);
        if (!EidolonsGame.BOSS_FIGHT)
            Flags.setLevelTestMode(args.length > 4);

        if (!Flags.isIggDemo()) {
            Flags.setFastMode(args.length > 1);
            Flags.setFullFastMode(args.length > 3);
        }
        if (Flags.isIDE()) {
            // Flags.setJarlike(!Flags.isFastMode());
            if (Flags.isFastMode())//|| CoreEngine.isActiveTestMode())
                TestMasterContent.setAddSpells(true);
            if (Flags.isFullFastMode()) {
                TestMasterContent.setAddAllSpells(true);
                if (Flags.isMe())
                    OptionsMaster.setOptionsPath("C:\\Users\\justm\\AppData\\Local\\Eidolons\\fast options.xml");
            }
        }
//        if (!CoreEngine.isJar())
//                if (CoreEngine.uploadPackage == CoreEngine.UPLOAD_PACKAGE.Aphotic) {
//                    args = "FULL;DEMO;0;0".split(";");
//                }
        String[] commands = args;
//        if (commands.length == 1) {
//            if (PathUtils.splitPath(commands[0]).size() > 1)
//                OptionsMaster.setOptionsPath(commands[0]);
//            else
//                CoreEngine.setJarlike(true);
//        }

        for (String command : commands) {
            if (command.contains(MainMenu.MAIN_MENU_ITEM.MAP_PREVIEW.toString())) {
                Flags.setMapPreview(true);
            }
        }

        new MainLauncher().start();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        //        if (CoreEngine.isFastMode()) {
        //            CoreEngine.setJar(true);
        //        }
        if (commands.length > 1) {

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

                                engineLauncher.setCustomLaunch(new CustomLaunch(replace));
                            }
                        }
                    }
                }
            }
        }
    }

    private static EngineLauncher createEngineLauncher() {
        return new EngineLauncher();
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
