package main.content.enums;

import main.data.ability.construct.VarHolder;
import main.system.auxiliary.StringMaster;

import java.awt.*;

/**
 * Created by JustMe on 2/14/2017.
 */
public class GenericEnums {
    public enum ASPECT {
        NEUTRAL(0, "Cosmic Crystal", "Tombstone", ""),
        ARCANUM(1, "Arcane Crystal", "Arcane Gateway", "Arcane Mastery"),
        LIFE(2, "Life Crystal", "Life Gateway", "Life Mastery"),
        DARKNESS(3, "Dark Crystal", "Shadow Gateway", "Shadow Mastery"),
        CHAOS(4, "Chaos Crystal", "Chaos Gateway", "Chaos Mastery"),
        LIGHT(5, "Lucent Crystal", "Lucent Gateway", "Holy Mastery"),
        DEATH(6, "Death Crystal", "Death Gateway", "Death Mastery"),
        // LIFE(6, "Life Crystal"),
        ;

        private int code;
        private String crystal;
        private String mastery;
        private String gateway;

        ASPECT(int code, String crystal, String gateway, String mastery) {
            this.code = code;
            this.crystal = crystal;
            this.setGateway(gateway);
            this.setMastery(mastery);
        }

        public static ASPECT getAspectByCode(int code) {
            for (ASPECT a : ASPECT.values()) {
                if (a.getCode() == code) {
                    return a;
                }
            }
            return null;
        }

        public static ASPECT getAspect(String name) {
            return valueOf(name.toUpperCase());
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        // public static ASPECTS getAspectByCode(int code){
        // return valueOf(name.toUpperCase());
        // }

        public String getCrystalName() {
            return crystal;
        }

        public String getMastery() {
            return mastery;
        }

        public void setMastery(String mastery) {
            this.mastery = mastery;
        }

        public String getGateway() {
            return gateway;
        }

        public void setGateway(String gateway) {
            this.gateway = gateway;
        }
    }

    public enum BUFF_TYPE {
        RULES, SPELL, PASSIVE, STANDARD,
        // buff-debuff into SPELL by default...
    }

    public enum DAMAGE_MODIFIER {
        VORPAL, PERIODIC, QUIET,
        UNBLOCKABLE, ENDURANCE_ONLY
    }

    public enum DAMAGE_TYPE {
        PIERCING(),
        BLUDGEONING(),
        SLASHING(),
        POISON(true),
        FIRE(true),
        COLD(true),
        LIGHTNING(true),
        ACID(true),

        SONIC(true),
        LIGHT(true),

        ARCANE(false),
        CHAOS(false),

        SHADOW(false),
        HOLY(false),
        DEATH(false),
        PSIONIC(false),

        //
        // chopping = bludg||slash ;
        PHYSICAL(false),
        PURE(false),
        MAGICAL(false),
        // ASTRAL(false),
        // ELEMENTAL(false)
        ;

        private boolean magical;
        private boolean natural;

        DAMAGE_TYPE() {

        }

        DAMAGE_TYPE(boolean natural) {
            this.setMagical(true);
            this.natural = natural;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }

        public String getResistanceName() {
            return name() + "_" + "RESISTANCE";
        }

        public boolean isMagical() {
            return magical;
        }

        public void setMagical(boolean magical) {
            this.magical = magical;
        }

        public boolean isNatural() {
            return natural;
        }

    }

    public enum RESIST_GRADE {
        Impregnable(200), Resistant(150), Normal(100), Vulnerable(50), Ineffective(0);
        private int percent;

        RESIST_GRADE(int percent) {
            this.percent = percent;
        }

        public int getPercent() {
            return percent;
        }

    }

    /*
         * 24th of April, Hour of Magic
         */
    public enum ROLL_TYPES implements VarHolder {
        MIND_AFFECTING("Willpower"),
        FAITH("Faith"),
        REFLEX("Reflex"),
        ACCURACY("Accuracy"),
        REACTION("Reaction", true),
        BODY_STRENGTH("Body Strength"),
        QUICK_WIT("Quick Wit"),
        FORTITUDE("Fortitude"),
        DISARM("Disarm"),
        MASS("Mass"),
        DETECTION("Detection"),
        STEALTH("Stealth"),
        DEFENSE("Defensive"),
        IMMATERIAL("Immaterial"),
        DISPEL("Dispel"),
        UNLOCK("Unlock"),
        DISARM_TRAP("Disarm Trap"),
        FORCE("Force"),;
        boolean logToTop;
        private String name;

        ROLL_TYPES(String s, boolean logToTop) {
            this(s);
            this.logToTop = logToTop;
        }

        ROLL_TYPES(String s) {
            if (StringMaster.isEmpty(s)) {
                s = StringMaster.getWellFormattedString(name());
            }
            this.name = s;
        }

        public String getName() {
            return name;
        }

        @Override
        public Object[] getVarClasses() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getVariableNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public Image getImage() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isLogToTop() {
            return logToTop;
        }

    }

    public enum STD_BOOLS {
        DISPELABLE,
        STACKING,
        NON_REPLACING,
        NO_FRIENDLY_FIRE,
        NO_ENEMY_FIRE,
        PERMANENT_ITEM,
        C_VALUE_OVER_MAXIMUM,
        NO_SELF_FIRE,
        SHORTEN_DIAGONALS,
        SOURCE_DEPENDENT,
        MULTI_TARGETING,
        APPLY_THRU,
        CANCELLABLE,
        ARMOR_CHANGE,
        BLOCKED,
        RANDOM,
        INVISIBLE_BUFF,
        SPECIAL_ITEM,
        SELF_DAMAGE,
        INDESTRUCTIBLE,
        INVERT_ON_ENEMY,
        BROAD_REACH,
        LEAVES_NO_CORPSE,
        PASSABLE,
        NON_DISPELABLE,
        DIVINATION_SPELL_GROUPS_INVERTED,
        WRAPPED_ITEM,
        STEALTHY_AOOS,
        DURATION_ADDED,
        UPWARD_Z,
        SPECTRUM_LIGHT,
        CANCEL_FOR_FALSE,
        BUCKLER_THROWER,
        LEFT_RIGHT_REACH,

        // TODO performance would be enhanced of course if I had real booleans
        // instead of a container to be checked.
    }
}
