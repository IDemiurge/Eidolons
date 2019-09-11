
package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActionManager.STD_MODE_ACTIONS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.Goal;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.battlecraft.ai.tools.AiLogger;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.logic.battlefield.vision.StealthRule;
import eidolons.game.core.Eidolons;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.datatypes.DequeImpl;
import main.system.math.Formula;

import java.util.ArrayList;
import java.util.List;

public class ActionManager extends AiHandler {

    public ActionManager(AiMaster master) {
        super(master);

    }

    public static Costs getTotalCost(List<Action> actions) {
        XLinkedMap<PARAMETER, Formula> map = new XLinkedMap<>();
        for (PARAMETER p : DC_ContentValsManager.PAY_PARAMS) {
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

    public void initIntents() {
        Chronos.mark("initIntents");
        for (Unit unit : AggroMaster.getAggroGroup()) {
            if (unit.getAI().getCombatAI().getLastSequence() == null) {
                Action action = chooseAction(true);
                //check no side fx ?

                //don't pop the action?
                main.system.auxiliary.log.LogMaster.log(1, unit + " has Intent action: " + action);
            }

        }
        Chronos.logTimeElapsedForMark("initIntents");
    }

    public Action chooseAction() {
        return chooseAction(false);
    }

    public Action chooseAction(boolean intent) {
        UnitAI ai = getMaster().getUnitAI();
        if (ai.checkStandingOrders(EidolonsGame.DUEL)) {
            getUnitAi().getCombatAI().setLastSequence(ai.getStandingOrders());
            Action ordered = ai.getStandingOrders().popNextAction();
            ordered.setOrder(true);
            if (ordered == ai.getStandingOrders().getLastAction()) {
                ai.setStandingOrders(null);
                main.system.auxiliary.log.LogMaster.dev(getUnit() + "'s last order: " + ordered);
            } else {
                main.system.auxiliary.log.LogMaster.dev(getUnit() + "'s next order: " + ordered);
            }
            return ordered;
        }
        if (!intent) {
            getUnitAi().getCombatAI().setLastSequence(null);
        }
        getPathSequenceConstructor().clearCache(); // TODO try not to? :)
        Unit unit = getUnit();

        if (unit != ai.getUnit()) {
            getCellPrioritizer().reset();
        } else {
        }

        checkDeactivate();

        if (ListMaster.isNotEmpty(ai.getForcedActions())) {
            Action action = ai.getForcedActions().get(0);
            ai.getForcedActions().remove(0);
            return action;
        }

//        if (!ai.isEngaged()) {
//       TODO      return behaviorMaster.getBehaviorAction(ai);
//        }

        FACING_DIRECTION originalFacing = unit.getFacing();
        Coordinates originalCoordinates = unit.getCoordinates();
        Action action = null;
        ActionSequence chosenSequence = null;
        boolean atomic = false;
        if (isAtomicAiOn())
            try {
                atomic = getAtomicAi().checkAtomicActionRequired(ai);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (atomic)
            if (isAtomicAiOn())
                try {
                    action = getAtomicAi().getAtomicAction(ai);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    action = getAtomicAi().getAtomicWait(ai.getUnit());
                }
        if (action == null) {
            List<ActionSequence> actions = new ArrayList<>();
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
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                unit.setCoordinates(originalCoordinates);
                unit.setFacing(originalFacing);
            }

        }

        if (chosenSequence == null) {

            if (action == null) {
                action = getForcedAction(ai);
            }

            ai.getCombatAI().setLastSequence(new ActionSequence(action));

            return action;
        } else {
//            if (chosenSequence.getType() == GOAL_TYPE.DEFEND)
//                return chosenSequence.popNextAction(); what for?

        }
        if (unit.getUnitAI().getLogLevel() > AiLogger.LOG_LEVEL_NONE) {
            if (AI_Manager.DEV_MODE)
                game.getLogManager().log(LOG.GAME_INFO, ai.getUnit().getName()
                        + " chooses task: " + chosenSequence.getTask().toShortString());

            String message = unit + " has chosen: "
                    + chosenSequence + " with priority of "
                    + StringMaster.wrapInParenthesis(chosenSequence.getPriority() + "");
            LogMaster.log(LOG_CHANNEL.AI_DEBUG, message);
            SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.AI, message);
        }
        //TODO for behaviors? ai-issued-orders?

        ai.getCombatAI().setLastSequence(chosenSequence);
        ai.checkSetOrders(chosenSequence);
        return chosenSequence.popNextAction();
    }


    public Action getForcedAction(UnitAI ai) {
        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        GOAL_TYPE goal = AiEnums.GOAL_TYPE.PREPARE;

        Action action = null;
        if (behaviorMode != null) {
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.PANIC) {
                action = new Action(ai.getUnit().getAction("Cower in Terror"));
            }
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
                action = new Action(ai.getUnit().getAction("Stumble About"));
            }
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.BERSERK) {
                if (RandomWizard.chance(100)) { //igg demo hack
                    if (RandomWizard.chance(66)) { //igg demo hack
                        action = new Action(ai.getUnit().getAction(
                                RandomWizard.random() ? "Turn Clockwise" :
                                        "Turn Anticlockwise"));
                    } else
                        action = new Action(ai.getUnit().getAction("Move"));

                    getGame().getLogManager().log(getUnit().getName() + "'s Fury forces him to "
                            + action.getActive().getName());
                } else {
                    action = new Action(ai.getUnit().getAction("Helpless Rage"));
                    getGame().getLogManager().log(getUnit().getName() + " is beyond himself - with "
                            + action.getActive().getName());
                }
            }
            action.setTaskDescription("Forced Behavior");
            return action;
        }
        action = getAtomicAi().getAtomicActionForced(ai);
        if (action != null)
            return action;
        try {
            action = getAtomicAi().getAtomicActionPrepare(getUnit().getAI());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
            if (getUnit().getBehaviorMode() != null) {
                return getForcedForBehavior(getUnit(), getUnit().getBehaviorMode());
            }
            LogMaster.log(1, getUnit() + " has been Forced to wait!");
            return getAction(getUnit(), DC_ActionManager.STD_SPEC_ACTIONS.Wait.name(), null);
        }
        ActionSequence sequence = getPriorityManager().chooseByPriority(actions);

        LogMaster.log(1, getUnit() + " has been Forced to choose " + "" + sequence
                + " with priority of " + sequence.getPriority());

        getMaster().getMessageBuilder().append("Forced Task: " + sequence.getTask().toShortString());

        action = sequence.popNextAction();
        if (action == null) {
            LogMaster.log(1, getUnit() + " has been Forced to Defend!");
            return getAction(getUnit(), STD_MODE_ACTIONS.Defend.name(), null);
        }
        return action;
    }

    private Action getForcedForBehavior(Unit unit, BEHAVIOR_MODE behaviorMode) {
        switch (behaviorMode) {
            case PANIC:
                return new Action(unit.getAction("Cower in Terror"));
            case BERSERK:
                return new Action(unit.getAction("Helpless Rage"));
            case CONFUSED:
                return new Action(unit.getAction("Stumble About"));
        }
        return getAction(getUnit(), DC_ActionManager.STD_SPEC_ACTIONS.Wait.name(), null);
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
