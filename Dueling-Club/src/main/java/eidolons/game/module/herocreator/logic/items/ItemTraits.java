package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.datatypes.WeightMap;

import static eidolons.game.module.herocreator.logic.items.ItemTraits.EIDOLON_ASPECT.*;

/**
 * Created by JustMe on 10/21/2018.
 * <p>
 * based on perks?
 * <p>
 * <p>
 * Usage:
 * Loot
 * Equip enemies
 * Add to shops
 * <p>
 * data from ...
 * <p>
 * Trait groups by "style"?
 */
public class ItemTraits {

    public static final String VAR = "$var";


    public enum EIDOLON_ASPECT {
        /*
        kind of like the stuff I did for OLD EIDOLONS
        by principle?
        by deity?
        special for eidolons?
        what would be cool for EiN campaign?    */
        EVIL,
        DARK,
        MOON,
        TWILIGHT,

        LIGHT,
        PURE,
        SUN,

        FLAME,
        LAVA,
        WARPED,
        CHAOS,
        BLOOD,

        VENOM,
        ICE,
        OCEAN,

        WOOD,
        STONE,
        SAND,

        WIND,
        LIGHTNING,

        ASH,
        MIST,
        NETHER,
        VOID,

        METAL,
        COSMOS,
        TECH,
        CRYSTAL,

    }

    //how many levels should they have?
    /*
    interesting abils:

    Stamina/... drain on atk/...

IDEA: just use adj/noun from corresponding traits?

    ("bind(charisma, spellpower)")
    ("bind(charisma, wisdom)")
    ("bind(charisma, strength)")

    ("bind(spellpower, strength)")
    ("bind(spellpower, willpower)")
    ("bind(spellpower, charisma)")

    ("bind(willpower, strength)")
    ("bind(willpower, spellpower)")
    ("bind(willpower, vitality)")

    ("bind(agility, dexterity)")
    ("bind(agility, intelligence)")
    ("bind(agility, strength)")

    ("bind(dexterity, agility)")
    ("bind(dexterity, intelligence)")
    ("bind(dexterity, charisma)")

    generic: adjective for N1, noun for N2 ...

     */
    public enum ITEM_TRAIT {
        /*
        numbered-versions of traits - invisible in game?

where does QUANTITY become QUALITY?
        //armor special

        adopting traits for different item types...
        e.g. Poison - dmg, onHit or resist ...

        will this translate well into some other data format?
        like xls?

*/

        SPITE("", "", "vindictive"), //sharp(attack, defense)
        CURSED("", "", "ominous;portent;fateful;possessed;dominating"), //"sharp(resist penetration, resistance"
        GRIM("", "", "sinister;callous;scornful;bereft;seething"),
        IRE("", "vexed;enmity;bellicose;growling;belligerent;choleric;audacious;ornery;"),

//   TODO      STEADFAST,
//    TODO     DURABLE,

        TOUGHNESS("tou2", "toughness;solidity;stoutness;sturdiness;resilience;",
                "tough;solid;stout;sturdy;stalwart;",
                map().chain(STONE, 10).chain(METAL, 8).chain(WOOD, 4).chain(CRYSTAL, 4), all()),
        INITIATIVE("ini2", "quickness;speed;rapidity;rush;swiftness;",
                "quick;speedy;rapid;rushing;swift;",
                map().chain(WIND, 10).chain(LIGHTNING, 8).chain(FLAME, 4), all()),

        ATTACK("atk2", "aim;.;precision;.;deadeye;", "skillful;.;precise;.;deadly",
                map().chain(LIGHTNING, 10).chain(BLOOD, 8).chain(FLAME, 4), all()),
        DEFENSE("def2", ";.;alacrity;.;", "experienced;.;expert;.;master",
                map().chain(WIND, 10).chain(STONE, 8).chain(WOOD, 4), all()),
        SPIRIT("spirit2", "boldness;bravery;courage;valor;lionheart", "bold;brave;courageous;valiant;fearless",
                map().chain(LIGHT, 10).chain(SUN, 8).chain(FLAME, 4), all()),
        //
        FORTITUDE("fortitude2", "survivor;.;fortitude;.;stand;", "steady;.;fortified;.;massive",
                map().chain(OCEAN, 10).chain(STONE, 8).chain(WOOD, 4), all()),
        STEALTH("stealth2", "stealth;.;furtiveness;.;invisibility", "stealthy;.;furtive;.;unseen",
                map().chain(DARK, 10).chain(MOON, 8).chain(TWILIGHT, 4), all()),
        STAMINA("stamina2", "Stamina;.;Bull;.;Resilience", "Persevering;.;Relentless;.;Tireless",
                map().chain(OCEAN, 10).chain(WIND, 8).chain(BLOOD, 4), all()),
        STARTING_FOCUS("starting_focus2", "sharpness;.;keenness;.;razor", "sharp;.;keen;.;razorsharp",
                map().chain(WIND, 10).chain(TWILIGHT, 8).chain(FLAME, 4), all()),

