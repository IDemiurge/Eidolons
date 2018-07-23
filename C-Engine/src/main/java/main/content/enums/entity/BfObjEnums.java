package main.content.enums.entity;

/**
 * Created by JustMe on 2/14/2017.
 */
public class BfObjEnums {
    public enum BF_OBJECT_GROUP {
        WALL, COLUMNS, RUINS, CONSTRUCT, GATEWAY, GRAVES,

        WINDOWS, MAGICAL, HANGING, INTERIOR, STATUES,

        LOCK, ENTRANCE, TRAP, DOOR, LIGHT_EMITTER, CONTAINER, TREASURE,

        DUNGEON, WATER, TREES, ROCKS, VEGETATION, REMAINS, CRYSTAL,;
    }

    public enum BF_OBJECT_SIZE {

        TINY, SMALL, MEDIUM, LARGE, HUGE
    }

    public enum BF_OBJECT_TAGS {
        INDESTRUCTIBLE,
        PASSABLE,
        SUMMONED,
        COLLAPSABLE,
        ASSYMETRICAL,
        LANDSCAPE,
        OVERLAYING,
        WATER,
        ITEM,
        HUGE,
        LARGE
    }

    public enum BF_OBJECT_TYPE {
        NATURAL, STRUCTURE, PROP, SPECIAL
    }

    public enum BF_OBJ_MATERIAL {
        RED_OAK,
        IRONWOOD,
        BLACKWOOD,
        PALEWOOD,
        BILEWOOD,
        WAILWOOD,
        FEYWOOD,
        COTTON,
        SILK,
        IVORY,
        BLACK_BONE,
        MAN_BONE,
        DRAGON_BONE,
        THIN_LEATHER,
        TOUGH_LEATHER,
        THICK_LEATHER,
        FUR,
        LIZARD_SKIN,
        TROLL_SKIN,
        DRAGONHIDE,
        GRANITE,
        MARBLE,
        ONYX,
        OBSIDIAN,
        CRYSTAL,
        SOULSTONE,
        STAR_EMBER,
        SILVER,
        GOLD,
        COPPER,
        BRASS,
        BRONZE,
        IRON,
        STEEL,
        MITHRIL,
        PLATINUM,
        ADAMANTIUM,
        METEORITE,
        BRIGHT_STEEL,
        DEFILED_STEEL,
        DARK_STEEL,
        WRAITH_STEEL,
        PALE_STEEL,
        WARP_STEEL,
        DEMON_STEEL,
        MOON_SILVER,
        ELDRITCH_STEEL
    }

    public enum BF_OBJ_QUALITY {
        TOUGH,
        CRUMBLING,
        BRITTLE,
        DURABLE,
        RESISTANT,
        ARMORED,
        THICK,
        TOUGH_II,
        CRUMBLING_II,
        BRITTLE_II,
        DURABLE_II,
        RESISTANT_II,
        ARMORED_II,
        THICK_II,
        TOUGH_III,
        CRUMBLING_III,
        BRITTLE_III,
        DURABLE_III,
        RESISTANT_III,
        ARMORED_III,
        THICK_III,
    }

