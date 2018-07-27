package eidolons.system.utils;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.libgdx.texture.Images;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.entity.HeroEnums.CLASS_PERK_GROUP;
import main.content.enums.entity.HeroEnums.PERK_PARAM;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.NumberUtils;

/*
Double perks - combine two !


 */
public class PerkGenerator {
    private static final int PERK_LEVELS = 3;

    public static void main(String[] args) {
        generatePerks();

    }

    public static void generatePerks() {
        DC_Engine.mainMenuInit();
        generateParameterPerks();
        adjustTypes();
        XML_Writer.writeXML_ForTypeGroup(DC_TYPE.PERKS);
//        generateAbilityPerks();
//        generatePassivePerks();

    }

    private static void adjustTypes() {
        for (ObjType sub : DataManager.getTypes(DC_TYPE.CLASSES)) {
            CLASS_GROUP class_group=
              new EnumMaster<CLASS_GROUP>().retrieveEnumConst(CLASS_GROUP.class,
             sub.getProperty(G_PROPS.CLASS_GROUP));

            String string = sub.getName();

            if (sub.getIntParam(PARAMS.CIRCLE)>0){
                string = class_group
                 +"_"+string;
            }
            else {
                if (class_group==CLASS_GROUP.WIZARD)
                    string =  "Wizard_Apprenctice" ;
                else
                 string = class_group.toString();
            }
            if (class_group==CLASS_GROUP.SORCERER)
                string = string.replace(class_group.toString() ,"Apostate");

            CLASS_PERK_GROUP group = new EnumMaster<CLASS_PERK_GROUP>().
             retrieveEnumConst(CLASS_PERK_GROUP.class, string);
            if (group!=null )
                sub.setProperty(PROPS.CLASS_PERK_GROUP, group.name());
            else {
                continue;
            }
        }
        XML_Writer.writeXML_ForTypeGroup(DC_TYPE.CLASSES);
    }

    private static void generateParameterPerks() {
        for (int level = 0; level < PERK_LEVELS; level++) {
            for (PERK_PARAM sub : HeroEnums.PERK_PARAM.values()) {
                PARAMETER param = ContentValsManager.getPARAM(sub.name());
                ObjType type = new ObjType(getName(sub, level)
                , DC_TYPE.PERKS);

//                type.setParam(param, sub.values[level] + "");
//                if (sub.percentage)
//                    type.setProperty(G_PROPS.STD_BOOLS, STD_BOOLS.PERCENT_MOD.name());
                float amount = sub.values[level];
                String boni=param.getName()+"(" +amount;
                if (sub.percentage)
                    boni += "%";
                boni += ")";
                type.setProperty(PROPS.PARAMETER_BONUSES,
                 boni);
                type.setProperty(G_PROPS.DESCRIPTION,
                 getDescription(sub, amount));
                type.setProperty(PROPS.PERK_PARAM,
                 sub.toString());

                type.setProperty(G_PROPS.PERK_GROUP,
                 getName(sub, 0));
                type.setProperty(G_PROPS.PERK_PARAMS,
                 sub.name());
                type.setParam(G_PARAMS.PERK_LEVEL,
                 level);
                type.setParam(PARAMS.CIRCLE,
                 level);
                type.setProperty(G_PROPS.IMAGE,
                 getImage(sub, level));
                type.setProperty(G_PROPS.GROUP, "Parameter");

                //TODO image
                DataManager.addType(type);
            }
        }
    }

    private static String getDescription(PERK_PARAM sub, float amount) {
        return "Increases hero's " +
         sub +" by " +amount;
    }

    private static String getImage(PERK_PARAM sub, int level) {
//generate tiered via overlays
        PARAMETER relatedValue = ContentValsManager.getPARAM(sub.name());
        if (relatedValue != null) {
            return StrPathBuilder.build(PathFinder. getPerkImagePath(),
             relatedValue.getName() + ".png");
        }
        //mastery

        return Images.UNKNOWN_PERK;
    }

    private static String getName(PERK_PARAM sub, int level) {
        if (level==0)
            return  sub.name;
        return sub.name+ " " + NumberUtils.getRoman(level+1);
    }

    public enum PERK_ABILITY {
        Consume_Magic, Arcane_Fury, Mark_Sorcerer,
    }

    public enum PERK_PARAM_COMBO {
//        MAGIC_RESISTANCES(5, 15, 30, false, "Relentless"),
//        PHYSICAL_RESISTANCES(5, 15, 30, false, "Relentless"),

    }

    public enum PERK_TYPE {
        MASTERY, ATTRIBUTE, PARAMETER, ABILITY, PASSIVE,
        //
    }

}
