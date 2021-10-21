package main.content.enums.entity;

import main.data.filesys.PathFinder;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.MapMaster;
import main.system.images.ImageManager;

import java.awt.*;
import java.util.Map;

/**
 * Created by JustMe on 2/14/2017.
 */
public class SpellEnums {

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

        SYLVAN, ELEMENTAL, SAVAGE,
        ;
    }

    public enum SPELL_POOL {
        MEMORIZED, DIVINED, VERBATIM, SPELLBOOK
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


    public enum SPELL_CATEGORY {
        SINGLE,
        SUMMON,
        CONJURE,
        ZONE,
        GLOBAL
    }
}
