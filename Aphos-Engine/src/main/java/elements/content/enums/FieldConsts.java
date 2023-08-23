package elements.content.enums;

import framework.field.FieldPos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 6/13/2023 what other static data do we need?
 */
public class FieldConsts {
    public static final Map<Cell, Set<Cell>> adjMap = new HashMap<>();

    static {
        adjMap.put(Cell.Front_Enemy_1, toSet(Cell.Vanguard_Top, Cell.Back_Enemy_2, Cell.Back_Enemy_1, Cell.Front_Enemy_2, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Front_Enemy_2, toSet(Cell.Vanguard_Top, Cell.Vanguard_Bot, Cell.Back_Enemy_3, Cell.Back_Enemy_2, Cell.Back_Enemy_1, Cell.Front_Enemy_1, Cell.Front_Enemy_3));
        adjMap.put(Cell.Front_Enemy_3, toSet(Cell.Vanguard_Bot, Cell.Back_Enemy_2, Cell.Back_Enemy_3, Cell.Front_Enemy_2, Cell.Bottom_Flank_Enemy));

        adjMap.put(Cell.Back_Enemy_2, toSet(Cell.Rear_Enemy, Cell.Back_Enemy_1, Cell.Back_Enemy_3, Cell.Front_Enemy_1, Cell.Front_Enemy_2, Cell.Front_Enemy_3));
        adjMap.put(Cell.Back_Enemy_3, toSet(Cell.Rear_Enemy, Cell.Back_Enemy_2, Cell.Front_Enemy_3, Cell.Front_Enemy_2, Cell.Bottom_Flank_Enemy));

        adjMap.put(Cell.Back_Enemy_1, toSet(Cell.Rear_Enemy, Cell.Back_Enemy_2, Cell.Front_Enemy_1, Cell.Front_Enemy_2, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Back_Enemy_2, toSet(Cell.Rear_Enemy, Cell.Back_Enemy_1, Cell.Back_Enemy_3, Cell.Front_Enemy_1, Cell.Front_Enemy_2, Cell.Front_Enemy_3));
        adjMap.put(Cell.Back_Enemy_3, toSet(Cell.Rear_Enemy, Cell.Back_Enemy_2, Cell.Front_Enemy_3, Cell.Front_Enemy_2, Cell.Bottom_Flank_Enemy));
        adjMap.put(Cell.Bottom_Flank_Enemy, toSet(Cell.Rear_Enemy, Cell.Vanguard_Bot, Cell.Back_Enemy_3, Cell.Front_Enemy_3, Cell.Bottom_Flank_Player));
        adjMap.put(Cell.Top_Flank_Enemy, toSet(Cell.Rear_Enemy, Cell.Vanguard_Top, Cell.Back_Enemy_1, Cell.Front_Enemy_1, Cell.Top_Flank_Player));
        adjMap.put(Cell.Rear_Enemy, toSet(Cell.Back_Enemy_1, Cell.Back_Enemy_2, Cell.Back_Enemy_3, Cell.Bottom_Flank_Enemy, Cell.Top_Flank_Enemy));

        adjMap.put(Cell.Front_Player_1, toSet(Cell.Vanguard_Top, Cell.Back_Player_2, Cell.Back_Player_1, Cell.Front_Player_2, Cell.Top_Flank_Player));
        adjMap.put(Cell.Front_Player_2, toSet(Cell.Vanguard_Top, Cell.Vanguard_Bot, Cell.Back_Player_3, Cell.Back_Player_2, Cell.Back_Player_1, Cell.Front_Player_1, Cell.Front_Player_3));
        adjMap.put(Cell.Front_Player_3, toSet(Cell.Vanguard_Bot, Cell.Back_Player_2, Cell.Back_Player_3, Cell.Front_Player_2, Cell.Bottom_Flank_Player));

        adjMap.put(Cell.Back_Player_2, toSet(Cell.Rear_Player, Cell.Back_Player_1, Cell.Back_Player_3, Cell.Front_Player_1, Cell.Front_Player_2, Cell.Front_Player_3));
        adjMap.put(Cell.Back_Player_3, toSet(Cell.Rear_Player, Cell.Back_Player_2, Cell.Front_Player_3, Cell.Front_Player_2, Cell.Bottom_Flank_Player));

        adjMap.put(Cell.Back_Player_1, toSet(Cell.Rear_Player, Cell.Back_Player_2, Cell.Front_Player_1, Cell.Front_Player_2, Cell.Top_Flank_Player));
        adjMap.put(Cell.Back_Player_2, toSet(Cell.Rear_Player, Cell.Back_Player_1, Cell.Back_Player_3, Cell.Front_Player_1, Cell.Front_Player_2, Cell.Front_Player_3));
        adjMap.put(Cell.Back_Player_3, toSet(Cell.Rear_Player, Cell.Back_Player_2, Cell.Front_Player_3, Cell.Front_Player_2, Cell.Bottom_Flank_Player));
        adjMap.put(Cell.Bottom_Flank_Player, toSet(Cell.Rear_Player, Cell.Vanguard_Bot, Cell.Back_Player_3, Cell.Front_Player_3, Cell.Bottom_Flank_Enemy));
        adjMap.put(Cell.Top_Flank_Player, toSet(Cell.Rear_Player, Cell.Vanguard_Top, Cell.Back_Player_1, Cell.Front_Player_1, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Rear_Player, toSet(Cell.Back_Player_1, Cell.Back_Player_2, Cell.Back_Player_3, Cell.Bottom_Flank_Player, Cell.Top_Flank_Player));
        
        adjMap.put(Cell.Vanguard_Top, toSet(Cell.Vanguard_Bot,  Cell.Front_Enemy_2, Cell.Front_Enemy_1,Cell.Front_Player_2, Cell.Front_Player_1, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Vanguard_Bot, toSet(Cell.Vanguard_Top,  Cell.Front_Enemy_2, Cell.Front_Enemy_3,Cell.Front_Player_2, Cell.Front_Player_3, Cell.Bottom_Flank_Enemy));
    }

    public static Set<Cell> getAdjacent(Cell cell) {
        return adjMap.get(cell);
    }

    private static Set<Cell> toSet(Cell... positions) {
        return Arrays.stream(positions).collect(Collectors.toSet());
    }

    public enum Shape {
        Line,
    }

    public enum Direction {
        UP(false, 90, true, null, false),
        DOWN(false, 270, true, null, true),
        LEFT(false, 180, false, false, null),
        RIGHT(false, 360, false, true, null),
        UP_LEFT(true, 135, true, false, false),
        UP_RIGHT(true, 45, true, true, false),
        DOWN_RIGHT(true, 315, true, true, true),
        DOWN_LEFT(true, 225, true, false, true),
        ;
        public Boolean growX;
        public Boolean growY;
        private boolean vertical;
        private boolean diagonal;
        private int degrees;

        Direction(boolean diagonal, int degrees, boolean vertical,
                  Boolean growX, Boolean growY) {
            this.vertical = vertical;
            this.growX = growX;
            this.growY = growY;
            this.diagonal = diagonal;
            this.degrees = degrees;
        }
    }

    public enum CellType {
        Flank,
        Van,
        Front,
        Back,
        Rear,
        Reserve,
    }

    public enum Cell {
        Multi(555, 555, null),
        Reserve_ally(999, 999, CellType.Reserve),
        Reserve_enemy(666, 666, CellType.Reserve),
        Vanguard_Bot(3, 102, CellType.Van), //2 vals? /100 and %100
        Vanguard_Top(3, 102, CellType.Van),
        Rear_Player(0, 1, CellType.Rear),
        Rear_Enemy(0, 1, CellType.Rear),

        Top_Flank_Player(100, 3, CellType.Flank),
        Top_Flank_Enemy(0, 3, CellType.Flank),

        Bottom_Flank_Player(0, -1, CellType.Flank),
        Bottom_Flank_Enemy(0, -1, CellType.Flank),


        Front_Player_1(1, null, CellType.Front),
        Back_Player_1(2, null, CellType.Back),
        Front_Enemy_1(-1, null, CellType.Front),
        Back_Enemy_1(-2, null, CellType.Back),

        Front_Player_2(1, null, CellType.Front),
        Back_Player_2(2, null, CellType.Back),
        Front_Enemy_2(-1, null, CellType.Front),
        Back_Enemy_2(-2, null, CellType.Back),

        Front_Player_3(1, null, CellType.Front),
        Back_Player_3(2, null, CellType.Back),
        Front_Enemy_3(-1, null, CellType.Front),
        Back_Enemy_3(-2, null, CellType.Back),
        ;
        public final Integer x;
        public final Integer y;
        public final CellType type;

        Cell(Integer x, Integer y, CellType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public boolean isFlank() {
            return type == CellType.Flank;
        }

        public boolean isRear() {
            return type == CellType.Rear;
        }

        public boolean isMain() {
            return type == CellType.Front || type == CellType.Back;
        }
    }


    public static final FieldPos[] all = {
            new FieldPos(Cell.Vanguard_Bot, 0), //0
            new FieldPos(Cell.Vanguard_Top, 1), //1

            new FieldPos(Cell.Top_Flank_Player, 0), //2
            new FieldPos(Cell.Front_Player_1, 0), //3
            new FieldPos(Cell.Front_Player_2, 1), //4
            new FieldPos(Cell.Front_Player_3, 2), //5
            new FieldPos(Cell.Back_Player_1, 0), //6
            new FieldPos(Cell.Back_Player_2, 1), //7
            new FieldPos(Cell.Back_Player_3, 2), //8
            new FieldPos(Cell.Bottom_Flank_Player, 0), //9
            new FieldPos(Cell.Rear_Player, 0), //10

            new FieldPos(Cell.Top_Flank_Enemy, 0), //11
            new FieldPos(Cell.Front_Enemy_1, 0), //12
            new FieldPos(Cell.Front_Enemy_2, 1), //13
            new FieldPos(Cell.Front_Enemy_3, 2), //14
            new FieldPos(Cell.Back_Enemy_1, 0), //15
            new FieldPos(Cell.Back_Enemy_2, 1), //16
            new FieldPos(Cell.Back_Enemy_3, 2), //17
            new FieldPos(Cell.Bottom_Flank_Enemy, 0), //18
            new FieldPos(Cell.Rear_Enemy, 0), //19

    };
}
