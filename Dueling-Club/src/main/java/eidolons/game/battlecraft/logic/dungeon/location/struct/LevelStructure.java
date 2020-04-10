package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import eidolons.system.audio.MusicMaster;
import main.content.CONTENT_CONSTS;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;

public class LevelStructure {
    public enum EDIT_VALUE_TYPE{
        text,
        number,
        none,
        enum_const,
        multi_enum_const,
        image,
        objType,
        file,

    }

    public interface EditableValue {
        EDIT_VALUE_TYPE getEditValueType();

        default Object getArg() {
            return null;
        }
    }

    public enum ZONE_VALUE implements EditableValue {

        name(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),
        wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        alt_wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        cell_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonLevel.CELL_IMAGE.class;
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
                return AmbienceDataSource.AMBIENCE_TEMPLATE.class;
            }
        },
       ;

        private EDIT_VALUE_TYPE type;

        ZONE_VALUE(EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }
    public enum BLOCK_VALUE implements EditableValue {


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
        wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        alt_wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        cell_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonLevel.CELL_IMAGE.class;
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
                return AmbienceDataSource.AMBIENCE_TEMPLATE.class;
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
        background,
        filepath,
        floor_type,

        //additional?
        lighting,
        fires_color,
        default_pillar_type,
        default_shard_type,
        //COMMON

        name(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),
        wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        alt_wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        cell_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonLevel.CELL_IMAGE.class;
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
                return AmbienceDataSource.AMBIENCE_TEMPLATE.class;
            }
        },

        ;
        private EDIT_VALUE_TYPE type;

        FLOOR_VALUES(){

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
        border_width,
        border_type,

        width_buffer,
        height_buffer,

        default_pillar_type,
        default_shard_type,

        tile_map, layer_data,



        name(EDIT_VALUE_TYPE.text),
        origin(EDIT_VALUE_TYPE.none),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        illumination(EDIT_VALUE_TYPE.number),
        wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        alt_wall_type(EDIT_VALUE_TYPE.objType){
            @Override
            public Object getArg() {
                return DC_TYPE.BF_OBJ;
            }
        },
        style(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.DUNGEON_STYLE.class;
            }
        },
        cell_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonLevel.CELL_IMAGE.class;
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
                return AmbienceDataSource.AMBIENCE_TEMPLATE.class;
            }
        },


        ;
        private EDIT_VALUE_TYPE type;

        MODULE_VALUE() {
        }

        MODULE_VALUE(EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
//        entrance, tile_map, layer_data,

        //RNG


    }
    public enum BORDER_TYPE {
        wall,
        chism,
        irregular,
        wall_and_chism, wall_alt,
        //obj type name for custom
    }

    public enum QUEST_DUNGEON_VALUES {

    }

    public enum BOSS_DUNGEON_VALUES {

    }

    public enum CAMPAIGN_VALUES {

    }

}
