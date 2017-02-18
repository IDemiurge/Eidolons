package main.game.logic.dungeon.ai;

import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.ai.UnitAI;
import main.game.battlefield.VisionManager;
import main.game.logic.battle.player.Player;

import java.util.LinkedList;
import java.util.List;

public class DungeonCrawler {
    private static boolean aiTestOn = true;

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
            // check detections - perhaps it's really just about making a check
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
