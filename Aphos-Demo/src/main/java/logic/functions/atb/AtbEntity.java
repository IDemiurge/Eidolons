package logic.functions.atb;

import eidolons.entity.unit.Unit;
import logic.entity.Entity;

public interface AtbEntity {

    float getAtbReadiness();

    void setAtbReadiness(float v);

    boolean isImmobilized();

    float getInitiative();

    float getTimeTillTurn();

    void setTimeTillTurn(float i);

    Entity getEntity();

    float getInitialInitiative();

    int getDisplayedAtbReadiness();
}