    public enum BF_OBJ_TYPES implements OBJ_TYPE_ENUM {
        AETHER_FONT,
        EYE_OF_THE_WARP,
        AETHER_SPHERE,
        ARCANE_SIGIL,
        HOLY_SIGIL,
        CHAOS_SIGIL,
        SACRED_ALTAR,
        DEMON_SIGIL,
        OBLIVION_SIGIL,
        VOID_DOORWAY,
        WATCHERS_IN_THE_WALLS,
        SNAKE_TRAP,
        BALLISTA_TRAP,
        ALCHEMIST_SET,
        ALCHEMIST_LABORATORY,
        WIZARDRY_TOOLS,
        WIZARD_TABLE,
        WIZARD_CLOSET,
        WEAPONS_RACK,
        ARMOR_SUIT,
        ARMOR_STAND,
        ARMORY_WALL,
        STONE_KNIGHT,
        DEMON_STATUE,
        ANGEL_STATUE,
        OCCULT_STATUE,
        LIBRARY_SHELF,
        BOOK_SHELF,
        SKULL_PILE,
        GOLDEN_KNIGHT,
        ALCHEMICAL_DEVICE,
        IRON_KNIGHT,
        STUDY_TABLE,
        BATTLE_REMAINS,
        ALCHEMY_ENGINE,
        ANVIL,
        FORGE,
        WIZARD_TRINKETS,
        MYSTIC_POOL,
        TORTURE_CHAIR,
        OVEN,
        LIGHTBRINGER,
        DECEIVER,
        TWILIGHT_ANGEL,
        DARK_ONE,
        SOULGEM,
        SILVER_KNIGHT,
        GARGOYLE_STATUE,
        GARGOYLE_GUARDIAN,
        MARBLE_GARGOYLE,
        GEAR_MECHANISM,
        STEAM_ENGINE,
        WALL_GEARS,
        TAVERN_TABLE,
        GAMBLING_TABLE,
        HUNG_SKELETON,
        CLOCKWORK_DEVICE,
        IRON_GRID,
        DARK_PENDULUM,
        DARK_ANGEL_STATUE,
        EXPERIMENT_DEVICE,
        KNIGHTLY_ARMOR,
        KNIGHTLY_WEAPONS,
        SWORD_STAND,
        ARMORY_STAND,
        TORTURE_DEVICE,
        RACK,
        DWARF_STATUE,
        INSIGNIA,
        DRAIN,
        SEWER_BARS,
        TUNNEL_GRID,
        STONE_ARCH,
        SEWER,
        WATER_CHANNEL,
        FLOOR_GRID,
        WOODEN_TABLE,
        WOODEN_BENCH,
        SNAKE_CARVING,
        ELDER_STATUE,
        KNIGHTUNICODE39CODEENDS_SHIELD,
        LIBRARY_WALL,
        BOOKCASE,
        MOSSY_STATUE,
        FORGOTTEN_GOD,
        IMPALED_SKULL,
        ELVEN_STATUE,
        CROSSBOW_STAND,
        WEAPON_CLOSET,
        CLOSET,
        RUNE_INSCRIPTION,
        KNIGHT_EMBLEM,
        DARK_EMBLEM,
        WALL_SHIELD,
        HUNTER_TROPHY,
        HANGING_SWORDS,
        HANGING_SHIELD,
        GRAVESTONE,
        FRESH_GRAVE,
        DESECRATED_GRAVE,
        ALTAR,
        DRAGON_ALTAR,
        SEALED_SARCOPHAGUS,
        SARCOPHAGUS,
        COFFIN,
        OVERGROWN_GRAVE,
        OVERGROWN_TOMBSTONE,
        TOMBSTONE,
        NOBLE_GRAVESTONE,
        CRUSADER_SHIELD,
        TITAN_HEAD,
        DEVIL_STATUE,
        FALLEN_STATUE,
        BROKEN_STATUE,
        GLOWING_GLYPH,
        MAGIC_CIRCLES,
        BROKEN_SERPENT_STATUE,
        SATYR_STATUE,
        CATHEDRAL_GARGOYLE,
        COBWEBBED_STATUE,
        ARCANE_APPARATUS,
        ARCANE_MACHINES,
        WITCH_STATUES,
        ELECTRIFIER,
        TRANSLOCATOR,
        CHARGER,
        ICICLES,
        ICE_CRUST,
        GRAIN_SACKS,
        TELESCOPE,
        GLOBE,
        CONCENTRIC_RINGS,
        ORRERY,
        ASTROLABE,
        CATHEDRAL_CLOCK,
        GEAR_MACHINE,
        ICE_SHELL,
        BED,
        DUMMY_HUNG_OBJ,
        ELDRITCH_RUNE,
        FIERY_RUNE,
        ANCIENT_RUNE,
        SWORD_RACK,
        SPEAR_RACK,
        AXE_RACK,
        HAMMER_RACK,
        HALBERT_RACK,
        GREATSWORD_RACK,
        PRISTINE_GEMSTONE,
        PRISTINE_AMETHYST,
        DESECRATED_SARCOPHAGUS,
        LORDUNICODE39CODEENDS_TOMB,
        TOMB_NICHE,
        DOOR,
        IRON_DOOR,
        STONE_DOOR,
        CEMETARY_GATE,
        CEMETARY_GATE_OPEN,
        CEMETARY_GATE_BLOCKED,
        IRON_BARS,
        VAULT_DOOR,
        BARRED_DOOR,
        HEAVY_DOOR,
        TREASURE_CHEST,
        RUSTY_CHEST,
        TREASURE_PILE,
        CRATE,
        BARRELS,
        STURDY_CHEST,
        BARREL,
        OLD_CHEST,
        SILVER_CHEST,
        IRON_CHEST,
        ARMORED_CHEST,
        DARK_TUNNEL,
        STAIRS,
        PORTAL,
        WINDING_STAIRS,
        SPIRAL_STAIRWAY,
        TOWER_STAIRS,
        DARK_ENTRANCE,
        CAVE_ENTRANCE,
        TOMB_PORTAL,
        INSECT_TUNNEL,
        STAIRCASE,
        BARROW_ENTRANCE,
        MAUSOLEUM_STAIRS,
        UPPER_STAIRS,
        HEAVY_DOORS,
        BRAZIER,
        SKULL_TORCH,
        TORCH,
        LANTERN,
        WITCHFIRE_BRAZIER,
        FIREPIT,
        BONFIRE,
        OFFERING_FIRE,
        BURNING_SKULL,
        FIERY_SKULL,
        TORCH_COLUMN,
        CANDLES,
        PRISM,
        POWER_WARDS,
        ELDRITCH_ROD,
        HANGING_BRAZIER,
        DEATH_PIT,
        ELVEN_LANTERN,
        CASTLE_WINDOW,
        ELVEN_BRAZIER,
        COBWEBBED_CRATE,
        PRESTINE_STAIRS,
        DARK_STAIRCASE,
        TEMPLE_WINDOWS,
        PRESTINE_CHEST,
        ESSENCE_VAULT,
        FOCUS_VAULT,
        EERIE_PORTAL,
        PADLOCK,
        WOODEN_DOOR,
        CRUDE_DOOR,
        DARK_DOOR,
        STONE_GATE,
        PALACE_GATE,
        CASTLE_GATE,
        ANCIENT_DOOR,
        BONE_DOOR,
        SKULL_DOOR,
        DWARVEN_DOOR,
        ORNATE_DOOR,
        ENCHANTED_ORNATE_DOOR,
        PURPLE_ENCHANTED_ORNATE_DOOR,
        RED_ENCHANTED_ORNATE_DOOR,
        BLUE_ENCHANTED_ORNATE_DOOR,
        TEAL_ENCHANTED_ORNATE_DOOR,
        DWARVEN_RUNE_DOOR,
        AMETHYST_LANTERN,
        SAPPHIRE_LANTERN,
        DIAMOND_LANTERN,
        RUBY_LANTERN,
        TOPAZ_LANTERN,
        UPWARD_STAIRS,
        DOWNWARD_STAIRS,
        WINDING_DOWNWARD_STAIRS,
        WINDING_UPWARD_STAIRS,
        DARK_WINDING_UPWARD_STAIRS,
        DARK_WINDING_DOWNWARD_STAIRS,
        WIDE_UPWARD_STAIRS,
        WIDE_DARK_UPWARD_STAIRS,
        WIDE_DOWNWARD_STAIRS,
        HANGING_WITCHFIRE_BRAZIER,
        HANGING_NETHERFLAME_BRAZIER,
        HANGING_HOLY_FIRE_BRAZIER,
        EMERALD_LANTERN,
        NETHERFLAME_BRAZIER,
        COLDFIRE_BRAZIER,
        HANGING_HELLFIRE_BRAZIER,
        GLOWING_RUNES,
        GLOWING_SILVER_RUNE,
        GLOWING_ARCANE_RUNE,
        HELLFIRE_BRAZIER,
        HOLY_FLAME_BRAZIER,
        BONE_DOOR_ENCHANTED,
        CRIMSON_DOOR,
        CAVE_EXIT,
        ASH_URN,
        ENCHANTED_ASH_URN,
        FALLEN_COLUMN,
        STONE_WALL,
        CAVE_WALL,
        DWARVEN_WALL,
        BRICK_WALL,
        ICE_WALL,
        BONE_WALL,
        SECRET_WALL,
        WOODEN_WALL,
        ICE_BLOCK,
        WOODEN_CORNER,
        WOODEN_PLANKS,
        ROTTEN_PLANKS,
        SOLID_ROCK,
        INSCRIBED_WALL,
        RUNIC_WALL,
        SCARRED_ROCK,
        TILED_WALL,
        IRON_WALL,
        RUNESTONE_WALL,
        IRON_FENCE,
        OVERGROWN_FENCE,
        DELAPIDATED_FENCE,
        FENCE,
        RUINED_WALL,
        THICKET,
        MOSS_WALL,
        THORN_WALL,
        DEAD_BRANCHES,
        OVERGROWN_WALL,
        MOSSUNICODE45CODEENDCOVERED_WALL,
        MOSSY_WALL,
        WHITE_MARBLE_WALL,
        BLACK_MARBLE_WALL,
        BLUE_MARBLE_WALL,
        WOODEN_FENCE,
        CRUMBLING_WALL,
        ORNAMENTED_WOODEN_WALL,
        FORCE_FIELD,
        MARBLE_COLUMN,
        RUINED_STRUCTURE,
        RUINED_COLUMN,
        RUINED_GATEWAY,
        OBELISK,
        TOWER,
        TOWER_OPENING,
        TENT,
        RUINED_MASONRY,
        WELL,
        COLUMN,
        ORNAMENTED_COLUMN,
        PARAPET,
        LIONHEAD_FONTAIN,
        ANCIENT_FONTAIN,
        DARK_FONTAIN,
        WATERY_ALTAR,
        GOLDEN_FOUNTAIN,
        WATERWORKS,
        WATER_RESERVOIRE,
        MONOLITH,
        FOUNDATION,
        RUNE_COLUMN,
        MEMORIAL_STONE,
        OVERGROWN_COLUMN,
        WAR_TENT,
        ARCANE_GATEWAY,
        CHAOS_GATEWAY,
        SHADOW_GATEWAY,
        LUCENT_GATEWAY,
        LIFE_GATEWAY,
        DEATH_GATEWAY,
        OBLIVION_GATE,
        NETHER_GATE,
        ABYSSAL_GATE,
        COBWEBBED_THICKET,
        COBWEBBED_COLUMN,
        DARK_COLUMN,
        CANNON,
        CRYSTAL_SHRINE,
        ARCHWAY,
        SNOWCOVERED_RUINS,
        WOODEN_ARCHWAY,
        SHACK,
        WALL_OF_SKULLS,
        OLD_STONE_WALL,
        ANCIENT_WALL,
        VOLCANIC_WALL,
        JAGGED_STONE_WALL,
        SMOOTH_STONE_WALL,
        CARVED_STONE_WALL,
        ELDRITCH_SPHERE,
        ELDRITCH_SHRINE,
        COSMIC_CRYSTAL,
        CHAOS_CRYSTAL,
        ARCANE_CRYSTAL,
        LUCENT_CRYSTAL,
        DEATH_CRYSTAL,
        DARK_CRYSTAL,
        LIFE_CRYSTAL,
        ROCKS,
        ANCIENT_OAK,
        TREE_SAPLING,
        DEAD_TREE,
        OAK,
        FALLEN_TREE,
        MOSSY_BOULDER,
        SLEEK_ROCK,
        SHRUB,
        STALAGMITE,
        STALACTITE,
        NATURAL_COLUMN,
        REMAINS,
        OLD_BONES,
        DESECRATED_REMAINS,
        SHATTERED_REMAINS,
        ICE_SPIKE,
        TREE_STUMP,
        MOSSY_ROCKS,
        FOREST_CRAGS,
        VAMPIRE_REMAINS,
        DARK_TREE,
        GIANT_TREE,
        ELDER_TREE,
        WITCH_TREE,
        GNARLED_TREE,
        MISSHAPEN_TREE,
        HAUNTED_TREE,
        OLD_TREE,
        YOUNG_OAK,
        UNDERGROUND_COLUMN,
        RUNESTONE,
        DARKENED_POND,
        TREE_ROOTS,
        MOSSY_ROOTS,
        DARK_WATER,
        WATER,
        SHALLOW_WATER,
        FUNGI_VERDE,
        TRANSLUCENT_FUNGI,
        AMETHYST,
        TOPAZ,
        RUBY,
        SAPPHIRE,
        COBWEBBED_TREE,
        COBWEBBED_BRANCHES,
        COBWEBBED_SKULL,
        FUNGI_COVER,
        FUNGI_COVERED_STUMP,
        BOVINE_SKULL,
        BROKEN_SKELETON,
        IMP_STOOL,
        ICY_SPRING,
        WATERFALL,
        ICY_BROOK,
        CHARRED_STUMP,
        BURNING_STUMP,
        DECOMPOSING_CORPSE,
        CHARRED_REMAINS,
        PUTRID_REMAINS,
        DECOMPOSING_REMAINS,
        ANCIENT_REMAINS,
        ANCIENT_SKULL,
        GIANT_MUSHROOM,
        GIANT_LUMINESCENT_MUSHROOM,
        LIMINESCENT_FUNGI,
        LILAMORD,
        FEL_FUNGI,
        YELLOW_LIMINESCENT_FUNGI,
        PURPLE_LIMINESCENT_FUNGI,
        GREEN_LIMINESCENT_FUNGI,;
    }

