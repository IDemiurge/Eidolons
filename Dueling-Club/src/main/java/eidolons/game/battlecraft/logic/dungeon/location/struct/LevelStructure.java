package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.netherflame.dungeons.QD_Enums;
import eidolons.libgdx.bf.decor.shard.ShardEnums;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.Soundscape;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;
import main.content.enums.system.MetaEnums;

public class LevelStructure {
    public enum EDIT_VALUE_TYPE {
        text,
        number,
        none,
        enum_const, dataUnit,
        multi_enum_const,
        image,
        objType,
        file, coordinates, script,

    }

    public interface EditableValue {
        EDIT_VALUE_TYPE getEditValueType();

        default Object getArg() {
            return null;
        }
    }

    public enum ZONE_VALUE implements EditableValue {
        id(EDIT_VALUE_TYPE.number),

        name(EDIT_VALUE_TYPE.text),
        background(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),
        cell_variant(EDIT_VALUE_TYPE.number),
        cell_variant_alt(EDIT_VALUE_TYPE.number),
        cell_set(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.CELL_SET.class;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        alt_color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        ambience(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.AMBIENCE.class;
            }
        },
        vfx_template(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return AmbienceDataSource.VFX_TEMPLATE.class;
            }
        },

        music_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.MUSIC_THEME.class;
            }
        },
        soundscape(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return Soundscape.SOUNDSCAPE.class;
            }
        },
        shard_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        shard_type_alt(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        ;

        private final EDIT_VALUE_TYPE type;

        ZONE_VALUE(EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }

    public enum BLOCK_VALUE implements EditableValue {

        id(EDIT_VALUE_TYPE.number),

        room_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return LocationBuilder.ROOM_TYPE.class;
            }
        },

        //COMMON

        name(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),
        cell_variant(EDIT_VALUE_TYPE.number),
        cell_variant_alt(EDIT_VALUE_TYPE.number),
        cell_set(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.CELL_SET.class;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        alt_color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        ambience(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.AMBIENCE.class;
            }
        },
        vfx_template(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return AmbienceDataSource.VFX_TEMPLATE.class;
            }
        },

        music_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.MUSIC_THEME.class;
            }
        },
        soundscape(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return Soundscape.SOUNDSCAPE.class;
            }
        },
        shard_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        shard_type_alt(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        ;
        EDIT_VALUE_TYPE type;

        BLOCK_VALUE() {
            this(EDIT_VALUE_TYPE.text);
        }

        BLOCK_VALUE(EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }

    public enum FLOOR_VALUES implements EditableValue {
        id(EDIT_VALUE_TYPE.number),
        background(LevelStructure.EDIT_VALUE_TYPE.file) {
            @Override
            public Object getArg() {
                return "resources/img/main/background";
            }
        },
        filepath,
        floor_type,
        location_type( EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.LOCATION_TYPE.class;
            }
        },
        //additional?
//        lighting,
//        fires_color,
//        default_,
//        default_shard_type,
        //COMMON
        readiness( EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MetaEnums.READINESS.class;
            }
        },
        name(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),

        cell_variant(EDIT_VALUE_TYPE.number),
        cell_variant_alt(EDIT_VALUE_TYPE.number),
        cell_set(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.CELL_SET.class;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        alt_color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        ambience(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.AMBIENCE.class;
            }
        },
        vfx_template(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return AmbienceDataSource.VFX_TEMPLATE.class;
            }
        },
        music_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.MUSIC_THEME.class;
            }
        },
        soundscape(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return Soundscape.SOUNDSCAPE.class;
            }
        },
        shard_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        shard_type_alt(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        start_module,
        module_grid,  cell_spans;
        private EDIT_VALUE_TYPE type;

        FLOOR_VALUES() {

        }

        FLOOR_VALUES(EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }

    public enum MODULE_VALUE implements EditableValue { //enough to create a standalone floor
        id(EDIT_VALUE_TYPE.number),
        readiness( EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MetaEnums.READINESS.class;
            }
        },
        border_width,
        border_type,

        width_buffer,
        height_buffer,

//        default_pillar_type,
//        default_shard_type,

        background,
        tile_map, layer_data,

        type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return QD_Enums.ModuleType.class;
            }
        },
        location(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return QD_Enums.QD_LOCATION.class;
            }
        },
        elevation(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return QD_Enums.ElevationLevel.class;
            }
        },

        name(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),

        cell_variant(EDIT_VALUE_TYPE.number),
        cell_variant_alt(EDIT_VALUE_TYPE.number),
        cell_set(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.CELL_SET.class;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },
        alt_color_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return CONTENT_CONSTS.COLOR_THEME.class;
            }
        },

        ambience(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.AMBIENCE.class;
            }
        },
        vfx_template(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return AmbienceDataSource.VFX_TEMPLATE.class;
            }
        },

        music_theme(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return MusicMaster.MUSIC_THEME.class;
            }
        },
        soundscape(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return Soundscape.SOUNDSCAPE.class;
            }
        },
        shard_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },
        shard_type_alt(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return ShardEnums.SHARD_TYPE.class;
            }
        },

        assets;
        private EDIT_VALUE_TYPE editValueType;

        MODULE_VALUE() {
        }

        MODULE_VALUE(EDIT_VALUE_TYPE type) {
            this.editValueType = type;
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return editValueType;
        }
//        entrance, tile_map, layer_data,

        //RNG


    }

    public enum BORDER_TYPE {
        wall,
        wall_alt,
        wall_chism,
        chism(true),
        irregular(true), irregular_void(true), irregular_plain(true),
        ;
        //obj type name for custom

        BORDER_TYPE() {
        }

        BORDER_TYPE(boolean chance) {
            this.chance = chance;
        }

        public boolean chance;
    }

    public enum QUEST_DUNGEON_VALUES {

    }

    public enum BOSS_DUNGEON_VALUES {

    }

    public enum CAMPAIGN_VALUES {

    }

}
