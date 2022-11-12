package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;

public class IdleAi extends AiBehavior {
    public IdleAi(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isTargetCoordinateValid() {
        return true;
    }

    @Override
    public Coordinates updatePreferredPosition() {
        return getUnit().getOriginalCoordinates();
    }

    @Override
    public boolean update() {
        return super.update();
    }

    @Override
    public boolean isPositionValid(Coordinates c) {
        return true;
    }


    @Override
    protected boolean checkNeedsToUpdate() {
        return true;
    }

    @Override
    protected float getTimeBeforeFail() {
        return 12;
    }

    @Override
    protected float getDefaultSpeed() {
        return 0.65f*super.getDefaultSpeed();
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return false;
    }

    @Override
    public UnitAI.AI_BEHAVIOR_MODE getType() {
        return UnitAI.AI_BEHAVIOR_MODE.GUARD;
    }
}
