package eidolons.game.battlecraft.ai.explore.behavior;

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
 * <p>
 * suppose each UnitAi had a number of behaviors at all times...
 * perhaps even competing!
 * <p>
 * each behavior has a waiting float and a way to convert it into actions
 * <p>
 * getNewOrders
 * checkOrdersValid
 * arrived
 * <p>
 * are they all about moving?
 * perhaps I could just rob GdxAi of some good concepts
 */
public class FollowAi extends AiBehavior {
    float lastDistanceFromLeader;

    public FollowAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    public void teleportToLeader() {
        //yeah...
        ai.getUnit().setCoordinates(ai.getGroup().getLeader().getCoordinates());
    }

    public void update(float delta) {
        //        checkNeedsUpdate();
        //        checkHasPriority();

        if (isNearby()) {
            timer = 0;
            status = BEHAVIOR_STATUS.WAITING;
            return;
        }
        boolean lost = checkLost();
        if (lost)
            if (checkCanTeleport()) {
                teleportToLeader();
            }

        initOrders();
        //        getOrders()
    }

    @Override
    protected void initOrders() {
        List<Coordinates> targetCells = new ArrayList<>(
         ai.getGroupAI().getLeader().getCoordinates().getAdjacentCoordinates());

        ActionPath path = master.getPathBuilder().getPathByPriority(targetCells);
        ActionSequence sequence = master.getActionSequenceConstructor().getSequenceFromPath(path, ai);

        //        Order order= new Order();
        ai.setStandingOrders(sequence);
    }

    @Override
    public ActionSequence getOrders(UnitAI ai) {
        return null;
    }

    @Override
    protected float getTimeBeforeFail() {
        return 20;
    }

    private boolean isNearby() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), ai.getGroup().getLeader());
        return dst <= getRequiredDistance();

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


}