    public enum BF_OBJ_TYPES_ implements OBJ_TYPE_ENUM {
        CASTLE_WINDOW,
        TEMPLE_WINDOWS,
        FALLEN_COLUMN,
        STONE_WALL,
        CAVE_WALL,
        DWARVEN_WALL,
        BRICK_WALL,
        ICE_WALL,
        BONE_WALL,
        SECRET_WALL,
        WOODEN_WALL,
        ICE_BLOCK,
        WOODEN_CORNER,
        WOODEN_PLANKS,
        ROTTEN_PLANKS,
        SOLID_ROCK,
        INSCRIBED_WALL,
        RUNIC_WALL,
        SCARRED_ROCK,
        TILED_WALL,
        IRON_WALL,
        RUNESTONE_WALL,
        THICKET,
        MOSS_WALL,
        THORN_WALL,
        DEAD_BRANCHES,
        OVERGROWN_WALL,
        MOSSUNICODE45CODEENDCOVERED_WALL,
        MOSSY_WALL,
        WHITE_MARBLE_WALL,
        BLACK_MARBLE_WALL,
        BLUE_MARBLE_WALL,
        WOODEN_FENCE,
        CRUMBLING_WALL,
        ORNAMENTED_WOODEN_WALL,
        FORCE_FIELD,
        COBWEBBED_THICKET,
        WALL_OF_SKULLS,
        OLD_STONE_WALL,
        ANCIENT_WALL,
        VOLCANIC_WALL,
        JAGGED_STONE_WALL,
        SMOOTH_STONE_WALL,
        CARVED_STONE_WALL,
        ELDRITCH_SPHERE,
        ELDRITCH_SHRINE,;
    }

