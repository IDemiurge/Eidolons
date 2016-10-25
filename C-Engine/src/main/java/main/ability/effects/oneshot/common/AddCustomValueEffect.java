package main.ability.effects.oneshot.common;

import main.ability.effects.oneshot.MicroEffect;
import main.data.ability.construct.VarEnum;

public class AddCustomValueEffect extends MicroEffect {

    private boolean param;
    private String name;
    private String value;

    public AddCustomValueEffect(VarEnum varEnum) {
        // name = varEnum.getString();

    }

    public AddCustomValueEffect(String name, String value) {
        this.param = true;
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean applyThis() {
        if (param)
            ref.getTargetObj().addCustomParameter(name, value);
        else
            ref.getTargetObj().addCustomProperty(name, value);

        return false;
    }
}
