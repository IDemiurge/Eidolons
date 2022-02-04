package main.content.enums.entity;

import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.images.ImageManager;

import java.util.Map;

/**
 * Created by JustMe on 2/14/2017. Contains all enums related to Units
 */
public final class UnitEnums {
    public enum CLASSIFICATIONS {
        BOSS(true),
        MECHANICAL(true),
        UNDEAD(true),
        SKELETAL,
        DEMON(true),
        HUMANOID(true),
        MONSTER,
        OUTSIDER,
        PSIONIC,
        ANIMAL,
        WRAITH,
        HERO(true),
        STRUCTURE(true),

        ELEMENTAL,
        CONSTRUCT(true),
        TALL(true),
        HUGE(true),
        SMALL(true),
        SHORT(true),
        ATTACHED,
        INSECT,
        REPTILE,
        AVIAN,
        GIANT, UNIQUE;
        boolean displayed;
        private String toolTip;

        CLASSIFICATIONS() {

        }

        CLASSIFICATIONS(boolean displayed) {
            this.displayed = displayed;
        }

        public String getToolTip() {
            if (toolTip == null) {
                return getName();
            }
            return toolTip;
        }

        public void setToolTip(String toolTip) {
            this.toolTip = toolTip;
        }

        public String getImagePath() {
            return "ui/value icons/classifications/" + getName() + ".jpg";

        }

        public String getName() {
            return StringMaster.format(name());
        }

        public boolean isDisplayed() {
            return displayed;
        }

        public void setDisplayed(boolean displayed) {
            this.displayed = displayed;
        }
    }

    public enum IMMUNITIES {
        MIND_AFFECTION, POISON, WOUNDS, ENSNARE, MORALE_REDUCTION, MORALE_REDUCTION_KILL,
        ;
    }

    public enum STANDARD_PASSIVES {
        /**
         *
         */
        BERSERKER("Wounds will only enrage this one!"), // berserk mode + apts!
        BLOODTHIRSTY("Fallen comrades will only spur this one into action!"),
        COLD_BLOODED("Fallen comrades will not discourage this unit and Lust, Hatred or Despair have no effect on it"),
        DISPASSIONATE("This unit is unaffected by any Morale effects"),
        FEARLESS("This unit is unaffected by negative Morale effects"),

        RELENTLESS("This unit is unaffected by Stamina effects"),
        ZOMBIFIED("This unit is unaffected by Focus effects"),
        SOULLESS("Will not provide Soul counters"), //
        FLESHLESS("Poison, disease and bleeding have no effect on this unit"),

        BLIND_FIGHTER("Concealed and Invisible units do not receive bonuses when fighting this unit"),
        NON_OBSTRUCTING("Non obstructing"),

        DARKVISION("Darkvision"),
        EYES_OF_LIGHT("Can see through Blinding Light"),
        AUTOMATA("Automata - does not restore action points between rounds"),
        TRAMPLE("Trample"),
        IMMATERIAL("Immaterial - material attackers must win a roll of Willpower against Spellpower to hit this unit"), // TODO
        // prevent
        // from
        // Wounds/Bleeding!
        VIGILANCE("Vigilance"),
        CHARGE("Charge"),
        DEXTEROUS("Dexterous units do not trigger Attacks of Opportunities when moving"),
        AGILE("Agile units retain clear shot through non-Huge obstacles"),
        // DE("Agile"),
        BLUDGEONING("Bludgeoning - Endurance damage this unit deals in melee is not reduced by armor"),
        FLYING("Flying - may pass over battlefield objects and receives 25% bonus against non-Flyers"),
        DOUBLE_RETALIATION("Double Retaliation"),
        DOUBLE_STRIKE("Double Strike"),
        FIRST_STRIKE("First Strike"),
        NO_RETALIATION("No Retaliation"),
        NO_MELEE_PENALTY("No Melee Penalty"),
        CLEAVE("CLEAVE"),
        TRUE_STRIKE("True Strike"),
        INDESTRUCTIBLE("Indestructible"),
        INVULNERABLE(StringMaster.format("INVULNERABLE")),

