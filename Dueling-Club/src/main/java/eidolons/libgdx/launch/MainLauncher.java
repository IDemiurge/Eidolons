package eidolons.libgdx.launch;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.bf.boss.anim.BossAnimator;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.TestMasterContent;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.io.OutputStream;
import java.util.Stack;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher {
    public static final Stack<Integer> presetNumbers = new Stack<>();
    private static final String LAST_CHOICE_FILE = "xml/last dc.xml";
    private static Stack<String> lastChoiceStack;
    public static boolean presetNumbersOn;

    public static void main(String[] args) {
        CoreEngine.setSwingOn(false);
//        if (!CoreEngine.isIDE())
        CoreEngine.setSafeMode(true);
        CoreEngine.setIggDemo(true);
        CoreEngine.setMainGame(true);
        CoreEngine.setDialogueTest(true);
        EidolonsGame.BRIDGE = true;
//        CoreEngine.setGraphicTestMode(args.length > 0);
//        CoreEngine.setActiveTestMode(args.length > 0);
//        CoreEngine.setReverseExit(args.length > 0);
        if (args.length > 0) {
            PathFinder.init();
            String[] parts = args[0].split(";");
            if (!parts[0].isEmpty()) {
                OptionsMaster.setOptionsMode(parts[0]);
                main.system.auxiliary.log.LogMaster.important(" Options Mode set" + parts[0]);
            }
                EidolonsGame.BOSS_FIGHT = args[0].contains("BOSS");
                EidolonsGame.BRIDGE = args[0].contains("bridge");
                CoreEngine.setLevelTestMode(false);
                 args = args[0].split(";");
        }
        CoreEngine.setSkillTestMode(args.length > 0);
        CoreEngine.setLiteLaunch(args.length > 0);
//        CoreEngine.setContentTestMode(args.length > 2);
        if (!EidolonsGame.BOSS_FIGHT)
            CoreEngine.setLevelTestMode(args.length > 4);

        if (!CoreEngine.isIggDemo()) {
            CoreEngine.setFastMode(args.length > 1);
            CoreEngine.setFullFastMode(args.length > 3);
        }
        BossAnimator.setFastMode(args.length > 5);
        if (CoreEngine.isIDE()) {
            CoreEngine.setJarlike(!CoreEngine.isFastMode());
            if (CoreEngine.isFastMode())//|| CoreEngine.isActiveTestMode())
                TestMasterContent.setAddSpells(true);
            if (CoreEngine.isFullFastMode()) {
                TestMasterContent.setAddAllSpells(true);
                if (CoreEngine.isMe())
                    OptionsMaster.setOptionsPath("C:\\Users\\justm\\AppData\\Local\\Eidolons\\fast options.xml");
            }
        }
//        if (!CoreEngine.isJar())
//                if (CoreEngine.uploadPackage == CoreEngine.UPLOAD_PACKAGE.Aphotica) {
//                    args = "FULL;DEMO;0;0".split(";");
//                }

//        try {
//            ProcessBuilder p = new ProcessBuilder(
////                    PathFinder.getRootPath()
//                    "C:\\transfer\\jars\\launch4j-3.11-win32\\launch4j"
//                    +
//                    "/launch4j.exe");
//            Process as = p.start();
//            OutputStream stream = as.getOutputStream();
//            stream.write("TEST THIS".getBytes());
//            stream.write("TEST THIS".getBytes());
//            stream.flush();
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }
        String[] commands = args;
//        if (commands.length == 1) {
//            if (PathUtils.splitPath(commands[0]).size() > 1)
//                OptionsMaster.setOptionsPath(commands[0]);
//            else
//                CoreEngine.setJarlike(true);
//        }

        for (String command : commands) {
            if (command.contains(MAIN_MENU_ITEM.MAP_PREVIEW.toString())) {
                CoreEngine.setMapPreview(true);
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
                MAIN_MENU_ITEM item = null;
                try {
                    item = MAIN_MENU_ITEM.valueOf(command.toUpperCase());
                } catch (Exception e) {
                }
                if (item != null) {
                    MainMenu.getInstance().getHandler().handle(item);
                } else {
                    if (NumberUtils.isInteger(command)) {
                        int i = NumberUtils.getInteger(command);
                        if (i < 0) {
                            i = getLast();
                        }
                        presetNumbersOn = true;
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
