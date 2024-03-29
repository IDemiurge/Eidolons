package eidolons.game.exploration.dungeon.objects;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.exploration.dungeon.objects.LockMaster.LOCK_ACTIONS;
import main.content.enums.entity.UnitEnums;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class LockMaster extends DungeonObjMaster<LOCK_ACTIONS> {
    public LockMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    @Override
    protected boolean actionActivated(LOCK_ACTIONS sub, Unit unit, DungeonObj obj) {
        return false;
    }

    @Override
    public List<ActiveObj> getActions(DungeonObj obj, Unit unit) {
        return null;
    }

    @Override
    public ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return null;
    }

    public List<Obj> getObjectsToUnlock(Unit unit) {
        List<Obj> list = new ArrayList<>();
        if (unit.getGame().getObjectByCoordinate(unit.getCoordinates(), false) != null) {
            list.add(unit.getGame().getObjectByCoordinate(unit.getCoordinates(), false));
        }
        for (Coordinates c : unit.getCoordinates().getAdjacentCoordinates()) {
            if (unit.getGame().getObjectByCoordinate(c, false) != null) {
                list.add(unit.getGame().getObjectByCoordinate(c, false));
            }

        }
        return list;
    }

    public void unlock(Entity lockedObj) {
        lockedObj.removeStatus(UnitEnums.STATUS.LOCKED + "");
        lockedObj.addStatus(UnitEnums.STATUS.UNLOCKED + "");

    }

    public void open(BattleFieldObject obj) {
        obj.removeStatus(UnitEnums.STATUS.LOCKED + "");
        obj.addStatus(UnitEnums.STATUS.UNLOCKED + "");
    }


    @Override
    public void open(DungeonObj obj, Ref ref) {

    }

    // Map<DC_Obj, List<>> map;
    public enum LOCK_ACTIONS implements DUNGEON_OBJ_ACTION {

    }
}