    public enum BF_OBJ_TYPES_CONTAINER implements OBJ_TYPE_ENUM {
        CRATE,
        BARRELS,
        BARREL,
        COBWEBBED_CRATE,
        ASH_URN,
        ENCHANTED_ASH_URN,;
    }

    public enum BF_OBJ_TYPES_DOOR implements OBJ_TYPE_ENUM {
        DOOR,
        IRON_DOOR,
        STONE_DOOR,
        CEMETARY_GATE,
        CEMETARY_GATE_OPEN,
        CEMETARY_GATE_BLOCKED,
        IRON_BARS,
        VAULT_DOOR,
        BARRED_DOOR,
        HEAVY_DOOR,
        WOODEN_DOOR,
        CRUDE_DOOR,
        DARK_DOOR,
        STONE_GATE,
        PALACE_GATE,
        CASTLE_GATE,
        ANCIENT_DOOR,
        BONE_DOOR,
        SKULL_DOOR,
        DWARVEN_DOOR,
        ORNATE_DOOR,
        ENCHANTED_ORNATE_DOOR,
        PURPLE_ENCHANTED_ORNATE_DOOR,
        RED_ENCHANTED_ORNATE_DOOR,
        BLUE_ENCHANTED_ORNATE_DOOR,
        TEAL_ENCHANTED_ORNATE_DOOR,
        DWARVEN_RUNE_DOOR,
        BONE_DOOR_ENCHANTED,
        CRIMSON_DOOR,;
    }

