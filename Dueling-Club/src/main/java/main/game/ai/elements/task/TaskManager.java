package main.game.ai.elements.task;

import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.XList;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.UnitAI;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.tools.Analyzer;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TaskManager extends AiHandler {

    public TaskManager(AiHandler master) {
        super(master);
    }

    public static Integer checkTaskArgReplacement(Task task, DC_ActiveObj action) {
        // "custom targeting" of sorts !
        TARGETING_MODE mode = action.getTargetingMode();
        if (mode == null) {
            mode = new EnumMaster<TARGETING_MODE>().retrieveEnumConst(TARGETING_MODE.class, action
                    .getProperty(G_PROPS.TARGETING_MODE));
        }
        if (mode != null) {
            if (action.getGame().getObjectById((Integer) task.getArg()) instanceof Unit) {
                Unit target = (Unit) action.getGame().getObjectById(
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

    public List<Task> getTasks(GOAL_TYPE goal, UnitAI ai, DC_ActiveObj action) {
        return getTasks(goal, ai, false, action);
    }

    public List<Task> getTasks(GOAL_TYPE goal, UnitAI ai, boolean forced, DC_ActiveObj action) {
        List<Task> list = new XList<>();
        if (ai.getCurrentOrder() != null)
            if (ai.getCurrentOrder().getArg() != null)
                return new LinkedList<>(
                 Arrays.asList(new Task[]{new Task(ai, goal, ai.getCurrentOrder().getArg())}));

        List<Integer> ids = new LinkedList<>();
        List<? extends DC_Obj> targets = new LinkedList<>();
        List<? extends DC_Obj> targets2 = new LinkedList<>();

        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        // ai.getGroup().getBehaviorPref();
        // ai.getGroup().getKnownEnemyCoordinatesMap();
        switch (goal) {
            case GUARD:
            case AMBUSH:
                // preCheck engagement level, default prefs
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
                targets = getCellPrioritizer().getApproachCells(ai);
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
                if (ai.getBehaviorMode() == AiEnums.BEHAVIOR_MODE.PANIC) {
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
                if (behaviorMode == AiEnums.BEHAVIOR_MODE.BERSERK || behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
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
                    if ( q.getRef().getSourceObj()==unit) { //q.isOwnedBy(ai.getUnit().getOwner())
                        ids.add(q.getId());
                   }
                }
                break;

            default:
                list.add(new Task(forced, ai, goal, null));
                break;
        }
        if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
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
//        ActionManager.setTargetPool(targets); ???

        return list;
    }
    private void checkPrune(List<? extends DC_Obj> targets, GOAL_TYPE goal, UnitAI ai,
                            DC_ActiveObj action) {
        getPruneMaster().pruneTargetsForAction(targets, goal, ai, action);
    }

}
