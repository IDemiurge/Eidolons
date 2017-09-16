package main.game.module.dungeoncrawl.explore;

import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.content.mode.STD_MODES;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.GroupAI;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.goal.Goal;
import main.game.bf.Coordinates;
import main.game.core.ActionInput;
import main.game.logic.action.context.Context;
import main.system.datatypes.DequeImpl;

import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 9/15/2017.
 */
public class ExplorePartyMaster extends ExplorationHandler {
    DequeImpl<Unit> companions;
    Coordinates lastPosition;
    private Unit mainHero;

    public ExplorePartyMaster(ExplorationMaster master) {
        super(master);

    }

    public Collection<Unit> getAllies() {
        return master.getGame().getPlayer(true).getControlledUnits_();
    }

    public void reset() {
        companions = initCompanions();
        mainHero = initMainHero();
        lastPosition = mainHero.getCoordinates();
        for (Unit unit : companions) {
            unit.getAI().setAttached(
             checkAttached(unit)
            );

        }
        initEnemyGroups();
    }

    private void initEnemyGroups() {
        List<GroupAI> groups = master.getGame().getAiManager().getGroups();
        groups.forEach(groupAI -> {


        });
    }

    protected DequeImpl<Unit> initCompanions() {
        DequeImpl<Unit> companions = new DequeImpl<>(getAllies());
        companions.removeIf(unit -> unit.isMainHero());
        return companions;
    }

    protected Unit initMainHero() {
        return (Unit) master.getGame().getPlayer(true).getHeroObj();
    }

    public void leaderActionDone(ActionInput input) {
        reset();
        DC_ActiveObj active = input.getAction();
        if (active.isMove() || active.isSpell()) {
            checkFollow();
        } else {
            if (active.getActionGroup() == ACTION_TYPE_GROUPS.MODE) {

                companions.forEach(unit -> {
                    Goal goal = getNewGoal(active, unit.getAI());
                    master.getGame().getAiManager().setUnit(unit);
                    List<ActionSequence> sequences = master.getGame().getAiManager().
                     getActionSequenceConstructor().
                     createActionSequencesForGoal(goal, unit.getAI());

                    ActionSequence sequence = master.getGame().getAiManager().getPriorityManager().
                     chooseByPriority(sequences);
                    unit.getAI().setStandingOrders(sequence);
                });
            }
        }
    }

    public Goal getNewGoal(DC_ActiveObj active, UnitAI ai) {
        if (mainHero.getMode() instanceof STD_MODES) {
            switch ((STD_MODES) mainHero.getMode()) {
                case RESTING:
                case MEDITATION:
                case CONCENTRATION:
                    return new Goal(GOAL_TYPE.RESTORE, ai, false);
            }
        }
        if (active.getActionGroup() == ACTION_TYPE_GROUPS.ORDER) {

        }
        //stealth
        //search
        //wander?
        return null;
    }

    //after each player action?
    private void checkFollow() {
        for (Unit unit : companions) {
            unit.getAI().setAttached(checkAttached(unit));
            if (!unit.getAI().isAttached())
                if (isFollowOn(unit)) {
                    Action move = getFollowMove(unit);
                    if (move != null) {
                        ActionInput input = new ActionInput(move.getActive(), new Context(move.getRef()));
                        master.getAiMaster().queueAiAction(input);

                    }
                }
        }
    }

    protected boolean checkAttached(Unit unit) {
        if (unit.getCoordinates().equals(mainHero.getCoordinates()))
            return true;
        if (unit.getCoordinates().isAdjacent(mainHero.getCoordinates()))
            return true;
        return false;
    }


    protected Action getFollowMove(Unit unit) {
        Action move = master.getGame().getAiManager().getAtomicAi().getAtomicMove(lastPosition, unit);
        if (!checkMove(move, unit))
            return null;
        return move;
    }

    private boolean checkMove(Action move, Unit sub) {
        if (!move.canBeActivated()) {
            return false;
        }
        if (!move.canBeTargeted()) {
            return false;
        }
        return true;
    }

    protected boolean isFollowOn(Unit sub) {
//        if (sub.getAI().isDetached())
//            return false;
        return true;
    }


}