    public enum BF_OBJ_TYPES_DUNGEON implements OBJ_TYPE_ENUM {
        STALAGMITE,
        STALACTITE,
        NATURAL_COLUMN,
        UNDERGROUND_COLUMN,
        FUNGI_VERDE,
        TRANSLUCENT_FUNGI,
        FUNGI_COVER,
        FUNGI_COVERED_STUMP,
        IMP_STOOL,
        GIANT_MUSHROOM,
        GIANT_LUMINESCENT_MUSHROOM,
        LIMINESCENT_FUNGI,
        LILAMORD,
        FEL_FUNGI,
        YELLOW_LIMINESCENT_FUNGI,
        PURPLE_LIMINESCENT_FUNGI,
        GREEN_LIMINESCENT_FUNGI,;
    }

    public enum BF_OBJ_TYPES_GEM implements OBJ_TYPE_ENUM {
        AMETHYST,
        TOPAZ,
        RUBY,
        SAPPHIRE,;
    }

    public enum BF_OBJ_TYPES_GRAVES implements OBJ_TYPE_ENUM {
        GRAVESTONE,
        FRESH_GRAVE,
        DESECRATED_GRAVE,
        SEALED_SARCOPHAGUS,
        SARCOPHAGUS,
        COFFIN,
        OVERGROWN_GRAVE,
        OVERGROWN_TOMBSTONE,
        TOMBSTONE,
        NOBLE_GRAVESTONE,
        DESECRATED_SARCOPHAGUS,
        LORDUNICODE39CODEENDS_TOMB,
        TOMB_NICHE,;
    }

