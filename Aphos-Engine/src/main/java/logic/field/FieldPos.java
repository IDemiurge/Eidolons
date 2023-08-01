package logic.field;

/**
 * Created by Alexander on 6/10/2023
 *
 * 2x3 plus flanks, vanguard and rear Additional flanks/rear (due to ambush)
 *
 * In terms of coordinates - get range between function!
 */
public class FieldPos {
    public static final FieldPos[] all = {
            new FieldPos(CellType.Vanguard, 0),
            new FieldPos(CellType.Vanguard, 1),

            new FieldPos(CellType.Top_Flank_Enemy, 0),
            new FieldPos(CellType.Top_Flank_Player, 0),
            new FieldPos(CellType.Bottom_Flank_Enemy, 0),
            new FieldPos(CellType.Bottom_Flank_Player, 0),
            new FieldPos(CellType.Rear_Enemy, 0),
            new FieldPos(CellType.Rear_Player, 0),

            new FieldPos(CellType.Front_Enemy, 0),
            new FieldPos(CellType.Front_Enemy, 1),
            new FieldPos(CellType.Front_Enemy, 2),

            new FieldPos(CellType.Front_Player, 0),
            new FieldPos(CellType.Front_Player, 1),
            new FieldPos(CellType.Front_Player, 2),
            
            new FieldPos(CellType.Back_Enemy, 0),
            new FieldPos(CellType.Back_Enemy, 1),
            new FieldPos(CellType.Back_Enemy, 2),

            new FieldPos(CellType.Back_Player, 0),
            new FieldPos(CellType.Back_Player, 1),
            new FieldPos(CellType.Back_Player, 2),
    };
    CellType type;
    int number; //from bottom up, or main to additional

    public FieldPos(CellType type, int number) {
        this.type = type;
        this.number = number;
    }

    public FieldPos getInFront() {
        // return Field.getInFront(this);
        return null;
    }

    public enum CellType {
        Vanguard(0, null),
        Rear_Player(0, null),
        Rear_Enemy(0, null),

        Top_Flank_Player(0, null),
        Top_Flank_Enemy(0, null),
        Bottom_Flank_Player(0, null),
        Bottom_Flank_Enemy(0, null),


        Front_Player(1, null),
        Back_Player(2, null),
        Front_Enemy(-1, null),
        Back_Enemy(-2, null),
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
    public int getRange(FieldPos pos1,FieldPos pos2 ){
        int xDiff =Math.abs(pos1.getX() - pos2.getX());
        int yDiff =Math.abs(pos1.getY() - pos2.getY());
        return 0;
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