        SP_DMG_MOD("sp_dmg_mod2", "calamity;.;incineration;.;annihilation;",
                "incinerating;.;disintegrating;.;annihilating",
                map().chain(FLAME, 10).chain(EIDOLON_ASPECT.CHAOS, 8).chain(EVIL, 6).chain(LIGHTNING, 4), all()),

        STRENGTH("str2", "strength;brute;ogre;giant;might;", "strong;brutal;brutish;giant;mighty;",
                map().chain(BLOOD, 10).chain(LAVA, 8).chain(FLAME, 4).chain(METAL, 4), all()),
        //+weight bull
        VITALITY("vit2", "vitality;.;troll;.;dragon", "healthy;.;robust;.;indomitable",
                map().chain(WOOD, 10).chain(OCEAN, 8).chain(LAVA, 4).chain(STONE, 4), all()),
        AGILITY("agi2", "agility;.;great agility;.;alacrity", "agile;.;adroit;.;swift;",
                map().chain(LIGHTNING, 10).chain(FLAME, 8).chain(BLOOD, 4), all()),
        DEXTERITY("dex2", "deftness;.;nimbleness;.;dexterity", "deft;.;nimble;.;dexterous",
                map().chain(WIND, 10).chain(OCEAN, 8).chain(LIGHTNING, 4), all()),
        WILLPOWER("wil2", "willpower;.;grit;.;dominance", "stalwart;.;gritty;.;indomitable",
                map().chain(METAL, 10).chain(LIGHT, 8).chain(CRYSTAL, 4).chain(EVIL, 4), all()),
        INTELLIGENCE("int2", "wit;shrewdness;intelligence;brainpower;prodigy", "witty;shrewd;intelligent;prodigious;.",
                map().chain(ICE, 10).chain(EIDOLON_ASPECT.CHAOS, 8).chain(OCEAN, 6).chain(CRYSTAL, 4), all()),
        WISDOM("wis2", "wisdom;.;great wisdom;.;sagesse", "wise;.;sagacious;mindful;All-Seeing",
                map().chain(OCEAN, 10).chain(COSMOS, 8).chain(TWILIGHT, 6).chain(OCEAN, 4), all()),
        KNOWLEDGE("kno2", "knowledge;.;erudition;secrets;secret doctrine", "knowledgeable;.;erudite;secretive;omniscious",
                map().chain(CRYSTAL, 10).chain(LIGHTNING, 8).chain(FLAME, 4), all()),
        CHARISMA("cha2", "charisma;.;splendour;glory;perfection",
                "charismatic;.;splendid;glorious;perfect",
                map().chain(LIGHT, 10).chain(EIDOLON_ASPECT.CHAOS, 8).chain(FLAME, 4), all()), //bind(charisma, spellpower)

        //TODO DMG
        //smiting
        PIERCING("dmg2(PIERCING)", "needle;.;spike;.;lance", "piercing;.;penetrating;.;impaling",
                map().chain(WIND, 10).chain(LIGHTNING, 8).chain(FLAME, 4), all()),
        SLASHING("dmg2(SLASHING)", "blades;.;scissors;.;razors", "cutting;.;shredding;.;decapitating",
                map().chain(WIND, 10).chain(LIGHTNING, 8).chain(FLAME, 4), all()),
        BLUDGEONING("dmg2(BLUDGEONING)", "fist;.;boulder;.;hammer;.;", "bludgeoning;.;crushing;.;devastating",
                map().chain(WIND, 10).chain(LIGHTNING, 8).chain(FLAME, 4), all()),

