package eidolons.game.battlecraft.ai.explore;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationHandler;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import io.vertx.core.impl.ConcurrentHashSet;
import main.entity.obj.Obj;
import main.system.auxiliary.data.ListMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AggroMaster extends ExplorationHandler {
    public static final float AGGRO_RANGE = 2.5f;
    public static final float AGGRO_GROUP_RANGE = 1.5f;
    private static final int DEFAULT_ENGAGEMENT_DURATION = 2;
    private static boolean aiTestOn = true;
    private static boolean sightRequiredForAggro = true;
    private static List<Unit> lastAggroGroup;
    private int minDistance = 4;

    public AggroMaster(ExplorationMaster master) {
        super(master);
    }

    public static List<Unit> getAggroGroup() {
        //        Unit hero = (Unit) DC_Game.game.getPlayer(true).getHeroObj();
        List<Unit> list = new ArrayList<>();
        for (Unit ally : DC_Game.game.getPlayer(true).collectControlledUnits_()) {
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

        if (ListMaster.isNotEmpty(list) ||ListMaster.isNotEmpty(lastAggroGroup))
            main.system.auxiliary.log.LogMaster.log(1, "Aggro group: " + list +
             "; last: " + lastAggroGroup);
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
            if (PositionMaster.getExactDistance(hero, unit)>=
                    4+ unit.getSightRangeTowards(hero))
                continue;
            if (unit.getAI().getEngagementDuration() > 0) {
                set.add(unit);
            }
            if (unit.getAI().isEngaged()) {
                set.add(unit);
                newAggro = true;
                unit.getAI().setEngaged(false);
            }
            if (!unit.getGame().getVisionMaster().getVisionRule().isAggro(hero, unit))
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
        return false;
        //        Unit unit = ai.getUnit();
        // PERCEPTION_STATUS_PLAYER status =
        // ai.getGroup().getPerceptionStatus();

        // after each action? some events may trigger it separately
        // any hostile action triggers Engagement with the group, even if they
        // don't see you
        // HEARING would be an important factor...
        // for (DC_HeroObj unit : getCreeps()) {
        //        List<Unit> relevantEnemies = getRelevantEnemies(unit);
        //        for (Unit hero : relevantEnemies) {
        // preCheck detections - perhaps it's really just about making a preCheck
        // before AS-constr.
        // sometimes creeps may be engaged but not know what hit them...
        // different aggro levels?
        // if (status == PERCEPTION_STATUS_PLAYER.KNOWN_TO_BE_THERE)
        // return true;
        //            if (VisionManager.checkVisible(hero)) {
        //                return true;
        //            }
        //        }
        // }
    }

    public static void unitAttacked(DC_ActiveObj action, Obj targetObj) {

        if (targetObj.isMine()) {
            action.getOwnerUnit().
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

    private boolean checkEngaged() {
        List<Unit> aggroGroup = AggroMaster.getAggroGroup();

        if (!aggroGroup.isEmpty()) {
            for (Unit sub : aggroGroup) {
                sub.getAI().setStandingOrders(null);
            }
            for (Unit sub : master.getGame().getUnits()) {
                sub.getAI().setOutsideCombat(false);
                if (!aggroGroup.contains(sub))
                    if (!sub.isMine())
                        //                    if (!master.getAiMaster().getAllies().contains(sub))
                        sub.getAI().setOutsideCombat(true);
            }
            return true;
        }
        //        for (Unit ally :allies) {
        // check block if (unit.getCoordinates())
        //         TODO    if (master. getGame().getVisionMaster().getDetectionMaster().checkDetected(ally))
        //                return true;
        //        }
        return false;
    }

    public boolean checkExplorationDefault() {
        return !checkEngaged();
    }

    public void checkStatusUpdate() {
        if (checkEngaged()) {
            master.switchExplorationMode(false);
        } else {
            if (checkDanger()) {
                //?
            } else {
                master.switchExplorationMode(true);
            }
        }

    }

    private boolean checkDanger() {
        //range? potential vision?
        //        for (Unit unit : allies) {
        //            if (master. getGame().getAiManager().getAnalyzer().getClosestEnemyDistance(unit)
        //                > minDistance)
        //                return true;
        //        }
        return false;

    }

    public enum CRAWL_STATUS {
        EXPLORE,
        DANGER, //rounds on? behaviors ?
        ENGAGED,
        TIME_RUN
    }


    public enum ENGAGEMENT_LEVEL {

        UNSUSPECTING, // will use its behavior and rest actions
        SUSPECTING, // will search, ambush or stalk
        ALARMED // will not Rest or otherwise let down their guard
        ,
        AGGRO // will engage and make combat-actions
    }


}
