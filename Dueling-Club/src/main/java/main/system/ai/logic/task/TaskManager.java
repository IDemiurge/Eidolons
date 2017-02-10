package main.system.ai.logic.task;

import main.content.CONTENT_CONSTS.BEHAVIOR_MODE;
import main.content.CONTENT_CONSTS.TARGETING_MODE;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.data.XList;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.ai.UnitAI;
import main.system.ai.logic.actions.ActionManager;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;
import main.system.ai.logic.path.CellPrioritizer;
import main.system.ai.logic.priority.PriorityManager;
import main.system.ai.tools.Analyzer;
import main.system.ai.tools.ParamAnalyzer;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TaskManager {
    // include non-targeted actions as well, zone and so on

    private static final Integer DEFAULT_PRUNE_SIZE = 5;
    private static Integer forcedPruneSize;

    public static Integer checkTaskArgReplacement(Task task, DC_ActiveObj action) {
        // "custom targeting" of sorts !
        TARGETING_MODE mode = action.getTargetingMode();
        if (mode == null) {
            mode = new EnumMaster<TARGETING_MODE>().retrieveEnumConst(TARGETING_MODE.class, action
                    .getProperty(G_PROPS.TARGETING_MODE));
        }
        if (mode != null) {
            if (action.getGame().getObjectById((Integer) task.getArg()) instanceof DC_HeroObj) {
                DC_HeroObj target = (DC_HeroObj) action.getGame().getObjectById(
                        (Integer) task.getArg());
                switch (mode) {

                    case ANY_ITEM:
                        break;
                    case ANY_ARMOR:
                        if (target.getArmor() != null) {
                            return target.getArmor().getId();
                        }
                        return null;
                    case ANY_WEAPON:
                        if (target.getMainWeapon() != null) {
                            return target.getMainWeapon().getId();
                        }
                        if (target.getSecondWeapon() != null) {
                            return target.getSecondWeapon().getId();
                        }
                        return null;

                    case ENEMY_ITEM:
                    case ENEMY_ARMOR:
                        if (target.getArmor() != null) {
                            return target.getArmor().getId();
                        } else {
                            return null;
                        }
                    case ENEMY_WEAPON:
                        if (target.getMainWeapon() != null) {
                            return target.getMainWeapon().getId();
                        }
                        if (target.getSecondWeapon() != null) {
                            return target.getSecondWeapon().getId();
                        }
                        return null;
                    case MY_ITEM:
                        break;
                    case MY_WEAPON:
                        if (action.getOwnerObj().getMainWeapon() != null) {
                            return action.getOwnerObj().getMainWeapon().getId();
                        }
                        if (action.getOwnerObj().getSecondWeapon() != null) {
                            return action.getOwnerObj().getSecondWeapon().getId();
                        }
                        return null;
                    case MY_ARMOR:
                        if (action.getOwnerObj().getArmor() != null) {
                            return action.getOwnerObj().getArmor().getId();
                        }
                        return null;

                    case CORPSE:
                        break;
                    case MULTI:
                        break;
                }
            }
        }

        return (Integer) task.getArg();
    }

    public static Integer getForcedPruneSize() {
        return forcedPruneSize;
    }

    public static void setForcedPruneSize(Integer forcedPruneSize) {
        TaskManager.forcedPruneSize = forcedPruneSize;
    }

    public List<Task> getTasks(GOAL_TYPE goal, UnitAI ai, DC_ActiveObj action) {
        return getTasks(goal, ai, false, action);
    }

    public List<Task> getTasks(GOAL_TYPE goal, UnitAI ai, boolean forced, DC_ActiveObj action) {
        List<Task> list = new XList<>();

        List<Integer> ids = new LinkedList<>();
        List<? extends DC_Obj> targets = new LinkedList<>();
        List<? extends DC_Obj> targets2 = new LinkedList<>();

        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        // ai.getGroup().getBehaviorPref();
        // ai.getGroup().getKnownEnemyCoordinatesMap();
        switch (goal) {
            case GUARD:
            case AMBUSH:
                // check engagement level, default prefs
            case SELF:
            case STEALTH:
                list.add(new Task(forced, ai, goal, ai.getUnit().getId()));
                break;
            // SPEC MODE - KIND OF ON ALERT...
            case PATROL:
                targets = Analyzer.getWanderCells(ai);
                break;
            case WANDER: // RANDOM DESTINATION MOVEMENT, BLOCK SPECIAL MOVES
                // limit max distance from original spawning position
                // TODO sometimes in chosen direction
                targets = Analyzer.getWanderCells(ai);
                break;
            case STALK:
                // limit max distance from enemy position - by stealth perhaps
                // targets = Analyzer.getStalkCells(ai);
                // ai.getGroup().getKnownEnemies
                break;
            case APPROACH:
                targets = CellPrioritizer.getApproachCells(ai);
                break;

            case SEARCH:
                // or maybe the last-seen enemies?
                if (!forced) {
                    if (ai.getUnit().getBuff("Search Mode") == null) {
                        list.add(new Task(forced, ai, goal, ai.getUnit().getId()));
                        break;
                    }
                }
                targets = Analyzer.getSearchCells(ai);
                break;
            case RETREAT:
                if (ai.getBehaviorMode() == BEHAVIOR_MODE.PANIC) {
                    // only border cells => flee
                }
                targets = Analyzer.getSafeCells(ai);
                break;
            case MOVE:
                targets = Analyzer.getMoveTargetCells(ai);
                break;

            case CUSTOM_HOSTILE:
                targets = Analyzer.getVisibleEnemies(ai);
                checkPrune(targets, goal, ai, action);
                break;
            case CUSTOM_SUPPORT:
                targets = Analyzer.getAllies(ai);
                checkPrune(targets, goal, ai, action);
                break;
            case ZONE_DAMAGE:
                targets = Analyzer.getZoneDamageCells(ai.getUnit());

                targets2 = Analyzer.getAdjacentEnemies(ai.getUnit(), false);
                checkPrune(targets, goal, ai, action);
                break;
            case SUMMONING:
                targets = Analyzer.getSummonCells(ai, action);
                break;
            case DEBILITATE:
            case DEBUFF:
            case ATTACK:
                if (behaviorMode == BEHAVIOR_MODE.BERSERK || behaviorMode == BEHAVIOR_MODE.CONFUSED) {
                    targets = (Analyzer.getUnits(ai, true, true, true, false));
                } else {
                    // if (forced)
                    // targets = (Analyzer.getUnits(ai, false, true, true,
                    // false, true));
                    // else
                    targets = Analyzer.getVisibleEnemies(ai); // TODO detected!
                }

                checkPrune(targets, goal, ai, action);
                break;

            case AUTO_DAMAGE:
            case AUTO_DEBUFF:
            case AUTO_BUFF:
                // list.add(new Task(ai, goal, null));
            case RESTORE:
            case BUFF:
                targets = Analyzer.getAllies(ai);
                checkPrune(targets, goal, ai, action);
                break;
            case WAIT:
                targets = Analyzer.getWaitUnits(ai);
                break;

            case COATING:
                Set<Obj> objects = action.getTargeting().getFilter().getObjects(action.getRef());
                for (Obj q : objects) {
                    if (q.isOwnedBy(ai.getUnit().getOwner())) {
                        ids.add(q.getId());
                    }
                }
                break;

            default:
                list.add(new Task(forced, ai, goal, null));
                break;
        }
        if (behaviorMode == BEHAVIOR_MODE.CONFUSED) {
            DC_Obj target = targets.get(new RandomWizard<>().getRandomListIndex(targets));
            List<Task> tasks = new LinkedList<>();
            tasks.add(new Task(forced, ai, goal, target.getId()));
            return tasks;
        }
        if (list.isEmpty()) {
            for (DC_Obj obj : targets) {
                list.add(new Task(forced, ai, goal, obj.getId()));
            }
            for (DC_Obj obj : targets2) {
                list.add(new Task(forced, ai, goal, obj.getId()));
            }
            for (Integer id : ids) {
                list.add(new Task(forced, ai, goal, id));
            }
        }
        ActionManager.setTargetPool(targets);

        return list;
    }

    private void checkPrune(List<? extends DC_Obj> targets, GOAL_TYPE goal, UnitAI ai,
                            DC_ActiveObj action) {
        /*
		 * cache for each goal?
		 *
		 * the 'pruneSize' must be a minimum of targets to prune... and beyond
		 * that, there should be some default prune logic
		 */
        for (DC_Obj e : targets) {
            if (!(e instanceof DC_HeroObj)) {
                return; // another method?
            }
        }
        int size = targets.size();
        int toPrune = size - getPruneSize(goal);
        if (toPrune <= 0) // this is only the max size, how to ensure pruning of
            // 'valid' targets too?
        {
            if (action.isRanged() || action instanceof DC_SpellObj) {
                return; // TODO sometimes it's not the size, but the distance!
            }
        }
        // for melee...
        Boolean byCapacity = true;
        Boolean byHealth = true;
        Boolean byDistance = true;
        Boolean byDanger = false;
        Boolean byPower = true;
        Boolean byType = false;
        switch (goal) {
            case RESTORE:
                byHealth = false;
                break;
            case ATTACK:
                byHealth = false;
                byDanger = true;
                break;
            case BUFF:
                byDanger = true; // TODO ignore near-dead and 'surrounded'
                // enemies ready to fall
                break;
            case CUSTOM_HOSTILE:
            case CUSTOM_SUPPORT:
        }
        int minDistance = 999;
        List<DC_Obj> pruneList = new LinkedList<>();
        // TODO sort() first? to be sure to cut off the tail...
        int maxPower = ParamAnalyzer.getMaxParam(PARAMS.POWER, new LinkedList<>(targets));
        int limit = 0;
        boolean first = true;
        pruneLoop:
        while ((first || pruneList.size() < toPrune) && limit < 5) {
            first = false;
            for (DC_Obj t : targets) {
                if (pruneList.contains(t)) {
                    continue;
                }
                boolean result = false;
                while (true) {
                    if (byDistance) {
                        int distance = PositionMaster.getDistance(t, ai.getUnit());
                        if (distance < minDistance) {
                            minDistance = distance;
                        } else {
                            result = (getDistancePruneFactor(limit, t, ai, action)) < distance
                                    - minDistance;
                        }
                        if (result) {
                            break;
                        }
                    }

                    if (byCapacity) {
                        float capacity = PriorityManager.getCapacity((DC_HeroObj) t);
                        // < 0.1f

                        if (result) {
                            break;
                        }
                    }

                    if (byHealth) {
                        int health = PriorityManager.getHealthFactor(t, byHealth);// ?
                        result = (health < getHeathPruneFactor(limit, t, ai, action));
                        if (result) {
                            break;
                        }
                    }
                    if (byPower) {
                        result = t.getIntParam(PARAMS.POWER) * 100 / maxPower < getPowerFactor(
                                limit, ai, action);
                        if (result) {
                            break;
                        }
                    }
                    // by danger
                    break;
                }
                if (result) {
                    pruneList.add(t);
                    continue pruneLoop;
                }

            }
            // if nobody was pruned, increase limits
            limit++;
        }

        for (DC_Obj t : pruneList) {
            targets.remove(t);
        }

    }

    private int getPowerFactor(int limit, UnitAI ai, DC_ActiveObj action) {
        // cruel vs merciful
        ai.checkMod(AI_MODIFIERS.CRUEL);
        if (action.isMelee()) {
            return 10 + limit * 3;
        }
        return 30 + limit * 2;
    }

    private int getHeathPruneFactor(int limit, DC_Obj t, UnitAI ai, DC_ActiveObj action) {
        if (action.isMelee()) {
            return 15 + limit * 3;
        }
        return 10 + limit * 2;
        // TODO shouldn't it work both ways?.. sometimes too much
        // power/health/capacity/danger is prune factor!
    }

    private int getCapacityPruneFactor(int limit, DC_Obj t, UnitAI ai, DC_ActiveObj action) {
        if (action.isMelee()) {
            return 15 + limit * 3;
        }
        return 10 + limit * 2;
    }

    private int getDistancePruneFactor(int limit, DC_Obj t, UnitAI ai, DC_ActiveObj action) {
        if (action.isRanged()) {
            return action.getIntParam(PARAMS.RANGE) * 3 / 2 - limit;
        }
        // TODO for quick flyers?
        return 6 - limit;
    }

    private Integer getPruneSize(GOAL_TYPE goal) {
        if (forcedPruneSize != null) {
            return forcedPruneSize;
        }
        return DEFAULT_PRUNE_SIZE;
    }

}
