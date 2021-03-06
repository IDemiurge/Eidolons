package main;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.PROPS;
import eidolons.content.values.ValuePageManager;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.data.arena.A_ValuePageManager;
import main.entity.type.ObjType;
import main.gui.builders.EditViewPanel;
import main.system.auxiliary.ContainerUtils;

import java.util.*;

public class AV_DataManager {

    private static final VALUE[] COPY_VALS_ENCOUNTERS = {
            PROPS.PRESET_GROUP,
            PROPS.EXTENDED_PRESET_GROUP,
            PROPS.SHRUNK_PRESET_GROUP,
    };
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
    private final Map<ObjType, Stack<ObjType>> stackMap = new HashMap<>();
    private static ObjType copiedType;

    public static void init() {
        ObjectMap<String, List<VALUE>> IGNORE_MAP = new ObjectMap<>();
        for (int code = 0; code < IGNORED_VALUES.length; code++) {
            List<VALUE> list = new ArrayList<>();
            list.addAll(Arrays.asList(IGNORED_FROM_ALL_VALUES));
            list.addAll(Arrays.asList(IGNORED_VALUES[code]));
            IGNORE_MAP.put(DC_TYPE.getTypeByCode(code).getName(), list);
        }
        ContentValsManager.setAV_IgnoredValues(IGNORE_MAP);
    }

    public static List<String> getValueNames(EditViewPanel.ValueSet valueSet, String key) {
        // if (valueSet== EditViewPanel.ValueSet.arena) {
        //     return A_ValuePageManager.getValuesAV(DC_TYPE.getType(key));
        // }
        List<VALUE> values;
        try {
            values = ValuePageManager.getValuesForAV(DC_TYPE.getType(key));
            if (values == null) {
                return ContentValsManager.getArcaneVaultValueNames(key);
            }
            List<String> list = ContainerUtils.convertToStringList(values);
            return list;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
            return ContentValsManager.getArcaneVaultValueNames(key);

    }

    public static void paste(ObjType selectedType) {
        if (copiedType == null) {
            return;
        }
        if (copiedType.getOBJ_TYPE_ENUM() != selectedType.getOBJ_TYPE_ENUM()) {
            copiedType = null;
            return;
        }
        VALUE[] values_to_copy = new VALUE[0];
        if (copiedType.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
            switch (((DC_TYPE) copiedType.getOBJ_TYPE_ENUM())) {
                case ENCOUNTERS:
                    //TODO alt
                    values_to_copy = new VALUE[]{
                            PROPS.FILLER_TYPES,
                            PROPS.REINFORCEMENT_TYPE,
                            PROPS.PRE_BATTLE_EVENT,
                            PROPS.AFTER_BATTLE_EVENT,
                            PROPS.LOOT_TYPE,
                    };
            }
        }
        if (values_to_copy == null) {
            return;
        }
        for (VALUE value : values_to_copy) {
            selectedType.setValue(value, copiedType.getValue(value));
        }
    }

    public static void copy(ObjType selectedType) {
        copiedType = selectedType;
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
