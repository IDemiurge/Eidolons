package main.content.enums.entity;

import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;

/**
 * Created by Alexander on 2/1/2022
 */
public class PerkEnums {
    public enum PERK_PASSIVE {
        VIGILANT,
        OPPORTUNISTIC,
        VENGEFUL,
        DISPASSIONATE,
        BLOODTHIRSTY,
        DARKVISION,
        DEXTEROUS,
        AGILE,
        FIRST_STRIKE,
        NO_RETALIATION,
        BERSERKER,
        // RELENTLESS("This unit is unaffected by Stamina effects"),
        // ZOMBIFIED("This unit is unaffected by Focus effects"),
        // SOULLESS("Will not provide Soul counters"), //
        // FLESHLESS("Poison, disease and bleeding have no effect on this unit"),
        // BLIND_FIGHTER("Concealed and Invisible units do not receive bonuses when fighting this unit"),
        // CHARGE("Charge"),
        // TRUE_STRIKE("True Strike"),
        // BROAD_REACH("Can attack melee targets to the sides"),
        // HIND_REACH("Can attack melee targets behind"),
        // CRITICAL_IMMUNE("Immune to Critical Hits"),
        // SNEAK_IMMUNE("Immune to Sneak Attacks"),
        // MIND_AFFECTING_IMMUNE("Immune to Mind-Affecting"),
        // FAVORED("This unit receives full bonus of its deity"),
        // SHORT("Non-Short units do not get their vision or missile attacks obstructed by this unit"),
        // TALL("Does not get its vision or missile attacks obstructed by non-Tall units"),
        // HUGE("")
    }

    public enum PERK_PARAM {
        SPIRIT(3, 7, 15, false, "Indomitable"),
        GRIT(3, 7, 15, false, "Relentless"),
        FORTITUDE(3, 7, 15, false, "Hardy"),
        REFLEX(3, 7, 15, false, "Adroit"),
        WIT(3, 7, 15, false, "Witty"),
        LUCK(3, 7, 15, false, "Lucky"),

        Q_SPIRIT(3, 7, 15, false, "Sycophantic"),
        Q_GRIT(3, 7, 15, false, "Daisy"),
        Q_FORTITUDE(3, 7, 15, false, "Wispy"),
        Q_REFLEX(3, 7, 15, false, "Stiff"),
        Q_WIT(3, 7, 15, false, "Dull"),
        Q_LUCK(3, 7, 15, false, "Unlucky"),

        RESISTANCE(10, 20, 32, false, "Resilient"),
        SPELLPOWER(3, 7, 16, false, "Overpowering"),

        Q_RESISTANCE(10, 20, 32, false, "Vulnerable"),
        Q_SPELLPOWER(3, 7, 16, false, "Feeble"),

        EXTRA_ATTACKS(3, 6, 10, false, "Bold"),
        FREE_MOVES(3, 6, 10, false, "Springy"),

        Q_EXTRA_ATTACKS(3, 6, 10, false, "Reserved"),
        Q_FREE_MOVES(3, 6, 10, false, "Heavy-Footed"),

        INITIATIVE(1, 2, 3, false, "Swift"),
        ATTACK(14, 40, 110, false, "Accurate"),
        DEFENSE(12, 35, 100, false, "Nimble"),
        ARMOR(6, 24, 80, false, "Hard Skinned"),

        Q_INITIATIVE(1, 2, 3, false, "Slow"),
        Q_ATTACK(14, 40, 110, false, "Cannon-shot"),
        Q_DEFENSE(12, 35, 100, false, "Clumsy"),
        Q_ARMOR(6, 24, 80, false, "Thin Skinned"),


        TOUGHNESS(30, 100, 250, false, "Tough"),
        ENDURANCE(75, 250, 650, false, "Steadfast"),
        ESSENCE(45, 110, 320, false, "Aligned"),
        STARTING_FOCUS(7, 15, 30, false, "Focused"),
        FOCUS_SPEED(1, 2, 3, false, "Adaptive"),

        Q_TOUGHNESS(30, 100, 250, false, "Fragile"),
        Q_ENDURANCE(75, 250, 650, false, "Faint"),
        Q_ESSENCE(45, 110, 320, false, "Unquiet"),
        Q_STARTING_FOCUS(7, 15, 30, false, "Distracted"),
        Q_CARRYING_CAPACITY(115, 300, 700, false, "Weak"),
        Q_DETECTION(25, 65, 125, false, "Inattentive"),
        Q_STEALTH(8, 22, 50, false, "Thunderous"),
        Q_FOCUS_SPEED(1, 2, 3, false, "Rigid"),

        CARRYING_CAPACITY(115, 300, 700, false, "Mighty"),
        DETECTION(25, 65, 125, false, "Observant"),
        STEALTH(8, 22, 50, false, "Surreptitious"),

        //UNIQUE PERKS
        RESISTANCE_PENETRATION(21, 42, 70, false, "Theurge"),
        BLOCK_CHANCE(6, 24, 80, false, "Crafty"),
        LEADERSHIP_MASTERY(10, 22, 40, false, "Confident"),

        KNOWLEDGE_CAP(3, 8, 18, false, "Erudite"),
        MEMORIZATION_CAP(14, 35, 80, false, "Prodigious"),
        DIVINATION_CAP(14, 35, 80, false, "Favored"),
        ESSENCE_ABSORPTION(11, 25, 60, false, "Meditative"),

        ARMOR_PENETRATION(6, 24, 80, false, "Deadly"),
        QUICK_SLOTS(2, 5, 10, false, "Resourceful"),
        SNEAK_ATTACK_MOD(25, 40, 75, false, "Cunning"),
        SIGHT_RANGE(1, 2, 3, false, "Sharp Eyed"),


        //sagacious
        ;

        //rolls?

        public PARAMETER param;
        public float[] values;
        public boolean percentage;
        public String name;

        PERK_PARAM(float value1, float value2, float value3, boolean percentage, String name) {
            this.values = new float[3];
            values[0] = value1;
            values[1] = value2;
            values[2] = value3;
            this.percentage = percentage;
            this.name = name;

        }

        public PARAMETER getParam() {
            param = ContentValsManager.getPARAM(name());
            return param;
        }
    }
}
