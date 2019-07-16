package main.content.enums;

import main.data.ability.construct.VarHolder;
import main.system.auxiliary.StrPathBuilder;
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
        RULES, SPELL, PASSIVE, STANDARD, OTHER,
        // buff-debuff into SPELL by default...
    }

    public enum DAMAGE_CASE {
        SPELL,
        ATTACK,
        ACTION,
    }

    public enum DAMAGE_MODIFIER {
        VORPAL, PERIODIC, QUIET,
        UNBLOCKABLE, ENDURANCE_ONLY,
        ARMOR_AVERAGED
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
        PHYSICAL(),
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
    public enum DIFFICULTY {
        NEOPHYTE(33, 25 ),
        NOVICE(50, 35 ),
        DISCIPLE(65, 50,true ),
        ADEPT(80, 75,true),
        CHAMPION(100, 100,true),
        AVATAR(125, 150,true);

//        public int glory_coef;

        private int attributePercentage;
        private int masteryPercentage;
        private boolean enemySneakAttacksOn;

        DIFFICULTY(int attributePercentage, int masteryPercentage, boolean enemySneakAttacksOn) {
            this.attributePercentage = attributePercentage;
            this.masteryPercentage = masteryPercentage;
            this.enemySneakAttacksOn = enemySneakAttacksOn;
        }

        DIFFICULTY(int attributePercentage, int masteryPercentage) {
            this.attributePercentage = attributePercentage;
            this.masteryPercentage = masteryPercentage;
        }

        public int getAttributePercentage() {
            return attributePercentage;
        }

        public int getMasteryPercentage() {
            return masteryPercentage;
        }

        public boolean isEnemySneakAttacksOn() {
            return enemySneakAttacksOn;
        }

        public void setEnemySneakAttacksOn(boolean enemySneakAttacksOn) {
            this.enemySneakAttacksOn = enemySneakAttacksOn;
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
        FORCE("Force"), HEARING("Hearing");
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
        PERCENT_MOD,
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
        INVULNERABLE,
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
        FAUX,
        NO_GOLD, NAMED, BUFFING, OFF_DEFAULT, LIVING_STATUE;

        // TODO performance would be enhanced of course if I had real booleans
        // instead of a container to be checked.
    }

    /**
     * Created by JustMe on 6/28/2018.
     */
    public enum VFX {
    //    IMPACT_demonology,
    //    IMPACT_scare,
    //    IMPACT_psychic,
    //    CAST_darkness,
    //    CAST_black_hand,
    //    CAST_black_hand2,
    //    CAST_black_hand3,
    //    CAST_blindness,
    //
    //    CAST_dark_shapes,
    //
    //    CAST_drain_focus,
    //    CAST_drain_focus2,
    //    CAST_drain_focus3,
    //
    //    CAST_celestial1,
    //    CAST_celestial2,
    //    CAST_celestial3,

        dissipation("spell/shape/soul dissipation"),
        dissipation_pale("spell/shape/soul dissipation pale"),
        soul_bleed("unit/black soul bleed 3"),
        darkness("unit/chaotic dark"),

        DARK_MIST("mist","dark mist2"),
        DARK_MIST_LITE("mist","dark mist2 light"),
        SMOKE_TEST("Smoke_Test1.pt"),
        DARK_SOULS("dark souls"),
        DARK_SOULS2("dark souls2"),
        DARK_SOULS3("dark souls3"),
        SKULL("skulls"),
        SKULL2("skulls2"),
        SKULL3("skulls3"),

        //TODO sub-emitters

        SNOW("snow","snow"),
        SNOW_TIGHT("snow","snow tight"),
        SNOW_TIGHT2("snow","snow tight2"),
        SNOWFALL_SMALL("snow","snowfall small"),
        SNOWFALL("snow","snowfall"),
        SNOWFALL_THICK("snow","snowfall thick"),
        WISPS("woods","wisps"),
        LEAVES("woods","leaves"),
        STARS("woods","stars"),
        FALLING_LEAVES("woods","falling leaves"),
        LEAVES_LARGE("woods","leaves large"),
        FALLING_LEAVES_WINDY("woods","falling leaves windy2"),

        MIST_WHITE("mist","conceal west wind"),
        MIST_WHITE2("mist","conceal west wind2"),
        MIST_WHITE3("mist","conceal west wind3"),
        MIST_WIND("mist","white mist wind"),
        MIST_COLD("mist","cold wind"),
        MIST_CYAN("mist","cyan mist2"),
        MIST_SAND_WIND("mist","sand wind"),

        MIST_BLACK("ambient", "MIST ARCANE"), //"black mist","clouds wind light2"),
        MIST_TRUE("mist","MIST TRUE"),
        MIST_TRUE2("mist","MIST TRUE2"),
        MIST_ARCANE("ambient", "MIST ARCANE"),
        MIST_NEW("ambient", "MIST NEW2"),
        THUNDER_CLOUDS("ambient", "THUNDER CLOUDS"),
        THUNDER_CLOUDS_CRACKS("ambient", "thunder clouds with cracks"),
        FLIES("ambient", "flies2"),
        MOTHS("ambient", "MOTHS"),
        MOTHS_BLUE("ambient", "MOTHS BLUE TIGHT"),
        MOTHS_BLUE2("ambient", "MOTHS BLUE TIGHT2"),
        MOTHS_BLUE3("ambient", "MOTHS BLUE TIGHT3"),
        MOTHS_GREEN("ambient", "MOTHS GREEN"),
        MOTHS_TIGHT("ambient", "MOTHS TIGHT"),
        MOTHS_TIGHT2("ambient", "MOTHS TIGHT2"),
        POISON_MIST("ambient", "POISON MIST"),
        POISON_MIST2("ambient", "POISON MIST2"),
        ASH("ambient", "ash falling"),
        CINDERS("ambient", "CINDERS tight"),
        CINDERS2("ambient", "CINDERS tight2"),
        CINDERS3("ambient", "CINDERS tight3"),
        SMOKE,
        ;
        private static final boolean NEW_MIST =false ;
        public String path;

        VFX() {
            String[] parts = name().split("_");
            String realName = name().replace(parts[0], "").replace("_", " ").trim();
            this.setPath(StrPathBuilder.build(
             parts[0], realName));
        }

        VFX(String... pathParts) {
            this.setPath(StrPathBuilder.build(pathParts));
            if (NEW_MIST){
                if (pathParts[0].equalsIgnoreCase("mist")) {
                    setPath("mist/a new subtle mist 2");
                }
            }
        }


        public boolean isPreloaded() {
            return true;
        }

        public boolean isAtlas() {
            return true;
        }

        public String getPath() {
            if (isAtlas()) {
                return StrPathBuilder.build("atlas", path);
            }
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
