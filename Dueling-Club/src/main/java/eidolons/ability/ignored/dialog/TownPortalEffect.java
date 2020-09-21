package eidolons.ability.ignored.dialog;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.anims.std.sprite.CustomSpriteAnim;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.audio.DC_SoundMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;

public class TownPortalEffect extends DC_Effect { //TODO make this a subclass!
    @Override
    public boolean applyThis() {
//TODO confirm instead?
        EUtils.onConfirm("Use this to journey back to safety?", true,
                () -> {
                    String path = RandomWizard.random() ?
                            Sprites.SHADOW_DEATH:
                            Sprites.SHADOW_SUMMON;
//                "spell/town portal.txt";
                    DC_ActiveObj action;
                    if (ref.getActive() == null) {
                        action  = getSourceUnitOrNull().getLastAction();
                    } else {
                        action  = (DC_ActiveObj) ref.getActive();
                    }
                    CustomSpriteAnim anim = new CustomSpriteAnim(action, path) {
                    };
                    anim.setRef(ref);
                    DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__TOWN_PORTAL_START);
                    anim.setOnDone(p -> {
                        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__TOWN_PORTAL_DONE);
                        Eidolons.onNonGdxThread(() ->
                                getGame().getMetaMaster().getTownMaster().tryReenterTown());
                    });
                    GuiEventManager.trigger(GuiEventType.CUSTOM_ANIMATION, anim);


                });

        boolean result = (boolean) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.CONFIRM);

        return result;
//        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
//                msg, img, btn, false, getRunnable(), getChannel(), true));
    }

    private Runnable getRunnable() {
        return () -> {
            getGame().getMetaMaster().getTownMaster().tryReenterTown();

        };
    }
}
