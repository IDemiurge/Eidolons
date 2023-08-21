package combat.battlefield;

import framework.entity.field.FieldEntity;
import framework.field.Environment;
import framework.field.FieldPos;

/**
 * Created by Alexander on 6/13/2023
 * what other static data do we need?
 */
public class BattleFieldConsts { //extends BattleHandler

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
