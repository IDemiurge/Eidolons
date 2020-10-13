package eidolons.libgdx.bf.grid.moving;


import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.bf.grid.moving.PlatformCell.PLATFORM_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import java.util.Set;

import static eidolons.libgdx.bf.grid.moving.PlatformData.PLATFORM_VALUE.*;

public class PlatformData extends DataUnit<PlatformData.PLATFORM_VALUE> {

    public PlatformData(String substring) {
        super(substring);
    }

    public static final PLATFORM_VALUE[] std_values = {
            waitPeriod
    };

    public PlatformData(Set<Coordinates> cells) {
        for (PLATFORM_VALUE value : std_values) {
            setValue(value, getDefaultValue(value));
        }
        setCells(cells);
        setValue(type, PLATFORM_TYPE.rock.name());
        // setValue(cell_type, DungeonEnums.CELL_IMAGE.diamond);
    }

    public void setCells(Set<Coordinates> cells) {
        StringBuilder c = new StringBuilder();
        for (Coordinates cell : cells) {
            c.append(cell.toString()).append(",");
        }
        setValue(PLATFORM_VALUE.cells, c.toString());
    }

    @Override
    public Class<? extends PLATFORM_VALUE> getEnumClazz() {
        return PLATFORM_VALUE.class;
    }

    public int getDefaultValue(PLATFORM_VALUE value) {
        switch (value) {
            case waitPeriod:
                return 2;
        }
        return 0;
    }

    public PLATFORM_TYPE getType() {
        String type = getValue(PLATFORM_VALUE.type);
        if (type.isEmpty()) {
            return PLATFORM_TYPE.island;
        }
        return new EnumMaster<PLATFORM_TYPE>().retrieveEnumConst(PLATFORM_TYPE.class, type);
    }

    public DIRECTION getDirection() {
        return new EnumMaster<DIRECTION>().retrieveEnumConst(DIRECTION.class,
                getValue(enter_direction));
       // return  DirectionMaster.getRelativeDirection(getOrigin(), getDestination());
    }


    public enum PLATFORM_VALUE implements LevelStructure.EditableValue {
        cells,
        name,

        type(LevelStructure.EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return PLATFORM_TYPE.class;
            }
        },
        destination(LevelStructure.EDIT_VALUE_TYPE.coordinates),
        max_speed,
        min_speed,
        acceleration,
        waitPeriod,
        powered,
        onCycle, time(),
        enter_direction(LevelStructure.EDIT_VALUE_TYPE.enum_const) {
            @Override
            public Object getArg() {
                return DIRECTION.class;
            }
        };

        private LevelStructure.EDIT_VALUE_TYPE editType;

        PLATFORM_VALUE(LevelStructure.EDIT_VALUE_TYPE type) {
            this.editType = type;
        }

        PLATFORM_VALUE() {
        }

        public LevelStructure.EDIT_VALUE_TYPE getEditValueType() {
            return editType;
        }

    }
}
