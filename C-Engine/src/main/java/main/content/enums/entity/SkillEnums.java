package main.content.enums.entity;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/14/2017.
 */
public class SkillEnums {
    public enum ATTRIBUTE {
        STRENGTH,
        VITALITY,
        AGILITY,
        DEXTERITY,
        WILLPOWER,
        INTELLIGENCE,
        WISDOM,
        KNOWLEDGE,
        SPELLPOWER,
        CHARISMA,;
    }

    public enum MASTERY {
        PSYCHIC_MASTERY,
        SHADOW_MASTERY,
        WITCHERY_MASTERY,
        FIRE_MASTERY,
        AIR_MASTERY,
        WATER_MASTERY,
        EARTH_MASTERY,
        REDEMPTION_MASTERY,
        BENEDICTION_MASTERY,
        CELESTIAL_MASTERY,

        CONJURATION_MASTERY,
        SORCERY_MASTERY,
        ENCHANTMENT_MASTERY,

        WARP_MASTERY,
        DESTRUCTION_MASTERY,
        DEMONOLOGY_MASTERY,

        AFFLICTION_MASTERY,
        BLOOD_MAGIC_MASTERY,
        NECROMANCY_MASTERY,

        ELEMENTAL_MASTERY,
        SAVAGE_MASTERY,
        SYLVAN_MASTERY,

        VOID_MASTERY,

        WIZARDRY_MASTERY,
        SPELLCRAFT_MASTERY,
        DIVINATION_MASTERY,

        BLADE_MASTERY,
        BLUNT_MASTERY,
        AXE_MASTERY,
        POLEARM_MASTERY,
        UNARMED_MASTERY,
        TWO_HANDED_MASTERY,
        DUAL_WIELDING_MASTERY,

        SHIELD_MASTERY,
        ARMORER_MASTERY,
        DEFENSE_MASTERY,

        STEALTH_MASTERY,
        DETECTION_MASTERY,
        MOBILITY_MASTERY,
        ATHLETICS_MASTERY,
        MEDITATION_MASTERY,
        DISCIPLINE_MASTERY,
        MARKSMANSHIP_MASTERY,
        ITEM_MASTERY,
        WARCRY_MASTERY;

        MASTERY() {

        }

    }

    public enum MASTERY_RANK {
        NONE(0),
        NOVICE(1),
        APPRENTICE(5),
        ADEPT(10),
        ADVANCED(15),
        SPECIALIST(20),
        EXPERT(30),
        MASTER(40),
        GRAND_MASTER(50),;
        private int masteryReq;

        MASTERY_RANK(int masteryReq) {
            this.masteryReq = masteryReq;
        }

        public int getMasteryReq() {
            return masteryReq;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(toString());
        }
    }

    public enum SKILL_GROUP {
        BODY_MIND, SPELLCASTING, WEAPONS, OFFENSE, DEFENSE, MISC,

        CRAFT,

        ARCANE_ARTS, LIFE_ARTS, DARK_ARTS, CHAOS_ARTS, HOLY_ARTS, DEATH_ARTS,

        // STEALTH, DETECTION, ++ BOW/CROSSBOW
    }

    public enum SKILL_TYPE {
        MASTERY, SPECIAL, TRAIT, PRINCIPLE
    }
}