        ACID("dmg2(acid)", "", "caustic;acerbic;"),
        SEARING("dmg2(fire);", "cinders;embers;arson;combustion;conflagration;", "sizzling;.;searing;.;"),
        DARKSOME("dmg2(shadow)", "shade;shadows;dark;", ""),
        IRIDESCENT("dmg2(light)", "", "bright;shimmering;shining;luminous;iridescent"),
        FRIGID("dmg2(ice);", "", "chilly;cold;frigid;blood-chilling;bone-chilling;"),
        ELECTRIFIED("dmg2(lightning)", "static;shock;arc;lightning;thunderbolt",
                "charged;shocking;arcing;electrified;crackling"),
        THUNDEROUS("dmg2(sonic)", "", "loud;rumbling;thunderous;deafening;roaring"),
        HOLY("dmg2(holy)", "", "devout;saint;sacred;hallowed;"),//"bound(integrity, charisma);bound(charisma, attributes)"

        DEATHLY("dmg2(death)", "grave;.;death;.;mourning;", "cheerless;morose;somber;grave;mournful"),
        PSIONIC("dmg2(psionic)", "shades;mirages;illusions;hallucinations;phantoms;", "eldritch;"),

        CHAOS("dmg2(chaos)", "", "creeping;writhing;formless;twisted;roiling;"),
        MYSTICAL("dmg2(arcane)", "", ""), //essence regen;


        //TODO COUNTERS

        ENVENOMED("", "SPIDER;", "toxic;mortal"),
        CRIMSON("", "", "red;blooded;crimson;blood-soaked;sanguine;"),
        SMOLDERING("", "", "smoke;ash;ruin;"), //LAVA
        FETID("", "sickness;foulness;bile;defilement;pandemic", "sick;foul;defiled;pandemic;"),

        //TODO RESIST / SP

        PURE("", "", "clean;fair;white;pure;purest"),//"bound(integrity, charisma);bound(charisma, attributes)"

        ELDER("sp(sylvan)", "", "mossy;moss-grown;leafy;green;sylvan"),
        ASHEN("", "", "pale;wan;ashen;anemic;lifeless"),
        UNDULATING("water", "wave;tide;tsunami;"),
        MIDNIGHT("", "evernight", "dreary;"),
        PRIMEVAL("EARTH", "", "olden;elder;ancient;primordial;primeval;"),
        WINDSWEPT("sp2(air)", "wind;gale;storm;whirlwind;maelstrom", "windy;windlike;stormy;tempestuous"),

        APHOTIC("", "abyss;eclipse", "blackened"),
        CELESTIAL("", "high;rising;aquiline;"),
        FLAMING("", "heat;fire;flame;blaze;inferno;", "blistering;fiery;flaming;blazing;infernal"),


        FLAMEHEART("", "", "ardent;zealous;rekindled"),
        SOARING("", "", "dawn;sun;zenith;solstice"),

        SADISTIC("", "ravish;malignancy;sadism;ecstasy;rapture",
                "gloating;malignant;sadistic;ecstatic;rapturous"),

        VAMPIRIC("", "", "bat;bloodlust;bloodhunger;vampire;nosferatu"),
        //bloodcraze;


        DEATHLESS("", "", "skeletal;undying;immortal;deathless;eternal"),
        SPECTRAL("", "", "disembodied;ghostly;wraithlike;spectral"),

        VEILED("", "sombra", "dimmed;obscured;concealed;shadowy;veiled"),

        WHISPERED("", "", "hidden;secret;unsung;arcane;whispered"),
        VOICELESS("", "", "massive resist, essence trick, sp penalty, bind(wisdom, willpower)"),

        //PARAM ON HIT
        FRIGHT("", "", "horrid;frightful;fearsome;terrifying;consternating"),
        MESMERIC("", "", "dazing;macabre"),
        FEL("", "", "wretched;tarnished;ravaged"), //STA DRAIN


        FULL_MOON("", "", "lambent;moonrise;witch-hour;moonshade;moonlight;"),

        GLAMOUR("", "bliss;decadent; adoration;worship", "blithe"),


        FORLORN("", "chagrin;", "somber;barren;forsaken;wailful;"),
        ECHO("", "", "haunting"),

