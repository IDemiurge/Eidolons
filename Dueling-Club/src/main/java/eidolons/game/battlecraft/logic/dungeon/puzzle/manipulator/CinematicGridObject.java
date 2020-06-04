package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import main.content.enums.entity.BfObjEnums;
import main.game.bf.Coordinates;

public class CinematicGridObject extends GridObject {

    protected BfObjEnums.CUSTOM_OBJECT object;

    protected float origX;
    protected float origY;

    public CinematicGridObject(Coordinates c,   BfObjEnums.CUSTOM_OBJECT object) {
        super(c, object.spritePath);
        this.object = object;
    }

    @Override
    protected boolean isClearshotRequired() {
        return false;
    }

    @Override
    protected double getDefaultVisionRange() {
        return 0;
    }

    @Override
    protected int getFps() {
        return 20;
    }
}
