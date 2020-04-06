package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import main.data.tree.LayeredData;
import main.system.data.DataUnit;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LevelStructure {
    public enum EDIT_VALUE_TYPE{
        text,
        number,
        enum_const,
        multi_enum_const,
        image,
        file,

    }

    public abstract static class StructureData<T extends Enum<T>, S extends LayeredData> extends DataUnit<T> {
        protected S structure;

        public StructureData(S structure) {
            this.structure = structure;
            init();
        }

        protected abstract void init();

        public abstract Class<? extends T> getEnumClazz();

        @Override
        public String[] getRelevantValues() {
            return Arrays.stream(getEnumClazz().getEnumConstants()).map(constant -> constant.toString()).
                    collect(Collectors.toList()).toArray(new String[0]);
        }

        public S getStructure() {
            return structure;
        }

        public void apply() {
        }
    }

    public interface EditableValue {
        EDIT_VALUE_TYPE getEditValueType();

        default Object getArg() {
            return null;
        }
    }

    public enum ZONE_VALUE {
        name,
        id,
        illumination,
        style,
        color_theme,
        ambience,

    }
    public enum BLOCK_VALUE implements EditableValue {
        name(),
        origin(),
        width(EDIT_VALUE_TYPE.number),
        height(EDIT_VALUE_TYPE.number),
        wall_type,
        alt_wall_type,

        zone(EDIT_VALUE_TYPE.number),
        cell_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonLevel.CELL_IMAGE.class;
            }
        },
        room_type(EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return LocationBuilder.ROOM_TYPE.class;
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

    public enum MODULE_VALUE { //enough to create a standalone floor
        name,
        width,
        height,
        origin,

        border_width,
        border_type,

        width_buffer,
        height_buffer,

        default_wall,
        default_style,

        ambience,
        lighting, //both hue and rays
        fires_color,
        vfx_template,
        default_pillar_type,
        default_shard_type,

        tile_map, layer_data,

//        entrance, tile_map, layer_data,

        //RNG


    }
    public enum BORDER_TYPE {
        wall,
        chism,
        irregular,
        wall_and_chism,
        //obj type name for custom
    }

    public enum FLOOR_VALUES {

    }
    public enum QUEST_DUNGEON_VALUES {

    }

    public enum BOSS_DUNGEON_VALUES {

    }

    public enum CAMPAIGN_VALUES {

    }

}
