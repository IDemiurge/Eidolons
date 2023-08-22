package elements.content.enums;

import framework.field.FieldPos;

/**
 * Created by Alexander on 6/13/2023
 * what other static data do we need?
 */
public class FieldEnums { //extends BattleHandler

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
    public static final FieldPos[] all = {
            new FieldPos(FieldPos.CellType.Vanguard_Bot, 0),
            new FieldPos(FieldPos.CellType.Vanguard_Bot, 1),

            new FieldPos(FieldPos.CellType.Top_Flank_Enemy, 0),
            new FieldPos(FieldPos.CellType.Top_Flank_Player, 0),
            new FieldPos(FieldPos.CellType.Bottom_Flank_Enemy, 0),
            new FieldPos(FieldPos.CellType.Bottom_Flank_Player, 0),
            new FieldPos(FieldPos.CellType.Rear_Enemy, 0),
            new FieldPos(FieldPos.CellType.Rear_Player, 0),

            new FieldPos(FieldPos.CellType.Front_Enemy, 0),
            new FieldPos(FieldPos.CellType.Front_Enemy, 1),
            new FieldPos(FieldPos.CellType.Front_Enemy, 2),

            new FieldPos(FieldPos.CellType.Front_Player, 0),
            new FieldPos(FieldPos.CellType.Front_Player, 1),
            new FieldPos(FieldPos.CellType.Front_Player, 2),

            new FieldPos(FieldPos.CellType.Back_Enemy, 0),
            new FieldPos(FieldPos.CellType.Back_Enemy, 1),
            new FieldPos(FieldPos.CellType.Back_Enemy, 2),

            new FieldPos(FieldPos.CellType.Back_Player, 0),
            new FieldPos(FieldPos.CellType.Back_Player, 1),
            new FieldPos(FieldPos.CellType.Back_Player, 2),
    };

}