        // NEW
        UNLIMITED_RETALIATION("Unlimited Retaliation"),
        OPPORTUNIST("Opportunist "),
        BROAD_REACH("Can attack melee targets to the sides"),
        HIND_REACH("Can attack melee targets behind"),
        CRITICAL_IMMUNE("Immune to Critical Hits"),
        SNEAK_IMMUNE("Immune to Sneak Attacks"),
        MIND_AFFECTING_IMMUNE("Immune to Mind-Affecting"),

        CLEAVING_COUNTERS("Can cleave with counter attacks"),

        CLEAVING_CRITICALS("Critical attacks are always Slashing and may Cleave"),

        WEAKENING_POISON("Will lose stamina per turn for every Poison counter"),
        HALLUCINOGETIC_POISON("Critical attacks are always Slashing and may Cleave"),
        FAVORED("This unit receives full bonus of its deity"),

        SHORT("Non-Short units do not get their vision or missile attacks obstructed by this unit"),
        TALL("Does not get its vision or missile attacks obstructed by non-Tall units"),
        HUGE(""),
        SMALL("Will not block vision and missiles for non-Small units"),
        CLUMSY("This unit has difficulty with diagonal leaps"),
        DRUIDIC_VISIONS("Instead of adding Favor buff, will restore Essence when receiving already known spells thru Divination"),
        HOLY_PRAYER("Will restore Morale when receiving already known spells thru Divination"),
        // CORVIDAE("Immune to sneak attacks"),
        VOIDWALKER("Can traverse Void cells"),
        IMMOBILE("Cannot act");


        static {
            DARKVISION.setToolTip("Unit is unaffected by Concealment penalties");
            NON_OBSTRUCTING
                    .setToolTip("Non obstructing units do not block other units' vision or missile targeting");
            NO_RETALIATION.setToolTip("Cannot be counter-attacked by units without Vigilance");
            OPPORTUNIST
                    .setToolTip("Will not trigger Attack of Opportunity when using items or casting spells");
            VIGILANCE
                    .setToolTip("Unit is always On Alert and will counter attack units with No Retaliation");
        }

        boolean var;
        private String name;
        private String toolTip;

        STANDARD_PASSIVES(String toolTip) {
            setToolTip(toolTip);
        }

        public String getToolTip() {
            return toolTip;
        }

        public void setToolTip(String toolTip) {
            this.toolTip = toolTip;
        }

        public String getImagePath() {
            return "ui/value icons/passives/" + getName() + ".jpg";

        }

        public String getName() {
            if (name == null) {
                name = StringMaster.format(name());
            }

            return name;
        }

    }

    public enum STATUS {
        // UNIT
        SNEAKING,
        AIRBORNE,
        DEAD,
        LATE,
        DEFENDING,
        WAITING,
        IMMOBILE,
        SILENCED,
        CHARMED,
        POISONED,
        WOUNDED,
        CRITICALLY_WOUNDED,
        FATIGUED,
        EXHAUSTED,
        DISCOMBOBULATED,
        HAZED,
        GUARDING,

        // FATIGUED,
        // EXHAUSTED,
        // SPELL
        ILLUMINATED,

        INVISIBLE,
        SPOTTED,

        CONCEALED,
        BLOCKED,
        PREPARED,

        // OBJ
        CLAIMED,
        CHANNELED,
        // terrain

        TRAPPED,
        BLEEDING,
        FROZEN,
        RUNNING_LATE,
        ON_ALERT,
        ABLAZE,
        CONTAMINATED,
        ASLEEP,
        HIDDEN,
        FREEZING,
        SOAKED,
        ENSNARED,
        REVEALED,
        PRONE,
        BROKEN,
        UNLOCKED,
        LOCKED,
        UNCONSCIOUS,
        UNDYING,
        ENGAGED,
        VIRULENT,
        DISABLED, //("Is not active now")

        // cannot
        // act/counter,
        // gets
        // -75%
        // defense,
        // loses
        // armor
        // //
        // ever-sneak; loses Height obstruction
        // next time prone unit 'moves', it auto-starts a channeling
        // "getting up" which is a Move Action with
        // 4 / 4 ap/sta. If not enough Stamina... Or AP...
        // based on Weight! versus Strength...

        //

        // seems similar to Ensnare - must spend X ap/sta to get rid of the
        // stuff...

