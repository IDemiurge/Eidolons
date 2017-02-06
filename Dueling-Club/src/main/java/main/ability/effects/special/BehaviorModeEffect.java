package main.ability.effects.special;

import main.ability.effects.oneshot.common.ModifyPropertyEffect;
import main.content.CONTENT_CONSTS.BEHAVIOR_MODE;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.EnumMaster;

public class BehaviorModeEffect extends ModifyPropertyEffect {

    // private BEHAVIOR_MODE mode;

    private BEHAVIOR_MODE mode;

    public BehaviorModeEffect(String mode) {
        super(G_PROPS.MODE, MOD_PROP_TYPE.SET, mode);
        this.setMode(new EnumMaster<BEHAVIOR_MODE>().retrieveEnumConst(BEHAVIOR_MODE.class, mode));
    }

    public BehaviorModeEffect(BEHAVIOR_MODE mode) { // just *mode*?
        super(G_PROPS.MODE, MOD_PROP_TYPE.SET, mode.toString());
        this.setMode(mode);
    }

    @Override
    public boolean applyThis() {
        if (ref.getTargetObj() instanceof DC_HeroObj) {
            DC_HeroObj unit = (DC_HeroObj) ref.getTargetObj();
            if (unit.isUnconscious()) {
                return false;
            }
        }
        return super.applyThis();

    }

    public BEHAVIOR_MODE getMode() {
        return mode;
    }

    public void setMode(BEHAVIOR_MODE mode) {
        this.mode = mode;
    }

}
