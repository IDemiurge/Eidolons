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

/**
 * Created by JustMe on 10/18/2018.
 */
public class WanderAi extends AiGroupBehavior {

    DIRECTION direction;
    boolean blocked;
    private boolean clockwise;

    public WanderAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }

    @Override
    protected AI_BEHAVIOR_MODE getType() {
        return AI_BEHAVIOR_MODE.WANDER;
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
            if (isNearby())
                return false;
        return true;
    }
    @Override
    protected DC_Obj updateLeaderTarget() {
        if (ai.getGroupAI() == null) {
            //TODO do we handle this?
        }
        if (target == null) {
            // no cell was found?
        }

        blocked = isProgressObstructed(ai);
        if (blocked) {
            //still blocked... wait for the next cycle
            return null;
        }
        Coordinates cell = getCoordinates().getAdjacentCoordinate(direction);
        if (origin.dst_(getCoordinates()) >= getMaxDistance()) {
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
        return WanderAiMaster.isProgressObstructed(direction, ai, GOAL_TYPE.WANDER);
    }

    private DIRECTION checkUpdateDirection() {
        if (direction == null) {
            return DirectionMaster.getRandomDirection();
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
            for (int i = 1; i <= 2; i++) {
                DIRECTION d = direction.rotate(45 * i);
                if (!WanderAiMaster.isProgressObstructed(d, ai, GOAL_TYPE.WANDER))
                    return d;
                d = direction.rotate(-45 * i);
                if (!WanderAiMaster.isProgressObstructed(d, ai, GOAL_TYPE.WANDER))
                    return d;
            }
        }
        //ideally, we should not be blocked by a single obstacle

        return direction;
    }

    private int getMaxDistance() {
        if (block == null) {
            block = master.getGame().getDungeonMaster().getDungeonLevel().getBlockForCoordinate(
             ai.getUnit().getCoordinates());
            if (block == null) {
                for (Coordinates c : ai.getUnit().getCoordinates().getAdjacent()) {
                    block = master.getGame().getDungeonMaster().getDungeonLevel().getBlockForCoordinate(
                     c);
                    if (block != null)
                        break;
                }
            }
        }
        if (block == null) {
            return 10;
        }
        return Math.max(block.getHeight(), block.getWidth());
        //        }
        //        return WanderAiMaster.getMaxWanderTotalDistance(group, GOAL_TYPE.WANDER);
    }


    public int getMaxWanderDistance() {
        // default - percent of size? 'don't leave the Block'
        // getType()
        // checkMod(trueBrute)
        return 5;
    }
}
