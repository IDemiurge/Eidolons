package main.content.enums.entity;

import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.util.Map;

/**
 * Created by JustMe on 2/14/2017.
 */
public class SpellEnums {
    private static final String STD_CONSTS = "({Mastery}+{Spellpower}+{spell_spell_difficulty})";
    private static final String MSTR_SP = "({Mastery}+{Spellpower})";
    private static final String SPELL_DIFFICULTY = "{spell_spell_difficulty}";
    private static final String MSTR = "{Mastery}";
    private static final String SP = "{Spellpower}";

    public enum RESISTANCE_MODIFIERS {
        NO_SPELL_ARMOR, NO_ARMOR, NO_RESISTANCE, NO_DEFENCE, NO_DAMAGE_TO_UNDEAD,

    }

    public enum RESISTANCE_TYPE {
        REDUCE_DAMAGE, CHANCE_TO_BLOCK, REDUCE_DURATION, IRRESISTIBLE,

    }

    public enum SPELL_GROUP {
        FIRE,
        AIR,
        WATER,
        EARTH,
        CONJURATION, ENCHANTMENT, SORCERY, TRANSMUTATION, VOID,

        WITCHERY, SHADOW, PSYCHIC,

        NECROMANCY, AFFLICTION, BLOOD_MAGIC,

        WARP, DEMONOLOGY, DESTRUCTION,

        CELESTIAL, BENEDICTION, REDEMPTION,

        SYLVAN, ELEMENTAL, SAVAGE,;
    }

    public enum SPELL_POOL {
        MEMORIZED, DIVINED, VERBATIM, SPELLBOOK;
    }

    public enum SPELL_SUBGROUP {
        FIRE,
        LAVA,
        CLAY,
        SAND,
        STONE,
        ICE,
        WATER,
        LIGHTNING,
        WIND,
        BONE,
        WRAITH,
        SOUL,
        FLESH,
        BLOOD,
        VAMPIRIC,
        DEATH_MAGIC,
        POISON,
        ENTROPY,
    }

    public enum SPELL_TABS {
        ARCANE,
    }

    public enum SPELL_TAGS {
        RANDOM_FACING,
        FACE_SUMMONER,
        EXCLUSIVE_SUMMON,
        MIND_AFFECTING,
        COMBAT_ONLY,
        FIRE,
        LAVA,
        CLAY,
        SAND,
        STONE,
        ICE,
        WATER,
        LIGHTNING,
        WIND,
        BONE,
        WRAITH,
        SOUL,
        FLESH,
        BLOOD,
        VAMPIRIC,
        DEATH_MAGIC,
        POISON,

        DEMONIC,
        UNHOLY,
        ENTROPY,


        DIVINE,
        ELDRITCH,
        RANGED_TOUCH,
        CHANNELING,
        INSTANT,
        MISSILE,
        TOP_DOWN,
        ;
    }

    public enum SPELL_TYPE {
        SORCERY, SUMMONING, ENCHANTMENT

    }

    public enum SPELL_UPGRADE {
        ELEMENTAL(" Deals additional 15% as Lightning, Cold and Fire damage each.", 25, 25, " el", "passives;", getDamagePercentSpecialEffect(
         false, "15,Lightning", "15,Cold", "15,Fire")),
        ASTRAL(" Deals additional 10% as Holy, Shadow, " + "Chaos and Death damage each.", 25, 25, " as", "passives;", getDamagePercentSpecialEffect(
         false, "10,Holy", "10,Shadow", "10,Chaos", "10,Death")),

        PSIONIC(" Deals additional Psionic damage based on the spell's Difficulty.", 25, 25, " el", "passives;", getDamageSpecialEffect("({Mastery}+{Spellpower}+{spell_spell_difficulty}) +({Mastery}+{Spellpower}+{spell_spell_difficulty}) *({Mastery}+{Spellpower}+{spell_spell_difficulty}) /5,Psionic")),

        IRIDESCENT(" Deals additional 15% as Lightning, Fire and Holy damage each.", 25, 25, " ir", "passives;", getDamagePercentSpecialEffect(
         false, "15,Light", "15,Holy", "15,Fire"), "", "", "", "", "resistance mod;range", "-25>|<1"),

        BLAZE("Deals additional 25% Fire damage and applies an amount of Blaze counters equal to 10% of damage dealt.", 25, 25, " ir", "passives;", getDamagePercentSpecialEffect(
         false, "25,Fire")
         + ">|<" + getAddCounterSpecialEffect("Blaze,{active_damage_last_dealt}/10")),

        MESMERIC(" Reduces target's Focus by " + STD_CONSTS + "/2.", 25, 25, " me", "passives;", getValueModSpecialEffect("C_Focus,-"
         + STD_CONSTS + "/2")),

