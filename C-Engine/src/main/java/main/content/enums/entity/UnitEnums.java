package main.content.enums.entity;

import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.util.Map;

/**
 * Created by JustMe on 2/14/2017.
 */
public class UnitEnums {
    public enum CLASSIFICATIONS {
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
        GIANT,;
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
            return "UI\\value icons\\classifications\\" + getName() + ".jpg";

        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public boolean isDisplayed() {
            return displayed;
        }

        public void setDisplayed(boolean displayed) {
            this.displayed = displayed;
        }
    }

    public enum FACING_MUTUAL {
        FACE_TO_FACE, FROM_BEHIND, FLANKING, PARALLEL
    }

    public enum FACING_SINGLE {

        IN_FRONT, BEHIND, TO_THE_SIDE, NONE

    }

    public enum IMMUNITIES {
        MIND_AFFECTION, POISON, WOUNDS, ENSNARE, MORALE_REDUCTION, MORALE_REDUCTION_KILL,;
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
        TRANSPARENT("Transparent"),
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
        INVULNERABLE(StringMaster.getWellFormattedString("INVULNERABLE")),

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
        HOLY_PRAYER("Will restore Morale when receiving already known spells thru Divination")
        // CORVIDAE("Immune to sneak attacks"),

        ;

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
            return "UI\\value icons\\passives\\" + getName() + ".jpg";

        }

        public String getName() {
            if (name == null) {
                name = StringMaster.getWellFormattedString(name());
            }

            return name;
        }

    }

    public enum STANDARD_WOUNDS {
        MISSING_RIGHT_HAND, MISSING_LEFT_HAND, MISSING_RIGHT_EYE, MISSING_LEFT_EYE,
    }

    public enum STATUS {
        // UNIT
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

        // FATIGUED,
        // EXHAUSTED,
        // SPELL
        ILLUMINATED,

        INVISIBLE,
        SPOTTED,

        CONCEALED,
        BLOCKED,
        PREPARED, // COOLDOWN

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

        PRONE,
        BROKEN,
        UNLOCKED,
        LOCKED,
        UNCONSCIOUS,
        ENGAGED,
        VIRULENT, // cannot
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

        ;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }
    public enum COUNTER {
        Bleeding_Counter,
        Blaze_Counter,
        Poison_Counter,
        Freeze_Counter,
        Disease_Counter,
        Ensnared_Counter,
        Moist_Counter,
        Electrified_Counter{
            public boolean isNegativeAllowed() {
                return true ;
            }
        },
        Lava_Counter,
        Ash_Counter,
        Clay_Counter,
        Encase_Counter,
        Grease_Counter,
        Rage_Counter,
        Madness_Counter,
        Despair_Counter,
        Lust_Counter,
        Hatred_Counter,

        Virtue_Counter,
        Illumination_Counter,
        Concealment_Counter,
        Haze_Counter,
        Zeal_Counter,
        Encryption_Counter,
        Void_Counter,
        Magnetized,
        Time_Warped{
            public boolean isNegativeAllowed() {
                return true ;
            }
        },
        Mutagenic,
        Zen,
        Loyalty,
        Demon_debt{
            public boolean isNegativeAllowed() {
                return true ;
            }
        },
        Demon_names,
        Ward,

        Soul_Counter,
        Undying_Counter,
        Blight_Counter,
        Corrosion_Counter, Oblivion_Counter, Taint_Counter, Aether_Counter, Warp_Counter, Suffocation_Counter;

        private Map<COUNTER, COUNTER_INTERACTION> interactionMap;
        private COUNTER down;
        private COUNTER up;
        public boolean isNegativeAllowed(){
            return false;
        }

        COUNTER() {
        }

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

        public String getName() {
            return toString();
        }

        public Image getImage() {
            return ImageManager.getCounterImage(this);
        }

        public void isNegative() {
            // TODO Auto-generated method stub

        }

        public void setInteractionMap(Map<COUNTER, COUNTER_INTERACTION> interactionMap) {
            this.interactionMap = interactionMap;
        }

        public Map<COUNTER, COUNTER_INTERACTION> getInteractionMap() {
            return interactionMap;
        }

        public void setDown(COUNTER down) {
            this.down = down;
        }

        public COUNTER getDown() {
            return down;
        }

        public void setUp(COUNTER up) {
            this.up = up;
        }

        public COUNTER getUp() {
            return up;
        }
    }
    public enum COUNTER_INTERACTION {
        CONVERT_TO,  CONVERT_FROM, MUTUAL_DELETION, DELETE_OTHER, DELETE_SELF,
        TRANSFORM_UP, TRANSFORM_DOWN,
        GROW_SELF,GROW_OTHER,GROW_BOTH,
    }
        public enum COUNTER_OPERATION {
        TRANSFER_TO,
            TRANSFER_FROM,

            ;

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
        WRAITH_MONSTROCITY,;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

    }
}
