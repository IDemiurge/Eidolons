package framework.field;

/**
 * Created by Alexander on 6/10/2023
 *
 * 2x3 plus flanks, vanguard and rear Additional flanks/rear (due to ambush)
 *
 * In terms of coordinates - get range between function!
 */
public class FieldPos {
    private FieldPos[] areaPos;
    private CellType type;
    private int number; //from bottom up, or main to additional

    public FieldPos(FieldPos... pos) {
        areaPos=pos;
        type = CellType.Multi;
    }
    public FieldPos(CellType type) {
        this(type, 0);
    }
    public FieldPos(CellType type, int number) {
        this.type = type;
        this.number = number;
    }

    public FieldPos getInFront() {
        // return Field.getInFront(this);
        return null;
    }
    //from bottom up, from left to right
//x y are not always useful (e.g. some fields kinda have 2 values?), but sometimes
    public enum CellType {
        Multi(555, 555),
        Reserve_ally(999, 999),
        Reserve_enemy(666, 666),
        Vanguard_Bot(3, 102), //2 vals? /100 and %100
        Vanguard_Top(3, 102),
        Rear_Player(0, 1),
        Rear_Enemy(0, 1),

        Top_Flank_Player(100, 3),
        Top_Flank_Enemy(0, 3),

        Bottom_Flank_Player(0, -1),
        Bottom_Flank_Enemy(0, -1),


        Front_Player(1, null), //3x
        Back_Player(2, null), //3x
        Front_Enemy(-1, null), //3x
        Back_Enemy(-2, null), //3x
;
        Integer x, y;

        CellType(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }
    }
/*
Obviously, Vanguard cells are just SPECIAL, not between, or?!
When determining Melee targeting OR zone targeting
 */

    @Override
    public boolean equals(Object obj) {
        if (type == CellType.Multi) {
            for (FieldPos pos : areaPos) {
                if (pos.equals(obj))
                    return true;
            }
        }
        if (obj instanceof FieldPos) {
            if (!((FieldPos) obj).getType().equals(type)) {
                return false;
            }
            if (!((FieldPos) obj).getX().equals(type)) {
                return false;
            }
            if (!((FieldPos) obj).getY().equals(type)) {
                return false;
            }
            return true;
        }
        return super.equals(obj);
    }

    public int getRange(FieldPos pos1, FieldPos pos2 ){
        int xDiff =Math.abs(pos1.getX() - pos2.getX());
        int yDiff =Math.abs(pos1.getY() - pos2.getY());
        return 0;
    }

    public CellType getType() {
        return type;
    }

    private Integer getX() {
        if (type.x==null)
            return number;
        return type.x;
    }
    private Integer getY() {
        if (type.y==null)
            return number;
        return type.y;
    }
}
