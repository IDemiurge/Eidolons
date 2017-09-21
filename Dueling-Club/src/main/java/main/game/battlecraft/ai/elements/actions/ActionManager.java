package main.game.battlecraft.ai.elements.actions;

import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.DC_ContentManager;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import main.entity.active.DC_UnitAction;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.elements.goal.Goal;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.ai.tools.ParamAnalyzer;
import main.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import main.game.battlecraft.logic.battlefield.vision.StealthRule;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;
import main.system.datatypes.DequeImpl;
import main.system.math.Formula;

import java.util.LinkedList;
import java.util.List;

public class ActionManager extends AiHandler {

    public ActionManager(AiMaster master) {
        super(master);

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

    @Override
    public void initialize() {
        super.initialize();
        getAtomicAi().initialize();
        getBehaviorMaster().initialize();
    }

    public Action chooseAction() {
        UnitAI ai = getMaster().getUnitAI();
        if (ai.checkStandingOrders()) {
            return ai.getStandingOrders().get(0);
        }

        getPathSequenceConstructor().clearCache(); // TODO try not to? :)
        if (getUnit() != ai.getUnit()) {
            getCellPrioritizer().reset();
        } else {
        }
//        ai.setEngaged(AggroMaster.checkEngaged(ai));

        checkDeactivate();

        if (ListMaster.isNotEmpty(ai.getForcedActions())) {
            Action action = ai.getForcedActions().get(0);
            ai.getForcedActions().remove(0);
            return action;
        }

//        if (!ai.isEngaged()) {
//       TODO      return behaviorMaster.getBehaviorAction(ai);
//        }

        FACING_DIRECTION originalFacing = getUnit().getFacing();
        Coordinates originalCoordinates = getUnit().getCoordinates();
        Action action = null;
        ActionSequence chosenSequence = null;
        boolean atomic = false;
        if (isAtomicAiOn())
            try {
                atomic = getAtomicAi().checkAtomicActionRequired(ai);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (!atomic) {
            List<ActionSequence> actions = new LinkedList<>();
            try {
                List<ActionSequence> sequences = getActionSequenceConstructor().createActionSequences(ai);
                for (ActionSequence a : sequences) {
                    if (a.get(0).canBeActivated()) {
                        // if (a.getOrCreate(0).canBeTargeted())
                        actions.add(a);
                    }
                }
                if (ListMaster.isNotEmpty(actions)) {
                    chosenSequence = DC_PriorityManager.chooseByPriority(actions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getUnit().setCoordinates(originalCoordinates);
                getUnit().setFacing(originalFacing);
            }

        }

        if (chosenSequence == null) {
            if (isAtomicAiOn())
                action = getAtomicAi().getAtomicAction(ai);
            if (action == null) {
                action = getForcedAction(ai);
            }
            return action;
        } else {
            if (chosenSequence.getType() == GOAL_TYPE.DEFEND)
                return chosenSequence.nextAction();

        }
        if (getUnit().getUnitAI().getLogLevel() > UnitAI.LOG_LEVEL_NONE) {
            if (Launcher.DEV_MODE)
                game.getLogManager().log(LOG.GAME_INFO, ai.getUnit().getName()
                 + " chooses task: " + chosenSequence.getTask().toShortString());
            LogMaster.log(LOG_CHANNELS.AI_DEBUG, "Action chosenSequence chosen: "
             + chosenSequence + StringMaster.wrapInParenthesis(chosenSequence.getPriority() + ""));
        }
        //TODO for behaviors? ai-issued-orders?
        ai.checkSetOrders(chosenSequence);
        return chosenSequence.nextAction();
    }


    public Action getForcedAction(UnitAI ai) {
        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        GOAL_TYPE goal = AiEnums.GOAL_TYPE.PREPARE;

        Action action = null;
        if (behaviorMode != null) {
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.PANIC) {
                action = new Action(ai.getUnit().getAction("Cower"));
            }
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
                action = new Action(ai.getUnit().getAction("Stumble"));
            }
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.BERSERK) {
                action = new Action(ai.getUnit().getAction("Rage"));
            }
            action.setTaskDescription("Forced Behavior");
        }
        try {
            action = getAtomicAi().getAtomicActionPrepare(getUnit().getAI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (action != null) {
            return action;
        }

        List<ActionSequence> actions = getActionSequenceConstructor()
         .createActionSequencesForGoal(new Goal(goal, ai, true), ai);
        if (ai.checkMod(AI_MODIFIERS.TRUE_BRUTE)) {
            goal = AiEnums.GOAL_TYPE.ATTACK;
            actions.addAll(getActionSequenceConstructor().createActionSequencesForGoal(new Goal(goal, ai, true), ai));

        }
        if (behaviorMode == null) {
            if (ParamAnalyzer.isFatigued(getUnit())) {
                actions.add(new ActionSequence(AiEnums.GOAL_TYPE.PREPARE, getAction(getUnit(),
                 STD_MODE_ACTIONS.Rest.name())));
            }
            if (ParamAnalyzer.isHazed(getUnit())) { // when is that used?
                actions.add(new ActionSequence(AiEnums.GOAL_TYPE.PREPARE, getAction(getUnit(),
                 STD_MODE_ACTIONS.Concentrate.name())));
            }
        }
        if (actions.isEmpty()) {
            return getAction(getUnit(), STD_MODE_ACTIONS.Defend.name(), null);
        }
        ActionSequence sequence = getPriorityManager().chooseByPriority(actions);

        LogMaster.log(1, getUnit() + " has been Forced to choose " + "" + sequence
         + " with priorioty of " + sequence.getPriority());

        getMaster().getMessageBuilder().append("Forced Task: " + sequence.getTask().toShortString());

        action = sequence.nextAction();
        if (action == null) {
            return getAction(getUnit(), STD_MODE_ACTIONS.Defend.name(), null);
        }
        return action;
    }

    private Integer checkWaitForBlockingAlly() {

        Coordinates c = getUnit().getCoordinates()
         .getAdjacentCoordinate(getUnit().getFacing().getDirection());
        Obj obj = getUnit().getGame().getObjectVisibleByCoordinate(c);
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

        Action action = new Action(AiActionFactory.getUnitAction(unit, name));
        if (target != null) {
            action.getRef().setTarget(target);
        }
        return action;
    }

    private Action getAction(Unit unit, String name) {
        return new Action(AiActionFactory.getUnitAction(unit, name));
    }

    private void checkDeactivate() {
        DequeImpl<DC_UnitAction> list = getUnit().getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ACTION);
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
                            result = getUnit().getBuff(StealthRule.SPOTTED) != null;
                            // preCheck has actions before turn left!
                            break;
                        case "Search Mode":
                            result = Analyzer.getVisibleEnemies(getUnit().getUnitAI()).isEmpty();
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
