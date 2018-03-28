package main.test.debug;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

public class DebugUtilities {
    public static final int QUASI_INFINITE_VALUE = 90000;

    static PARAMETER[] GOD_MODE_INFINITE_PARAMETERS = {PARAMS.ENDURANCE,
     PARAMS.TOUGHNESS, PARAMS.N_OF_ACTIONS, PARAMS.ESSENCE,
     PARAMS.STAMINA, PARAMS.FOCUS, PARAMS.SPIRIT, PARAMS.SIGHT_RANGE,};
    // ++ x-ray vision
    static int[] GOD_MODE_INFINITE_VALS = {QUASI_INFINITE_VALUE / 10,
     QUASI_INFINITE_VALUE / 20, 99, QUASI_INFINITE_VALUE / 50,
     QUASI_INFINITE_VALUE / 100, QUASI_INFINITE_VALUE / 100,
     QUASI_INFINITE_VALUE / 1000, QUASI_INFINITE_VALUE / 10000};

    private static Map<Obj, ObjType> normalTypeMap;

    private static Map<Obj, ObjType> godTypeMap;

    private static String godSpells = "Leap into Darkness;Teleport;Shadow Bolt;";

    public static void initGodMode(DC_UnitModel obj, boolean on) {
        if (!on) {
            ObjType oldType = getNormalTypeMap().get(obj);
            obj.applyType(oldType);
            obj.addDynamicValues();
            return;
        }
        ObjType type = obj.getType();
        getNormalTypeMap().put(obj, type);
        // godTypeMap

        ObjType godType = getGodTypeMap().get(obj);
        if (godType == null) {
            godType = new ObjType(type);
            getGodTypeMap().put(obj, godType);
            obj.getGame().initType(godType);
            setGodParams(godType);
            setGodProps(godType);

        }
        obj.applyType(godType);
        obj.addDynamicValues();
    }

    private static void setGodProps(Entity entity) {
        setGodActions(entity);
        setGodSpells(entity);
    }

    private static void setGodSpells(Entity entity) {
        // String spells = StringMaster.constructContainer(DataManager
        // .getTypeNames(OBJ_TYPES.SPELLS));

        entity.setProperty(PROPS.VERBATIM_SPELLS, godSpells);
    }

    private static void setGodActions(Entity entity) {
        String actions = StringMaster.constructContainer(DataManager
         .getTypeNames(DC_TYPE.ACTIONS));
        entity.setProperty(G_PROPS.ACTIVES, actions);

    }

    private static void setGodParams(Entity entity) {
        int i = 0;
        for (PARAMETER p : GOD_MODE_INFINITE_PARAMETERS) {
            entity.setParam(p, GOD_MODE_INFINITE_VALS[i]);
            i++;
        }
        i = 0;
        // for (PARAMETER portrait : GOD_MODE_INFINITE_PARAMETERS) {
        // portrait = ContentManager.getCurrentParam(portrait);
        // entity.setParam(portrait, GOD_MODE_INFINITE_VALS[i]);
        // i++;
        // }
        // entity.setParam(PARAMS.C_MORALE, QUASI_INFINITE_VALUE / 10);
        entity.setParam(PARAMS.C_INITIATIVE, QUASI_INFINITE_VALUE / 100);
    }

    public static Map<Obj, ObjType> getNormalTypeMap() {
        if (normalTypeMap == null) {
            normalTypeMap = new HashMap<>();
        }
        return normalTypeMap;
    }

    public static Map<Obj, ObjType> getGodTypeMap() {
        if (godTypeMap == null) {
            godTypeMap = new HashMap<>();
        }
        return godTypeMap;
    }

}
