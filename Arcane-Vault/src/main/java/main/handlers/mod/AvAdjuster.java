package main.handlers.mod;

import eidolons.ability.ActionGenerator;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.libgdx.GdxStringUtils;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.handlers.gen.AvGenHandler;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.List;

public class AvAdjuster {
    private static final List<ObjType> idSet = new ArrayList<>();
    public static boolean autoAdjust;
    private static int classId;

    private static void updateSpells() {
        // for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            // ContentGenerator.generateSpellParams(type);
        // }
    }

    private static void checkAdjustIcon(OBJ_TYPE obj_type) {
        for (ObjType type : DataManager.getTypes(obj_type)) {
            if (obj_type instanceof DC_TYPE) {
                switch (((DC_TYPE) obj_type)) {
                    case WEAPONS:
                        type.setImage(GdxStringUtils.getWeaponIconPath(type));
                        break;
                    case ARMOR:
                        type.setImage(GdxStringUtils.getArmorIconPath(type));
                        break;
                    case JEWELRY:
                        type.setImage(GdxStringUtils.getItemIconPath(type));
                        break;
                }
            }

        }
    }

    protected static void checkTypeModifications(OBJ_TYPE obj_type) {
        if (obj_type == DC_TYPE.CHARS || obj_type == DC_TYPE.BF_OBJ
                || obj_type == DC_TYPE.UNITS) {

            if (obj_type == DC_TYPE.CHARS || obj_type == DC_TYPE.UNITS) {
                for (ObjType type : DataManager.getTypes(obj_type)) {
                    DC_ContentValsManager.addDefaultValues(type, false);
                }
            }


        }
        checkPrincipleProcessing(obj_type);
        checkAdjustIcon(obj_type);

        // if (obj_type == DC_TYPE.PARTY) {
        //     ContentGenerator.adjustParties();
        // }
        if (obj_type == DC_TYPE.SPELLS) {
            updateSpells();
        }
        if (obj_type == DC_TYPE.ARMOR) {
            AvGenHandler.generateNewArmorParams();
        } else if (obj_type == DC_TYPE.WEAPONS) {
            AvGenHandler.generateNewWeaponParams();
        }
        else if (obj_type == DC_TYPE.ENCOUNTERS) {
            adjustEncounters();
        } else if (obj_type == DC_TYPE.ACTIONS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                ActionGenerator.addDefaultSneakModsToAction(type);
            }
        } else if (obj_type == DC_TYPE.SKILLS) {
            if (isSkillSdAutoAdjusting()) {
                autoAdjustSkills();
            }

        } else if (obj_type == DC_TYPE.CLASSES)
        // if (isClassAutoAdjustingOn())
        {
            autoAdjustClasses();
        }

