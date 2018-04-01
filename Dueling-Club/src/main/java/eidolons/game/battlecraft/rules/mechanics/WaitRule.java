package eidolons.game.battlecraft.rules.mechanics;

import eidolons.entity.obj.unit.Unit;
import main.content.enums.system.MetaEnums;
import main.game.core.game.Game;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * remove from PQ, but then also reduce initiative? Or keep it high? if target
 * unit died/disappears/gets immobilized, wake the unit up
 */
public class WaitRule {
    public static final String WAIT_BUFF = "Waiting";

    static Map<Unit, Unit> waitMap;
    static Map<Integer, Integer> alertMap;

    private static Game game;

    public static void checkMap() {
        for (Unit unit : getWaitMap().keySet()) {
            if (unit == null) {
                continue;
            }
            if (checkWakeUp(unit,
             waitMap.get(unit))) {
                wakeUp(unit);
            }
        }
    }

    public static void addAlertUnit(Unit unit, Unit target) {
        /*
         * wake up if a unit comes close
		 * 
		 * wake up if targeted by a hostile action
		 * 
		 * wake up if the selected 'anchor' unit dies/disappears - NOT BY
		 * DEFAULT!
		 * 
		 * Don't forget it is a MODE
		 * 
		 * Wake up before turn's end perhaps or else provide focus/sta for spent
		 * actions
		 */

    }

    public static void addWaitingUnit(Unit unit, Unit target) {
        if (game == null) {
            game = unit.getGame();
        }
        getWaitMap().put(unit, target);
    }

    private static void wakeUp(Unit unit) {
        LogMaster.log(LogMaster.CORE_DEBUG_1,
         "waking unit up: " + unit);
        unit.removeBuff(WAIT_BUFF);
        getWaitMap().remove(unit);
        // TODO remove Wait mode
        // reset initiative

    }

    public static boolean checkWakeUp(Unit unit, Unit target) {
        if (!unit.hasBuff(WAIT_BUFF)) {
            return true;
        }
        if (target.hasBuff(MetaEnums.STD_BUFF_NAMES.Channeling.name())) {
            return true;
        }
        if (target.isDead()) {
            return true;
        }
        if (target.getOwner() != unit.getOwner()) {
            if (!target.checkInSightForUnit(unit)) {
                return true;
            }
        }
        return !target.canActNow();
    }

    public static Map<Integer, Integer> getAlertMap() {
        if (alertMap == null) {
            alertMap = new HashMap<>();
        }
        return alertMap;
    }

    private static Map<Unit, Unit> getWaitMap() {
        if (waitMap == null) {
            waitMap = new HashMap<>();
        }
        return waitMap;
    }

    public static void reset() {
        waitMap = new HashMap<>();
    }

}
