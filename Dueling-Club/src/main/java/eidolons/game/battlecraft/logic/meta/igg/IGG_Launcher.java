package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefingData;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

public class IGG_Launcher {


    private static String splitter = ";";

    public static void startIntro() {

    }

    public static void startBriefing(int missionIndex) {

    }

    public static void start(Runnable onDone) {
        new Thread(() -> {
            CoreEngine.setIggDemoRunning(true);
            if (isIntroSkipped()){
                onDone.run();
                return;
            }
            boolean aborted = !introBriefing();
//            showMainMenu();
//            WaitMaster.WAIT(255);
            onDone.run();
        }, " thread").start();
        /**
         * intro sequence
         * briefing
         * hero selection
         * dungeon
         */
//        FileManager.readFile(PathFinder.getTextPath() + "demo data.txt");
// data unit?
    }

    private static boolean isIntroSkipped() {
        if (CoreEngine.isJar()) {
            return OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.INTRO_OFF);
        }
        return CoreEngine.isActiveTestMode();
    }

    private static boolean introBriefing() {
        String txtData =
                "We were six in the end, when we met the faceless fiend. " +
                        "We were only two when it was over.;\n" +

                        "Yet it was enough. I did not have the will to do what was necessary. I let Fiona live, and she betrayed me for it. " +
                        "Now Apholon is gone, and with it my chance to change my fate.;" +

                        "No matter what I do, the Prophecy haunts me, ensnares me in its web. I do not wish to be any world’s savior, least of all this one’s.; \n" +

                        "Yet I obtained the sacred ash, the final piece of the puzzle I worked so hard on all these years.;" +


                        "Now I know how the Sorcerer King raised his army, became more than a mere necromancer, but a god who could bring back the fallen, time and again, " +
                        "ad infinitum, while his minions brought him new souls to consume. \n;";

        String imgData =
                IGG_Images.BRIEF_ART.EIDOLONS_CENTER.getPath() + ";"
                + IGG_Images.BRIEF_ART.LEVI_FIGHT.getPath() + ";"
                        + IGG_Images.BRIEF_ART.APHOLON.getPath()+ ";"
                        + IGG_Images.BRIEF_ART.RITUAL.getPath()+ ";"
                        + IGG_Images.BRIEF_ART.ENTER_GATE.getPath()

                ;
        String[] imgs = imgData.split(splitter);
        String[] msgs = txtData.split(splitter);
        BriefingData briefingData = new BriefingData(
                "atlas.txt",
                IGG_Images.PROMO_ART.THE_HALL.getPath()
                , imgs, msgs, true);
        String name = "Briefing!";
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.BRIEFING, new EventCallbackParam(briefingData)));
        GuiEventManager.trigger(GuiEventType.BRIEFING_START,
                 briefingData)  ;
        return (boolean) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.BRIEFING_COMPLETE);
//        String videoData = null;
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.CINEMATIC, name), videoData);

    }

    public static boolean isDemo(ObjType type) {
        return type.getGroup().equalsIgnoreCase("demo");
    }
}
