package main.game.module.dungeoncrawl.ai;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.system.math.PositionMaster;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AggroMaster {
    public static final float AGGRO_RANGE = 2.5f;
    public static final float AGGRO_GROUP_RANGE = 1.5f;
    private static boolean aiTestOn = true;
    private static boolean sightRequiredForAggro = true;


    public static List<Unit> getAggroGroup() {
//        Unit hero = (Unit) DC_Game.game.getPlayer(true).getHeroObj();
        List<Unit> list = new LinkedList<>();
        for (Unit ally : DC_Game.game.getPlayer(true).getControlledUnits_()) {
            if (sightRequiredForAggro) {
                if (!VisionManager.checkDetected(ally)) {
                    continue;
                }
            }
            for (Unit unit : getAggroGroup(ally)) {
                if (!list.contains(unit))
                    list.add(unit);
            }
        }

        return list;
    }

    public static Set<Unit> getAggroGroup(Unit hero) {
        List<Unit> list =
         new LinkedList<>(DC_Game.game.getUnits());
//        Analyzer.getEnemies(hero, false, false, false);
//            if (ExplorationMaster.isExplorationOn())
        list.removeIf(unit -> !unit.canAct());
//            boolean engaged;
//            if (sub.getAI().isEngaged())
//                engaged = true; //could just check mode!

        list.removeIf(unit -> !unit.isEnemyTo(DC_Game.game.getPlayer(true)));
        list.removeIf(unit ->
         !checkAggro(unit, hero, AGGRO_RANGE)
        );

        Set<Unit> aggroGroup = new LinkedHashSet<>();
        for (Unit sub : list) {
            List<Unit> aggroed = new LinkedList<>(DC_Game.game.getUnits());
            aggroed.removeIf(unit ->
             !unit.canActNow() ||
              !unit.isEnemyTo(DC_Game.game.getPlayer(true)) ||
              aggroGroup.contains(sub) ||
              !checkAggro(unit, hero, AGGRO_GROUP_RANGE));
            aggroed.forEach(unit -> {
                aggroGroup.addAll(unit.getAI().getGroup().getMembers());
            });
        }

        return aggroGroup;
    }

    private static boolean checkAggro(Unit unit, Unit hero, double range) {
        return PositionMaster.getExactDistance(
         hero.getCoordinates(), unit.getCoordinates()) <= range;
    }

    public static boolean isAiTestOn() {
        return aiTestOn;
    }

    public static boolean checkEngaged(UnitAI ai) {
        Unit unit = ai.getUnit();
        // PERCEPTION_STATUS_PLAYER status =
        // ai.getGroup().getPerceptionStatus();

        // after each action? some events may trigger it separately
        // any hostile action triggers Engagement with the group, even if they
        // don't see you
        // HEARING would be an important factor...
        // for (DC_HeroObj unit : getCreeps()) {
        List<Unit> relevantEnemies = getRelevantEnemies(unit);
        for (Unit hero : relevantEnemies) {
            // preCheck detections - perhaps it's really just about making a preCheck
            // before AS-constr.

            // sometimes creeps may be engaged but not know what hit them...

            // different aggro levels?
            // if (status == PERCEPTION_STATUS_PLAYER.KNOWN_TO_BE_THERE)
            // return true;

            if (VisionManager.checkVisible(hero)) {
                return true;
            }
        }
        // }

        return false;

    }

    private static List<Unit> getRelevantEnemies(Unit unit) {
        List<Unit> list = new LinkedList<>();
        for (Unit enemy : unit.getGame().getUnits()) {
            if (enemy.getOwner().equals(Player.NEUTRAL) || enemy.getOwner().equals(unit.getOwner())) {
                continue;
            }
            if (unit.getZ() != enemy.getZ()) {
                continue;
            }
            list.add(enemy);
        }
        return list;

    }

    private static List<Unit> getCreeps() {
        List<Unit> list = new LinkedList<>();
        for (Unit unit : DC_Game.game.getUnits()) {

        }

        return list;
    }


    public enum ENGAGEMENT_LEVEL {

        UNSUSPECTING, // will use its behavior and rest actions
        SUSPECTING, // will search, ambush or stalk
        ALARMED // will not Rest or otherwise let down their guard
        ,
        AGGRO // will engage and make combat-actions
    }

	/*
     * Extended Battlefield - don't limit coordinates, don't add if outside vision
	 * Ray effects
	 * Global BF effects 
	 * 
	 * Grid coordinate offset
	 * 
	 * just add the same x/y for centering; although some may want additional offset to accomodate targeting or sight range (vertical)
	 * 
	 * borders of the battlefield 
	 *
	 * generic-dungeons - changing (caching) battlefield, filtering bf-obj add by sublevel
	 * >> enter/leave for each hero? 
	 * 
	 * entrance-based spawning 
	 * 
	 * wandering creeps - tweaked AI  
	 * 
	 * hidden treasures 
	 * 
	 * doors: next to indestructible impassible walls 
	 * 
	 *  Will a minimap really be necessary? 
	 * 
	 * 
	 * 
	 */

}
