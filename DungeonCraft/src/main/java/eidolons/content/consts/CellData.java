package eidolons.content.consts;

import eidolons.entity.obj.DC_Cell;
import main.system.data.DataUnit;

public class CellData extends DataUnit<CellData.CELL_VALUE> {

    public CellData(String data) {
        super(data);
    }

    @Override
    public Class<? extends CELL_VALUE> getEnumClazz() {
        return CELL_VALUE.class;
    }

    @Override
    protected String getSeparator() {
        return super.getSeparator();
    }

    @Override
    protected String getPairSeparator() {
        return super.getPairSeparator();
    }

    public void apply(DC_Cell cell) {
        if (getIntValue(CELL_VALUE.cell_variant)!=0) {
        cell.setCellVariant(getIntValue(CELL_VALUE.cell_variant));
        }

        if (getIntValue(CELL_VALUE.cell_version)!=0) {
            cell.setCellVersion(getIntValue(CELL_VALUE.cell_version));
        }
    }


    public enum CELL_VALUE {
        cell_set,
        cell_version,
        cell_variant,
        cell_rotation,
        // pillar,
        // wall
    }
    }