        FEARSOME(" Reduces target's Morale by " + STD_CONSTS + ".", 25, 25, " fe", "passives;", getValueModSpecialEffect("C_Morale,-"
         + STD_CONSTS + "")),

        DARK_FIRE(" Deals additional 10% as Fire and Shadow damage.", 25, 25, " as", "passives;", getDamagePercentSpecialEffect(
         false, "25,Shadow", "25,Fire")),

        CORROSION(" Adds " + STD_CONSTS + "/10 Corrosion counters.", 25, 25, " as", "passives;", getAddCounterSpecialEffect("Corrosion,"
         + STD_CONSTS + "/10")),

        BLIGHT(" Adds " + STD_CONSTS + "/8 Blight counters.", 25, 25, " as", "passives;", getAddCounterSpecialEffect("Blight,"
         + STD_CONSTS + "/8")),

        DRAINING(" Reduces target's Stamina by " + STD_CONSTS + "/4.", 25, 25, " dr", "passives;", getValueModSpecialEffect("C_Stamina,-"
         + STD_CONSTS + "/4")),

        HELLFIRE(" Deals additional 10% as " + "Chaos and Fire damage.", 25, 25, " as", "passives;", getDamagePercentSpecialEffect(
         false, "25,Chaos", "25,Fire"), "", "", "", "", "", ""),

        PUTRID(" Adds " + STD_CONSTS + "/10 Disease counters.", 20, 15, " as", "passives;", getAddCounterSpecialEffect("Disease,"
         + STD_CONSTS + "/10")),

        TOXIC(" Adds " + "" + "/5 Poison counters.", 15, 10, " as", "passives;", getAddCounterSpecialEffect("Poison,"
         + "" + "/5")),

        APHOTIC,

        VAMPIRIC(" Drains (10+{Mastery}/2)% of damage dealt into caster's Endurance.",
         35, 25, " de", "passives;", "LifeStealSpell(10+{Mastery}/2)"),
        DEATH_CHILL(" Adds " + STD_CONSTS + "/10 Freeze counters.", 20, 15, " de", "passives;", getAddCounterSpecialEffect("Freeze,"
         + STD_CONSTS + "/5")),
        SHOCK,

        ELDRITCH,
        CHRONO,
        GRAVITY,
        VOID,

        ADAMANTIUM, // spellpower is increased by X?
        METEORITE,
        DARK_STEEL,
        MOON_SILVER,
        GHOSTLY,
        SOUL,
        // resistance penetration? special roll? add Intelligence to the Beat
        WICKED,
        VOODOO(" Random curse/impossible to dispel unless caster is dead/.", 20, 15, " de", "passives;", getAddCounterSpecialEffect("Freeze,"
         + STD_CONSTS + "/5")),
        ENTROPY, // % of max toughness

        DESPAIR,
        HATRED,
        LUST,
        TERROR,

        CRIMSON, // % of missing endurance

        MUTAGENIC,
        ACIDIC,
        DIRE(" Increases Spellpower by " + SPELL_DIFFICULTY + " and an additional {Mastery}/10%.", 35, 25, " de", "passives;", "AddParam(Spellpower Bonus 10+{Mastery}/2)", "", "", "", "", "Spellpower Bonus;Spellpower Mod", ""
         + SPELL_DIFFICULTY + StringMaster.UPGRADE_SEPARATOR + "{Mastery}/10"),
        RAGE,

        LIFEBLOOD, // append {endurance}*(min(10+{Mastery},{1})/100 to formula
        // so there is %
        ELDER,

        FROSTY,
        FIERY,
        PLASMA,

        UNDIMMED,
        ABSOLUTION,
        HERESY,
        VINDICATION,
        REST_IN_PEACE, // remove undying counters

        MOONLIGHT,
        PALE, //
        TWILIGHT, // ALT ENUM WITH SAME DISPLAYED NAME - FOR BENEDICTION

        STUN,
        // Roll(Mass,{active_damage_dealt},-, Knockback)
        KNOCKBACK,
        KNOCKDOWN,

        EMPOWERED,
        QUICKENED,
        SIMPLIFIED,
        EXTENDED,
        MULTIPLIED,
        ECHO,
        RECURRING,

        RAY,
        NOVA,
        BLAST,
        GLOBAL
        /*
		 * META MAGIC IN WIZARDRY!
		 */;
        private Map<String, String> addPropMap;
        private Map<String, String> setPropMap;
        private Map<String, String> paramModMap;
        private Map<String, String> paramBonusMap;
        private int spellDifficultyMod;
        private int costMod;
        private String imgSuffix;
        private String description;
        private Image glyphImageActive;
        private Image glyphSmallImage;
        private Image glyphImage;
        private Image glyphImageSelected;

