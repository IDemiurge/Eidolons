package eidolons.game.battlecraft.ai.elements.actions.sequence;

import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.battlecraft.ai.elements.actions.AiQuickItemAction;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.Goal;
import eidolons.game.battlecraft.ai.elements.goal.GoalManager;
import eidolons.game.battlecraft.ai.elements.task.Task;
import eidolons.game.battlecraft.ai.elements.task.TaskManager;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.ai.tools.target.ReasonMaster;
import eidolons.game.battlecraft.ai.tools.target.ReasonMaster.FILTER_REASON;
import eidolons.game.battlecraft.ai.tools.target.TargetingMaster;
import eidolons.game.battlecraft.ai.tools.time.TimeLimitMaster;
import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.List;

public class ActionSequenceConstructor extends AiHandler {

    int defaultDistancePruneFactor = 3;
    private Unit unit;
    private List<Coordinates> prioritizedCells;

    public ActionSequenceConstructor(AiMaster master) {
        super(master);
    }


    public List<ActionSequence> createActionSequences(UnitAI ai) {
        TimeLimitMaster.markTimeForAI(ai);
        List<ActionSequence> list = new ArrayList<>();
        getActionSequenceConstructor().setPrioritizedCells(null);
        boolean forced = false;
        if (ai.getCurrentOrder() != null)
            forced = true;
        for (GOAL_TYPE type : GoalManager.getGoalsForUnit(ai)) {
            List<ActionSequence> sequences;
            try {
                Goal goal = new Goal(type, null // ???
                 , ai);
                goal.setForced(forced);
                sequences = createActionSequencesForGoal(goal, ai);
                list.addAll(sequences);

            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (!TimeLimitMaster.checkTimeLimitForAi(ai)) {
                break;
            }
        }
        return list;
    }


    public List<ActionSequence> createActionSequencesForGoal(Goal goal, UnitAI ai) {
        List<ActionSequence> actionSequences = new ArrayList<>();
        List<ActiveObj> actions = AiUnitActionMaster.getFullActionList(goal.getTYPE(), ai.getUnit());
        //TODO NF Rules revamp
        // actions.addAll(addSubactions(actions));
        for (ActiveObj action : actions) {
            if (!TimeLimitMaster.checkTimeLimitForAi(getUnitAi()))
                break;

            Chronos.mark(getChronosPrefix() + action);
            List<Task> tasks = getTaskManager().getTasks(goal.getTYPE(), ai, goal.isForced(), action);
            for (Task task : tasks) {
                if (task.isBlocked()) {
                    continue;
                }
                if (actionSequences.size() > 0) {
                    long time = TimeLimitMaster.getTimeLimitForAction();
                    if (Chronos.getTimeElapsedForMark(getChronosPrefix() + action) > time) {
                        LogMaster.log(1, "*********** TIME ELAPSED FOR  "
                         + action + StringMaster.wrapInParenthesis(time + ""));
                        break;
                    }
                }
//                String string = task.toString();
//                Obj obj = action.getGame().getObjectById((Integer) task.getArg());
//                if (obj != null) {
//                    string = obj.getName();
//                }
                try {
//                    Chronos.mark(getChronosPrefix() + string);
                    actionSequences.addAll(getSequences(task, action));
//                    Chronos.logTimeElapsedForMark(getChronosPrefix() + string);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            Chronos.logTimeElapsedForMark(getChronosPrefix() + action);
        }
        return actionSequences;
    }

    private List<ActionSequence> getSequences(Task task, ActiveObj active) {
        List<ActionSequence> sequences = new ArrayList<>();
        Ref ref = task.getUnit().getRef().getCopy();
        Integer arg = TaskManager.checkTaskArgReplacement(task, active);
//        if (arg == null) {
//       if (isArgNeeded(active))     return;
//        }
        ref.setTarget(arg);
        List<ActionSequence> newSequences = null;
        AiAction aiAction = AiActionFactory.newAction(active, ref);
        aiAction.setTask(task);
        try {
            newSequences = getSequencesWithPathsForAction(aiAction, task.getArg(), task);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (ListMaster.isNotEmpty(newSequences)) {
            sequences.addAll(newSequences);
        } else {
            // if no pathing is required/available [QUICK FIX]
            if (!aiAction.canBeTargeted(arg)) {
                return sequences;
            }
            ActionSequence sequence = constructSingleActionSequence(aiAction, task);
            if (sequence != null) {
                if (active.isRanged()) {
                    sequences.addAll(AiUnitActionMaster.splitRangedSequence(sequence));
                } else {
                    sequences.add(sequence);
                }
            } else {

                // if (unit.getUnitAi().getLogLevel() > UnitAI.LOG_LEVEL_NONE)
                // TODO smarter logging?
                // main.system.auxiliary.LogMaster.log(1, "***" +
                // action.toString()
                // + " could not be constructed into an action sequence!");

                // return;
            }
        }
        return sequences;
    }



    private List<ActionSequence> getSequencesWithPathsForAction(AiAction aiAction, Object arg, Task task) {
        List<ActionSequence> list = new ArrayList<>();
        Game game = aiAction.getRef().getGame();
        unit = aiAction.getSource();

        if (task.getAI().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.PANIC) {
            // target action = FLEE;
        }
        if (task.getType() == AiEnums.GOAL_TYPE.RETREAT) {
            {
                return getSequencesFromPaths(
                 getPathSequenceConstructor().getRetreatPaths(arg), task, aiAction);
            } // TODO
        }

        boolean singleAction = aiAction.isSingle();
        if (task.getType() == GOAL_TYPE.PROTECT) {
            singleAction = aiAction.getSource().getCoordinates().equals(
             aiAction.getTarget().getCoordinates());

        } else if (!singleAction) {
            if (arg != null) {
                singleAction =
                 // action.canBeTargeted(StringMaster.getInteger(arg
                 // .toString()));
                 TargetingMaster.canBeTargeted(aiAction, true);
            } else {
                singleAction = (aiAction).canBeActivated();
            }
        }

        // if (!singleAction)
        // if (ReasonMaster.getReasons(action).getOrCreate(0)==FILTER_REASON.FACING)
        if (singleAction) {
            ActionSequence sequence = constructSingleActionSequence(aiAction, task);
            if (sequence == null) {
                return null;
            }
            list.add(sequence);
            return list;
        }
        if (!task.isForced()) { //TODO REFACTOR
            if (task.getType() != GOAL_TYPE.PROTECT && task.getType() != AiEnums.GOAL_TYPE.ATTACK && task.getType() != AiEnums.GOAL_TYPE.RETREAT
             && task.getType() != AiEnums.GOAL_TYPE.SEARCH && task.getType() != AiEnums.GOAL_TYPE.MOVE
             && !task.getType().isBehavior()) {
                return null;
            }
        }
        if (task.getType() == AiEnums.GOAL_TYPE.SUMMONING) {
            return null;
        }
        if (task.getType() == AiEnums.GOAL_TYPE.ZONE_DAMAGE) {
            return null; // TODO until pathing/cell-pr. is fixed
        }
        if (!task.isForced()) {
            if (task.getAI().getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
                return null;
            }
        }

        Unit unit = (Unit) aiAction.getRef().getSourceObj();
        List<ActiveObj> moveActions = getMoveActions(aiAction);

        if (!ListMaster.isNotEmpty(moveActions)) {
            // [QUICK FIX]
            if (!unit.getAction(ActionEnums.STD_ACTIONS.Turn_Anticlockwise.name())
             .canBeActivated(aiAction.getRef(), true)
             && !unit.getAction(ActionEnums.STD_ACTIONS.Move.name()).canBeActivated(
             aiAction.getRef(), true)) {
                return null;
            }
        } else {
            // if (prioritizedCells == null)
            // prioritizedCells = CellPrioritizer
            // .getMeleePriorityCellsForUnit(unit.getUnitAi(),
            // moveActions, action);
        }

        List<ActionPath> paths = getPathSequenceConstructor().getPathSequences(moveActions, aiAction);
        list = getSequencesFromPaths(paths, task, aiAction);
//      TODO ?!  if (list.isEmpty()) {
//            paths = getPathSequenceConstructor().getPathSequences(moveActions, action);
//            list = getSequencesFromPaths(paths, task, action);
//        }
        return list;

    }
    public ActionSequence getSequenceFromPath(ActionPath path , UnitAI ai) {
        return getSequenceFromPath(path, new Task(ai, GOAL_TYPE.MOVE, null));
    }
    public ActionSequence getSequenceFromPath(ActionPath path , Task task) {
       return new ActionSequence(path.getActions(), task, task.getAI());

    }

    public List<ActionSequence> getSequencesFromPaths(List<ActionPath> paths, Task task,
                                                      AiAction aiAction) {
        List<ActionSequence> list = new ArrayList<>();
        for (ActionPath path : paths) {
            if (!TimeLimitMaster.checkTimeLimitForAi(getUnitAi()))
                break;
            ActionSequence sequence = new ActionSequence(path.getActions(), task, task.getAI());
            if (aiAction.getActive().isRanged()) {
                List<AiAction> rangedAttackSequence = constructSingleAttackSequence(aiAction, task);
                if (rangedAttackSequence.isEmpty()) {
                    return list;
                }
                // TODO
                sequence.getActions().addAll(rangedAttackSequence);
            } else {
                sequence.getActions().add(aiAction);
            }
            list.add(sequence);
        }
        return list;
    }

    // now replaced with Atomic logic?
    private List<AiQuickItemAction> getRangedReloadAction(AiAction aiAction) {
        Obj weapon = aiAction.getActive().getRef().getObj(KEYS.RANGED);
        WEAPON_GROUP weapon_group = null;
        List<AiQuickItemAction> list = new ArrayList<>();
        if (weapon instanceof WeaponItem) {
            WeaponItem _WeaponItem = (WeaponItem) weapon;
            if (_WeaponItem.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BOWS) {
                weapon_group = ItemEnums.WEAPON_GROUP.ARROWS;
            }
            if (_WeaponItem.getWeaponGroup() == ItemEnums.WEAPON_GROUP.CROSSBOWS) {
                weapon_group = ItemEnums.WEAPON_GROUP.BOLTS;
            }

            for (QuickItem ammo : aiAction.getSource().getQuickItems()) {
                if (!ammo.isAmmo()) {
                    continue;
                }
                ammo.construct();
                if (ammo.getWrappedWeapon().getWeaponGroup() == weapon_group) {
                    AiQuickItemAction qia = new AiQuickItemAction(ammo);
                    if (qia.canBeActivated()) {
                        list.add(qia);
                    }

                }
            }
        }
        return list;
    }

    private String getChronosPrefix() {
        return "TIMED AI ACTION ";
    }

    private List<ActiveObj> getMoveActions(AiAction aiAction) {
        return DC_MovementManager.getMoves(unit);
    }

    public ActionSequence constructSingleActionSequence(AiAction targetAiAction, Task task) {
        return constructSingleActionSequence(targetAiAction, task, false);
    }
    public ActionSequence constructSingleActionSequence(AiAction targetAiAction, Task task, boolean free) {
        List<AiAction> aiActions = new ArrayList<>();
        UnitAI ai = task.getAI();
        targetAiAction.getRef().setID(KEYS.ACTIVE, targetAiAction.getActive().getId());
        switch (task.getType()) {
            case ATTACK: {
                aiActions = constructSingleAttackSequence(targetAiAction, task); // only facing!
                break;
            }
            case DEBUFF:
            case BUFF:
                aiActions.add(targetAiAction);
                break;
            // case RETREAT:
            // // preCheck *FLEE*
            // // actions = getMoveSequence(targetAction, task);
            // break;
            // case SEARCH:
            // break;
            // case SELF:
            // break;
            // case ZONE_SPECIAL:
            // break;
            default:
                aiActions.add(targetAiAction);
                break;
        }
        if (aiActions.isEmpty()) {
            return null;
        }
        // not very good
        AiAction aiAction = aiActions.get(0);
        if (!free)
        if (!aiAction.canBeActivated()) {

            LogMaster.log(LOG_CHANNEL.AI_DEBUG2, "No sequence for "
             + aiActions.get(aiActions.size() - 1) + " - " + aiAction.getActive().getName() + ": "
             + aiAction.getActive().getCosts().getReason());
            return null;
        }

        return new ActionSequence(aiActions, task, ai);
    }

    private List<AiAction> constructSingleAttackSequence(AiAction targetAiAction, Task task) {
        List<AiAction> list = new ArrayList<>();
        if (task.getArg() instanceof Integer) {
            Integer id = (Integer) task.getArg();

//            if (targetAction.getActive().isRanged()) {
//                targetAction.getActive().setForcePresetTarget(true);
//                if (!targetAction.canBeActivated()) {
//                    if (ReasonMaster.checkReasonCannotActivate(targetAction,
//                            SPECIAL_REQUIREMENTS.REF_NOT_EMPTY.getText(KEYS.RANGED.toString(),
//                                    KEYS.AMMO.toString()))) {
//                        List<AiQuickItemAction> reloadActions = getRangedReloadAction(targetAction);
//                        // will then split- ActionManager.splitRangedSequence()
//                        if (reloadActions.isEmpty()) {
//                            return list;
//                        }
//                        list.addAll(reloadActions);
//                    }
//                }
//            }
            if (targetAiAction.canBeTargeted(id)) {
                list.add(targetAiAction);
            } else {
                List<FILTER_REASON> reasons = ReasonMaster.getReasonsCannotTarget(targetAiAction);
                reasons.remove(FILTER_REASON.VISION); // ??
                if (reasons.size() > 1 && !targetAiAction.getActive().isRanged()) {
                    return list;
                }
               if (targetAiAction.getActive().isRanged()) {
                    list.add(targetAiAction);
                }
            }
        }

        return list;
    }

    private Coordinates getNextClosestCoordinate(Unit unit, AiAction targetAiAction) {
        // TODO Auto-generated method stub
        return null;
    }


    // getOrCreate the *best* move sequence? create all then auto-compare via priority
    // manager
    // private   Collection<? extends Action> getMoveSequence(Action
    // action) {
    // Conditions conditions = (action.getActive()
    // .getTargeting().getFilter().getConditions());
    // DistanceCondition condition = null;
    // for (Condition c : conditions) {
    // if (c instanceof DistanceCondition) {
    // condition = (DistanceCondition) c;
    // break;
    // }
    // }
    // Formula distance = condition.getDistance();
    // Coordinates target_coordinates = action.getRef().getTargetObj()
    // .getCoordinates();
    // Coordinates source_coordinates = action.getRef().getSourceObj()
    // .getCoordinates();
    // List<DC_ActiveObj> moveActions = ActionManager
    // .getFullActionList(GOAL_TYPE.CLOSE_IN, (DC_HeroObj) action
    // .getRef().getSourceObj());
    //
    // Condition cellConditions = new Conditions(new NotCondition(
    // new OccupiedCondition(KEYS.MATCH.toString())),
    // new DistanceCondition(distance, KEYS.MATCH.toString(),
    // KEYS.TARGET.toString()));
    // List<Obj> viableCoordinates = new Filter<Obj>(action.getRef(),
    // cellConditions).filter(DC_Game.game.getCellsSet());
    // target_coordinates = PositionMaster
    // .getClosestCoordinate(source_coordinates, viableCoordinates);
    // // List<ActionSequence> sequences = new ArrayList<>();
    // // for (Obj cell : viableCoordinates) {
    // // ActionSequence sequence = generateMoveSequence(source_coordinates,
    // // cell
    // // .getCoordinates(), moveActions);
    // // sequences.add(sequence);
    // // }
    // // // create a list of sequences, then choose by priority
    // // // or perhaps just go for the closest cell always, what could be the
    // // // reason not to? Nothing that isn't above AI's level! :)
    // // PriorityManager.sortByPriority(sequences);
    // return generateMoveSequence(source_coordinates, target_coordinates,
    // moveActions)
    // .getActions();
    // }
    //
    // private   ActionSequence generateMoveSequence(Coordinates
    // source_coordinates, Coordinates target_coordinates, List<DC_ActiveObj>
    // moveActions) {
    // // how do I simulate special moves best?
    // // or is it like we just generate all the possible sequences and
    // // choose... by shortness?
    // // still, I have to simulate the action's effect!
    // // and since their effects are "selective"...
    // // in the worst case, I can of course settle for ignoring the special
    // // moves...
    //
    // // for (DC_ActiveObj action : moveActions)
    // // if (action.getName().equals(STD_ACTIONS.Move.name()))
    // // standardMove = action;
    //
    // // let's built a path cell by cell then, ensuring the right facing at
    // // each step!
    // // maybe not "turning sequence", but just *individual turns* for each
    // // move if necessary
    //
    // // yes, i can build such a path for *std moves*...
    //
    // // I would want units to
    //
    // // PriorityManager.sortByPriority(sequences);
    // // return sequences.getOrCreate(0);
    //
    // return null;
    // }

    public List<Coordinates> getPrioritizedCells() {
        return prioritizedCells;
    }

    public void setPrioritizedCells(List<Coordinates> prioritizedCells) {
        this.prioritizedCells = prioritizedCells;
    }



    // perhaps it should build a move sequence for each cell *from which*
    // the targeting can be done? pruned of course by proximity...
    // this way flyers would be able to land behind their targets
    // use these move actions to build a path

}
