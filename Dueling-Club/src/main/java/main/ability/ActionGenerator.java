package main.ability;

import main.content.DC_TYPE;
import main.content.ValuePages;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ActionType;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

public class ActionGenerator {

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
            if (StringMaster.compare(type.getProperty(G_PROPS.ACTION_TAGS), ActionEnums.ACTION_TAGS.MAIN_HAND
                    + "", false)) {
                ActionType offHandType = new ActionType(type);
                offHandType.addProperty(G_PROPS.ACTION_TAGS, StringMaster
                        .getWellFormattedString(ActionEnums.ACTION_TAGS.OFF_HAND + ""));
                offHandType.removeProperty(G_PROPS.ACTION_TAGS,

                        StringMaster.getWellFormattedString(ActionEnums.ACTION_TAGS.MAIN_HAND + ""));
                //
                offHandType.setName(getOffhandActionName(type.getName()));
                offHandType.addProperty(G_PROPS.ACTION_TAGS, getOffhandActionName(type.getName()));
                DataManager.addType(offHandType);
            }

        }
    }

    public static void init() {
        if (!CoreEngine.isArcaneVault()) {
            try {
                generateOffhandActions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String getOffhandActionName(String name) {
        return StringMaster.getWellFormattedString(ActionEnums.ACTION_TAGS.OFF_HAND + "") + " " + name;
    }

}