    public enum BF_OBJ_TYPES_LIGHT_EMITTERS implements OBJ_TYPE_ENUM {
        BRAZIER,
        SKULL_TORCH,
        TORCH,
        LANTERN,
        WITCHFIRE_BRAZIER,
        FIREPIT,
        BONFIRE,
        OFFERING_FIRE,
        BURNING_SKULL,
        FIERY_SKULL,
        TORCH_COLUMN,
        CANDLES,
        PRISM,
        POWER_WARDS,
        ELDRITCH_ROD,
        HANGING_BRAZIER,
        ELVEN_LANTERN,
        ELVEN_BRAZIER,
        AMETHYST_LANTERN,
        SAPPHIRE_LANTERN,
        DIAMOND_LANTERN,
        RUBY_LANTERN,
        TOPAZ_LANTERN,
        HANGING_WITCHFIRE_BRAZIER,
        HANGING_NETHERFLAME_BRAZIER,
        HANGING_HOLY_FIRE_BRAZIER,
        EMERALD_LANTERN,
        NETHERFLAME_BRAZIER,
        COLDFIRE_BRAZIER,
        HANGING_HELLFIRE_BRAZIER,
        GLOWING_RUNES,
        GLOWING_SILVER_RUNE,
        GLOWING_ARCANE_RUNE,
        HELLFIRE_BRAZIER,
        HOLY_FLAME_BRAZIER,;
    }

    public enum BF_OBJ_TYPES_LOCK implements OBJ_TYPE_ENUM {
        PADLOCK,;
    }

    public enum BF_OBJ_TYPES_MAGICAL implements OBJ_TYPE_ENUM {
        AETHER_FONT,
        EYE_OF_THE_WARP,
        AETHER_SPHERE,
        ARCANE_SIGIL,
        HOLY_SIGIL,
        CHAOS_SIGIL,
        SACRED_ALTAR,
        DEMON_SIGIL,
        OBLIVION_SIGIL,
        VOID_DOORWAY,
        WATCHERS_IN_THE_WALLS,
        SNAKE_TRAP,
        BALLISTA_TRAP,
        CATHEDRAL_CLOCK,;
    }

