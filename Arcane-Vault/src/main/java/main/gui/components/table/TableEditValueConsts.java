package main.gui.components.table;

import eidolons.content.PROPS;
import main.content.*;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.SpellEnums;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.StringComparison;
import main.swing.generic.components.editors.EDITOR;
import main.swing.generic.components.editors.SoundChooser;

public class TableEditValueConsts {
    public static final String[] VAR_MULTI_ENUM_LIST_IDS = {
            PROPS.FILLER_TYPES.name(),

            PROPS.CONTAINER_CONTENT_VALUE.name(),
            PROPS.CONTAINER_CONTENTS.name(),
            G_PROPS.PRINCIPLES.name(),
            "Encounter Subgroup", // contains()? otherwise overshadows
            G_PROPS.SPECIAL_REQUIREMENTS.getName(),
            PROPS.ATTRIBUTE_PROGRESSION.getName(), PROPS.MASTERY_PROGRESSION.getName(),
            PROPS.ROLL_TYPES_TO_DISPEL_EACH_TURN.getName(), PROPS.ROLL_TYPES_TO_SAVE.getName(),

    };
    public static final Class<?>[] VAR_ENUM_CLASS_LIST = {
            CONTENT_CONSTS.SPECIAL_REQUIREMENTS.class

    };
    public static final Object[][] VAR_MULTI_ENUM_PAIRS = {
            {PROPS.OVERLAY_SPRITES.name(), BfObjEnums.SPRITES.class},
            {PROPS.UNDERLAY_SPRITES.name(), BfObjEnums.SPRITES.class},
            {PROPS.TEXTURES_OVERLAY.name(), BfObjEnums.TEXTURES.class},
            {PROPS.TEXTURES_UNDERLAY.name(), BfObjEnums.TEXTURES.class},
            {PROPS.CUSTOM_OBJECT.name(), BfObjEnums.CUSTOM_OBJECT.class},
            {PROPS.VFX.name(), BfObjEnums.BF_VFX.class},
    };
    public static final String[] SINGLE_ENUM_LIST_IDS = {
            //     PROPS.CUSTOM_OBJECT.name(),
            PROPS.REINFORCEMENT_TYPE.name(),
            PROPS.REINFORCEMENT_CUSTOM.name(),
            PROPS.REINFORCEMENT_CHANCE.name(),
            PROPS.REINFORCEMENT_STRENGTH.name(),
            PROPS.UNIT_GROUP_TYPE.name(),
            PROPS.AI_BEHAVIOR_MODE.name(),
            PROPS.LOOT_TYPE.name(),
            PROPS.AFTER_BATTLE_EVENT.name(),
            PROPS.PRE_BATTLE_EVENT.name(),
            G_PROPS.QUEST_GROUP.name(), G_PROPS.QUEST_TYPE.name(),
            G_PROPS.QUEST_REWARD_TYPE.name(),
            G_PROPS.QUEST_TIME_LIMIT.name(),
            G_PROPS.QUEST_LEVEL.name(),


            PROPS.CLASS_PERK_GROUP.name(),
            PROPS.COLOR_THEME.name(),
            PROPS.DIFFICULTY.name(),
            PROPS.ITEM_RARITY.name(),
            PROPS.WEAPON_ATTACKS.name(),
            G_PROPS.UNIT_GROUP.name(), G_PROPS.CUSTOM_HERO_GROUP.name(),
            PROPS.BF_OBJ_MATERIAL.name(), PROPS.BF_OBJECT_SIZE.name(),
            PROPS.SUBDUNGEON_TYPE.name(),
            G_PROPS.DUNGEON_SUBFOLDER.name(), G_PROPS.ENCOUNTER_SUBGROUP.name(),
            MACRO_PROPS.SHOP_TYPE.name(), MACRO_PROPS.SHOP_LEVEL.name(),
            MACRO_PROPS.SHOP_MODIFIER.name(), G_PROPS.BUFF_TYPE.name(),
            G_PROPS.WORKSPACE_GROUP.name(), G_PROPS.BF_OBJECT_TYPE.name(),
            G_PROPS.BF_OBJECT_GROUP.name(), G_PROPS.BF_OBJECT_CLASS.name(), PROPS.AI_TYPE.name(),
            PROPS.AI_LOGIC.name(), G_PROPS.BACKGROUND.name(), PROPS.EFFECTS_WRAP.name(),
            G_PROPS.DUNGEON_GROUP.name(),
            G_PROPS.ENCOUNTER_TYPE.name(),
            G_PROPS.ENCOUNTER_GROUP.name(), G_PROPS.KEYS.name(), G_PROPS.ABILITY_GROUP.name(),
            G_PROPS.ABILITY_TYPE.name(), G_PROPS.ITEM_MATERIAL_GROUP.name(),
            G_PROPS.ITEM_TYPE.name(), G_PROPS.ITEM_GROUP.name(), G_PROPS.ARMOR_TYPE.name(),
            G_PROPS.ARMOR_GROUP.name(), PROPS.DAMAGE_TYPE.name(), G_PROPS.WEAPON_SIZE.name(),
            G_PROPS.WEAPON_CLASS.name(), G_PROPS.WEAPON_GROUP.name(), G_PROPS.WEAPON_TYPE.name(),
            G_PROPS.QUALITY_LEVEL.name(), G_PROPS.MATERIAL.name(), G_PROPS.SKILL_GROUP.name(),
            G_PROPS.ACTION_TYPE.name(), G_PROPS.MASTERY.name(), G_PROPS.SPELL_GROUP.name(),
            PROPS.RESISTANCE_TYPE.name(), G_PROPS.TARGETING_MODE.name(), G_PROPS.RACE.name(),
            G_PROPS.ASPECT.name(),
            G_PROPS.SOUNDSET.name(),
            G_PROPS.RANK.name(),
            G_PROPS.SPELL_TYPE.name(),
            // "DEITY",
            PROPS.BF_OBJECT_SIZE.name(), PROPS.OBJECT_ARMOR_TYPE.name(), PROPS.DIMENSION.name()};
    public static final String[] MULTIPLE_ENUM_LIST_IDS = {
            PROPS.PARAMETER_BONUSES.getName(),
            PROPS.ATTRIBUTE_BONUSES.getName(), PROPS.DUNGEON_TAGS.name(),
            PROPS.MASTERY_GROUPS_MAGIC.name(), PROPS.MASTERY_GROUPS_WEAPONS.name(),
            PROPS.MASTERY_GROUPS_MISC.name(), G_PROPS.SPELL_UPGRADE_GROUPS.name(),
            PROPS.JEWELRY_ITEM_TRAIT_REPERTOIRE.name(),
            PROPS.JEWELRY_PASSIVE_ENCHANTMENT_REPERTOIRE.name(), G_PROPS.BF_OBJECT_TAGS.name(),
            PROPS.ROLL_TYPES_TO_DISPEL_EACH_TURN.name(),
            PROPS.ROLL_TYPES_TO_SAVE.name(), PROPS.QUALITY_LEVEL_RANGE.name(),
            PROPS.ALLOWED_MATERIAL.name(), PROPS.MASTERY_PROGRESSION.name(),
            G_PROPS.VARIABLE_TYPES.name(), PROPS.ATTRIBUTE_PROGRESSION.name(),
            PROPS.GROWTH_PRIORITIES.name(), G_PROPS.STD_BOOLS.name(),
            PROPS.TARGETING_MODIFIERS.name(), G_PROPS.SPECIAL_REQUIREMENTS.name(),
            PROPS.STANDARD_ACTION_PASSIVES.name(), PROPS.STANDARD_SPELL_PASSIVES.name(),
            G_PROPS.STANDARD_PASSIVES.name(), G_PROPS.ACTION_TAGS.name(),
            G_PROPS.SPELL_TAGS.name(), G_PROPS.CLASSIFICATIONS.name(),};
    public static final String[] ENUM_LIST_IDS = {};
    public static final Class<?>[] ENUM_LIST_CLASSES = {SpellEnums.SPELL_GROUP.class};
    public static final Object[][] MULTI_TYPE_PAIRS = {
            {PROPS.SHRUNK_PRESET_GROUP.getName(), DC_TYPE.UNITS},
            {PROPS.FILLER_TYPES.getName(), DC_TYPE.UNITS},
            {PROPS.PRESET_GROUP.getName(), DC_TYPE.UNITS},
            {PROPS.EXTENDED_PRESET_GROUP.getName(), DC_TYPE.UNITS},
            {PROPS.REINFORCEMENT_CUSTOM.getName(), DC_TYPE.ENCOUNTERS},
            {PROPS.SHRUNK_PRESET_GROUP.getName(), DC_TYPE.UNITS},
            {PROPS.PERKS.getName(), DC_TYPE.UNITS},
            {PROPS.PERKS.getName(), DC_TYPE.PERKS},
            {PROPS.PERKS.getName(), DC_TYPE.PERKS},
            {PROPS.PERKS.getName(), DC_TYPE.PERKS},
    };
    public static final String[] MULTI_TYPE_LIST_IDS = {
            MACRO_PROPS.FACTIONS.getName(), MACRO_PROPS.AREAS.getName(),
            G_PROPS.DEITY.getName(),   PROPS.VERBATIM_PRIORITY.name(),
            PROPS.MEMORIZATION_PRIORITY.name(), PROPS.MAIN_HAND_REPERTOIRE.name(),
            PROPS.ARMOR_REPERTOIRE.name(), PROPS.OFF_HAND_REPERTOIRE.name(),
            PROPS.QUICK_ITEM_REPERTOIRE.name(),
            PROPS.CLASSES.name(), PROPS.INVENTORY.name(), PROPS.QUICK_ITEMS.name(),

    };
    public static final OBJ_TYPE[] MULTI_TYPE_LIST = {
            MACRO_OBJ_TYPES.FACTIONS,
            DC_TYPE.CHARS, DC_TYPE.FLOORS,
            DC_TYPE.FLOORS, MACRO_OBJ_TYPES.FACTIONS, DC_TYPE.UNITS,
            MACRO_OBJ_TYPES.AREA,
            DC_TYPE.DEITIES,
            DC_TYPE.SPELLS,
            DC_TYPE.SPELLS,
            DC_TYPE.SPELLS,
            // OBJ_TYPES.JEWELRY,
            DC_TYPE.WEAPONS, DC_TYPE.ARMOR,
            DC_TYPE.WEAPONS,
            DC_TYPE.ITEMS,

            DC_TYPE.CLASSES,
            C_OBJ_TYPE.ITEMS,
            C_OBJ_TYPE.QUICK_ITEMS,
            DC_TYPE.SKILLS,

            // C_OBJ_TYPE.UNITS, C_OBJ_TYPE.UNITS, C_OBJ_TYPE.UNITS,
            // C_OBJ_TYPE.UNITS, C_OBJ_TYPE.UNITS,
            DC_TYPE.UNITS, DC_TYPE.UNITS, DC_TYPE.UNITS, DC_TYPE.UNITS, DC_TYPE.UNITS,
            MACRO_OBJ_TYPES.PLACE, DC_TYPE.SKILLS, DC_TYPE.SKILLS, DC_TYPE.SKILLS,
            DC_TYPE.SPELLS, DC_TYPE.SPELLS, DC_TYPE.SPELLS, DC_TYPE.SPELLS,
            DC_TYPE.UNITS, DC_TYPE.UNITS, DC_TYPE.DEITIES, DC_TYPE.DEITIES,
            DC_TYPE.DEITIES,};
    public static final String[] SINGLE_TYPE_LIST_IDS = {

            MACRO_PROPS.PLAYER_FACTION.getName(),
            PROPS.FIRST_CLASS.getName(),
            PROPS.SECOND_CLASS.getName(),
            G_PROPS.MAIN_HAND_ITEM.name(), G_PROPS.OFF_HAND_ITEM.name(),
            G_PROPS.ARMOR_ITEM.name(),};
    public static final OBJ_TYPE[] SINGLE_TYPE_LIST = {
            MACRO_OBJ_TYPES.FACTIONS,
            DC_TYPE.CLASSES, DC_TYPE.CLASSES,
            DC_TYPE.UNITS, DC_TYPE.WEAPONS, DC_TYPE.WEAPONS, DC_TYPE.ARMOR,};
    public static final String[] MULTI_VAR_TYPE_IDS = {MACRO_PROPS.INTERNAL_ROUTES.getName(),};
    public static final Object[][] MULTI_VAR_TYPES = {new Object[]{MACRO_OBJ_TYPES.ROUTE,
            Integer.class}};
    public static final VALUE[] SPRITE_IDS = {
            PROPS.ANIM_SPRITE_CAST,
            PROPS.ANIM_SPRITE_RESOLVE,
            PROPS.ANIM_SPRITE_MAIN,
            PROPS.ANIM_SPRITE_IMPACT,
            PROPS.ANIM_SPRITE_AFTEREFFECT,
            PROPS.ANIM_MISSILE_SPRITE,
    };
    public static final String SPRITE_PATH =
            PathFinder.getSpritesPathFull();
    public static final VALUE[] SOUND_IDS = {
            PROPS.ANIM_SOUND_AFTEREFFECT,
            PROPS.ANIM_SOUND_RESOLVE,
            PROPS.ANIM_SOUND_MAIN,
            PROPS.ANIM_SOUND_IMPACT,
            PROPS.ANIM_SOUND_AFTEREFFECT,
            PROPS.ANIM_SOUND_MISSILE,
    };
    public static final VALUE[] VFX_IDS = {
            PROPS.ANIM_VFX_CAST,
            PROPS.ANIM_VFX_RESOLVE,
            PROPS.ANIM_VFX_MAIN,
            PROPS.ANIM_VFX_IMPACT,
            PROPS.ANIM_VFX_AFTEREFFECT,
            PROPS.ANIM_MISSILE_VFX,
    };
    public static final String[] SINGLE_RES_FILE_IDS = {};
    public static final String[] RES_FILE_KEYS = {};
    public static final String[] SINGLE_RES_FOLDER_IDS = {
            //            G_PROPS.SOUNDSET.name(),
    };
    public static final String[] RES_FOLDER_KEYS = {"sound\\soundsets\\",};
    public static final String[] RES_KEYS = {
            "img\\mini\\sprites\\impact",
            // DungeonMaster.getDungeonBackgroundFolder()
    };
    public static final String[] SINGLE_RES_LIST_IDS = {G_PROPS.IMPACT_SPRITE.name(),
            // PROPS.MAP_BACKGROUND.name(),
    };
    public static final String[] MULTI_RES_FILE_IDS = {
    };
    public static final String[] MULTI_RES_FILE_KEYS = {
            "XML\\dungeons\\levels\\battle\\",
            "XML\\groups\\",

    };
    public static final Condition[] TYPE_LIST_CONDITIONS = {new StringComparison("{SOURCE_"
            + MACRO_PROPS.REGION.getName() + "}", "{MATCH_" + MACRO_PROPS.REGION.getName() + "}",
            true),};
    public static final String[] CONDITIONAL_MULTI_LIST_IDS = {MACRO_PROPS.INTERNAL_ROUTES
            .getName()};
    public static final OBJ_TYPE[] CONDITIONAL_MULTI_TYPE_LIST = {MACRO_OBJ_TYPES.PLACE};
    public static final String[] GROUP_FILTERED = {

    };
    public static final String[] FILTER_GROUPS = {
    };
    public static final String[] SUBGROUP_FILTERED = {};
    public static final String[] FILTER_SUBGROUPS = {"Background",};
    public static String imgIdentifier = G_PROPS.IMAGE.getName();
    public static String actIdentifier = G_PROPS.ACTIVES.getName();
    public static String pasIdentifier = G_PROPS.PASSIVES.getName();
    public static String emblemIdentifier = G_PROPS.EMBLEM.getName();
    public static String soundsetIdentifier = G_PROPS.CUSTOM_SOUNDSET.getName();
    public static EDITOR soundChooser = new SoundChooser();
}
