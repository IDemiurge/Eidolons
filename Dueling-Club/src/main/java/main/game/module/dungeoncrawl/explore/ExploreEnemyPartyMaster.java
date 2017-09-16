package main.game.module.dungeoncrawl.explore;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.GroupAI;
import main.game.core.ActionInput;

import java.util.Collection;

/**
 * Created by JustMe on 9/15/2017.
 */
public class ExploreEnemyPartyMaster extends ExplorePartyMaster {

    GroupAI groupAI;

    public ExploreEnemyPartyMaster(ExplorationMaster master) {
        super(master);
    }

    @Override
    public void reset() {
        super.reset();
    }

    public void setGroupAI(GroupAI groupAI) {
        this.groupAI = groupAI;
    }

    @Override
    public void leaderActionDone(ActionInput input) {
        setGroupAI(input.getAction().getOwnerObj().getAI().getGroupAI());
        super.leaderActionDone(input);
    }

    @Override
    public Collection<Unit> getAllies() {
        return groupAI.getMembers();
    }

    @Override
    protected Unit initMainHero() {
        return groupAI.getLeader();
    }
}
