package eidolons.ability.effects.oneshot.puzzle;

import eidolons.ability.effects.DC_Effect;
import eidolons.content.PROPS;
import main.content.CONTENT_CONSTS;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LightChangeEffect extends DC_Effect {

    boolean cycle;
    CONTENT_CONSTS.COLOR_THEME variant;

    public enum COLOR_VARIANT{
        NETHER_GREEN,
        BLUE,
        PURPLE,
        HOLY,
        CHAOS,
        // GEMS?
    }
    @Override
    public boolean applyThis() {
        //change obj type!! apply it... should be easy

//        getTarget().applyType(type);

//check?
        getTarget().setProperty(PROPS.COLOR_THEME, variant.toString());
        GuiEventManager.trigger(GuiEventType.RESET_LIGHT_EMITTER, getTarget());
        return false;
    }
}