        ROTTEN("", "", ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
                map().chain(BLOOD, 10).chain(CHAOS, 6).chain(FLAME, 4).chain(EVIL, 2),
                "Attack mod($var/4);ATTACK_AP_PENALTY($var/-3)", DC_TYPE.WEAPONS),

        VICIOUS("anger;wrath;fury;frenzy;furor", "angry;vicious;.;frenzied;incensed",
                ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
                map().chain(BLOOD, 10).chain(CHAOS, 6).chain(FLAME, 4).chain(EVIL, 2),
                "Attack mod($var/4);ATTACK_AP_PENALTY($var/-3)", DC_TYPE.WEAPONS),
        ;
        public static final ITEM_TRAIT[] all = ITEM_TRAIT.values();
        public boolean preferAdjective;
        String args;
        DC_TYPE[] types;
        int levels;
        ITEM_TRAIT_TYPE type;
        ITEM_TRAIT_RARITY rarity;
        WeightMap<EIDOLON_ASPECT> styleMap;
        String[] nouns;
        String[] adjectives;

        ITEM_TRAIT(String data, String data2) {
            this(data, data2, ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
                    map(), "", C_OBJ_TYPE.ITEMS.getTypes());
        }

        ITEM_TRAIT(String args, String data, String data2) {
            this(data, data2, ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
                    map(), args, C_OBJ_TYPE.ITEMS.getTypes());
        }

        ITEM_TRAIT(String args, String data, String data2, WeightMap<EIDOLON_ASPECT> styleMap,
                   DC_TYPE... types) {
            this(data, data2, ITEM_TRAIT_TYPE.VALUE, ITEM_TRAIT_RARITY.COMMON,
                    styleMap, args, types);
        }

        ITEM_TRAIT(String nouns, String adjectives, ITEM_TRAIT_TYPE type, ITEM_TRAIT_RARITY rarity,
                   WeightMap<EIDOLON_ASPECT> styleMap, String args,
                   DC_TYPE... types) {
            this.type = type;
            this.styleMap = styleMap;
            this.rarity = rarity;
            this.args = args;
            this.types = types;
            this.nouns = ContainerUtils.open(nouns);
            this.adjectives = ContainerUtils.open(adjectives);
            levels = ContainerUtils.openContainer(nouns).size();
        }


        private static DC_TYPE[] all() {
            return C_OBJ_TYPE.ITEMS.getTypes();
        }

        private static WeightMap<EIDOLON_ASPECT> map() {
            return new WeightMap<>(EIDOLON_ASPECT.class);
        }

        private static ObjType chooseType(DC_TYPE weapons, String group, ITEM_LEVEL level) {
            return DataManager.getRandomType(weapons, group);
        }

        public DC_TYPE[] getTypes() {
            return types;
        }

        public ITEM_TRAIT_TYPE getType() {
            return type;
        }

        public ITEM_TRAIT_RARITY getRarity() {
            return rarity;
        }

        public WeightMap<EIDOLON_ASPECT> getStyleMap() {
            return styleMap;
        }
    }

    public enum ITEM_TRAIT_RARITY {
        COMMON,
        UNCOMMON,
        RARE,
        EXCEPTIONAL,
    }

    /*
            specifically good for gameplay
    Illumination("Glimmer;Shining;.;Radiance", ".;.;Luminous",

            total must have for the setting

    Consternation
    Oblivion
    Deceit
    Damnation

    Void(Hollow;
    Requiem(Passing

    Dissolution
    Ascension
    Tranquility

    Retribution
    Sombra
    Sepulchre
    Ancient



             */
    public enum ITEM_TRAIT_TYPE {
        VALUE,

        ON_HIT,
        ON_ATTACK,
        ON_DEATH,
        ON_KILL,
        AURA,
        AURA_ALLIES,
        AURA_ENEMIES,
        AURA_RECURRING,

        ACTIVE_CHARGES,//adds special action to weapon's actives
        ACTIVE,//adds special action to weapon's actives

    }


    public enum TRAIT_EFFECT_SCALE {
        minor(0.25f),
        lesser(0.33f),
        medium(0.5f),
        major(0.66f),
        greater(0.75f),
        grand(1),
        ;
        float coef;

        TRAIT_EFFECT_SCALE(float coef) {
            this.coef = coef;
        }
    }

