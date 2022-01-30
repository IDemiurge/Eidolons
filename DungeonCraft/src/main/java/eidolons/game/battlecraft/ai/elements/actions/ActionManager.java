
package eidolons.game.battlecraft.ai.elements.actions;

import eidolons.content.ContentConsts;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.goal.Goal;
import eidolons.game.battlecraft.ai.tools.AiLogger;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.battlecraft.ai.tools.priority.DC_PriorityManager;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.StealthRule;
import eidolons.game.core.atb.AtbMaster;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
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

import static main.content.enums.entity.ActionEnums.HIDDEN_ACTIONS.*;
import static main.content.enums.entity.ActionEnums.DEFAULT_ACTION;

public class ActionManager extends AiHandler {

    public ActionManager(AiMaster master) {
        super(master);

    }

    public static Costs getTotalCost(List<AiAction> aiActions) {
        XLinkedMap<PARAMETER, Formula> map = new XLinkedMap<>();
        for (PARAMETER p : ContentConsts.PAY_PARAMS) {
            map.put(p, new Formula(""));
        }
        for (AiAction a : aiActions) {
            for (Cost c : a.getActive().getCosts().getCosts()) {
                Formula formula = map.get(c.getPayment().getParamToPay());
                if (formula != null) {
                    formula.append("+" + c.getPayment().getAmountFormula().toString());
                }

            }
            map.put(PARAMS.C_ATB, new Formula("" + AtbMaster.getReadinessCost(a.getActive())));
        }
        map.keySet().removeIf(p-> map.get(p).toString().isEmpty());

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
        for (Unit unit : game.getManager().getEnemies()) {
            if (unit.getAI().getCombatAI().getLastSequence() == null) {
                AiAction aiAction = chooseAction(true);
                //check no side fx ?

                //don't pop the action?
                main.system.auxiliary.log.LogMaster.log(1, unit + " has Intent action: " + aiAction);
            }

        }
        Chronos.logTimeElapsedForMark("initIntents");
    }

    public AiAction chooseAction() {
        return chooseAction(false);
    }

    public AiAction chooseAction(boolean intent) {
        UnitAI ai = getMaster().getUnitAI();
        if (ai.checkStandingOrders(false)) {
            getUnitAi().getCombatAI().setLastSequence(ai.getStandingOrders());
            AiAction ordered = ai.getStandingOrders().popNextAction();
            ordered.setOrder(true);
            if (ordered == ai.getStandingOrders().getLastAction()) {
                ai.setStandingOrders(null);
                main.system.auxiliary.log.LogMaster.devLog(getUnit() + "'s last order: " + ordered);
            } else {
                main.system.auxiliary.log.LogMaster.devLog(getUnit() + "'s next order: " + ordered);
            }
            return ordered;
        }
        if (!intent) {
            getUnitAi().getCombatAI().setLastSequence(null);
        }
        getPathSequenceConstructor().clearCache(); // TODO try not to? :)
        Unit unit = getUnit();

        checkDeactivate();

        if (ListMaster.isNotEmpty(ai.getForcedActions())) {
            AiAction aiAction = ai.getForcedActions().get(0);
            ai.getForcedActions().remove(0);
            return aiAction;
        }

        unit.initTempCoordinates();
        AiAction aiAction = null;
        ActionSequence chosenSequence = null;
        if (isAtomicAiOn())
            try {
                AiEnums.AI_LOGIC_CASE atomic = getAtomicAi().checkAtomicActionRequired(ai);
                if (atomic != null)
                    aiAction = getAtomicAi().getAtomicAction(ai, atomic);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }


        if (aiAction == null) {
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
                unit.removeTempCoordinates();
            }

        }

