package eidolons.game.netherflame.igg;

import eidolons.game.EidolonsGame;
import eidolons.game.netherflame.igg.story.brief.BriefingData;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SystemOptions;
import eidolons.system.text.DescriptionTooltips;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

public class IGG_Launcher {


    public static boolean INTRO_RUNNING = false;
    private static String splitter = ";";

    public static void start(Runnable onDone) {
        new Thread(() -> {
            CoreEngine.setIggDemoRunning(true);
            if (isIntroSkipped()) {
                onDone.run();
                return;
            }
            boolean aborted = !introBriefing();
            //TODO what to do?
            onDone.run();
        }, " thread").start();
    }

    private static boolean isIntroSkipped() {
        if (        EidolonsGame.FOOTAGE
        ) {
            return true;
        }
        if (CoreEngine.uploadPackage== CoreEngine.UPLOAD_PACKAGE.Aphotic){
            return true;
        }
        if (CoreEngine.isJar()) {
            return OptionsMaster.getSystemOptions().getBooleanValue(SystemOptions.SYSTEM_OPTION.INTRO_OFF);
        }
        return false;
    }

    public static boolean epilogueBriefing() {
//show glory etc after!

        return true;
    }
    public static boolean introBriefing() {
         DescriptionTooltips.init();
        String txtData = DescriptionTooltips.getTipMap().get("intro slides");

        String imgData =
                IGG_Images.BRIEF_ART.EIDOLONS_CENTER.getPath() + ";"
                        + IGG_Images.BRIEF_ART.LEVI_FIGHT.getPath() + ";"
                        + IGG_Images.BRIEF_ART.APHOLON.getPath() + ";"
                        + IGG_Images.BRIEF_ART.RITUAL.getPath() + ";"
                        + IGG_Images.BRIEF_ART.ENTER_GATE.getPath();
        String[] imgs = imgData.split(splitter);
        String[] msgs = txtData.split(splitter);

        BriefingData briefingData = new BriefingData(
                Sprites.BG_DEFAULT,
                IGG_Images.PROMO_ART.THE_HALL.getPath()
                , imgs, msgs, true);


        INTRO_RUNNING = true;
        GuiEventManager.trigger(GuiEventType.BRIEFING_START,
                briefingData);
        boolean result = (boolean) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.BRIEFING_COMPLETE);
        INTRO_RUNNING = false;
        return result;


//        String videoData = null;
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.CINEMATIC, name), videoData);

    }

    public static boolean isDemo(ObjType type) {
        return type.getGroup().equalsIgnoreCase("demo");
    }
}
