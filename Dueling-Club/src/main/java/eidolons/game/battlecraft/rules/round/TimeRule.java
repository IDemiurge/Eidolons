package eidolons.game.battlecraft.rules.round;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// The idea is to make rounds imitate real-time combat as accurately as possible
// if somebody is moving fast, they won't give time for others to catch up... 
// if somebody appears late in the round, they shouldn't have time to act much 

public class TimeRule {

	/*
     * NEW Auto-add Late buff to those who are too slow (x% initiative
	 * difference) Add remaining initiative to the next round? if current
	 * initiative is less than X% of the time that was given for the round...
	 * 
	 * The problem now is that a unit can do any number of actions even if it
	 * only appeared late in the round, as long as it has enough initiative!
	 * Original idea was to limit by ap_cost...
	 * 
	 * There should be a middleground - slow units should never getOrCreate to move if
	 * round is sped up, but near-speed units should be able to take some
	 * actions reasonably, perhaps I should use the TimeMap -
	 * 
	 * 1) Cut off slow units 2) Cut off fast units who appeared late
	 * (summoningSickness kind of does it?) Limit
	 * 
	 * 
	 * 
	 * 3)
	 */

    public static final int INIT_DIFF_THRESHOLD = 10; // affected
    // via
    // global
    // sorcery
    // time
    // warp!
    private static final String BUFF_NAME = "Late";
    private static final String BUFF_NAME_PRELIMINARY = "Late";
    // "Running Late";
    private static boolean active = false;
    private DC_Game game;

    private Map<DC_Obj, Integer> timeMap = new HashMap<>();

    private int maxTime;
    private int timeRemaining;
    private int baseTime;
    private Unit speedyUnit;
    private Integer threshold;

    public TimeRule(DC_Game game) {
        this.game = game;
    }

    public void newRound() {
        if (DC_Engine.isAtbMode()) {
            return;
        }
        active = false;
        threshold = null;
        int totalTime = 0;
        for (Unit unit : game.getUnits()) {
            Integer totalInitiative = unit.getIntParam(PARAMS.C_INITIATIVE)
             - unit.getIntParam(PARAMS.C_INITIATIVE_BONUS);
            if (totalInitiative > totalTime) {
                totalTime = totalInitiative;
            }
        }
        baseTime = totalTime;
        maxTime = 0;
        timeRemaining = baseTime;
        timeMap.clear();
    }

    public void reset() {

    }

    public boolean checkTime(DC_ActiveObj action, int time_used) {
        DC_Obj unitObj = action.getOwnerUnit();
        Integer prevTime = timeMap.get(unitObj);
        if (prevTime == null) {
            prevTime = 0;
        }
        int time = prevTime + time_used;

        timeMap.put(unitObj, time);

        if (time > maxTime) {
            maxTime = time;
            int delta = getTimeRemaining() - (baseTime - time);
            float percent = new Float(delta) / baseTime;
            if (game.getDungeonMaster().getExplorationMaster() != null)
                game.getDungeonMaster().getExplorationMaster().
                 getAiMaster().tryMoveAiTurnBased(percent);
            //% of time spent...

            timeRemaining = baseTime - time;
            game.getLogManager().log(LOG.GAME_INFO,
             "*** Time remaining for this round: " + timeRemaining, ENTRY_TYPE.ACTION);
        }
        return timeRemaining <= 0;

    }

    public void checkRunningLate(Unit unit) {
        boolean result = getTimeThreshold() > unit.getIntParam(PARAMS.C_INITIATIVE);
        if (result) {
            addBuff(unit, true);
        }
    }

    private int getTimeThreshold() {
        if (threshold == null) {
            threshold = speedyUnit.getIntParam(PARAMS.INITIATIVE) * INIT_DIFF_THRESHOLD / 100;
        }
        return threshold;
    }

    public boolean checkEndTurn() {
        List<Unit> lateUnits = new ArrayList<>();
        // TODO just remove all laters and see if it's over
        // boolean result =true;
        for (Unit unit : game.getUnits()) {
            if (unit.canActNow()) {
                if (unit.getBuff(BUFF_NAME_PRELIMINARY) == null) {
                    return false;
                } else {
                    lateUnits.add(unit);
                }
            }
        }

        for (Unit unit : lateUnits) {
            // transferInitiative(unit);
            unit.setParam(PARAMS.C_INITIATIVE_TRANSFER, Math.max(0, unit
             .getIntParam(PARAMS.C_INITIATIVE)));
            // negative transfer should be possible, but...
        }

        game.getLogManager().log("*** " + speedyUnit.getName() + " prompts the round to end"
         // +"The hour has come for this round to end!"
        );

        return true;
    }

    public boolean actionComplete(DC_ActiveObj action, int time_cost) {
        if (DC_Engine.isAtbMode()) {
            return false;
        }
        if (!active) {
            if (checkTime(action, time_cost)) {
                active = true;
                // game.getLogManager()
                // .log("***"
                // + action.getOwnerUnit().getName()
                // +
                // " is ready to start a new round, other units have only one more action to make!");
                // other units will have their remaining initiative transferred
                // to the next round
                // as well as action points? or half at least?
                // late buff will be nominal to update that the unit will not make
                // if unless something speeds him up!

                speedyUnit = action.getOwnerUnit();

                game.getLogManager().log(
                 "***" + speedyUnit.getName() + " acts swiftly, units with less than "
                  + getTimeThreshold() + " will be out of time for this round.");

                // return false;
            }
        }

        if (!active) {
            return false;
        }

        // DC_Obj unitObj = action.getOwnerUnit();
        // addBuff(unitObj, false);
        // game.getLogManager().log(
        // "***" + action.getOwnerUnit().getName()
        // + " is out of time for this round");
        for (Unit unit : game.getUnits()) {
            checkRunningLate(unit);
        }

        return checkEndTurn();

    }

    private void addBuff(Unit unitObj, boolean preliminary) {
        if (unitObj.isBfObj()) {
            return;
        }
        AddStatusEffect effect;
        effect = new AddStatusEffect(UnitEnums.STATUS.LATE);
        new AddBuffEffect((preliminary) ? BUFF_NAME_PRELIMINARY : BUFF_NAME, effect, 1).apply(Ref
         .getSelfTargetingRefCopy(unitObj));

    }

    public int getTimePercentageRemaining() {
        if (baseTime == 0)
            return 0;
        return Math.round(new Float(timeRemaining * 100 / baseTime));
    }

    public int getBaseTime() {
        return baseTime;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

}