        OFF;


        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum STD_UNDEAD_TYPES {
        GHOST,
        GHOUL,
        LICH,
        SKELETON,
        VAMPIRE,
        WRAITH_LORD,
        ZOMBIE,
        WRAITH_BEAST,
        ZOMBIE_BEAST,
        UNDEAD_BEAST,
        SKELETAL_BEAST,
        VAMPIRE_BEAST,
        WRAITH_MONSTROCITY,
        ;

        public String toString() {
            return StringMaster.format(name());
        }

    }

    // PERHAPS ENCOUNTERS COULD HAVE DEITY PROPERTY AS WELL;
    public enum UNIT_GROUP {
        Ravenguard("Traitor,Corrupted,Royal"),
        PRISONERS,
        HUMANS("Militia,Scum,Guards,Army,"),
        BANDITS("Pirates,Thieves Guild,Robbers,"),
        HUMANS_KNIGHTS("ravenguard,holy,"),
        HUMANS_CRUSADERS,
        PIRATES,
        HUMANS_BARBARIANS,

        DWARVEN_SCUM(),
        DWARVEN_LORDS(),
        DWARVES("forsworn,clansmen,"),
        NORTH("norse,woads,brutes,north"),

        ORCS("goblins,orcs"),
        PALE_ORCS,
        TUTORIAL,
        UNDEAD("Plague,Crimson,Wraith,Pale"),
        UNDEAD_PLAGUE("Plague,Crimson,Wraith,Pale"),
        UNDEAD_CRIMSON("Plague,Crimson,Wraith,Pale"),
        UNDEAD_WRAITH("Plague,Crimson,Wraith,Pale"),

        DEMONS("chaos,abyss,demons,demon worshippers"),
        DEMONS_HELLFIRE("chaos,abyss,demons,demon worshippers"),
        DEMONS_ABYSS("chaos,abyss,demons,demon worshippers"),
        DEMONS_WARPED("chaos,abyss,demons,demon worshippers"),

        MAGI,
        MISTSPAWN,
        CULT_CONGREGATION,
        CULT_CERBERUS("constructs,apostates,magi,,"),
        CULT_DEATH,
        CULT_DARK,
        CULT_CHAOS,

        ELEMENTALS(""),
        CONSTRUCTS,
        DARK_ONES,
        MUTANTS,
        CELESTIALS,

        CRITTERS("critters,spiders,nocturnal"),
        CRITTERS_SPIDERS("critters,spiders,nocturnal"),
        CRITTERS_COLONY,
        DUNGEON("chaos,demons,worshippers,"),


        FOREST("greenies,creatures"),
        ANIMALS("Animals,Wolves,Wargs,wild,"),
        REPTILES, BANDIT_SCUM(), SPIDERS,
        ;

        private final String subgroups;

        UNIT_GROUP(String groups) {
            this.subgroups = groups;
        }

        UNIT_GROUP() {
            this.subgroups = name();
        }

        public String getSubgroups() {
            return subgroups;
        }

    }



    public enum UNIT_GROUPS implements OBJ_TYPE_ENUM {
        //generate!
        ANIMALS,
        APOSTATES,
        ASSEMBLY_OF_MAGI,
        BANDITS,
        BLOOD,
        BRUTES,
        CELESTIALS,
        CHAOS,
        COLONY,
        CONSTRUCTS,
        CRITTERS,
        DEMON_WORSHIPPERS,
        DEMONS,
        DUNGEON,
        FOREST_CREATURES,
        GOBLINS,
        GREENIES,
        GUARDIANS,
        GUARDS,
        HOLY,
        MELEE,
        MERCS_EASY,
        MERCS_FULL,
        MILITIA,
        MUTANTS,
        NIGHT,
        NORSE,
        NORTH,
        ORCS,
        PIRATES,
        PLAGUE,
        RAVENGUARD_FULL,
        RAVENGUARD,
        ROBBERS_EASY,
        ROBBERS,
        SCUM,
        SOLDIERS_FULL,
        SPIDERS,
        THIEVING_CREW,
        TWILIGHT,
        UNDEAD_ADEPT,
        VAMPIRE,
        WARGS,
        WILD,
        WOADS,
        WOLVES,
        DWARVES
    }
}