        if (obj_type == DC_TYPE.ACTIONS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                if (type.checkProperty(G_PROPS.ACTION_TYPE, ActionEnums.ACTION_TYPE.STANDARD_ATTACK.toString())) {
                    DC_ContentValsManager.addDefaultValues(type, false);
                }
            }
        }

        if (obj_type == DC_TYPE.WEAPONS || obj_type == DC_TYPE.ARMOR) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                DC_ContentValsManager.addDefaultValues(type, false);
            }
        }
        if (obj_type == DC_TYPE.BF_OBJ) {
            AvGenHandler.generateBfObjProps();
        }
        if (obj_type.isTreeEditType() || obj_type == DC_TYPE.CHARS || obj_type == DC_TYPE.UNITS) {

            if (obj_type == DC_TYPE.CHARS) {
                // XML_Reader.checkHeroesAdded();
            }

            for (ObjType type : DataManager.getTypes(obj_type)) {
                //                if (C_OBJ_TYPE.ITEMS.equals(obj_type)) {
                //                    initRarity(type);
                //                }
                if (obj_type == DC_TYPE.CHARS) {
                    if (!type.isInitialized()) {
                        Game.game.initType(type);
                    }

                    if (StringMaster.isEmpty(type.getProperty(PROPS.OFFHAND_NATURAL_WEAPON))) {
                        type.setProperty((PROPS.OFFHAND_NATURAL_WEAPON), PROPS.NATURAL_WEAPON
                                .getDefaultValue());
                    }

                } else {
                    // XML_Transformer.adjustProgressionToWeightForm(type,
                    // true);
                    // XML_Transformer.adjustProgressionToWeightForm(type,
                    // false);
                }
            }

        }
    }

    private static void adjustEncounters() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.ENCOUNTERS)) {
            if (!FileManager.isFile(type.getImagePath())) {
                for (String substring : ContainerUtils.openContainer(type.getProperty(PROPS.PRESET_GROUP))) {
                    ObjType objType = DataManager.getType(substring, DC_TYPE.UNITS);
                    if (objType != null) {
                        type.setImage(objType.getImagePath());
                        break;
                        //104?
                    }
                }
            }
        }
    }

    private static void checkPrincipleProcessing(OBJ_TYPE obj_type) {
        if (obj_type == DC_TYPE.DEITIES || obj_type == DC_TYPE.SKILLS
                || obj_type == DC_TYPE.CLASSES || obj_type == DC_TYPE.CHARS
                || obj_type == DC_TYPE.UNITS) {
            for (ObjType type : DataManager.getTypes(obj_type)) {
                //                PrincipleMaster.processPrincipleValues(type);
            }
        }

    }

    public static void autoAdjustClasses() {
        classId = 0;
        idSet.clear();
        // getChildren(), parentCirlce
        for (ObjType type : DataManager.getTypes(DC_TYPE.CLASSES)) {
            autoAdjustClass(type);
        }
    }

    private static void autoAdjustClass(ObjType type) {

        // String passives = type.getProperty(G_PROPS.PASSIVES);
        // for (String passive : StringMaster.open(passives)) {
        // if (passive.contains("paramBonus")) {
        // //shouldn't overlap, really, nevermind...
        //
        // if (passive.contains("paramMod"))
        // +="%";
        // }
        // passives.replace(passive+ ";", "");
        // passives.replace(passive, "");
        // }
        // type.setProperty(G_PROPS.PASSIVES, passives);

        // String attrString = "";
        // String paramString = "";
        // for (PARAMETER portrait : type.getParamMap().keySet()) {
        // String value = type.getParams(portrait);
        // if (value.isEmpty())
        // continue;
        // if (value.equals("0"))
        // continue;
        // if (ContentManager.isValueForOBJ_TYPE(OBJ_TYPES.CHARS, portrait)) {
        // if (portrait.isAttribute())
        // attrString += portrait.getName() + StringMaster.wrapInParenthesis(value) +
        // ";";
        // else
        // paramString += portrait.getName() + StringMaster.wrapInParenthesis(value) +
        // ";";
        //
        // type.setParam(portrait, 0);
        // }
        // }
        // type.addProperty(PROPS.ATTRIBUTE_BONUSES, attrString);
        // type.addProperty(PROPS.PARAMETER_BONUSES, paramString);
        // String value = type.getProperty(PROPS.ATTRIBUTE_BONUSES);
        // if (value.contains(";;")) {
        // String replace = value.replace(";;", ";");
        // type.setProperty(PROPS.ATTRIBUTE_BONUSES,
        // StringMaster.cropLast(replace, 1));
        // }
        // value = type.getProperty(PROPS.PARAMETER_BONUSES);
        // if (value.contains(";;")) {
        // String replace = value.replace(";;", ";");
        // type.setProperty(PROPS.PARAMETER_BONUSES,
        // StringMaster.cropLast(replace, 1));
        //
        // }

        // TODO remove empty

        ObjType parent = DataManager.getParent(type);
        if (parent == null) {
            parent = DataManager.getType(type.getProperty(PROPS.BASE_CLASSES_ONE),
                    DC_TYPE.CLASSES);
        }
        if (parent != null) {
            autoAdjustClass(parent);
            type.setParam(PARAMS.CIRCLE, parent.getIntParam(PARAMS.CIRCLE) + 1);
            // inherit principles

            if (!type.checkProperty(G_PROPS.PRINCIPLES)) {
                type.setProperty(G_PROPS.PRINCIPLES, parent.getProperty(G_PROPS.PRINCIPLES));

            }

        } else {
            type.setParam(PARAMS.CIRCLE, 0);
        }

        resetAltBaseTypes(type);

        if (idSet.contains(type)) {
            return;
        }
        int id = NumberUtils.getIntParse(type.getProperty(G_PROPS.ID));
        if (id % 2 != 1) {
            type.setProperty(G_PROPS.ID, "" + classId);
            classId += 2;
            idSet.add(type);
        }

        // multiclass?

        // subling count
        // id - ?

        // link variant?
        // type.getIntParam(PARAMS.CIRCLE) - parent.getIntParam(PARAMS.CIRCLE)
    }

    private static void resetAltBaseTypes(ObjType type) {
    }

    public static void autoAdjustSkills() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SKILLS)) {
            autoAdjustSkill(type);
        }
    }

    private static void autoAdjustSkill(ObjType type) {

        String reqs = type.getProperty(PROPS.REQUIREMENTS);
        if (reqs.contains("Principles")) {
            String cleanedReqs = reqs;
            for (String sub : ContainerUtils.open(reqs)) {
                if (!sub.contains("Principles")) {
                    continue;
                }
                cleanedReqs = cleanedReqs.replace(sub, "");
                cleanedReqs = cleanedReqs.replace(";;", ";");
            }
            type.setProperty(PROPS.REQUIREMENTS, cleanedReqs);
            LogMaster.log(1, reqs + "; cleanedReqs =" + cleanedReqs);
        }

    }

    private static boolean isSkillSdAutoAdjusting() {
        return autoAdjust;
    }
}
