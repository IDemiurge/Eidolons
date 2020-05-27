package eidolons.libgdx.bf.grid.moving;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;
import main.system.data.DataUnit;

import java.util.Set;

import static eidolons.libgdx.bf.GridMaster.getImagePath;
import static eidolons.libgdx.bf.grid.moving.PlatformData.PLATFORM_VALUE.*;

public class PlatformData extends DataUnit<PlatformData.PLATFORM_VALUE> {

    public PlatformData(String substring) {
        super(substring);
    }

    public static final PLATFORM_VALUE[] std_values = {
            max_speed, min_speed, acceleration, waitPeriod
    };

    public PlatformData(Set<Coordinates> cells) {
        for (PLATFORM_VALUE value : std_values) {
            setValue(value, getDefaultValue(value));
        }
        String c = "";
        for (Coordinates cell : cells) {
            c += cell.toString() + ";";
        }
        setValue(PLATFORM_VALUE.cells, c);
        setValue(cell_type, DungeonEnums.CELL_IMAGE.diamond);
    }

    public TextureRegion getTexture() {
        return TextureCache.getOrCreateR(
                getImagePath(
                        DungeonEnums.CELL_IMAGE.valueOf(getValue(cell_type)), 1));
    }

    @Override
    public Class<? extends PLATFORM_VALUE> getEnumClazz() {
        return PLATFORM_VALUE.class;
    }

    public int getDefaultValue(PLATFORM_VALUE value) {
        switch (value) {
            case max_speed:
                return 150;
            case min_speed:
                return 50;
            case acceleration:
                return 15;
            case waitPeriod:
                return 5;
        }
        return 0;
    }

    public enum PLATFORM_VALUE implements LevelStructure.EditableValue {
        cells,
        name,
        cell_type(LevelStructure.EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DungeonEnums.CELL_IMAGE.class;
            }
        },
        destination(LevelStructure.EDIT_VALUE_TYPE.coordinates),
        max_speed,
        min_speed,
        acceleration,
        waitPeriod,
        powered,
        onCycle;

        private LevelStructure.EDIT_VALUE_TYPE type;

        PLATFORM_VALUE(LevelStructure.EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        PLATFORM_VALUE() {
        }

        public LevelStructure.EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }

    }
}
