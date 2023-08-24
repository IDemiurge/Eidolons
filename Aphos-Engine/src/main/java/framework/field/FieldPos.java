package framework.field;

import elements.content.enums.FieldConsts;

/**
 * Created by Alexander on 6/10/2023
 *
 * 2x3 plus flanks, vanguard and rear Additional flanks/rear (due to ambush)
 *
 * In terms of coordinates - get range between function!
 */
public class FieldPos {
    private FieldPos[] areaPos;
    private final FieldConsts.Cell cell;

    public FieldPos(FieldPos... pos) {
        areaPos=pos;
        cell = FieldConsts.Cell.Multi;
    }
    public FieldPos(int cardinal) {
        this(FieldConsts.all[cardinal]);
    }
    public FieldPos(FieldConsts.Cell cell) {
        this(cell, 0);
    }

    public FieldPos(FieldConsts.Cell type, int number) {
        this.cell = type;
    }

    //need more adj maps
    public FieldPos getInFront() {
        // return Field.getInFront(this);
        return null;
    }

    public boolean isFlank() {
        return cell.isFlank();
    }

    @Override
    public boolean equals(Object obj) {
        if (cell == FieldConsts.Cell.Multi) {
            for (FieldPos pos : areaPos) {
                if (pos.equals(obj))
                    return true;
            }
        }
        if (obj instanceof FieldPos) {
            if (!((FieldPos) obj).getCell().equals(cell)) {
                return false;
            }
        }
        return super.equals(obj);
    }

    public FieldPos[] getAreaPos() {
        return areaPos;
    }

    public FieldConsts.Cell getCell() {
        return cell;
    }

    public Integer getX() {
        return cell.x;
    }
    public Integer getY() {
        return cell.y;
    }
}
