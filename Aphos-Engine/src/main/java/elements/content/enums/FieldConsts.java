package elements.content.enums;

import elements.exec.targeting.area.CellSets;
import framework.field.FieldPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static elements.exec.targeting.area.CellSets.*;

/**
 * Created by Alexander on 6/13/2023 what other static data do we need?
 */
public class FieldConsts {
    public static final Map<Cell, Set<Cell>> adjMap = new HashMap<>();
    public static final Map<Cell, Map<Direction, Cell>> adjMapDirection = new HashMap<>();

    //IDEA: what if the order of this set determined Direction? ...
    // OR we could try to use X:Y here
    //Special logic: Flank - has 2 pos on bottom?! Well, it's bottom_left/right! interesting...
    public static void putAdj(Cell key, Set<Cell> set){
        adjMap.put(key, set);
        //TODO
        // Map<Direction, Cell> dirMap= new HashMap<>();
        // adjMapDirection.put(key, dirMap);
        // for (Cell cell : set) {
        //     for (Direction value : Direction.values()) {
        //         if (key.getDirection(cell)==value)
        //             dirMap.put(value, cell);
        //     }
        // }
    }
    static {
        putAdj(Cell.Front_Enemy_1, Set.of(Cell.Vanguard_Top, Cell.Back_Enemy_2, Cell.Back_Enemy_1, Cell.Front_Enemy_2, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Front_Enemy_2, Set.of(Cell.Vanguard_Top, Cell.Vanguard_Bot, Cell.Back_Enemy_3, Cell.Back_Enemy_2, Cell.Back_Enemy_1, Cell.Front_Enemy_1, Cell.Front_Enemy_3));
        adjMap.put(Cell.Front_Enemy_3, Set.of(Cell.Vanguard_Bot, Cell.Back_Enemy_2, Cell.Back_Enemy_3, Cell.Front_Enemy_2, Cell.Bottom_Flank_Enemy));

        adjMap.put(Cell.Back_Enemy_2, Set.of(Cell.Rear_Enemy, Cell.Back_Enemy_1, Cell.Back_Enemy_3, Cell.Front_Enemy_1, Cell.Front_Enemy_2, Cell.Front_Enemy_3));
        adjMap.put(Cell.Back_Enemy_3, Set.of(Cell.Rear_Enemy, Cell.Back_Enemy_2, Cell.Front_Enemy_3, Cell.Front_Enemy_2, Cell.Bottom_Flank_Enemy));

        adjMap.put(Cell.Back_Enemy_1, Set.of(Cell.Rear_Enemy, Cell.Back_Enemy_2, Cell.Front_Enemy_1, Cell.Front_Enemy_2, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Back_Enemy_2, Set.of(Cell.Rear_Enemy, Cell.Back_Enemy_1, Cell.Back_Enemy_3, Cell.Front_Enemy_1, Cell.Front_Enemy_2, Cell.Front_Enemy_3));
        adjMap.put(Cell.Back_Enemy_3, Set.of(Cell.Rear_Enemy, Cell.Back_Enemy_2, Cell.Front_Enemy_3, Cell.Front_Enemy_2, Cell.Bottom_Flank_Enemy));
        adjMap.put(Cell.Bottom_Flank_Enemy, Set.of(Cell.Rear_Enemy, Cell.Vanguard_Bot, Cell.Back_Enemy_3, Cell.Front_Enemy_3, Cell.Bottom_Flank_Player));
        adjMap.put(Cell.Top_Flank_Enemy, Set.of(Cell.Rear_Enemy, Cell.Vanguard_Top, Cell.Back_Enemy_1, Cell.Front_Enemy_1, Cell.Top_Flank_Player));
        adjMap.put(Cell.Rear_Enemy, Set.of(Cell.Back_Enemy_1, Cell.Back_Enemy_2, Cell.Back_Enemy_3, Cell.Bottom_Flank_Enemy, Cell.Top_Flank_Enemy));

        adjMap.put(Cell.Front_Player_1, Set.of(Cell.Vanguard_Top, Cell.Back_Player_2, Cell.Back_Player_1, Cell.Front_Player_2, Cell.Top_Flank_Player));
        adjMap.put(Cell.Front_Player_2, Set.of(Cell.Vanguard_Top, Cell.Vanguard_Bot, Cell.Back_Player_3, Cell.Back_Player_2, Cell.Back_Player_1, Cell.Front_Player_1, Cell.Front_Player_3));
        adjMap.put(Cell.Front_Player_3, Set.of(Cell.Vanguard_Bot, Cell.Back_Player_2, Cell.Back_Player_3, Cell.Front_Player_2, Cell.Bottom_Flank_Player));

        adjMap.put(Cell.Back_Player_2, Set.of(Cell.Rear_Player, Cell.Back_Player_1, Cell.Back_Player_3, Cell.Front_Player_1, Cell.Front_Player_2, Cell.Front_Player_3));
        adjMap.put(Cell.Back_Player_3, Set.of(Cell.Rear_Player, Cell.Back_Player_2, Cell.Front_Player_3, Cell.Front_Player_2, Cell.Bottom_Flank_Player));

        adjMap.put(Cell.Back_Player_1, Set.of(Cell.Rear_Player, Cell.Back_Player_2, Cell.Front_Player_1, Cell.Front_Player_2, Cell.Top_Flank_Player));
        adjMap.put(Cell.Back_Player_2, Set.of(Cell.Rear_Player, Cell.Back_Player_1, Cell.Back_Player_3, Cell.Front_Player_1, Cell.Front_Player_2, Cell.Front_Player_3));
        adjMap.put(Cell.Back_Player_3, Set.of(Cell.Rear_Player, Cell.Back_Player_2, Cell.Front_Player_3, Cell.Front_Player_2, Cell.Bottom_Flank_Player));
        adjMap.put(Cell.Bottom_Flank_Player, Set.of(Cell.Rear_Player, Cell.Vanguard_Bot, Cell.Back_Player_3, Cell.Front_Player_3, Cell.Bottom_Flank_Enemy));
        adjMap.put(Cell.Top_Flank_Player, Set.of(Cell.Rear_Player, Cell.Vanguard_Top, Cell.Back_Player_1, Cell.Front_Player_1, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Rear_Player, Set.of(Cell.Back_Player_1, Cell.Back_Player_2, Cell.Back_Player_3, Cell.Bottom_Flank_Player, Cell.Top_Flank_Player));
        
        adjMap.put(Cell.Vanguard_Top, Set.of(Cell.Vanguard_Bot,  Cell.Front_Enemy_2, Cell.Front_Enemy_1,Cell.Front_Player_2, Cell.Front_Player_1, Cell.Top_Flank_Enemy));
        adjMap.put(Cell.Vanguard_Bot, Set.of(Cell.Vanguard_Top,  Cell.Front_Enemy_2, Cell.Front_Enemy_3,Cell.Front_Player_2, Cell.Front_Player_3, Cell.Bottom_Flank_Enemy));
    }

    public static Set<Cell> getAdjacent(Cell cell) {
        return adjMap.get(cell);
    }

    public static Cell getAdjacent(Cell cell, Direction direction) {
        //aha! so left/right is actually top/bot? Yeah so far I don't wanna bother with FACING - but maybe we could
        //consider some FLIP ?
        return  adjMapDirection.get(cell).get(direction);

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

        public static Direction get(String name) {
            return EnumFinder.get(Direction.class, name);
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

    //X / Y are set wrong and may be deprecated!
    public enum Cell {
        Multi(555, 555, null),
        Reserve_ally(999, 999, CellType.Reserve),
        Reserve_enemy(666, 666, CellType.Reserve),

        Vanguard_Bot(0,1,  CellType.Van), //2 vals? /100 and %100
        Vanguard_Top(0,2,  CellType.Van),
        //TODO SWAP minus
        Rear_Player(3,1,  CellType.Rear),
        Rear_Enemy(-3, 1, CellType.Rear),

        Top_Flank_Player(100, 3, CellType.Flank),
        Top_Flank_Enemy(-100, 3, CellType.Flank),

        Bottom_Flank_Player(100, -1, CellType.Flank),
        Bottom_Flank_Enemy(-100, -1, CellType.Flank),


        Front_Player_1(1, 0, CellType.Front),
        Back_Player_1(2, 0, CellType.Back),
        Front_Enemy_1(-1, 0, CellType.Front),
        Back_Enemy_1(-2, 0, CellType.Back),

        Front_Player_2(1, 1, CellType.Front),
        Back_Player_2(2, 1, CellType.Back),
        Front_Enemy_2(-1, 1, CellType.Front),
        Back_Enemy_2(-2, 1, CellType.Back),

        Front_Player_3(1, 2, CellType.Front),
        Back_Player_3(2, 2, CellType.Back),
        Front_Enemy_3(-1, 2, CellType.Front),
        Back_Enemy_3(-2, 2, CellType.Back),
        ;
        public final Integer x;
        public final Integer y;
        public final CellType type;

        Cell(Integer x, Integer y, CellType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public boolean isPlayerZone() {
            return x>0 && isMain();
        }
        public boolean isEnemyZone() {
            return x<0 && isMain();
        }
        public boolean isFlank() {
            return type == CellType.Flank;
        }

        public boolean isRear() {
            return type == CellType.Rear;
        }
        public boolean isVan() {
            return type == CellType.Van;
        }

        public boolean isBack() {
            return type == CellType.Back;
        }

        public boolean isMain() {
            return type == CellType.Front || type == CellType.Back;
        }

        public Direction getDirection(Cell cell) {
            for (Direction direction : Direction.values()) {
                if (check(direction, x, y, cell.x, cell.y))
                    return direction;
            }
            return null;
        }

        private boolean check(Direction direction, Integer x, Integer y, Integer x1, Integer y1) {
            //middle position - both? well this is kinda custom...
            //there's UP_LEFT for that!
            switch (direction) {
                //what about positions that are between?
                case UP: return x == x1 && y> y1;
                case DOWN:
                    break;
                case LEFT:
                    break;
                case RIGHT:
                    break;
                case UP_LEFT:
                    break;
                case UP_RIGHT:
                    break;
                case DOWN_RIGHT:
                    break;
                case DOWN_LEFT:
                    break;
            }
            return false;
        }

    }


    public static final FieldPos[] all = {
            new FieldPos(Cell.Vanguard_Bot), //0
            new FieldPos(Cell.Vanguard_Top), //1

            new FieldPos(Cell.Top_Flank_Player), //2
            new FieldPos(Cell.Front_Player_1), //3
            new FieldPos(Cell.Front_Player_2), //4
            new FieldPos(Cell.Front_Player_3), //5
            new FieldPos(Cell.Back_Player_1), //6
            new FieldPos(Cell.Back_Player_2), //7
            new FieldPos(Cell.Back_Player_3), //8
            new FieldPos(Cell.Bottom_Flank_Player), //9
            new FieldPos(Cell.Rear_Player), //10

            new FieldPos(Cell.Top_Flank_Enemy), //11
            new FieldPos(Cell.Front_Enemy_1), //12
            new FieldPos(Cell.Front_Enemy_2), //13
            new FieldPos(Cell.Front_Enemy_3), //14
            new FieldPos(Cell.Back_Enemy_1), //15
            new FieldPos(Cell.Back_Enemy_2), //16
            new FieldPos(Cell.Back_Enemy_3), //17
            new FieldPos(Cell.Bottom_Flank_Enemy), //18
            new FieldPos(Cell.Rear_Enemy), //19

    };
}
