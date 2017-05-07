package main.game.battlecraft;

import main.content.DC_ContentManager;
import main.data.DataManager;
import main.entity.obj.DC_Cell;
import main.entity.active.DC_ActionManager;
import main.swing.DC_GuiManager;
import main.swing.components.obj.drawing.DrawHelper;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.entity.ConditionMaster;
import main.system.launch.CoreEngine;
import main.system.options.OptionsMaster;

public class DC_Engine {


    public static void init() {
        Chronos.mark("DC INIT");
        systemInit();
        gameInitialization();
        Chronos.logTimeElapsedForMark("DC INIT");
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
        DC_ContentManager.initTypeDynamicValues();
    }

    public static void gameInitialization() {
        new DC_ContentManager().init();
        ConditionMaster.setInstance(new DC_ConditionMaster());
        CoreEngine.dataInit(false);
        microInitialization();

    }

    public static void microInitialization() {
        // DC_CostsFactory.createCostsForTypes();
        DC_ActionManager.init();
        DC_Cell.setEMPTY_CELL_TYPE(DataManager.getType(StringMaster.STD_TYPE_NAMES.Cell.toString(),
                "terrain"));
        DrawHelper.init();
    }
}
