package eidolons.game.battlecraft;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.active.DC_ActionManager;
import eidolons.system.DC_ConditionMaster;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.DescriptionTooltips;
import main.data.xml.XML_Reader;
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

    public static void analyzeUserSystem() {

    }

    private static boolean trainingOff;

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
        systemInit(!CoreEngine.isLevelEditor());

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
