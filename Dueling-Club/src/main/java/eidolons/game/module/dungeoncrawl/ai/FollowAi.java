package eidolons.game.module.dungeoncrawl.ai;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;

/**
 * Created by JustMe on 10/12/2018.
 */
public class FollowAi extends AiBehavior{

    public void teleportToLeader(UnitAI ai){
        //yeah...
        ai.getUnit().setCoordinates(ai.getGroup().getLeader().getCoordinates());
    }

    public void act(float delta){
        /*
        suppose each UnitAi had a number of behaviors at all times...
        perhaps even competing!

        each behavior has a waiting float and a way to convert it into actions

         getNewOrders
         checkOrdersValid
         arrived

         are they all about moving?

         perhaps I could just rob GdxAi of some good concepts

         */


        }

    public boolean isLost(UnitAI ai) {
        //unable to get closer for X seconds?
        return false;
    }

    @Override
    public ActionSequence getOrders(UnitAI ai) {
        return null;
    }
}
