package main.ability.effects.common;

import main.ability.effects.MicroEffect;
import main.content.enums.entity.UnitEnums.STATUS;

public class AddStatusEffect extends MicroEffect {
    protected String status;

    public AddStatusEffect(String STATUS) {
        this.status = STATUS;
    }

    public AddStatusEffect(STATUS status) {
        this.status = status.toString();
    }

    @Override
    public String toString() {
        return "Add Status Effect: " + status;
    }

    @Override
    public boolean applyThis() {
        ref.getTargetObj().addStatus(status);
        return true;
    }
}
