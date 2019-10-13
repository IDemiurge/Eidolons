package eidolons.libgdx.launch;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.igg.CustomLaunch;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.libgdx.bf.boss.anim.BossAnimator;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.libgdx.texture.Sprites;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.TestMasterContent;
import main.data.filesys.PathFinder;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Stack;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher {
    public static final Stack<Integer> presetNumbers = new Stack<>();
    public static   String BG ;
    private static final String LAST_CHOICE_FILE = "xml/last dc.xml";
   public static  Integer HERO_INDEX =-1;
    private static final String FOOTAGE_SEQUENCE =
            "ready/ship.xml;" +
                    "ready/graveyard.xml;" +
            "ready/wood.xml;" +
                    "crawl/cavern.xml;" +
                    "crawl/Guild Dungeon.xml;" +
                    "crawl/hell.xml;" +
                    "crawl/Underdark.xml;" +
            "ready/dark castle.xml;" +
            "ready/the fortress.xml;" +
                    "crawl/Ancient Ruins.xml;" +
                    "crawl/Ravenguard Dungeon.xml;" +
                    "crawl/Dwarven Halls.xml;" +
                    "ready/ravenguard sanctum.xml;" +


                    "ready/spire.xml;" +
                    "ready/mix.xml;" +
            "footage/demon circle.xml;" +
            "footage/serpentium.xml;" +
            "levels/underworld.xml;" +
            "modules/bastion.xml;" +

                    "levels/vault.xml;" +
                    "modules/black river.xml;" +
                    "modules/reaver.xml;" +

                    "demo/underreach.xml;" +
                    "crawl/guild dungeon.xml;" +
                    "demo/Outer Cloister.xml;" +

                    "crawl/cavern.xml;" +
                    "crawl/cavern.xml;" +

                    "levels/vault.xml;" +
                    "levels/vault.xml;" +
                    "levels/vault.xml;";
    private static Stack<String> lastChoiceStack;
    public static boolean presetNumbersOn;
    private static CustomLaunch customLaunch;

    public static void main(String[] args) {
        EidolonsGame.setVar("non_test", true);
        EidolonsGame.setVar("tutorial", true);
        GpuTester.test();
        CoreEngine.setSwingOn(false);
//        if (!CoreEngine.isIDE())
        CoreEngine.setSafeMode(true);
        CoreEngine.setIggDemo(true);
        CoreEngine.setMainGame(true);
        CoreEngine.setDialogueTest(true);
//        EidolonsGame.BRIDGE = true;
//        CoreEngine.setGraphicTestMode(args.length > 0);
//        CoreEngine.setActiveTestMode(args.length > 0);
//        CoreEngine.setReverseExit(args.length > 0);
        if (args.length > 0) {
            PathFinder.init();
            if (args[0].contains("town")) {
                EidolonsGame.TOWN=true;
            }

                if (args[0].contains("selectfootage")) {
                    CoreEngine.swingOn = true;
                    CoreEngine.systemInit();
//                int i = DialogMaster.inputInt(0);
                    String level = ListChooser.chooseString(ContainerUtils.openContainer(FOOTAGE_SEQUENCE));
                    args[0] = args[0] + ";" + level;

//                int i = DialogMaster.optionChoice("", FOOTAGE_SEQUENCE.split(";"));
//                if (i==-1) {
//                    EidolonsGame.SELECT_SCENARIO=true;
//                } else
//                    args[0] = args[0] + "." + i;

            }
            if (args[0].contains("selecthero")) {
                HERO_INDEX = DialogMaster.inputInt(0);
                if (HERO_INDEX==-1) {
                    EidolonsGame.SELECT_HERO=true;
                }
            }
            String[] parts = args[0].split(";");
            if (!parts[0].isEmpty()) {
                OptionsMaster.setOptionsMode(parts[0]);
                LogMaster.important(" Options Mode set: " + parts[0]);
            }
            EidolonsGame.BOSS_FIGHT = args[0].contains("BOSS");
            EidolonsGame.BRIDGE = args[0].contains("bridge");
            EidolonsGame.FOOTAGE = args[0].contains("footage");
            EidolonsGame.PUZZLES = args[0].contains("puzzle");


            EidolonsGame.DUEL = args[0].contains("duel");
            EidolonsGame.DUEL_TEST = args[0].contains("duel");
            EidolonsGame.TRANSIT_TEST = args[0].contains("transit");

            if (EidolonsGame.DUEL_TEST) {
                EidolonsGame.BRIDGE = true;
            }
            CoreEngine.setLevelTestMode(false);
            args = args[0].split(";");
        }
        CoreEngine.setSkillTestMode(args.length > 0);
//        CoreEngine.setLiteLaunch(args.length > 0);
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
//                if (CoreEngine.uploadPackage == CoreEngine.UPLOAD_PACKAGE.Aphotic) {
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
                    } else {

                        String[] p = command.split("=");
                        if (p.length > 1) {
                            EidolonsGame.setVar(p[0], Boolean.valueOf(p[1]));
                        } else {
                            if (command.contains(".") || command.contains("::")) {
                                if (command.length() < 3) {
                                    command = FOOTAGE_SEQUENCE.split(";")
                                            [Integer.valueOf(command.replace(".", ""))];
                                }
                                setCustomLaunch(new CustomLaunch(command.replace("_", " ")));
                            }
                        }
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

    public static CustomLaunch getCustomLaunch() {
        return customLaunch;
    }

    public static void setCustomLaunch(CustomLaunch customLaunch) {
        main.system.auxiliary.log.LogMaster.important("customLaunch set: " + customLaunch);
        MainLauncher.customLaunch = customLaunch;
        BG=getBgForLvl(customLaunch.getValue(CustomLaunch.CustomLaunchValue.xml_path));
    }

    private static String getBgForLvl(String value) {
            switch (value) {
                case "ready/graveyard.xml":
                case "crawl/Dwarven Halls.xml":
                    CoreEngine.setReverseExit(true);
                    return null ;
                case "ready/ship.xml":
                    CoreEngine.setReverseExit(true);
//                    return Sprites.BG_BASTION;
                return "main/background/ship flip.jpg";

                case "ready/ravenguard sanctum.xml":
                case "crawl/Guild Dungeon.xml":
                case "ready/wood.xml":
                case "ready/dark castle.xml":
                case "ready/the fortress.xml":

                case "crawl/cavern.xml":
                case "crawl/hell.xml":
                case "crawl/Underdark.xml":
                case "crawl/Ancient Ruins.xml":
                case "crawl/Ravenguard Dungeon.xml":
            }
        return null ;
    }

    @Override
    protected void engineInit() {
        super.engineInit();
        DC_Engine.dataInit();
    }
}