        if (chosenSequence == null) {

            if (aiAction == null) {
                aiAction = getForcedAction(ai);
            }

            ai.getCombatAI().setLastSequence(new ActionSequence(aiAction));

            return aiAction;
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
            SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.AI, message);
        }
        //TODO for behaviors? ai-issued-orders?

        ai.getCombatAI().setLastSequence(chosenSequence);
        ai.checkSetOrders(chosenSequence);
        return chosenSequence.popNextAction();
    }


    public AiAction getForcedAction(UnitAI ai) {
        BEHAVIOR_MODE behaviorMode = ai.getBehaviorMode();
        GOAL_TYPE goal = AiEnums.GOAL_TYPE.PREPARE;

        AiAction aiAction = null;
        if (behaviorMode != null) {
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.PANIC) {
                aiAction = new AiAction(ai.getUnit().getAction(Cower_In_Terror.toString()));
            }
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.CONFUSED) {
                aiAction = new AiAction(ai.getUnit().getAction(Stumble_About.toString()));
            }
            if (behaviorMode == AiEnums.BEHAVIOR_MODE.BERSERK) {
                // TODO make real
                if (RandomWizard.chance(66)) {
                    aiAction = new AiAction(ai.getUnit().getAction(
                            RandomWizard.random() ? "Turn Clockwise" :
                                    "Turn Anticlockwise"));
                } else
                    aiAction = new AiAction(ai.getUnit().getAction("Move"));

                getGame().getLogManager().log(getUnit().getName() + "'s Fury forces him to "
                        + aiAction.getActive().getName());
            }

            aiAction.setTaskDescription("Forced Behavior");
            return aiAction;
        }
        aiAction = getAtomicAi().getAtomicActionForced(ai);
        if (aiAction != null)
            return aiAction;
        try {
            aiAction = getAtomicAi().getAtomicActionPrepare(getUnit().getAI());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (aiAction != null) {
            return aiAction;
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
                        ActionEnums.DEFAULT_ACTION.Rest.name())));
            }
            if (ParamAnalyzer.isHazed(getUnit())) { // when is that used?
                actions.add(new ActionSequence(AiEnums.GOAL_TYPE.PREPARE, getAction(getUnit(),
                        ActionEnums.DEFAULT_ACTION.Concentrate.name())));
            }
        }
        if (actions.isEmpty()) {
            if (getUnit().getBehaviorMode() != null) {
                return getForcedForBehavior(getUnit(), getUnit().getBehaviorMode());
            }
            LogMaster.log(1, getUnit() + " has been Forced to wait!");
            return getAction(getUnit(), DEFAULT_ACTION.Wait.name(), null);
        }
        ActionSequence sequence = getPriorityManager().chooseByPriority(actions);

        LogMaster.log(1, getUnit() + " has been Forced to choose " + "" + sequence
                + " with priority of " + sequence.getPriority());

        getMaster().getMessageBuilder().append("Forced Task: ").append(sequence.getTask().toShortString());

        aiAction = sequence.popNextAction();
        if (aiAction == null) {
            LogMaster.log(1, getUnit() + " has been Forced to Defend!");
            return getAction(getUnit(), ActionEnums.DEFAULT_ACTION.Defend.name(), null);
        }
        return aiAction;
    }

    private AiAction getForcedForBehavior(Unit unit, BEHAVIOR_MODE behaviorMode) {
        switch (behaviorMode) {
            case PANIC:
                return new AiAction(unit.getAction("Cower in Terror"));
            case BERSERK:
                return new AiAction(unit.getAction("Helpless Rage"));
            case CONFUSED:
                return new AiAction(unit.getAction("Stumble About"));
        }
        return getAction(getUnit(), DEFAULT_ACTION.Wait.name(), null);
    }

    private AiAction getAction(Unit unit, String name, Integer target) {

        AiAction aiAction = new AiAction(AiActionFactory.getUnitAction(unit, name));
        if (target != null) {
            aiAction.getRef().setTarget(target);
        }
        return aiAction;
    }

    private AiAction getAction(Unit unit, String name) {
        return new AiAction(AiActionFactory.getUnitAction(unit, name));
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
