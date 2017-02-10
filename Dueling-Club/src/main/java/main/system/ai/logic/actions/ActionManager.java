package main.system.ai.logic.actions;

import main.content.CONTENT_CONSTS.ACTION_TYPE;
import main.content.CONTENT_CONSTS.AI_LOGIC;
import main.content.CONTENT_CONSTS.BEHAVIOR_MODE;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.PROPS;
import main.data.XList;
import main.entity.Ref;
import main.entity.active.DC_ItemActiveObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.DC_UnitAction;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.DungeonCrawler;
import main.rules.DC_ActionManager;
import main.rules.DC_ActionManager.STD_ACTIONS;
import main.rules.DC_ActionManager.STD_MODE_ACTIONS;
import main.rules.mechanics.StealthRule;
import main.system.ai.AI_Manager;
import main.system.ai.UnitAI;
import main.system.ai.logic.behavior.BehaviorMaster;
import main.system.ai.logic.generic.AtomicAi;
import main.system.ai.logic.goal.Goal;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;
import main.system.ai.logic.goal.GoalManager;
import main.system.ai.logic.path.CellPrioritizer;
import main.system.ai.logic.priority.PriorityManager;
import main.system.ai.logic.target.SpellMaster;
import main.system.ai.logic.task.Task;
import main.system.ai.logic.task.TaskManager;
import main.system.ai.tools.Analyzer;
import main.system.ai.tools.ParamAnalyzer;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.LogMaster.LOG_CHANNELS;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class ActionManager {

    BehaviorMaster behaviorMaster;
    private AI_Manager manager;
    private GoalManager goalManager;
    private PriorityManager priorityManager;
    private TaskManager taskManager;
    private DC_HeroObj unit;
    private Coordinates originalCoordinates;
    private FACING_DIRECTION originalFacing;
    private List<DC_ActiveObj> actions;

    public ActionManager(AI_Manager manager) {
        this.manager = manager;
        this.goalManager = manager.getGoalManager();
        this.priorityManager = manager.getPriorityManager();
        this.taskManager = manager.getTaskManager();
        this.behaviorMaster = new BehaviorMaster();
    }

    public static Integer selectTargetForAction(DC_ActiveObj a) {
        /*
         * getOrCreate possible targets init goal type prioritize
		 */
        GOAL_TYPE type = GoalManager.getGoalFromAction(a);

        Obj target = null;
        int max_priority = Integer.MIN_VALUE;
        Set<Obj> objects = null;
        try {
            objects = a.getTargeting().getFilter().getObjects();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Obj obj : objects) {
            ActionSequence sequence = new ActionSequence(type, new Action(a, obj));
            sequence.setAi(a.getOwnerObj().getUnitAI());
            sequence.setType(type);
            int priority = PriorityManager.getPriority(sequence);
            if (priority > max_priority) {
                target = obj;
                max_priority = priority;
            }
        }
        if (target == null) {
            return null;
        }
        return target.getId();
    }

    private static List<ActionSequence> splitRangedSequence(ActionSequence sequence) {
        ArrayList<ActionSequence> list = new ArrayList<>();
        for (Action a : sequence.getActions()) {
            if (a instanceof QuickItemAction) {
                ArrayList<Action> actions = new ArrayList<>();
                actions.add(a);
                for (Action a1 : sequence.getActions()) {
                    if (!(a1 instanceof QuickItemAction)) {
                        actions.add(a1);
                    }
                }
                ActionSequence rangedSequence = new ActionSequence(actions, sequence.getTask(),
                        sequence.getAi());
                list.add(rangedSequence);
            }
        }
        if (list.isEmpty()) {
            list.add(sequence);
        }
        return list;
    }

    public static List<DC_ActiveObj> getFullActionList(GOAL_TYPE type, DC_HeroObj unit) {
        // cache
        List<DC_ActiveObj> actions = new XList<>();
        switch (type) {

            case PATROL:
            case WANDER:
            case RETREAT:
            case MOVE:
            case APPROACH:
                // dummy action!
                actions.add(getUnitAction(unit, "Move"));
                break;

            case ATTACK:
                if (unit.getActionMap().get(ACTION_TYPE.SPECIAL_ATTACK) != null) {
                    actions.addAll(unit.getActionMap().get(ACTION_TYPE.STANDARD_ATTACK));
                }
                if (unit.getActionMap().get(ACTION_TYPE.SPECIAL_ATTACK) != null) {
                    actions.addAll(unit.getActionMap().get(ACTION_TYPE.SPECIAL_ATTACK));
                }

                actions.remove(getUnitAction(unit, DC_ActionManager.OFFHAND_ATTACK));
                actions.remove(getUnitAction(unit, DC_ActionManager.THROW_MAIN));
                actions.remove(getUnitAction(unit, DC_ActionManager.THROW_OFFHAND));
                break;

            case DEFEND:
                actions.add(getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));
                actions.add(getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                break;

            case COWER:
                actions.add(getUnitAction(unit, "Cower"));
                break;
            case AMBUSH:
                if (!checkAddStealth(true, unit, actions)) {
                    actions.add(getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                }
                break;
            case STALK:
                if (!checkAddStealth(false, unit, actions)) {
                    actions.add(getUnitAction(unit, "Move"));
                }
                break;
            case STEALTH:
                checkAddStealth(false, unit, actions);
                break;
            case SEARCH: // can it be MOVE?
                if (unit.getBuff("Search Mode") == null) {
                    actions.add(getUnitAction(unit, "Search Mode"));
                } else {
                    actions.add(getUnitAction(unit, "Move"));
                }
                break;
            case WAIT:
                actions.add(getUnitAction(unit, STD_ACTIONS.Wait.name()));
                break;
            case PREPARE:
                actions.addAll(unit.getActionMap().get(ACTION_TYPE.MODE));
                if (!unit.isLiving()) {
                    actions.remove(getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));

                }
                actions.remove(getUnitAction(unit, STD_MODE_ACTIONS.Defend.name()));
                actions.remove(getUnitAction(unit, STD_MODE_ACTIONS.On_Alert.name()));
                break;
        }
        actions.addAll(filterActives(type, (unit.getSpells())));
        actions.addAll(filterActives(type, (unit.getQuickItemActives())));
        if (type.isFilterByCanActivate()) {
            actions = filterByCanActivate(unit, actions);
        }
        return actions;
    }

    private static DC_UnitAction getUnitAction(DC_HeroObj unit, String name) {
        return unit.getAction(name, true);
    }

    private static boolean checkAddStealth(boolean hidePref, DC_HeroObj unit,
                                           List<DC_ActiveObj> actions) {
        if (unit.getBuff("Stealth Mode") != null) {
            return false;
        }
        if (unit.getBuff("Hide Mode") != null) {
            return false;
        }

        if (!hidePref) {
            if (getUnitAction(unit, "Stealth Mode") != null) {
                actions.add(getUnitAction(unit, "Stealth Mode"));
                return true;
            }
        }
        if (getUnitAction(unit, "Hide Mode") != null) {
            actions.add(getUnitAction(unit, "Hide Mode"));
            return true;
        } else if (getUnitAction(unit, "Stealth Mode") != null) {
            actions.add(getUnitAction(unit, "Stealth Mode"));
            return true;
        }
        return false;

    }

    private static List<DC_ActiveObj> filterByCanActivate(DC_HeroObj unit,
                                                          List<DC_ActiveObj> actionsList) {
        List<DC_ActiveObj> list = new LinkedList<>();
        for (DC_ActiveObj a : actionsList) {
            if (a.canBeActivated(unit.getRef(), true) || checkException(a)) {
                list.add(a);
            }
        }
        return list;
    }

    private static boolean checkException(DC_ActiveObj a) {
        if (a.isRanged()) {
            if (!a.isThrow()) {
                return true;
            }
        }
        return false;
    }

    public static List<DC_ActiveObj> getMoveActions(DC_HeroObj unit) {
        List<DC_ActiveObj> list = new LinkedList<>();
        list.addAll(unit.getActionMap().get(ACTION_TYPE.ADDITIONAL_MOVE));
        List<DC_UnitAction> actionList = unit.getActionMap().get(ACTION_TYPE.SPECIAL_MOVE);
        if (ListMaster.isNotEmpty(actionList)) {
            list.addAll(actionList);
        }
        list.addAll(filterActives(GOAL_TYPE.MOVE, (unit.getSpells())));
        list.add(getUnitAction(unit, "Move"));
        return list;
    }

    private static Collection<? extends DC_ActiveObj> filterActives(GOAL_TYPE type,
                                                                    List<? extends DC_ActiveObj> spells) {
        List<DC_ActiveObj> list = new LinkedList<>();
        for (DC_ActiveObj spell : spells) {
            GOAL_TYPE goal = null;
            try {
                goal = SpellMaster.getGoal(spell);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (goal != null) {
                if (goal.equals(type)) {
                    list.add(spell);
                }
            }
        }
        return list;
    }

    public static List<DC_ActiveObj> getActionObjectList(List<Action> actions) {
        List<DC_ActiveObj> activeList = new LinkedList<>();
        if (actions != null) {
            for (Action object : actions) {
                if (object != null) {
                    activeList.add(object.getActive());
                }
            }
        }
        return activeList;
    }

    public static Collection<DC_ActiveObj> getSpells(AI_LOGIC logic, DC_HeroObj unit) {
        List<DC_ActiveObj> list = new LinkedList<>();
        for (DC_ActiveObj spell : unit.getSpells()) {
            if (spell.getProperty(PROPS.AI_LOGIC).equalsIgnoreCase(logic.toString())) {
                list.add(spell);
            } else {
                try {
                    if (SpellMaster.getSpellLogic(spell) == logic) {
                        list.add(spell);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;

    }

    public static void setTargetPool(List<? extends DC_Obj> targets) {
        // TODO Auto-generated method stub
    }

    public Action chooseAction(UnitAI ai) {
        if (ai.checkStandingOrders()) {
            return ai.getStandingOrders().get(0);
        }

        ActionSequenceConstructor.clearCache(); // TODO try not to? :)
        if (unit != ai.getUnit()) {
            CellPrioritizer.reset();
        } else {
            // TODO check fatigue etc
            // if (sequence != null) {
            // Action nextAction = sequence.getNextAction();
            // if (nextAction != null)
            // if (nextAction.canBeActivated())
            // if (nextAction.canBeTargeted())
            // return nextAction;
            // }
        }
        unit = ai.getUnit();
        ai.setEngaged(DungeonCrawler.checkEngaged(ai));

        checkDeactivate();

        if (ListMaster.isNotEmpty(ai.getForcedActions())) {
            Action action = ai.getForcedActions().get(0);
            ai.getForcedActions().remove(0);
            return action;
        }

        if (!ai.isEngaged()) {
            return behaviorMaster.getBehaviorAction(ai);
        }

        originalFacing = unit.getFacing();
        originalCoordinates = unit.getCoordinates();

        List<ActionSequence> actions = new LinkedList<>();
        try {
            // actions = createActionSequences(ai);
            for (ActionSequence a : createActionSequences(ai)) {
                if (a.get(0).canBeActivated()) {
                    if (checkNotBroken(a))
                    // if (a.getOrCreate(0).canBeTargeted())
                    {
                        actions.add(a);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            unit.setCoordinates(originalCoordinates);
            unit.setFacing(originalFacing);
        }
        Action action;
        ActionSequence sequence = null;
        if (ListMaster.isNotEmpty(actions)) {
            sequence = PriorityManager.chooseByPriority(actions);
        }

        if (sequence == null) {
            action = AtomicAi.getAtomicAction(ai);
            if (action == null) {
                action = getForcedAction(ai);
            }
            return action;
        }
        if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_NONE) {
            main.system.auxiliary.LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Action sequence chosen: "
                    + sequence + StringMaster.wrapInParenthesis(sequence.getPriority() + ""));
        }
        ai.checkSetOrders(sequence);
        return sequence.getNextAction();
    }

    private boolean checkNotBroken(ActionSequence as) {
        for (Action a : as.getActions()) {
            for (Action ba : AI_Manager.getBrokenActions()) {
                if (a.getActive().getType().getName().equals(ba.getActive().getType().getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    public Action getForcedAction(UnitAI ai) {
        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        GOAL_TYPE goal = GOAL_TYPE.PREPARE;

        List<ActionSequence> actions;

        if (behaviorMode == BEHAVIOR_MODE.PANIC) {
            return new Action(ai.getUnit().getAction("Cower"));
        }
        if (behaviorMode == BEHAVIOR_MODE.CONFUSED) {
            return new Action(ai.getUnit().getAction("Stumble"));
        }
        if (behaviorMode == BEHAVIOR_MODE.BERSERK) {
            return new Action(ai.getUnit().getAction("Rage"));
        }

        actions = createActionSequences(new Goal(goal, ai, true), ai);
        if (ai.checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
            goal = GOAL_TYPE.ATTACK;
            actions.addAll(createActionSequences(new Goal(goal, ai, true), ai));
        }
        if (behaviorMode == null) {
            if (ParamAnalyzer.isFatigued(unit)) {
                actions.add(new ActionSequence(GOAL_TYPE.PREPARE, getAction(unit,
                        STD_MODE_ACTIONS.Rest.name())));
            }
            if (ParamAnalyzer.isHazed(unit)) { // when is that used?
                actions.add(new ActionSequence(GOAL_TYPE.PREPARE, getAction(unit,
                        STD_MODE_ACTIONS.Concentrate.name())));
            }
            // Integer id = checkWaitForBlockingAlly(); TODO can actually check
            // if movement is blocked and wait on the least sturdy in line...
            // if (id != null) {
            // Task task = new Task(ai, GOAL_TYPE.PREPARE, ai);
            // ActionSequence sequence = ActionSequenceConstructor
            // .getSequence(
            // getAction(unit, STD_ACTIONS.Wait.name(), id),
            // task);
            // if (sequence != null)
            // actions.add(sequence);
            // }
        }
        if (actions.isEmpty()) {
            return getAction(unit, STD_MODE_ACTIONS.Defend.name(), null);
        }
        ActionSequence sequence = PriorityManager.chooseByPriority(actions);

        return sequence.getNextAction();
    }

    private Integer checkWaitForBlockingAlly() {

        Coordinates c = unit.getCoordinates()
                .getAdjacentCoordinate(unit.getFacing().getDirection());
        Obj obj = unit.getGame().getObjectVisibleByCoordinate(c);
        if (obj instanceof DC_HeroObj) {
            if (((DC_HeroObj) obj).canActNow())
                // if (!((DC_HeroObj) obj).checkStatus(STATUS.WAITING))
            {
                return obj.getId();
            }
        }
        return null;

    }

    private Action getAction(DC_HeroObj unit, String name, Integer target) {

        Action action = new Action(getUnitAction(unit, name));
        if (target != null) {
            action.getRef().setTarget(target);
        }
        return action;
    }

    private Action getAction(DC_HeroObj unit, String name) {
        return new Action(getUnitAction(unit, name));
    }

    private List<ActionSequence> createActionSequences(UnitAI ai) {
        List<ActionSequence> list = new ArrayList<>();
        ActionSequenceConstructor.setPrioritizedCells(null);
        for (GOAL_TYPE type : GoalManager.getGoalsForUnit(ai)) {
            list.addAll(createActionSequences(new Goal(type, null // ???
                    , ai), ai));
        }
        // if (Analyzer.getDetectedEnemies(ai).isEmpty()) {
        // list.addAll(createActionSequences(new Goal(GOAL_TYPE.SEARCH, null //
        // ???
        // , ai), ai));
        // } TODO
        // WAIT under special conditions only?
        return list;
    }

    private List<ActionSequence> createActionSequences(Goal goal, UnitAI ai) {
        List<ActionSequence> actionSequences = new LinkedList<>();
        actions = getFullActionList(goal.getTYPE(), ai.getUnit());
        actions.addAll(addSubactions(actions));
        for (DC_ActiveObj action : actions) {
            Chronos.mark(getChronosPrefix() + action);
            List<Task> tasks = taskManager.getTasks(goal.getTYPE(), ai, goal.isForced(), action);
            for (Task task : tasks) {
                if (task.isBlocked()) {
                    continue;
                }
                if (actionSequences.size() > 0) {
                    long time = TimeLimitMaster.getTimeLimitForAction();
                    if (Chronos.getTimeElapsedForMark(getChronosPrefix() + action) > time) {
                        main.system.auxiliary.LogMaster.log(1, "*********** TIME ELAPSED FOR  "
                                + action + StringMaster.wrapInParenthesis(time + ""));
                        break;
                    }
                }
                String string = task.toString();
                Obj obj = action.getGame().getObjectById((Integer) task.getArg());
                if (obj != null) {
                    string = obj.getName();
                }
                Chronos.mark(getChronosPrefix() + string);
                try {
                    addSequences(task, actionSequences, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Chronos.logTimeElapsedForMark(getChronosPrefix() + string);
            }
            Chronos.logTimeElapsedForMark(getChronosPrefix() + action);
        }
        return actionSequences;
    }

    private void addSequences(Task task, List<ActionSequence> sequences, DC_ActiveObj active) {
        Ref ref = task.getUnit().getRef().getCopy();
        Integer arg = TaskManager.checkTaskArgReplacement(task, active);
        if (arg == null) {
            return;
        }
        ref.setTarget(arg);
        List<ActionSequence> newSequences = null;
        Action action = newAction(active, ref);

        try {
            newSequences = ActionSequenceConstructor.getSequences(action, task.getArg(), task);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ListMaster.isNotEmpty(newSequences)) {
            sequences.addAll(newSequences);
        } else {
            // if no pathing is required/available [QUICK FIX]
            if (!action.canBeTargetedOnAny()) {
                return;
            }
            ActionSequence sequence = ActionSequenceConstructor.getSequence(action, task);
            if (sequence != null) {
                if (active.isRanged()) {
                    sequences.addAll(splitRangedSequence(sequence));
                } else {
                    sequences.add(sequence);
                }
            } else {

                // if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_NONE)
                // TODO smarter logging?
                // main.system.auxiliary.LogMaster.log(1, "***" +
                // action.toString()
                // + " could not be constructed into an action sequence!");

                // return;
            }
        }
    }

    private Action newAction(DC_ActiveObj action, Ref ref) {
        if (action instanceof DC_ItemActiveObj) {
            DC_ItemActiveObj itemActiveObj = (DC_ItemActiveObj) action;
            return new QuickItemAction(itemActiveObj.getItem(), ref);
        }
        return new Action(action, ref);
    }

    private String getChronosPrefix() {
        return "TIMED AI ACTION ";
    }

    private List<DC_ActiveObj> addSubactions(List<DC_ActiveObj> actions) {
        List<DC_ActiveObj> subactions = new LinkedList<>();
        for (DC_ActiveObj a : actions) {
            subactions.addAll(a.getSubActions());
        }
        return subactions;
    }

    private void checkDeactivate() {
        List<DC_UnitAction> list = unit.getActionMap().get(ACTION_TYPE.SPECIAL_ACTION);
        if (list == null) {
            return;
        }
        for (DC_UnitAction a : list) {
            if (a.isContinuousMode()) {
                if (a.checkContinuousModeDeactivate()) {
                    boolean result = false;
                    switch (a.getName()) {
                        case "Stealth Mode":
                        case "Hide":
                            // spotted?
                            result = unit.getBuff(StealthRule.SPOTTED) != null;
                            // check has actions before turn left!
                            break;
                        case "Search Mode":
                            result = Analyzer.getVisibleEnemies(unit.getUnitAI()).isEmpty();
                            break;
                    }

                    if (result) {
                        a.deactivate();
                    }
                }
            }
        }

    }

}
