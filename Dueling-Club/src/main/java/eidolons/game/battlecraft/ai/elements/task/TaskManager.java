package eidolons.game.battlecraft.ai.elements.task;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.battlecraft.rules.combat.attack.GuardRule;
import main.content.enums.entity.AbilityEnums.TARGETING_MODE;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.XList;
import main.entity.obj.Obj;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

    //TODO refactor class
public class TaskManager extends AiHandler {

    public TaskManager(AiMaster master) {
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
            if (task.getArg() instanceof Integer)
                if (action.getGame().getObjectById((Integer) task.getArg()) instanceof Unit) {
                    Unit target = (Unit) action.getGame().getObjectById(
                            (Integer) task.getArg());
                    switch (mode) {

                        case ANY_ITEM:
                        case MULTI:

                        case CORPSE:
                        case MY_ITEM:
                            break;
                        case ANY_ARMOR:
                            if (target.getArmor() != null) {
                                return target.getArmor().getId();
                            }
                            return null;
                        case ANY_WEAPON:
                        case ENEMY_WEAPON:
                            if (target.getMainWeapon() != null) {
                                return target.getMainWeapon().getId();
                            }
                            if (target.getOffhandWeapon() != null) {
                                return target.getOffhandWeapon().getId();
                            }
                            return null;

                        case ENEMY_ITEM:
                        case ENEMY_ARMOR:
                            if (target.getArmor() != null) {
                                return target.getArmor().getId();
                            } else {
                                return null;
                            }
                        case MY_WEAPON:
                            if (action.getOwnerUnit().getMainWeapon() != null) {
                                return action.getOwnerUnit().getMainWeapon().getId();
                            }
                            if (action.getOwnerUnit().getOffhandWeapon() != null) {
                                return action.getOwnerUnit().getOffhandWeapon().getId();
                            }
                            return null;
                        case MY_ARMOR:
                            if (action.getOwnerUnit().getArmor() != null) {
                                return action.getOwnerUnit().getArmor().getId();
                            }
                            return null;
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
                return new ArrayList<>(
                        Collections.singletonList(new Task(ai, goal, ai.getCurrentOrder().getArg())));

        List<Integer> ids = new ArrayList<>();
        List<? extends DC_Obj> targets = new ArrayList<>();
        List<? extends DC_Obj> targets2 = new ArrayList<>();
        List<BattleFieldObject> targets3 = new ArrayList<>();

        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        // ai.getGroup().getBehaviorPref();
        // ai.getGroup().getKnownEnemyCoordinatesMap();
        switch (goal) {
            case STAND_GUARD:
            case AMBUSH:
                // preCheck engagement level, default prefs
            case SELF:
            case STEALTH:
                list.add(new Task(forced, ai, goal, ai.getUnit().getId()));
                break;
            // SPEC MODE - KIND OF ON ALERT...
            case PATROL:
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
                targets = new ArrayList<>(getCellPrioritizer().getApproachCells(ai));
                break;

            case SEARCH:
                // or maybe the last-seen enemies?
                targets3.add(ai.getUnit());
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

            case AUTO_DAMAGE:
            case AUTO_DEBUFF:
            case AUTO_BUFF:
                // list.add(new Task(ai, goal, null));
            case RESTORE:
            case BUFF:
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
                    //                    List<Set<BattleFieldObject>> objs = Analyzer.getCells(ai, true, true, false).stream().map(
                    //                            c -> game.getObjectsOnCoordinate(c.getCoordinates())).collect(Collectors.toList());
                    for (DC_Cell cell : Analyzer.getCells(ai, true, false, false)) {
                        targets3.addAll(game.getObjectsOnCoordinateNoOverlaying(cell.getCoordinates()));
                    }
                } else {
                    // if (forced)
                    // targets = (Analyzer.getUnits(ai, false, true, true,
                    // false, true));
                    // else
                    targets = Analyzer.getVisibleEnemies(ai); // TODO detected!
                }

                checkPrune(targets, goal, ai, action);
                break;
            case WAIT:
                if (!DC_Engine.isAtbMode())
                    targets = Analyzer.getWaitUnits(ai);
                break;
            case PROTECT:
                if (RuleKeeper.isRuleOn(RULE.GUARD))
                    if (GuardRule.on)
                        targets = Analyzer.getProtectCells(ai);
                break;

            case COATING:
                Set<Obj> objects = action.getTargeting().getFilter().getObjects(action.getRef());
                for (Obj q : objects) {
                    if (q.getRef().getSourceObj() == getUnit()) { //q.isOwnedBy(ai.getUnit().getOwner())
                        ids.add(q.getId());
                    }
                }
                break;

            default:
                list.add(new Task(forced, ai, goal, null));
                break;
        }
        if (targets.isEmpty())
            if (targets3.isEmpty()) {
                return new ArrayList<>();
            }
        if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
            DC_Obj target = targets.get(new RandomWizard<>().getRandomIndex(targets));
            List<Task> tasks = new ArrayList<>();
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
            for (DC_Obj obj : targets3) {
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
