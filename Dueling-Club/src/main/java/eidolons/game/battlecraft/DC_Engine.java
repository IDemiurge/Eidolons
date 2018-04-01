package eidolons.game.battlecraft;

import eidolons.content.DC_ContentManager;
import eidolons.entity.active.DC_ActionManager;
import eidolons.swing.DC_GuiManager;
import eidolons.swing.components.obj.drawing.DrawHelper;
import eidolons.system.DC_ConditionMaster;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.log.Chronos;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;

public class DC_Engine extends CoreEngine {
    private static boolean atbMode = true;

    /*
    supposed to give access to Audio, Data, utilities, ...
     */
    static {
        jarInit();
    }

    public static void fullInit() {
        Chronos.mark("DC INIT");
        systemInit();
        dataInit();
        gameInit();
        Chronos.logTimeElapsedForMark("DC INIT");
    }

    public static void mainMenuInit() {
        systemInit();
        dataInit();
    }

    public static void jarInit() {
        CoreEngine.setEngineObject(new DC_Engine());
    }

    public static void gameStartInit() {
        gameInit();
    }

    public static void systemInit() {
        CoreEngine.systemInit();
        OptionsMaster.init();
        DC_GuiManager.init();
//        TextMaster.init(locale);
    }

    public static void dataInit() {
        Chronos.mark("DATA INIT");
        new DC_ContentManager().init();
        CoreEngine.dataInit(false);
        //read save game?
        DC_ContentManager.initTypeDynamicValues();
        Chronos.logTimeElapsedForMark("DATA INIT");
    }


    public static void gameInit() {
        // DC_CostsFactory.createCostsForTypes();
        Chronos.mark("GAME INIT");
        ConditionMaster.setInstance(new DC_ConditionMaster());
        DC_ActionManager.init();
        DrawHelper.init();
        Chronos.logTimeElapsedForMark("GAME INIT");
    }

    public static boolean isAtbMode() {
        return atbMode;
    }

    public static void setAtbMode(boolean atbMode) {
        DC_Engine.atbMode = atbMode;
    }
}
