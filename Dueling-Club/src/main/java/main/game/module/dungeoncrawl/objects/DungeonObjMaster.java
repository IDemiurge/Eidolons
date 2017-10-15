package main.game.module.dungeoncrawl.objects;

import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;

import java.util.List;

/**
 * Created by JustMe on 9/23/2017.
 * <p>
 * functions for interacting with various objects
 * <p>
 * > Do I need a class for each?
 */
public abstract class DungeonObjMaster<T extends DUNGEON_OBJ_ACTION> {
    DungeonMaster  dungeonMaster;

    public DungeonObjMaster(DungeonMaster dungeonMaster) {
        this.dungeonMaster = dungeonMaster;
    }

    public void takeItem(DungeonObj obj) {

    }

    public abstract List<DC_ActiveObj> getActions(BattleFieldObject obj, Unit unit);

    public abstract void open(DungeonObj obj, Ref ref);

    public enum DUNGEON_ITEM_ACTION {
        TAKE,
        INTERACT,
        USE,
    }

    //     public abstract boolean isVisible(BattleFieldObject obj);
//    isObstructing
//     isPassable
//    applyAction

}
