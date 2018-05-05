package eidolons.system.utils;

import main.content.ContentValsManager;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;

import static eidolons.system.utils.PerkGenerator.PERK_PARAM.*;

/*
Double perks - combine two !


 */
public class PerkGenerator {
    private static final int PERK_LEVELS = 3;

    public static void main(String[] args) {
        generatePerks();

    }

    public static void generatePerks() {

        generateParameterPerks();
//        generateAbilityPerks();
//        generatePassivePerks();

    }

    private static void generateParameterPerks() {
        for (int level = 0; level < PERK_LEVELS; level++) {
            for (PERK_PARAM sub : PERK_PARAM.values()) {
                PARAMETER param = ContentValsManager.getPARAM(sub.name());
                ObjType type = new ObjType(getName(sub, level));
//                , DC_TYPE.PERKS);
                type.setParam(param, sub.values[level] + "");
                if (sub.percentage)
                    type.setProperty(G_PROPS.STD_BOOLS, STD_BOOLS.PERCENT_MOD.name());

                //TODO image
                DataManager.addType(type);
            }
        }
    }

    private static String getName(PERK_PARAM sub, int level) {
        if (level==0)
            return  sub.name;
        return sub.name+ " " + StringMaster.getRoman(level+1);
    }

    public enum PERK_GROUP {

        FIGHTER,
        ROGUE,
        KNIGHT,
        TRICKSTER,

        SCOUT,
        RANGER,
        HERMIT,
        ACOLYTE(SPIRIT, ESSENCE, ESSENCE_REGEN, STAMINA, FOCUS_REGEN, RESISTANCE),
        ACOLYTE_MONK(SPIRIT, FOCUS_REGEN, RESISTANCE, DEFENSE, N_OF_ACTIONS, N_OF_COUNTERS),

        APOSTATE(RESISTANCE_PENETRATION, ESSENCE, ESSENCE_REGEN, FOCUS_REGEN, STARTING_FOCUS,MEMORIZATION_CAP ),
        WIZARD,


        MULTICLASS,;

        PERK_GROUP(PERK_PARAM... perks) {

        }
    }

    public enum PERK_ABILITY {
        Consume_Magic, Arcane_Fury, Mark_Sorcerer,
    }

    public enum PERK_PARAM {
        SPIRIT(5, 15, 30, false, "Indomitable"),
        RESISTANCE(5, 15, 30, false, "Resilient"),
        RESISTANCE_PENETRATION(5, 15, 30, false, "Penetrating"),
        SPELLPOWER(5, 15, 30, false, "Overpowering"),

        N_OF_COUNTERS(5, 15, 30, false, "Drilled"),
        ATTACK(5, 15, 30, false, "Accurate"),
        N_OF_ACTIONS(5, 15, 30, false, "Swift"),
        DEFENSE(5, 15, 30, false, "Nimble"),
        ARMOR(5, 15, 30, false, "Hard Skinned"),
        FORTITUDE(5, 15, 30, false, "Hardy"),

        SNEAK_ATTACK_MOD(5, 15, 30, false, "Cunning"),
        STAMINA_REGEN(5, 15, 30, false, "Crafty"),
        ESSENCE_REGEN(5, 15, 30, false, "Meditative"),
        FOCUS_REGEN(5, 15, 30, false, "Recovering"),
        STARTING_FOCUS(5, 15, 30, false, "Sharp"),

        TOUGHNESS(5, 15, 30, false, "Tough"),
        ENDURANCE(5, 15, 30, false, "Steadfast"),
        STAMINA(5, 15, 30, false, "Relentless"),
        ESSENCE(5, 15, 30, false, "Aligned"),

        CARRYING_CAPACITY(5, 15, 30, false, "Mighty"),
        ENDURANCE_REGEN(5, 15, 30, false, "Undying"),
        QUICK_SLOTS(5, 15, 30, false, "Resourceful"),
        DETECTION(5, 15, 30, false, "Observant"),
        STEALTH(5, 15, 30, false, "Surreptitious"),
        SIGHT_RANGE(5, 15, 30, false, "Sharp Eyed"),
        MEMORIZATION_CAP(5, 15, 30, false, "Prodigious"),
        DIVINATION_CAP(5, 15, 30, false, "Favored"),
        LEADERSHIP_MASTERY(5, 15, 30, false, "Sharp Eyed"),

        //sagacious
        ;

        //rolls?

        float[] values;
        boolean percentage;
        String name;

        PERK_PARAM(float value1, float value2, float value3, boolean percentage, String name) {
            this.values = new float[3];
            values[0] = value1;
            values[1] = value2;
            values[2] = value3;
            this.percentage = percentage;
            this.name = name;
        }
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