    public enum BF_OBJ_TYPES_PROP implements OBJ_TYPE_ENUM {
        ALCHEMIST_SET,
        ALCHEMIST_LABORATORY,
        WIZARDRY_TOOLS,
        WIZARD_TABLE,
        WIZARD_CLOSET,
        WEAPONS_RACK,
        ARMOR_SUIT,
        ARMOR_STAND,
        ARMORY_WALL,
        STONE_KNIGHT,
        DEMON_STATUE,
        ANGEL_STATUE,
        OCCULT_STATUE,
        LIBRARY_SHELF,
        BOOK_SHELF,
        SKULL_PILE,
        GOLDEN_KNIGHT,
        ALCHEMICAL_DEVICE,
        IRON_KNIGHT,
        STUDY_TABLE,
        BATTLE_REMAINS,
        ALCHEMY_ENGINE,
        ANVIL,
        FORGE,
        WIZARD_TRINKETS,
        MYSTIC_POOL,
        TORTURE_CHAIR,
        OVEN,
        LIGHTBRINGER,
        DECEIVER,
        TWILIGHT_ANGEL,
        DARK_ONE,
        SOULGEM,
        SILVER_KNIGHT,
        GARGOYLE_STATUE,
        GARGOYLE_GUARDIAN,
        MARBLE_GARGOYLE,
        GEAR_MECHANISM,
        STEAM_ENGINE,
        WALL_GEARS,
        TAVERN_TABLE,
        GAMBLING_TABLE,
        HUNG_SKELETON,
        CLOCKWORK_DEVICE,
        IRON_GRID,
        DARK_PENDULUM,
        DARK_ANGEL_STATUE,
        EXPERIMENT_DEVICE,
        KNIGHTLY_ARMOR,
        KNIGHTLY_WEAPONS,
        SWORD_STAND,
        ARMORY_STAND,
        TORTURE_DEVICE,
        RACK,
        DWARF_STATUE,
        INSIGNIA,
        DRAIN,
        SEWER_BARS,
        TUNNEL_GRID,
        STONE_ARCH,
        SEWER,
        WATER_CHANNEL,
        FLOOR_GRID,
        WOODEN_TABLE,
        WOODEN_BENCH,
        SNAKE_CARVING,
        ELDER_STATUE,
        KNIGHTUNICODE39CODEENDS_SHIELD,
        LIBRARY_WALL,
        BOOKCASE,
        MOSSY_STATUE,
        FORGOTTEN_GOD,
        IMPALED_SKULL,
        ELVEN_STATUE,
        CROSSBOW_STAND,
        WEAPON_CLOSET,
        CLOSET,
        RUNE_INSCRIPTION,
        KNIGHT_EMBLEM,
        DARK_EMBLEM,
        WALL_SHIELD,
        HUNTER_TROPHY,
        HANGING_SWORDS,
        HANGING_SHIELD,
        CRUSADER_SHIELD,
        TITAN_HEAD,
        DEVIL_STATUE,
        FALLEN_STATUE,
        BROKEN_STATUE,
        GLOWING_GLYPH,
        MAGIC_CIRCLES,
        BROKEN_SERPENT_STATUE,
        SATYR_STATUE,
        CATHEDRAL_GARGOYLE,
        COBWEBBED_STATUE,
        ARCANE_APPARATUS,
        ARCANE_MACHINES,
        WITCH_STATUES,
        ELECTRIFIER,
        TRANSLOCATOR,
        CHARGER,
        ICICLES,
        ICE_CRUST,
        GRAIN_SACKS,
        TELESCOPE,
        GLOBE,
        CONCENTRIC_RINGS,
        ORRERY,
        ASTROLABE,
        GEAR_MACHINE,
        ICE_SHELL,
        BED,
        DUMMY_HUNG_OBJ,
        ELDRITCH_RUNE,
        FIERY_RUNE,
        ANCIENT_RUNE,
        SWORD_RACK,
        SPEAR_RACK,
        AXE_RACK,
        HAMMER_RACK,
        HALBERT_RACK,
        GREATSWORD_RACK,
        PRISTINE_GEMSTONE,
        PRISTINE_AMETHYST,;
    }

    public enum BF_OBJ_TYPES_REMAINS implements OBJ_TYPE_ENUM {
        REMAINS,
        OLD_BONES,
        DESECRATED_REMAINS,
        SHATTERED_REMAINS,
        VAMPIRE_REMAINS,
        COBWEBBED_SKULL,
        BOVINE_SKULL,
        BROKEN_SKELETON,
        DECOMPOSING_CORPSE,
        CHARRED_REMAINS,
        PUTRID_REMAINS,
        DECOMPOSING_REMAINS,
        ANCIENT_REMAINS,
        ANCIENT_SKULL,;
    }

    public enum BF_OBJ_TYPES_ROCKS implements OBJ_TYPE_ENUM {
        ROCKS,
        MOSSY_BOULDER,
        SLEEK_ROCK,
        ICE_SPIKE,
        MOSSY_ROCKS,
        FOREST_CRAGS,
        RUNESTONE,;
    }

    public enum BF_OBJ_TYPES_RUINS implements OBJ_TYPE_ENUM {
        RUINED_WALL,
        RUINED_STRUCTURE,
        RUINED_COLUMN,
        RUINED_GATEWAY,
        RUINED_MASONRY,
        SNOWCOVERED_RUINS,;
    }

