package eidolons.game.module.dungeoncrawl.ai;

import io.vertx.core.impl.ConcurrentHashSet;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import eidolons.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AggroMaster {
    public static final float AGGRO_RANGE = 2.5f;
    public static final float AGGRO_GROUP_RANGE = 1.5f;
    private static final int DEFAULT_ENGAGEMENT_DURATION = 3;
    private static boolean aiTestOn = true;
    private static boolean sightRequiredForAggro = true;
    private static List<Unit> lastAggroGroup;


    public static List<Unit> getAggroGroup() {
//        Unit hero = (Unit) DC_Game.game.getPlayer(true).getHeroObj();
        List<Unit> list = new ArrayList<>();
        for (Unit ally : DC_Game.game.getPlayer(true).getControlledUnits_()) {
//            if (sightRequiredForAggro) {
//                if (!VisionManager.checkDetected(ally, true)) {
//                    continue;
//                }
//            }
            for (Unit unit : getAggroGroup(ally)) {
                if (!list.contains(unit))
                    list.add(unit);
            }
        }
        main.system.auxiliary.log.LogMaster.log(1, "Aggro group: " + list + "; last: " + lastAggroGroup);
        if (!ExplorationMaster.isExplorationOn()) if (!list.isEmpty()) {
            logAggro(list);
        }
        if (lastAggroGroup != null)
            if (lastAggroGroup.size() > list.size()) {
                main.system.auxiliary.log.LogMaster.log(1, "Aggro group reduced: " + lastAggroGroup + " last vs new: " + list);
            }
        lastAggroGroup = list;
        return list;
    }

    public static List<Unit> getLastAggroGroup() {
        return lastAggroGroup;
    }

    private static void logAggro(List<Unit> list) {
        if (!list.equals(lastAggroGroup)) {
            List<Unit> newUnits = new ArrayList<>(list);
            newUnits.removeIf(unit -> lastAggroGroup.contains(unit));
            if (!newUnits.isEmpty())
                list.get(0).getGame().getLogManager().logBattleJoined(newUnits);
        }
    }

    public static Set<Unit> getAggroGroup(Unit hero) {
        Set<Unit> set =
         new ConcurrentHashSet<>();
//        Analyzer.getEnemies(hero, false, false, false);
//            if (ExplorationMaster.isExplorationOn())

        boolean newAggro = false;
        for (Unit unit : DC_Game.game.getUnits()) {
            if (unit.isDead())
                continue;
            if (unit.isUnconscious())
                continue;
            if (!unit.isEnemyTo(DC_Game.game.getPlayer(true)))
                continue;
            if (unit.getAI().getEngagementDuration() > 0) {
                set.add(unit);
            }
            if (unit.getAI().isEngaged()) {
                set.add(unit);
                newAggro = true;
                unit.getAI().setEngaged(false);
            }
            if (hero.getPlayerVisionStatus(true) == UNIT_TO_PLAYER_VISION.INVISIBLE)
                continue;
            VISIBILITY_LEVEL visibility = VisionManager.getMaster().getVisibilityLevel(unit, hero);
            if (visibility != VISIBILITY_LEVEL.CLEAR_SIGHT)
                continue;

            if (unit.getVisibilityLevel() == VISIBILITY_LEVEL.UNSEEN)
                continue;
            //TODO these units will instead 'surprise attack' you or stalk

            newAggro = true;
            set.add(unit);
//            }
        }
        //TODO add whole group of each unit

        for (Unit unit : set) {
            if (unit.getAI().getGroup() != null) {
                for (Unit sub : unit.getAI().getGroup().getMembers()) {
                    set.add(sub);
                    if (newAggro) {
                        int duration = getEngagementDuration(sub.getAI());
                        sub.getAI().setEngagementDuration(duration);
                    }
                }
            }
        }


        for (Unit unit : set) {
            if (unit.getAI().getEngagementDuration() <= 1)
                return set;
        }
        return set;
    }

    private static int getEngagementDuration(UnitAI ai) {
        return DEFAULT_ENGAGEMENT_DURATION;
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
        List<Unit> list = new ArrayList<>();
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
        List<Unit> list = new ArrayList<>();
        for (Unit unit : DC_Game.game.getUnits()) {

        }

        return list;
    }

    public static void unitAttacked(DC_ActiveObj action, Obj targetObj) {
//        if (!ExplorationMaster.isExplorationOn()) {
//
//
//        } else {

        if (targetObj.isMine()) {
            action.getOwnerObj().
             getAI().setEngaged(true);
        } else {
            ((Unit) targetObj).getAI().setEngaged(true);
//                                GroupAI g = ((Unit) getAction().getTargetObj()).getAI().getGroup();
//                                //TODO
//                                if (g == null) {
//                                    ((Unit) getAction().getTargetObj()).getAI().setEngagementDuration(2);
//                                } else g.
//                                 getMembers().forEach(
//                                 unit -> unit.getAI().setEngagementDuration(2)
//                                );
        }


//        }
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