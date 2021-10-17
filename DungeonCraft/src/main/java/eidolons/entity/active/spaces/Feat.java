package eidolons.entity.active.spaces;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import main.entity.OBJ;

public interface Feat extends OBJ {
    String getImagePath();

    default boolean isActive() {
        return true;
    }

    DC_ActiveObj getActive();

    void invokeClicked();

    boolean canBeActivated();

    default Integer getCharges() {
        return getIntParam(PARAMS.C_CHARGES);
    }

    default Integer getCooldown() {
        return getIntParam(PARAMS.C_COOLDOWN);
    }

    default void cooldownActivated() {
        setParam(PARAMS.C_COOLDOWN, getIntParam(PARAMS.COOLDOWN));
    }

    default void chargeUsed() {
        modifyParameter(PARAMS.C_CHARGES, -1);
    }


    default void timePassed(int seconds) {
        int val = Math.max(0, getCooldown() - seconds);
        setParam(PARAMS.C_COOLDOWN, val);
    }
}
