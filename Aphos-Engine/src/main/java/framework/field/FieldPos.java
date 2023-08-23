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
    private final Integer number; //from bottom up, or main to additional

    public FieldPos(FieldPos... pos) {
        areaPos=pos;
        cell = FieldConsts.Cell.Multi;
        number = null;
    }
    public FieldPos(FieldConsts.Cell cell) {
        this(cell, 0);
    }
    public FieldPos(FieldConsts.Cell type, int number) {
        this.cell = type;
        this.number = number;
    }

    public FieldPos getInFront() {
        // return Field.getInFront(this);
        return null;
    }

    public boolean isFlank() {
        return cell.isFlank();
    }

    //from bottom up, from left to right
//x y are not always useful (e.g. some fields kinda have 2 values?), but sometimes

    /*
Obviously, Vanguard cells are just SPECIAL, not between, or?!
When determining Melee targeting OR zone targeting
 */

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
        if (cell.x==null)
            return number;
        return cell.x;
    }
    public Integer getY() {
        if (cell.y==null)
            return number;
        return cell.y;
    }
}
