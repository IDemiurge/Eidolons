package libgdx.launch;

import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.EngineLauncher;
import eidolons.game.core.launch.TestLaunch;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.TestMasterContent;
import main.data.filesys.PathFinder;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.util.DialogMaster;

public class LaunchFlags {


    public static void initFlags() {
        // GpuTester.test();
        CoreEngine.setSwingOn(false);
        Flags.setSafeMode(true);
        Flags.setIggDemo(true);
        Flags.setMainGame(true);
        Flags.setDialogueTest(true);
    }

    public static String[] processArgs(String[] args, EngineLauncher engineLauncher) {
        if (args.length > 0) {
            PathFinder.init();
            String arg = args[0];
            if (arg.contains("testLaunch")) {
                TestLaunch launch= TestEnvSetup.initTestEnvLaunch(arg);
                engineLauncher.setTestLaunch(launch);
            }

            if (arg.contains("town")) {
                EidolonsGame.TOWN = true;
            }
            CoreEngine.TEST_LAUNCH = arg.contains("test;");
            System.out.println("TEST LAUNCH =" + CoreEngine.TEST_LAUNCH);

            Flags.setJarlike(arg.contains("jarlike;"));
            System.out.println("jarlike =" + Flags.isJarlike());

            Flags.setFastMode(CoreEngine.TEST_LAUNCH);

            if (arg.contains("selecthero")) {
                MainLauncher.HERO_INDEX = DialogMaster.inputInt(0);
                if (MainLauncher.HERO_INDEX == -1) {
                    EidolonsGame.SELECT_HERO = true;
                }
            }
            String[] parts = arg.split("--");
            if (parts.length > 1) {
                OptionsMaster.setOptionsMode(parts[0]);
                LogMaster.important(" Options Mode set: " + parts[0]);
                arg = parts[1];
            }
            EidolonsGame.BOSS_FIGHT = arg.contains("BOSS");
            EidolonsGame.BRIDGE = arg.contains("bridge");
            EidolonsGame.FOOTAGE = arg.contains("footage");
            EidolonsGame.PUZZLES = arg.contains("puzzle");


            EidolonsGame.DUEL = arg.contains("duel");
            EidolonsGame.DUEL_TEST = arg.contains("duel");

            if (EidolonsGame.DUEL_TEST) {
                EidolonsGame.BRIDGE = true;
            }
            Flags.setLevelTestMode(false);
            args = arg.split(";");
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
                // if (Flags.isMe())
                //     OptionsMaster.setOptionsPath("C:\\Users\\justm\\AppData\\Local\\Eidolons\\fast options.xml");
            }
        }
        return args;
    }

}
