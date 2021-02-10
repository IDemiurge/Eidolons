package eidolons.ability;

import eidolons.content.ValuePages;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.entity.type.impl.ActionType;
import main.system.auxiliary.StringMaster;

import java.util.HashSet;
import java.util.Set;

public class ActionGenerator {

    private static final Set<String> offhandTypes = new HashSet<>();

    public static void addDefaultSneakModsToAction(ObjType type) {

        for (PARAMETER v : ValuePages.SNEAK_MODS) {
            if (type.getIntParam(v) == 0) {
                type.setParam(v, v.getDefaultValue());
            }
        }

    }

    public static void generateOffhandActions() {
        generateOffhandActions(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK);
        generateOffhandActions(ActionEnums.ACTION_TYPE.STANDARD_ATTACK);
    }

    public static void generateOffhandActions(ACTION_TYPE action_type) {
        for (ObjType type : DataManager.getTypesGroup(DC_TYPE.ACTIONS, "" + action_type)) {
            if (StringMaster.contains(type.getProperty(G_PROPS.ACTION_TAGS),
                    ActionEnums.ACTION_TAGS.MAIN_HAND
                            + "")) {
                generateOffhandAction(type);
            }

        }
    }

    public static ObjType generateOffhandAction(ObjType type) {
        if (offhandTypes.contains(type.getName())) {
            return (DataManager.getType(getOffhandActionName(type
                    .getName()), DC_TYPE.ACTIONS));
        }
        ActionType offHandType = new ActionType(type);
        offHandType.addProperty(G_PROPS.ACTION_TAGS, StringMaster
                .format(ActionEnums.ACTION_TAGS.OFF_HAND + ""));
        offHandType.removeProperty(G_PROPS.ACTION_TAGS,

                StringMaster.format(ActionEnums.ACTION_TAGS.MAIN_HAND + ""));
        //
        offHandType.setName(getOffhandActionName(type.getName()));
        offHandType.addProperty(G_PROPS.ACTION_TAGS, getOffhandActionName(type.getName()));
        DataManager.addType(offHandType);
        offhandTypes.add(type.getName());
        return offHandType;
    }

    public static void init() {
        if (!isLazy()) {
                try {
                    generateOffhandActions();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }

    }

    private static boolean isLazy() {
        return true;
    }

    public static String getOffhandActionName(String name) {
        return StringMaster.format(ActionEnums.ACTION_TAGS.OFF_HAND + "") + " " + name;
    }

}
