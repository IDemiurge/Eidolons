package eidolons.game.battlecraft.logic.meta.custom.model.assembly;

public class TransformData {
    Boolean rotation;
    Boolean rotation180;
    boolean flipX;
    boolean flipY;

    public TransformData(Boolean rotation, Boolean rotation180, boolean flipX, boolean flipY) {
        this.rotation = rotation;
        this.rotation180 = rotation180;
        this.flipX = flipX;
        this.flipY = flipY;
    }

    public Boolean getRotation() {
        return rotation;
    }

    public Boolean getRotation180() {
        return rotation180;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }
}