    public enum BF_OBJ_TYPES_STANDARD implements OBJ_TYPE_ENUM {
        ALTAR,
        DRAGON_ALTAR,
        DARK_TUNNEL,
        STAIRS,
        PORTAL,
        WINDING_STAIRS,
        SPIRAL_STAIRWAY,
        TOWER_STAIRS,
        DARK_ENTRANCE,
        CAVE_ENTRANCE,
        TOMB_PORTAL,
        INSECT_TUNNEL,
        STAIRCASE,
        BARROW_ENTRANCE,
        MAUSOLEUM_STAIRS,
        UPPER_STAIRS,
        HEAVY_DOORS,
        DEATH_PIT,
        PRESTINE_STAIRS,
        DARK_STAIRCASE,
        EERIE_PORTAL,
        UPWARD_STAIRS,
        DOWNWARD_STAIRS,
        WINDING_DOWNWARD_STAIRS,
        WINDING_UPWARD_STAIRS,
        DARK_WINDING_UPWARD_STAIRS,
        DARK_WINDING_DOWNWARD_STAIRS,
        WIDE_UPWARD_STAIRS,
        WIDE_DARK_UPWARD_STAIRS,
        WIDE_DOWNWARD_STAIRS,
        CAVE_EXIT,
        MARBLE_COLUMN,
        OBELISK,
        COLUMN,
        ORNAMENTED_COLUMN,
        PARAPET,
        LIONHEAD_FONTAIN,
        ANCIENT_FONTAIN,
        DARK_FONTAIN,
        WATERY_ALTAR,
        GOLDEN_FOUNTAIN,
        WATERWORKS,
        WATER_RESERVOIRE,
        MONOLITH,
        FOUNDATION,
        RUNE_COLUMN,
        MEMORIAL_STONE,
        OVERGROWN_COLUMN,
        ARCANE_GATEWAY,
        CHAOS_GATEWAY,
        SHADOW_GATEWAY,
        LUCENT_GATEWAY,
        LIFE_GATEWAY,
        DEATH_GATEWAY,
        OBLIVION_GATE,
        NETHER_GATE,
        ABYSSAL_GATE,
        COBWEBBED_COLUMN,
        DARK_COLUMN,
        CANNON,
        CRYSTAL_SHRINE,
        ARCHWAY,
        WOODEN_ARCHWAY,
        SHACK,
        COSMIC_CRYSTAL,
        CHAOS_CRYSTAL,
        ARCANE_CRYSTAL,
        LUCENT_CRYSTAL,
        DEATH_CRYSTAL,
        DARK_CRYSTAL,
        LIFE_CRYSTAL,
        SHRUB,
        DARKENED_POND,
        TREE_ROOTS,
        MOSSY_ROOTS,
        DARK_WATER,
        WATER,
        SHALLOW_WATER,
        ICY_SPRING,
        WATERFALL,
        ICY_BROOK,;
    }

    public enum BF_OBJ_TYPES_STRUCTURES implements OBJ_TYPE_ENUM {
        IRON_FENCE,
        OVERGROWN_FENCE,
        DELAPIDATED_FENCE,
        FENCE,
        TOWER,
        TOWER_OPENING,
        TENT,
        WELL,
        WAR_TENT,;
    }

    public enum BF_OBJ_TYPES_TREASURE implements OBJ_TYPE_ENUM {
        TREASURE_CHEST,
        RUSTY_CHEST,
        TREASURE_PILE,
        STURDY_CHEST,
        OLD_CHEST,
        SILVER_CHEST,
        IRON_CHEST,
        ARMORED_CHEST,
        PRESTINE_CHEST,
        ESSENCE_VAULT,
        FOCUS_VAULT,;
    }

    public enum BF_OBJ_TYPES_TREES implements OBJ_TYPE_ENUM {
        ANCIENT_OAK,
        TREE_SAPLING,
        DEAD_TREE,
        OAK,
        FALLEN_TREE,
        TREE_STUMP,
        DARK_TREE,
        GIANT_TREE,
        ELDER_TREE,
        WITCH_TREE,
        GNARLED_TREE,
        MISSHAPEN_TREE,
        HAUNTED_TREE,
        OLD_TREE,
        YOUNG_OAK,
        COBWEBBED_TREE,
        COBWEBBED_BRANCHES,
        CHARRED_STUMP,
        BURNING_STUMP,;
    }

    public enum BF_OBJ_WEIGHT {
        TINY,

        COLOSSAL,
    }
}
