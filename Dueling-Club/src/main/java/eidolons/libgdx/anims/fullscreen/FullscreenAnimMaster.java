package eidolons.libgdx.anims.fullscreen;

import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/12/2018.
 */
public class FullscreenAnimMaster {
    public FullscreenAnimMaster() {
        GuiEventManager.bind(GuiEventType.SHOW_FULLSCREEN_ANIM, p -> {


        });

    }
}
