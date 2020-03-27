package main.level_editor.backend.struct;

public class LE_Structure {

    public enum MODULE_VALUES { //enough to create a standalone floor
        name,
        width,
        height,
        zones,
        replace_default,
        default_wall,
        default_style,

        ambience,
        lighting, //both hue and rays
        fires_color,
        vfx_template,
        default_pillar_type,
        default_shard_type,

        irregular_border,
        border_wall,
        border_wall_type,
        border_void,

        entrance, tile_map, layer_data,

        //RNG


    }
    public enum FLOOR_VALUES{

    }
    public enum QUEST_DUNGEON_VALUES{

    }
    public enum BOSS_DUNGEON_VALUES{

    }
    public enum CAMPAIGN_VALUES{

    }

}