        SPELL_UPGRADE() {

        }

        SPELL_UPGRADE(String description, int spellDifficultyMod, int costMod, String imgSuffix,
                      String addProps, String addPropValues) {
            this(description, spellDifficultyMod, costMod, imgSuffix, addProps, addPropValues, "",
             "", "", "", "", "");
        }

        SPELL_UPGRADE(String description, int spellDifficultyMod, int costMod, String imgSuffix,
                      String addProps, String addPropValues, String setProps, String setPropValues,
                      String modParams, String modParamValues, String bonusParams, String bonusParamVals) {
            this.description = description;
            this.imgSuffix = imgSuffix;
            this.costMod = costMod;
            this.spellDifficultyMod = spellDifficultyMod;
            this.addPropMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(addProps), ContainerUtils.openContainer(addPropValues));
            this.setPropMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(setProps), ContainerUtils.openContainer(setPropValues));
            this.paramModMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(modParams), ContainerUtils.openContainer(modParamValues));
            this.paramBonusMap = new MapMaster<String, String>().constructMap(ContainerUtils
             .openContainer(bonusParams), ContainerUtils.openContainer(bonusParamVals,
             StringMaster.UPGRADE_SEPARATOR));
        }

        private static String getAddCounterSpecialEffect(String... amount_comma_dmg_type) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = "AddCounter";
            return getSpecialEffect(effectCase, abilName, amount_comma_dmg_type);

        }

        private static String getValueModSpecialEffect(String... param_comma_amount) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = "AddParam";
            return getSpecialEffect(effectCase, abilName, param_comma_amount);
        }

        private static String getDamageSpecialEffect(String... amount_comma_dmg_type) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = "SpellDamage";
            return getSpecialEffect(effectCase, abilName, amount_comma_dmg_type);
        }

        private static String getDamagePercentSpecialEffect(boolean toDealt,
                                                            String... amount_comma_dmg_type) {
            String effectCase = "ON_SPELL_IMPACT";
            String abilName = toDealt ? "DmgAddToDealt" : "DmgAdd";
            return getSpecialEffect(effectCase, abilName, amount_comma_dmg_type);

        }

        private static String getSpecialEffect(String effectCase, String abilName, String... args) {
            StringBuilder string = new StringBuilder("SpecEffect(" + effectCase + ",");
            for (String s : args) {
                string.append(abilName).append(StringMaster.wrapInParenthesis(s));
                string.append(StringMaster.AND_SEPARATOR);
            }
            string = new StringBuilder(StringMaster.cropLast(string.toString(), 1));
            string.append(")");
            return string.toString();
        }

        public int getSpellDifficultyMod() {
            return spellDifficultyMod;
        }

        public int getCostMod() {
            return costMod;
        }

        public Map<String, String> getAddPropMap() {
            return addPropMap;
        }

        public Map<String, String> getSetPropMap() {
            return setPropMap;
        }

        public Map<String, String> getParamModMap() {
            return paramModMap;
        }

        public Map<String, String> getParamBonusMap() {
            return paramBonusMap;
        }

        public String getImgSuffix() {
            return imgSuffix;
        }

        public void setImgSuffix(String imgSuffix) {
            this.imgSuffix = imgSuffix;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(toString());
        }

        public String getDescription() {
            return description;
        }

        public Image getGlyphImageSelected() {
            if (glyphImageSelected == null) {
                glyphImageSelected = ImageManager.getImage(PathFinder.getSpellUpgradeGlyphsFolder()
                 + StringMaster.getWellFormattedString(name()) + " s.png");
            }
            if (!ImageManager.isValidImage(glyphImageSelected)) {
                return glyphImage;
            }
            return glyphImageSelected;
        }

        public Image getGlyphImageActive() {
            if (glyphImageActive == null) {
                glyphImageActive = ImageManager.getImage(PathFinder.getSpellUpgradeGlyphsFolder()
                 + StringMaster.getWellFormattedString(name()) + " a.png");
            }
            if (!ImageManager.isValidImage(glyphImageSelected)) {
                return glyphImage;
            }
            return glyphImageActive;
        }

        public Image getGlyphImage() {
            if (glyphImage == null) {
                glyphImage = ImageManager.getImage(PathFinder.getSpellUpgradeGlyphsFolder()
                 + StringMaster.getWellFormattedString(name()) + ".png");
            }
            return glyphImage;
        }

        public Image getGlyphSmallImage() {
            if (glyphSmallImage == null) {
                Image img = getGlyphImage(); // active?
                glyphSmallImage = ImageManager.getSizedVersion(img, 33);
            }
            return glyphSmallImage;
        }

    }
}
