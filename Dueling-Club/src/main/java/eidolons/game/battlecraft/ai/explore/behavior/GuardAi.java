package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 9/23/2017.
 * <p>
 * Nevers loses sight of the X that it's guarding! or maybe with y%
 * <p>
 * idle => sleep ...
 * <p>
 * create a limited pool of positions and go thru them...
 */
public class GuardAi extends AiBehavior {

    DC_Obj guarded;
    private boolean needsToCheckGuarded;
    private List<Coordinates> guardedZone;
    private float timeBeforeCheck;

    public GuardAi(AiMaster master, UnitAI ai, DC_Obj guarded) {
        super(master, ai);
        this.guarded = guarded;
        log(" is watching " + guarded);
        timeBeforeCheck= getTimeBeforeCheck();
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }

    @Override
    protected boolean checkOrdersValid(Orders orders) {
        if (globalTimer>timeBeforeCheck) {
            orderToCheckOnGuarded();
            return false;
        }
        return super.checkOrdersValid(orders);
    }

    private void orderToCheckOnGuarded() {
        needsToCheckGuarded=true;
        resetTimer();
        timeBeforeCheck= getTimeBeforeCheck();
    }

    private float getTimeBeforeCheck() {
        return (10 + RandomWizard.getRandomInt(15))/getSpeed();
    }

    @Override
    protected DC_Obj updateTarget() {
        if (guardedZone == null)
            guardedZone = block.getCoordinatesList().stream().filter(c -> c.dst_(guarded.getCoordinates())
              < getMaxDistanceFromGuarded()
            && guarded.getGame().getMovementManager().canMove(null, c) //0 girth
            ).collect(Collectors.toList());

        List<Coordinates> validCells = new ArrayList<>(guardedZone);
        validCells.removeIf(c -> getCoordinates().dst(c) < 2);
        return getCell( validCells.get(RandomWizard.getRandomIndex(validCells)));
    }

    private double getMaxDistanceFromGuarded() {
        return 3;
    }

    @Override
    public AI_BEHAVIOR_MODE getType() {
        return AI_BEHAVIOR_MODE.GUARD;
    }


    @Override
    protected FACING_DIRECTION getRequiredFacing() {
        if (needsToCheckGuarded)
            for (FACING_DIRECTION newFacing : FACING_DIRECTION.normalFacing) {
                if (FacingMaster.getSingleFacing(newFacing,
                 getCoordinates(), target.getCoordinates()) == FACING_SINGLE.IN_FRONT) {
                    return newFacing;
                }
            }
        return super.getRequiredFacing();
    }

    @Override
    protected float getTimeBeforeFail() {
        return 50 / getSpeed();
    }

    @Override
    protected boolean failed() {
        return super.failed();
    }

    @Override
    protected double getDistanceForNearby() {
        return super.getDistanceForNearby();
    }

    @Override
    public ActionSequence getOrders() {
        return super.getOrders();
        //check mode
        /*
        get random cell and go there without ever getting farther than X from origin

        checkOnTheGuarded - just face it and queue waiting
         */


        //        Coordinates target = CoordinatesMaster.getRandomAdjacentCoordinate(getCoordinates());
        //        List<Action> list = getMaster(ai).getTurnSequenceConstructor().
        //         getTurnSequence(FACING_SINGLE.IN_FRONT, ai.getUnit(), target);
        //        return new ActionSequence(GOAL_TYPE.PROTECT, list.toArray(new Action[list.size()]));
    }


    protected boolean isEnabled(UnitAI ai) {
        return true;
    }

}
