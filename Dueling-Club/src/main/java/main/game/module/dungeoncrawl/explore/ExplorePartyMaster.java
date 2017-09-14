package main.game.module.dungeoncrawl.explore;

import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.bf.Coordinates;
import main.game.core.ActionInput;
import main.game.logic.action.context.Context;
import main.system.datatypes.DequeImpl;

import java.util.Set;

/**
 * Created by JustMe on 9/15/2017.
 */
public class ExplorePartyMaster extends ExplorationHandler {
    private Unit mainHero;
    DequeImpl<Unit> companions;
    Coordinates lastPosition;

    public ExplorePartyMaster(ExplorationMaster master) {
        super(master);
    }
    public ExplorationAiMaster getAiMaster() {
        return master.getAiMaster();
    }

    public Set<Unit> getAllies() {
        return getAiMaster().getAllies();
    }

    public void reset(){
        companions =initCompanions();
        mainHero =initMainHero();
        lastPosition = mainHero.getCoordinates();
        for (Unit unit : companions) {
            unit.getAI().setAttached(true);
        }
    }

    protected DequeImpl<Unit> initCompanions() {
        DequeImpl<Unit>  companions=new DequeImpl<>(getAllies());
        companions.removeIf(unit -> unit.isMainHero());
        return companions;
    }

    protected Unit initMainHero() {
        return (Unit) master.getGame().getPlayer(true).getHeroObj();
    }

    public void leaderActionDone(ActionInput input){
        reset();
        DC_ActiveObj active = input.getAction();
        if (active.isMove() || active.isSpell()) {
            checkFollow();
        } else {
            if (active.getActionGroup() == ACTION_TYPE_GROUPS.MODE) {
                checkNewAllyGoal();
            }
        }
    }
    public void checkNewAllyGoal(){

    }
    //after each player action?
    public void checkFollow(){
        for (Unit unit : companions) {
            unit.getAI().setAttached(checkFollows(unit));
        }
    }

    private boolean checkFollows(Unit unit) {
        if (isFollowOn(unit))
            return false;
        if (unit.getCoordinates().equals(mainHero.getCoordinates()))
            return true;
        if (unit.getCoordinates().isAdjacent(mainHero.getCoordinates()))
            return true;
        Action move = master.getGame().getAiManager().getAtomicAi().getAtomicMove(lastPosition, unit);
        if (!checkMove(move, unit))
            return false;
        ActionInput input = new ActionInput(move.getActive(), new Context(move.getRef()));
        master.getGame().getGameLoop().actionInput(input);

        return true;
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

    private boolean isFollowOn(Unit sub) {
//        if (sub.getAI().isDetached())
//            return false;
        return true;
    }



}
