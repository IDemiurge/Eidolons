package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.game.battlecraft.logic.meta.igg.story.brief.BriefingData;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.texture.Images;
import main.data.filesys.PathFinder;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;

public class IGG_Launcher {


    private static String splitter = ";";

    public static void startIntro() {

    }

    public static void startBriefing(int missionIndex) {

    }

    public static void start() {
        /**
         * intro sequence
         * briefing
         * hero selection
         * dungeon
         */

//        FileManager.readFile(PathFinder.getTextPath() + "demo data.txt");
// data unit?
        String txtData = "Testing the core!;Blast it...";
        String imgData = Images.STASH_LANTERN + ";" + Images.HC_SCROLL_BACKGROUND;
        CoreEngine.setIggDemoRunning(true);
        String[] imgs = imgData.split(splitter);
        String[] msgs = txtData.split(splitter);
        BriefingData briefingData = new BriefingData(
                "atlas.txt"
//                Images.WEAVE_BACKGROUND
                , imgs, msgs, true);
        String name = "Briefing!";
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.BRIEFING, new EventCallbackParam(briefingData)));

//        String videoData = null;
//        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new ScreenData(SCREEN_TYPE.CINEMATIC, name), videoData);
    }
}
