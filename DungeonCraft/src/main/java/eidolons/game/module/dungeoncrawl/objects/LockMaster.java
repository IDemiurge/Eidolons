package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.objects.LockMaster.LOCK_ACTIONS;
import eidolons.system.math.roll.RollMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.math.Formula;

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
    public List<DC_ActiveObj> getActions(DungeonObj obj, Unit unit) {
        return null;
    }

    public boolean tryUnlock(Entity lockedObj, Unit lockPicker) {
        return tryUnlock(lockedObj, lockPicker, null);

    }

    @Override
    public DC_ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return null;
    }

    public boolean tryUnlock(Entity lockedObj, Unit lockPicker, Formula formula) {
        boolean result = TrapMaster.checkTrapOnLock(lockedObj);
        if (!result) {
            return false;
        }
        Ref ref = new Ref(lockPicker);
        ref.setTarget(lockedObj.getId());
        // preCheck lockpick
        if (formula != null) {
            // TODO
        } else {
            result = RollMaster.roll(GenericEnums.ROLL_TYPES.UNLOCK, ref);
        }
        // roll
        lockedObj.getIntParam(PARAMS.LOCK_LEVEL);
        // DC_SoundMaster.playStandardSound(STD_SOUNDS.UNLOCK_SUCCESS);
        return result;
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
