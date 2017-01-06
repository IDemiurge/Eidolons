package main.game.logic.dungeon.special;

import main.content.CONTENT_CONSTS.ROLL_TYPES;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.PARAMS;
import main.content.PROPS;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.player.DC_Player;
import main.system.math.Formula;
import main.system.math.roll.RollMaster;

import java.util.LinkedList;
import java.util.List;

public class LockMaster {
    // Map<DC_Obj, List<>> map;

    public static void initLocks(DC_HeroObj mapObj) {
//        mapObj.getProperty(PROPS.LOCK_TYPE);
        mapObj.getIntParam(PARAMS.LOCK_LEVEL);
        ObjType type;
//        new LockObj(type, DC_Player.NEUTRAL, mapObj.getGame(), new Ref(mapObj.getGame()));
    }

    public static boolean isLocked(DC_Obj mapObj) {
//        List<LockObj> locks = locksMap.getOrCreate(mapObj.getCoordinates());
//        for (LockObj lock : locks) {
//            if (lock.isUnlocked())
//                return true;
//        }

        return false;
        // determined whether something is passable?
        // will add *unlock* action
    }

    public static boolean tryUnlock(Entity lockedObj, DC_HeroObj lockPicker) {
        return tryUnlock(lockedObj, lockPicker, null);

    }

    public static boolean tryUnlock(Entity lockedObj, DC_HeroObj lockPicker, Formula formula) {
        boolean result = TrapMaster.checkTrapOnLock(lockedObj);
        if (!result)
            return false;
        Ref ref = new Ref(lockPicker);
        ref.setTarget(lockedObj.getId());
        // check lockpick
        if (formula != null) {
            // TODO
        } else
            result = RollMaster.roll(ROLL_TYPES.UNLOCK, ref);
        // roll
        lockedObj.getIntParam(PARAMS.LOCK_LEVEL);
        // SoundMaster.playStandardSound(STD_SOUNDS.UNLOCK_SUCCESS);
        return result;
    }

    public static List<Obj> getObjectsToUnlock(DC_HeroObj unit) {
        List<Obj> list = new LinkedList<>();
        if (unit.getGame().getObjectByCoordinate(unit.getCoordinates(), false) != null)
            list.add(unit.getGame().getObjectByCoordinate(unit.getCoordinates(), false));
        for (Coordinates c : unit.getCoordinates().getAdjacentCoordinates()) {
            if (unit.getGame().getObjectByCoordinate(c, false) != null)
                list.add(unit.getGame().getObjectByCoordinate(c, false));

        }
        return list;
    }

    public static void unlock(Entity lockedObj) {
        lockedObj.removeStatus(STATUS.LOCKED + "");
        lockedObj.addStatus(STATUS.UNLOCKED + "");

    }

}
