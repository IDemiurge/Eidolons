package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.launch.Flags;

/**
 * Created by JustMe on 10/18/2018.
 */
public class WanderAi extends AiGroupBehavior {

    DIRECTION direction;
    boolean blocked;

    public WanderAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }
    public String getDebugInfo() {
        return ((int)sinceLastAction) + " "
         +((queuedAction == null)? "null" : queuedAction.getActive().getName())
         + "\n" + direction + "\n" +((target == null)?"null" : target.getCoordinates())
         +"\n" + origin.dst(getCoordinates())+ " " + ai.getGroupAI().getLeader().getName();
    }
    @Override
    public AI_BEHAVIOR_MODE getType() {
        return AI_BEHAVIOR_MODE.WANDER;
    }
    @Override
    protected double getDistanceForNearby() {
        return 2;//super.getDistanceForNearby();
    }
    @Override
    protected FACING_DIRECTION getRequiredFacing() {
        if (Math.abs(getUnit().getFacing().getDirection().getDegrees() -
         direction.getDegrees()) <= 45)
            return null;
        return FacingMaster.getFacingFromDirection(direction, true, false);
    }

    @Override
    protected boolean checkNeedsToUpdate() {
        direction = checkUpdateDirection(); //TODO elsewhere pls
        return super.checkNeedsToUpdate();
    }

    protected boolean checkNeedsNewOrdersForTarget() {
        if (target == null) {
            return false;
        }
        if (!ai.isLeader())
            return !isNearby();
        return true;
    }
    @Override
    protected DC_Obj updateLeaderTarget() {
        if (target == null) {
            // no cell was found?
        }

//        blocked = isProgressObstructed(ai);
//        if (blocked) {
//            //still blocked... wait for the next cycle
//            return null;
//        }
        Coordinates cell = getCoordinates().getAdjacentCoordinate(direction);
        if (origin.dst_(cell) >= getMaxDistance()+1) {
//            targetFailed();
            target=null;
            return null; //too far
        }
        return getCell(cell);
        //        return  getCell(CoordinatesMaster.getRandomAdjacentCoordinate( getCoordinates()));

    }


    @Override
    public Coordinates updatePreferredPosition() {
        //TODO ??? follow...
        return super.updatePreferredPosition();
    }

    @Override
    protected boolean isProgressObstructed(UnitAI ai) {
        return WanderAiMaster.isProgressObstructed(direction, ai, GOAL_TYPE.WANDER)
         && WanderAiMaster.isProgressObstructed(getUnit().getFacing().getDirection(), ai, GOAL_TYPE.WANDER);
    }

    @Override
    protected boolean failed() {
        log("failed, applying a fix...");
        direction=null;
        direction= checkUpdateDirection();
        resetSinceLastAction();
        return true;
    }

    @Override
    protected float getDefaultSpeed() {
        return super.getDefaultSpeed()/3;
    }

    @Override
    protected float getTimeBeforeFail() {
        return 35/speed;
    }



    protected DIRECTION checkUpdateDirection() {
        if (direction == null) {
            return FacingMaster.getRandomFacing(getUnit().getFacing()).getDirection();
        }
        if (target != null) {
            //check if arrived

            //check wait for follow

            //check if we're too far
            if (origin.dst_(getCoordinates()) >= getMaxDistance()) {
                //then 'return back'
                return DirectionMaster.getRelativeDirection(
                 getCoordinates(), origin);
            }
        }
        blocked = isProgressObstructed(ai);
        if (blocked) {
            for (int i = 2; i <= 2; i++) { //isDiagonalAllowed()
                if (RandomWizard.random())
                    i=-i;
                DIRECTION d = direction.rotate(45 * i);
                if (!WanderAiMaster.isProgressObstructed(d, ai, GOAL_TYPE.WANDER))
                    return d;
                d = direction.rotate(-45 * i);
                if (!WanderAiMaster.isProgressObstructed(d, ai, GOAL_TYPE.WANDER))
                    return d;
                if(i<0)
                    i=-i;
            }
        }
        //ideally, we should not be blocked by a single obstacle

        return direction;
    }

    protected int getMaxDistance() {
        if (block == null) {
            block = master.getGame().getDungeonMaster().getStructMaster().getLowestStruct(
             ai.getUnit().getCoordinates());
            if (block == null) {
                for (Coordinates c : ai.getUnit().getCoordinates().getAdjacent()) {
                    block = master.getGame().getDungeonMaster().getStructMaster().getLowestStruct(
                     c);
                    if (block != null)
                        break;
                }
            }
        }
        if (block == null) {
            return    getDefaultMaxDistance();
        }
        return Math.round(Math.min(7,
                origin.dst(block.getCenterCoordinate()) +
                        Math.max(block.getHeight() * getBlockSizeCoefForMaxDistance(), block.getWidth() * getBlockSizeCoefForMaxDistance())));
        //        }
        //        return WanderAiMaster.getMaxWanderTotalDistance(group, GOAL_TYPE.WANDER);
    }

    private int getDefaultMaxDistance() {
        if (getUnit().getAI().getGroup().getMembers().size()==0){
            return 1;
        }
        return Flags.isSafeMode()? 2: 7;
    }

    private float getBlockSizeCoefForMaxDistance() {
        return Flags.isSafeMode()? 0.1f: 0.5f;
    }

}
