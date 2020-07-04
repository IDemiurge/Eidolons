package main.content.enums;

import com.badlogic.gdx.graphics.GL20;
import main.data.ability.construct.VarHolder;
import main.data.filesys.PathFinder;
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
        private final String crystal;
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
            return StringMaster.format(name());
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
        NEOPHYTE(33, 25),
        NOVICE(50, 35),
        DISCIPLE(65, 50, true),
        ADEPT(80, 75, true),
        CHAMPION(100, 100, true),
        AVATAR(125, 150, true);

//        public int glory_coef;

        private final int attributePercentage;
        private final int masteryPercentage;
        private boolean enemySneakAttacksOn;
        private int powerPercentage=100;

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

        public int getPowerPercentage() {
            return powerPercentage;
        }

        public void setPowerPercentage(int powerPercentage) {
            this.powerPercentage = powerPercentage;
        }
    }

    public enum RESIST_GRADE {
        Impregnable(200), Resistant(150), Normal(100), Vulnerable(50), Ineffective(0);
        private final int percent;

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
        private final String name;

        ROLL_TYPES(String s, boolean logToTop) {
            this(s);
            this.logToTop = logToTop;
        }

        ROLL_TYPES(String s) {
            if (StringMaster.isEmpty(s)) {
                s = StringMaster.format(name());
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
        IMPASSABLE,
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
        NO_GOLD, NAMED, BUFFING, OFF_DEFAULT, LIVING_STATUE, SWIMMING, INVISIBLE

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

        //    CAST_black_hand2f,
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
        /*


         */

        FULLSCREEN_FOG("ambient/fullscreen/fullscreen fog"),
        FULLSCREEN_SNOW("ambient/fullscreen/fullscreen snow"),
        FULLSCREEN_LEAVES("ambient/fullscreen/fullscreen leaves"),

        CAST_dark("spell/cast/witchery circle slow6"),
        CAST_dark2("spell/cast/shadow center2"),
        CAST_dark3("spell/cast/blood circle slow3"),
        CAST_dark4("spell/cast/new storm"),
        CAST_dark5("spell/cast/witchery circle slow4"),
        cold_cast("spell/cast/cold cast"),

        weave_nether("spell/weave/nether weave"),
        missile_nether("spell/missile/nether missile"),
        missile_nether_nox("spell/missile/nether missile nox"),
        missile_electric("spell/missile/electric missile"),
        missile_electric_intense("spell/missile/electric missile intense2"),
//        missile_electric_intense("spell/missile/electric missile intense2"),
//        missile_electric("spell/missile/electric missile"),

        weave_arcane("spell/weave/arcane weave"),
        missile_arcane("spell/missile/arcane missile"),
        weave_arcane_pink("spell/weave/arcane weave pink"),
        missile_arcane_pink("spell/missile/arcane missile pink"),
        weave_arcane_pink2("spell/weave/arcane weave pink2"),
        missile_arcane_intense("spell/missile/arcane missile intense"),

        weave_death("spell/weave/death weave"),
        missile_death("spell/missile/death missile"),

        weave_warp("spell/weave/warp weave"),
        missile_warp("spell/missile/warp missile"),

        weave_chaos("spell/weave/holy weave red"),
        missile_chaos("spell/missile/chaos missile"),

        weave_pale("spell/weave/pale weave"),
        missile_pale("spell/missile/pale missile"),



        nether_impact3("spell/impact/nether impact3"),
        nether_impact2("spell/impact/nether impact2"),
        nether_impact("spell/impact/nether impact"),
        chaos_impact("spell/impact/chaos impact"),
        cold_impact("spell/impact/cold impact"),
        pale_impact("spell/impact/pale impact"),
        warp_impact("spell/impact/warp impact"),
        necro_impact("spell/impact/necro impact"),
        death_impact("spell/impact/death impact"),
        frost_impact("spell/impact/frost impact"),
        acid_impact("spell/impact/acid impact"),
        arcane_impact("spell/impact/arcane impact"),

        invert_fountain("invert/black fountain"),
        invert_storm("invert/shadow storm"),
        invert_storm_green("invert/shadow storm nether"),
        invert_storm_brewing("invert/shadow storm brewing"),
        invert_storm_ambi ("invert/shadow storm nether ambi2"),
        invert_vortex("invert/shadow vortex2"),

        invert_abyss("invert/black abyss"),
        invert_missile("invert/black missile cone 3"),
        invert_impact("invert/shadow impact"),
        invert_pillar("invert/black pillar high"),
        invert_bleed("invert/black soul bleed 3"),
        invert_ring("invert/black water ring max2"),
        invert_bloody_bleed2("invert/bloody bleed2"),
        invert_breath("invert/breath"),
        invert_darkness("invert/chaotic dark"),

        spell_chaos_flames("ambient/sprite/fires/chaos flames"),
        spell_demonfire("spell/missile/demon missile2"),
        spell_firewave("ambient/sprite/fires/fire wave chaos"),
        spell_volcano("ambient/sprite/fires/volcano"),
        spell_hollow_flames("ambient/sprite/fires/hollow flames"),

//        spell_chaos_flames("spell/nether/chaos flames"),
//        spell_demonfire("spell/nether/demon missile2"),
//        spell_firewave("spell/nether/fire wave chaos"),
//        spell_volcano("spell/nether/volcano"),
//        spell_hollow_flames("spell/nether/hollow flames"),

        dark_blood("invert/dark_blood"),
        dark_impact("invert/dark_impact"),


        spell_wraiths("spell/nether/wraiths 2"),

        spell_poison_veil("spell/nether/poison"),
        spell_teleport_fade("spell/nether/wind fade swirl chaos 2"),
        spell_fireball("spell/nether/chaos fireball"),
        spell_cold("spell/nether/cold 3"),
        spell_pale_ward("spell/nether/pale ward"),
        spell_ghostly_teleport("spell/nether/ghostly teleport"),
        spell_ghostly_teleport_small("spell/nether/ghostly teleport small wraith"),

        dissipation("spell/shape/soul dissipation"),
        dissipation_pale("spell/shape/soul dissipation pale"),
        soulflux_continuous("ambient/soulflux continuous"),
        soul_bleed("invert/black soul bleed 3"),
        soul_bleed_red("invert/bloody bleed2"),
        darkness("invert/chaotic dark"),

        DARK_MIST("mist", "dark mist2"),
        DARK_MIST_LITE //("mist", "dark mist2 light"),
         ("mist", "conceal west wind"),
        SMOKE_TEST("Smoke_Test1.pt"),
        DARK_SOULS("dark souls"),
        DARK_SOULS2("dark souls2"),
        DARK_SOULS3("dark souls3"),
        SKULL("skulls"),
        SKULL2("skulls2"),
        SKULL3("skulls3"),

        //TODO sub-emitters

        SNOW("snow", "snow"),
        SNOW_TIGHT("snow", "snow tight"),
        SNOW_TIGHT2("snow", "snow tight2"),
        SNOWFALL_SMALL("snow", "snowfall small"),
        SNOWFALL("snow", "snowfall"),
        SNOWFALL_THICK("snow", "snowfall thick"),
        WISPS("woods", "wisps"),
        LEAVES("woods", "leaves"),
        STARS("woods", "stars"),
        FALLING_LEAVES("woods", "falling leaves"),
        LEAVES_LARGE("woods", "leaves large"),
        FALLING_LEAVES_WINDY("woods", "falling leaves windy2"),

        BLACK_MIST_white_mist_wind("black mist", StringMaster.format("white_mist_wind")),
        BLACK_MIST_clouds_wind("black mist", StringMaster.format("clouds_wind")),
        BLACK_MIST_clouds_gravity("black mist", StringMaster.format("clouds_gravity")),
        BLACK_MIST_clouds_antigravity("black mist", StringMaster.format("clouds_antigravity")),

        MIST_WHITE("mist", "conceal west wind"),
        MIST_WHITE2("mist", "conceal west wind2"),
        MIST_WHITE3("mist", "conceal west wind3"),
        MIST_WIND("mist", "white mist wind"),
        MIST_COLD("mist", "cold wind"),
        MIST_CYAN("mist", "cyan mist2"),
        MIST_SAND_WIND("mist", "sand wind"),

        MIST_BLACK("ambient", "MIST ARCANE"), //"black mist","clouds wind light2"),
        MIST_TRUE("mist", "TRUE MIST"),
        MIST_TRUE2("mist", "TRUE MIST2"),
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
        SMOKE, DUMMY("dummy");
        private static final boolean NEW_MIST = false;
        public String path;

        VFX() {
            String[] parts = name().split("_");
            String realName = name().replace(parts[0], "").replace("_", " ").trim();
            this.setPath(StrPathBuilder.build(
                    parts[0], realName));
        }

        VFX(String... pathParts) {
            this.setPath(StrPathBuilder.build(pathParts));
            if (NEW_MIST) {
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

    public enum SOUND_CUE {
//        wimper,

        //missing
        chant_atmo,
        //awakening?
        mute_scream,
        fire_burst,
        breathing,
        heartbeat,
        dark_knight,
        dream,
        demon_growl,
        laugh,
        dark_laughter,
        slam,
        whispers,
        dark_tension,
        aether_thunder,

        portal_open,
        portal_close,
        screams,
        gong,
        ghost,
        inferno_atmo,
        windy;

        public String getPath() {
            return PathFinder.getSoundCuesPath() +
                    name().replace("_", " ").toLowerCase()
                    + ".mp3"
                    ;
        }

    }

    public enum ALPHA_TEMPLATE {

        GRID_LIGHT(0.19f, 1.32f, 1, 0.55f, 0.32f, 0.62f),

        HQ_HERO_SPRITE(0.08f, 0.5f, 0.2f, 0.4f, 0.6f, 1f),
        HQ_SPRITE(0.05f, 0.5f, 0.2f, 0, 0.2f, 0.7f),

        MOON(0.1f, 0, 1, 0.5f),
        SUN(0.1f, 0, 5, 0.5f, 0.7f, 1f),
        TOP_LAYER(0.2f, 1, 2, 0.6f, 0.15f, 0.5f),
        LIGHT(0.28f, 4, 0.8f, 2.6f, 0.1f, 0.4f),

        MOONLIGHT(0.4f, 5, 0.5F, 0.6f, 0.1f, 0.9f),
        CLOUD(0.1f, 3, 2, 0.12f, 0.45f, 1f),
        THUNDER(0.3f, 10, 0.3f, 0.5f, 0.05f, 1f),
        HIGHLIGHT(0.15f, 0, 1, 0.1f, 0.15f, 1f),
        HIGHLIGHT_MAP(0.1f, 0, 1, 0.4f, 0.75f, 1f),
        HIGHLIGHT_SPEAKER(0.25f, 0, 1, 0.1f, 0.535f, 1f),

        SHARD_OVERLAY(0.325f, 0.25F, 0.5F, 0.5f, 0.75f, 1f),
        ITEM_BACKGROUND_OVERLAY(0.15f, 0, 1.25f, 0.6f, 0.70f, 1f),

        VIGNETTE(0.1f, 1, 0, 0.3f, 0.4f, 1f),
        ATB_POS(0.4f, 0, 0.5F, 0.2f, 0.6f, 1f),
        OVERLAYS(0.15f, 0, 1, 0.1f, 0.75f, 1f),
        UNIT_BORDER(0.1f, 1, 2, 0.0f, 0.50f, 1f), // + EMBLEM COLOR & UNCONSCIOUS

        SHADE_CELL_GAMMA_SHADOW(0.05f, 0.5f, 0.2f, 0, 0.5f, 0.8f),
        SHADE_CELL_GAMMA_LIGHT(0.08f, 1.5f, 2.55f, 0.2f, 0.4f, 0.85f),
        SHADE_CELL_LIGHT_EMITTER(0.10f, 1.5f, 2.5f, 0.2f, 0.85f, 1),
        LIGHT_EMITTER_RAYS(0.25f, 1.0f, 0.5f, 0.4f, 0.25f, 1.0f),

        SHADE_CELL_HIGHLIGHT(0.4f, 1.5f, 0.3f, 0.4f, 0.15f, 1),
        DOORS(0.325f, 1.25F, 0.5F, 0.5f, 0.0f, 1f),

        BLOOM(0.1f, 0F, 0.0F, 0.88f, 0.3f, 1f),
        POST_PROCESS(0.1f, 0F, 0.0F, 0.88f, 0.3f, 1f),
        WATER(0.1f, 0.5F, 0.5F, 0.88f, 0.6f, 8f),
        SOULFORCE(0.125f, 1.25F, 0.5F, 0.5f, 0.2f, 0.8f),


        ;
        public float alphaStep;
        public float fluctuatingAlphaPauseDuration;
        public float fluctuatingFullAlphaDuration;
        public float fluctuatingAlphaRandomness;
        public float min, max;

        ALPHA_TEMPLATE(float alphaStep, float fluctuatingAlphaPauseDuration, float fluctuatingFullAlphaDuration, float fluctuatingAlphaRandomness, float min, float max) {
            this.alphaStep = alphaStep;
            this.fluctuatingAlphaPauseDuration = fluctuatingAlphaPauseDuration;
            this.fluctuatingFullAlphaDuration = fluctuatingFullAlphaDuration;
            this.fluctuatingAlphaRandomness = fluctuatingAlphaRandomness;
            this.min = min;
            this.max = max;
        }

        ALPHA_TEMPLATE(float alphaStep, float fluctuatingAlphaPauseDuration,
                       float fluctuatingFullAlphaDuration,
                       float fluctuatingAlphaRandomness) {
            this.alphaStep = alphaStep;
            this.fluctuatingAlphaPauseDuration = fluctuatingAlphaPauseDuration;
            this.fluctuatingFullAlphaDuration = fluctuatingFullAlphaDuration;
            this.fluctuatingAlphaRandomness = fluctuatingAlphaRandomness;
        }

    }

    public enum BLENDING {
        NORMAL(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
        INVERT_SCREEN(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA),
        PREMULTIPLIED_ALPHA(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA),
        SCREEN(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA),
        OVERLAY(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA),
        MULTIPLY(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA),
        SATURATE(GL20.GL_DST_COLOR, GL20.GL_ONE),
        DARKEN(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA),
        SUBTRACT(GL20.GL_DST_COLOR, GL20.GL_SRC_ALPHA),;

        public int blendDstFunc, blendSrcFunc, blendDstFuncAlpha, blendSrcFuncAlpha;

        BLENDING(int blendDstFunc, int blendSrcFunc) {
            this.blendDstFunc = blendDstFunc;
            this.blendSrcFunc = blendSrcFunc;
        }

        BLENDING(int blendDstFunc, int blendSrcFunc, int blendDstFuncAlpha, int blendSrcFuncAlpha) {
            this.blendDstFunc = blendDstFunc;
            this.blendSrcFunc = blendSrcFunc;
            this.blendDstFuncAlpha = blendDstFuncAlpha;
            this.blendSrcFuncAlpha = blendSrcFuncAlpha;
        }
    }
}
