package eidolons.game.module.dungeoncrawl.explore;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.goal.Goal;
import eidolons.game.core.ActionInput;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.mode.STD_MODES;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 9/15/2017.
 */
public class ExplorePartyMaster extends ExplorationHandler {
    DequeImpl<Unit> companions;
    Coordinates lastPosition;
    private Unit mainHero;
    private boolean updateRequired;
    private ActionInput lastAction;

    public ExplorePartyMaster(ExplorationMaster master) {
        super(master);

    }

    public Collection<Unit> getAllies() {
        return master.getGame().getPlayer(true).collectControlledUnits_();
    }

    public void reset() {
        //TODO DC Review - minions?
        if (companions == null)
            companions = initCompanions();
        mainHero = initMainHero();
        if (mainHero == null) {
            return;
        }
        lastPosition = mainHero.getCoordinates();
        for (Unit unit : companions) {
            unit.getAI().setAutoFollow(
                    checkAttached(unit)
            );

        }
    }


    protected DequeImpl<Unit> initCompanions() {
        DequeImpl<Unit> companions = new DequeImpl<>(getAllies());
        companions.removeIf(Unit::isMainHero);
        return companions;
    }

    protected Unit initMainHero() {
        return (Unit) master.getGame().getPlayer(true).getHeroObj();
    }

    //threaded use!
    public void timedCheck() {
        if (!updateRequired)
            return;

        reset();
        List<Unit> list = new ArrayList<>(companions);
        for (Unit unit : list) {
            if (unit.getAI().getStandingOrders() == null) {
                if (!tryFollow(unit)) {
                    checkNewGoal(unit);
                }
            }
        }
        updateRequired = false;
    }

    public void checkNewGoal(Unit unit) {
        DC_ActiveObj active = lastAction.getAction();
        if (active.getActionGroup() == ACTION_TYPE_GROUPS.MODE) {


            Goal goal = getNewGoal(active, unit.getAI());
            if (goal != null) {
                master.getGame().getAiManager().setUnit(unit);
                List<ActionSequence> sequences = master.getGame().getAiManager().
                        getActionSequenceConstructor().
                        createActionSequencesForGoal(goal, unit.getAI());
                if (!sequences.isEmpty()) {
                    ActionSequence sequence = master.getGame().getAiManager().getPriorityManager().
                            chooseByPriority(sequences);
                    unit.getAI().setStandingOrders(sequence);
                }
            }
        }
        //        if (mainHero.getMode()!=null ){
        //        }
    }

    public void leaderActionDone(ActionInput input) {
        lastAction = input;
        updateRequired = true;
        //        DC_ActiveObj active = input.getAction();
        //        if (active.isMove() || active.isSpell()) {
        //            checkFollow();
        //        } else {
        //
        //        }
    }

    public Goal getNewGoal(DC_ActiveObj active, UnitAI ai) {
        if (mainHero.getMode() instanceof STD_MODES) {
            switch ((STD_MODES) mainHero.getMode()) {
                case RESTING:
                case MEDITATION:
                case CONCENTRATION:
                    return new Goal(GOAL_TYPE.PREPARE, ai, false);
            }
        }
        if (active.getActionGroup() == ACTION_TYPE_GROUPS.ORDER) {

        }
        //stealth
        //search
        //wander?
        return null;
    }


    private boolean tryFollow(Unit unit) {
        unit.getAI().setAutoFollow(checkAttached(unit));
        if (!unit.getAI().isAutoFollow())
            if (isFollowOn(unit)) {
                Action move = getFollowMove(unit);
                if (move != null) {
                    ActionInput input = new ActionInput(move.getActive(), new Context(move.getRef()));
                    master.getAiMaster().queueAiAction(input);
                    return true;
                }
            }
        return false;
    }

    protected boolean checkAttached(Unit unit) {
        if (unit.getCoordinates().equals(mainHero.getCoordinates()))
            return true;
        return unit.getCoordinates().isAdjacent(mainHero.getCoordinates());
    }


    protected Action getFollowMove(Unit unit) {
        Action move = master.getGame().getAiManager().getAtomicAi().getAtomicMove(lastPosition, unit);

        if (move == null)
            return null;
        if (!checkMove(move, unit))
            return null;
        return move;
    }

    private boolean checkMove(Action move, Unit sub) {
        if (!move.canBeActivated()) {
            return false;
        }
        return move.canBeTargeted();
    }

    protected boolean isFollowOn(Unit sub) {
        //        if (sub.getAI().isDetached())
        //            return false;
        return true;
    }


}
