package eidolons.game.module.dungeoncrawl.ai;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.core.Eidolons;
import main.game.bf.Coordinates;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/12/2018.
 *
 suppose each UnitAi had a number of behaviors at all times...
 perhaps even competing!

 each behavior has a waiting float and a way to convert it into actions

 getNewOrders
 checkOrdersValid
 arrived

 are they all about moving?
 perhaps I could just rob GdxAi of some good concepts
 */
public class FollowAi extends AiBehavior{
    private final AiMaster master;
    UnitAI ai;
    float timer;
    float lastDistanceFromLeader;
    private boolean on;

    public FollowAi(UnitAI ai, AiMaster master) {
        this.ai = ai;
        this.master = master;
    }

    public void teleportToLeader(){
        //yeah...
        ai.getUnit().setCoordinates(ai.getGroup().getLeader().getCoordinates());
    }
public enum BEHAVIOR_STATUS{
        WAITING,
    RUNNING,
    BLOCKED,

}
    BEHAVIOR_STATUS status;
    public void act(float delta){
        timer+=delta;
        if (isNearby()){
            timer = 0;
            on = false;
            status = BEHAVIOR_STATUS.WAITING;
            return;
        }
        boolean lost = checkLost();
        if (lost)
        if (checkCanTeleport()){
            teleportToLeader();
        }

        initOrders();
//        getOrders()
        }

    private void initOrders() {
        List<Coordinates> targetCells=     new ArrayList<>(
         ai.getGroupAI().getLeader().getCoordinates().getAdjacentCoordinates()) ;

        ActionPath path = master.getPathBuilder().getPathByPriority(targetCells);
        ActionSequence sequence = master.getActionSequenceConstructor().getSequenceFromPath(path, ai);

//        Order order= new Order();
        ai.setStandingOrders(sequence);
    }
    @Override
    public ActionSequence getOrders(UnitAI ai) {
        return null;
    }

    private boolean isNearby() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), ai.getGroup().getLeader());
        return dst<=getRequiredDistance();

    }

    private double getRequiredDistance() {
        return 1;
    }

    private boolean checkCanTeleport() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), Eidolons.getMainHero());
        if (dst < Eidolons.getMainHero().
         getMaxVisionDistanceTowards(ai.getUnit().getCoordinates())) {
            return false;
        }
        return true;
    }

    public boolean checkLost() {
        //unable to get closer for X seconds?
        return timer>getTimeBeforeLost();
    }

    private float getTimeBeforeLost() {
        return 20;
    }

}
