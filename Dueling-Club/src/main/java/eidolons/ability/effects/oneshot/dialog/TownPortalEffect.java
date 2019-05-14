package eidolons.ability.effects.oneshot.dialog;

import eidolons.ability.effects.DC_Effect;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class TownPortalEffect extends DC_Effect { //TODO make this a subclass!
    @Override
    public boolean applyThis() {
//TODO confirm instead?

//        GuiEventManager.trigger(GuiEventType.CONFIRM, new TipMessageSource(
//        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
//                msg, img, btn, false, getRunnable(), getChannel(), true));


        return true;
    }

    private Runnable getRunnable() {
        return () -> {
            getGame().getMetaMaster().getTownMaster().tryReenterTown();

        };
    }
}
