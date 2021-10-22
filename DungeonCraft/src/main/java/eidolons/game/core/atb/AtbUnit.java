package eidolons.game.core.atb;

import eidolons.entity.unit.Unit;

/**
 * Created by JustMe on 4/5/2018.
 */
public interface AtbUnit {

    float getAtbReadiness();

    void setAtbReadiness(float v);

    boolean isImmobilized();

    float getInitiative();

    float getTimeTillTurn();

    void setTimeTillTurn(float i);

    Unit getUnit();

    float getInitialInitiative();

    int getDisplayedAtbReadiness();
}
