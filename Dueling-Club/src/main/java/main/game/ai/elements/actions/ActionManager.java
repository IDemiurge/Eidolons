package main.game.ai.elements.actions;

import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.DC_ContentManager;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.active.DC_UnitAction;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.AI_Manager;
import main.game.ai.UnitAI;
import main.game.ai.advanced.behavior.BehaviorMaster;
import main.game.ai.elements.actions.sequence.ActionSequence;
import main.game.ai.elements.actions.sequence.ActionSequenceConstructor;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.elements.goal.Goal;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.logic.types.atomic.AtomicAi;
import main.game.ai.tools.Analyzer;
import main.game.ai.tools.ParamAnalyzer;
import main.game.ai.tools.priority.DC_PriorityManager;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.ai.DungeonCrawler;
import main.game.logic.generic.DC_ActionManager.STD_MODE_ACTIONS;
import main.rules.action.StealthRule;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;
import main.system.datatypes.DequeImpl;
import main.system.math.Formula;

import java.util.LinkedList;
import java.util.List;

public class ActionManager extends AiHandler {

    BehaviorMaster behaviorMaster;

    public ActionManager(AiHandler master) {
        super(master);
        this.behaviorMaster = new BehaviorMaster(master);
    }

    public static Costs getTotalCost(List<Action> actions) {
        XLinkedMap<PARAMETER, Formula> map = new XLinkedMap<>();
        for (PARAMETER p : DC_ContentManager.PAY_PARAMS) {
            map.put(p, new Formula(""));
        }
        for (Action a : actions) {
            // a.getActive().getCosts().getRequirements().getFocusRequirement()
            // !

            if (a.getActive().isChanneling()) {

            }

            for (Cost c : a.getActive().getCosts().getCosts()) {
                Formula formula = map.get(c.getPayment().getParamToPay());
                if (formula != null) {
                    formula.append("+" + c.getPayment().getAmountFormula().toString());
                }

            }
        }
        return new Costs(map);
    }

    public Action chooseAction(UnitAI ai) {
        if (ai.checkStandingOrders()) {
            return ai.getStandingOrders().get(0);
        }

        getPathSequenceConstructor().clearCache(); // TODO try not to? :)
        if (unit != ai.getUnit()) {
            getCellPrioritizer().reset();
        } else {
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

        FACING_DIRECTION originalFacing = unit.getFacing();
        Coordinates originalCoordinates = unit.getCoordinates();

        List<ActionSequence> actions = new LinkedList<>();
        try {
            // actions = createActionSequences(ai);
            for (ActionSequence a : getActionSequenceConstructor().createActionSequences(
                    ai)) {
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
            unit.resetFacing(originalFacing);
        }
        Action action;
        ActionSequence sequence = null;
        if (ListMaster.isNotEmpty(actions)) {
            sequence = DC_PriorityManager.chooseByPriority(actions);
        }

        if (sequence == null) {
            action = AtomicAi.getAtomicAction(ai);
            if (action == null) {
                action = getForcedAction(ai);
            }
            return action;
        }
        if (unit.getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_NONE) {
            LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Action sequence chosen: "
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

        if (behaviorMode == AiEnums.BEHAVIOR_MODE.PANIC) {
            return new Action(ai.getUnit().getAction("Cower"));
        }
        if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
            return new Action(ai.getUnit().getAction("Stumble"));
        }
        if (behaviorMode == AiEnums.BEHAVIOR_MODE.BERSERK) {
            return new Action(ai.getUnit().getAction("Rage"));
        }

        actions = getActionSequenceConstructor().createActionSequences(new Goal(goal, ai, true), ai);
        if (ai.checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
            goal = GOAL_TYPE.ATTACK;
            actions.addAll(getActionSequenceConstructor().createActionSequences(new Goal(goal, ai, true), ai));
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
            // Integer id = checkWaitForBlockingAlly(); TODO can actually preCheck
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
        ActionSequence sequence = DC_PriorityManager.chooseByPriority(actions);

        return sequence.getNextAction();
    }

    private Integer checkWaitForBlockingAlly() {

        Coordinates c = unit.getCoordinates()
                .getAdjacentCoordinate(unit.getFacing().getDirection());
        Obj obj = unit.getGame().getObjectVisibleByCoordinate(c);
        if (obj instanceof Unit) {
            if (((Unit) obj).canActNow())
            // if (!((DC_HeroObj) obj).checkStatus(STATUS.WAITING))
            {
                return obj.getId();
            }
        }
        return null;

    }

    private Action getAction(Unit unit, String name, Integer target) {

        Action action = new Action(ActionFactory.getUnitAction(unit, name));
        if (target != null) {
            action.getRef().setTarget(target);
        }
        return action;
    }

    private Action getAction(Unit unit, String name) {
        return new Action(ActionFactory.getUnitAction(unit, name));
    }

    private void checkDeactivate() {
        DequeImpl<DC_UnitAction> list = unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ACTION);
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
                            // preCheck has actions before turn left!
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

    public ActionSequenceConstructor getActionSequenceConstructor() {
        return actionSequenceConstructor;
    }

}
