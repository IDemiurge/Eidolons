package eidolons.game.netherflame.dungeons.model.assembly;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;

import java.util.Map;

public class Transform {
    Boolean rotation;
    Boolean rotation180;
    boolean flipX;
    boolean flipY;

    public Transform(Boolean rotation, Boolean rotation180, boolean flipX, boolean flipY) {
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

    public String toString(boolean rotation) {
        return rotation ? "cw" : "ccw";
    }

    @Override
    public String toString() { //TODO
        return "" +
                "rotation " + rotation +
                ", rotation180=" + rotation180 +
                ", flipX=" + flipX +
                ", flipY=" + flipY +
                '}';
    }

    public void transformMap(Module module, Map map) {
        for (Object o : map.keySet()) {
            Coordinates c = null;
            if (o instanceof String) {

            }
            if (c == null) {
                continue;
            }
            String s = transform(module, c).toString();

        }
    }

    private Coordinates transform(Module module,Coordinates c) {
        if (rotation != null) {
            c.rotate(rotation, module.getEffectiveWidth(), module.getEffectiveHeight());
        }
        if (rotation180 != null) {
            c.rotate(rotation180, module.getEffectiveWidth(), module.getEffectiveHeight());
            c.rotate(rotation180, module.getEffectiveWidth(), module.getEffectiveHeight());
        }
        if (flipX)
            c.flipX(module.getEffectiveWidth());
        if (flipY)
            c.flipY(module.getEffectiveHeight());
        return c;
    }
}
