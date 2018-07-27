package main;

import eidolons.content.PROPS;
import eidolons.content.ValuePageManager;
import main.content.*;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;

import java.util.*;

public class AV_DataManager {

    static VALUE[] IGNORED_FROM_ALL_VALUES = {G_PROPS.TYPE,
            PROPS.FACING_DIRECTION, PROPS.VISIBILITY_STATUS,
            PROPS.DETECTION_STATUS, G_PROPS.STATUS, G_PROPS.MODE,};
    static VALUE[][] IGNORED_VALUES = {{}, // UNITS
            {}, // sp
            {}, // CHARS
            {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.LORE,}, // ABILS
            {}, // BF_OBJ
            {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.LORE,}, // BUFFS
            {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.LORE,}, // ACTIONS
            {G_PROPS.ASPECT, G_PROPS.DEITY,}, // ARMOR
            {G_PROPS.ASPECT, G_PROPS.DEITY,}, // WEAPONS
            {G_PROPS.ASPECT, G_PROPS.DEITY, G_PROPS.LORE,}, // SKILLS
    };
    private Map<ObjType, Stack<ObjType>> stackMap = new HashMap<>();

    public static void init() {
        Map<String, List<VALUE>> IGNORE_MAP = new HashMap<>();
        for (int code = 0; code < IGNORED_VALUES.length; code++) {
            List<VALUE> list = new ArrayList<>();
            list.addAll(Arrays.asList(IGNORED_FROM_ALL_VALUES));
            list.addAll(Arrays.asList(IGNORED_VALUES[code]));
            IGNORE_MAP.put(DC_TYPE.getTypeByCode(code).getName(), list);
        }
        ContentValsManager.setAV_IgnoredValues(IGNORE_MAP);
    }

    public static List<String> getValueNames(String key) {
        List<VALUE> values;
        try {
            values = ValuePageManager.getValuesForAV(DC_TYPE.getType(key));
            if (values == null) {
                return ContentValsManager.getArcaneVaultValueNames(key);
            }
            List<String> list = ContainerUtils.convertToStringList(values);
            return list;
        } catch (Exception e) {
            // main.system.ExceptionMaster.printStackTrace(e);
            return ContentValsManager.getArcaneVaultValueNames(key);
        }

    }

    public void addType(ObjType type) {
        stackMap.put(type, new Stack<>());
    }

    public void save(ObjType type) {
        Stack<ObjType> stack = stackMap.get(type);
        if (stack == null) {
            addType(type);
            stack = stackMap.get(type);
        }

        stack.push(new ObjType(type));

    }

    public void back(ObjType type) {
        Stack<ObjType> stack = stackMap.get(type);
        if (stack != null) {
            if (stack.isEmpty()) {
                return;
            }
            ObjType prev = stack.pop();
            type.getGame().initType(prev);
            type.cloneMaps(prev);
        }
    }

}
