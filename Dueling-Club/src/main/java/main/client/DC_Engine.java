package main.client;

import main.content.DC_ContentManager;
import main.data.DataManager;
import main.entity.obj.DC_Cell;
import main.game.logic.generic.DC_ActionManager;
import main.swing.DC_GuiManager;
import main.swing.components.obj.drawing.DrawHelper;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;
import main.system.options.OptionsMaster;
import main.system.text.TextMaster;
import main.system.threading.Weaver;

public class DC_Engine {

    public static final String LAUNCHED = "rdy";
    private static boolean macro = true;
    private static String locale;

    public static void init() {
        init(macro);
    }

    public static void init(boolean macroMode) {
        macro = macroMode;
        Chronos.mark("DC INIT");
        systemInit();
        gameInit();
        Chronos.logTimeElapsedForMark("DC INIT");
    }

    public static void gameInit() {
        gameInit(false);
    }

    public static void microInitialization(boolean newThread) {
        if (newThread) {
            new Thread(new Runnable() {
                public void run() {
                    new DC_Engine().microInitialization();
                }
            }).start();
        } else {
            new DC_Engine().microInitialization();
        }

    }

    public static void gameInit(boolean newThread) {
        if (newThread) {
            Weaver.inNewThread(new Runnable() {
                public void run() {
                    new DC_Engine().gameInitialization();
                }
            });
        } else {
            new DC_Engine().gameInitialization();
        }

    }

    public static void systemInit() {
        CoreEngine.systemInit();
        OptionsMaster.init();
        DC_GuiManager.init();
        TextMaster.init(locale);
    }



    public static void dataInit(boolean macroMode ) {
        new DC_ContentManager().init();
       new CoreEngine().dataInit(  macroMode);
        DC_ContentManager.initTypeDynamicValues();
    }

    public void gameInitialization() {
        new DC_ContentManager().init();
        ConditionMaster.setInstance(new DC_ConditionMaster());
        new CoreEngine().dataInit(macro);
        microInitialization();

    }

    public void microInitialization() {
        // DC_CostsFactory.createCostsForTypes();
        DC_ActionManager.init();
        DC_Cell.setEMPTY_CELL_TYPE(DataManager.getType(StringMaster.STD_TYPE_NAMES.Cell.toString(),
                "terrain"));
        DrawHelper.init();
    }
}
