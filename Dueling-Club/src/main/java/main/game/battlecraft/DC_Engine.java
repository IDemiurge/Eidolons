package main.game.battlecraft;

import main.content.DC_ContentManager;
import main.entity.active.DC_ActionManager;
import main.swing.DC_GuiManager;
import main.swing.components.obj.drawing.DrawHelper;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.log.Chronos;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;
import main.system.options.OptionsMaster;

public class DC_Engine {
/*
supposed to give access to Audio, Data, utilities, ...
 */
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
    public static void gameStartInit() {
        gameInit();
    }
        public static void systemInit() {
        CoreEngine.systemInit();
        OptionsMaster.init();
        DC_GuiManager.init();
//        TextMaster.init(locale);
    }

    public static void dataInit(  ) {
        new DC_ContentManager().init();
        CoreEngine.dataInit(  false);
        //read save game?
        DC_ContentManager.initTypeDynamicValues();
    }


    public static void gameInit() {
        // DC_CostsFactory.createCostsForTypes();
        ConditionMaster.setInstance(new DC_ConditionMaster());
        DC_ActionManager.init();
        DrawHelper.init();
    }
}
