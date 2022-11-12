package eidolons.game.battlecraft;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.mngr.action.DC_ActionManager;
import main.system.launch.Launch;
import eidolons.system.DC_ConditionMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.DescriptionTooltips;
import main.data.xml.XML_Reader;
import main.system.auxiliary.log.Chronos;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;

/*
supposed to give access to Audio, Data, utilities, ...
 */
public class DC_Engine extends CoreEngine {
    private static boolean atbMode = true;
    private static boolean trainingOff;

    static {
        jarInit();
    }

    public static void jarInit() {
        CoreEngine.setEngineObject(new DC_Engine());
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

    public static void gameStartInit() {
        gameInit();
    }

    public static void systemInit() {
        Launch.START(Launch.LaunchPhase._2_systemInit);
        systemInit(!CoreEngine.isLevelEditor());
        Launch.END(Launch.LaunchPhase._2_systemInit);

    }

    public static void systemInit(boolean initOptions) {
        CoreEngine.systemInit();
        if (initOptions) {
            OptionsMaster.init();
        }
    }

    public static void dataInit() {
        Chronos.mark("DATA INIT");
        new DC_ContentValsManager().init();

//        CoreEngine.dataInit(!CoreEngine.isFastMode());
        CoreEngine.dataInit(true);
        XML_Reader.setMacro(false); //fix this shit!
        //read save game?
        DC_ContentValsManager.initTypeDynamicValues();
        Chronos.logTimeElapsedForMark("DATA INIT");
    }


    public static void gameInit() {
        ConditionMaster.setInstance(new DC_ConditionMaster());
        DC_ActionManager.init();
         DescriptionTooltips.init();
    }

    public static boolean isAtbMode() {
        return atbMode;
    }

    public static void setAtbMode(boolean atbMode) {
        DC_Engine.atbMode = atbMode;
    }

    public static boolean isTrainingOff() {
        return trainingOff;
    }

    public static void setTrainingOff(boolean trainingOff) {
        DC_Engine.trainingOff = trainingOff;
    }

    public static boolean isRngSupported() {
        return true;
    }

    public static boolean isUseCustomTypesAlways() {
        return false;
    }
}