    //No numeric args required - just append 1-6 for power lvls, e.g.
    // bind5(Strength, Vitality)
    // or res4(shadow)
    /*
    mnem0nic required:
    Critical damage protection
     */
    public enum TRAIT_EFFECT_TEMPLATE {
        sp("^VAR ^VAR by $val"),
        aspect("^VAR ^VAR by $val"), //+sp% for spells of this aspect
        dmg("^VAR ^VAR by $val"),
        dmgWeapon("^VAR ^VAR by $val"), //with this weapon...
        dmgFrom("^VAR ^VAR by $val"), //N% of Shadow dmg you deal is dealt as Fire dmg on top of it
        res("^VAR ^VAR by $val"),
        armor("^VAR ^VAR by $val"),
        regen("^VAR ^VAR by $val"), //%?

        boost("^VAR ^VAR by $val"),
        depend("^VAR ^VAR by $val"),// a bonus that will scale based on % of a dynamic value
        discount("^VAR ^VAR by $val"), // spell/move/atk/all ("^VAR ^VAR by $val"), sta/ap/.../all
        mod("^VAR ^VAR by $val"), //% of base
        bind("^VAR ^VAR by $val"), // arg1 is boosted by % of arg2
        sharp("^VAR ^VAR by $val"),
        scaled("^VAR ^VAR by $val"),
        stack("^VAR ^VAR by $val"),
        random("^VAR ^VAR by $val"),
        chaotic("^VAR ^VAR by $val"), //will change from battle to battle
        hidden("^VAR ^VAR by $val"), //will be applied only after <?>
        conditional("^VAR ^VAR by $val"), //
        state("^VAR ^VAR by $val"),// only when a buff-status applies
        activated("^VAR ^VAR by $val"),// only once per dungeon?

        //prop
        abil("^VAR ^VAR by $val"),
        stdPassive("^VAR ^VAR by $val"),


        //mnemonic
        attrs("^VAR ^VAR by $val"),
        mastery,

        //wrappers
        greater("Boosts either ^VAR or ^VAR by $val, whichever is greater"),
        lesser("Boosts either ^VAR or ^VAR by $val, whichever is lesser"),
        //weapon special

        //armor

        //multi-form for diff types
        spRes, //sp from amulet/weapons, res from armor/rings
        ;
        String description;

        TRAIT_EFFECT_TEMPLATE(String description) {
            this.description = description;
        }

        TRAIT_EFFECT_TEMPLATE() {
        }
    }
    /*
    ++ generate special named items from trait combinations

    DEVOURING
    HUNGERING
    VILE
    DOOMRIDDEN

    FEL,
    SEARING,
    FRIGHTFUL,
    AGONIZING,
    TORMENTING,
    DEMENTIA,
    FEROCIOUS,
    OBLIVION,
    DEATHLESS,

    BLEEDING,
    PENETRATING,
    OMNISCIOUS,

    WEARY,

    BEREAVEMENT,
    UNWAKING,



     */

}

///*
//        depend("^VAR ^VAR by $val"),// a bonus that will scale based on % of a dynamic value
//        discount("^VAR ^VAR by $val"), // spell/move/atk/all ("^VAR ^VAR by $val"), sta/ap/.../all
//        mod("^VAR ^VAR by $val"), //% of base
//        bind("^VAR ^VAR by $val"), // arg1 is boosted by % of arg2
//
//       lesser
//
//       greater

/*

robbed:
Burly Vigorous Fervor
Jagged Ferocious Grinding Serrated
Measure Worth Inertia Celerity
Slaying Slaughter Evisceration Butchery
rampant havoc seething

Knight's ...
Wizard's ...

Hawkeye
Unearthly
Consecrated Pearl Saintly Faithful
 Shivering Boreal Hibernal
Blanched relinquish
Condensing (fire)
Septic Corrosive  Pestilent
Scintillating Prismatic Chromatic
Azure Lapis Cobalt sapphire
Russet Garnet
Tangerine Ocher Coral Amber Ambergris
poison - Viridian, Jade Noxious Fungal
Sanguinary
Cinnabar
Graverobber's Sentinel Warding Negation

Transcendence
mire sleazy oozing
 */

// */
//        //TODO offense
//
//        //TODO defensive