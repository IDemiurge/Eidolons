package eidolons.game.battlecraft.logic.meta.igg.pale;

import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.shaders.post.PostFxUpdater;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class PaleAspect {


    public static boolean ON;

    /**
     * transformation rules
     * <p>
     * movement
     * <p>
     * vision
     * <p>
     * avatar
     * <p>
     * enter/exit
     */

    private static Unit createPaleAvatar() {
        return null;
    }


    public static void enterPale(Unit unit) {
        ON = true;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.PALE_ASPECT);

        unit = createPaleAvatar();
        //reset vision etc
        unit.setPale(true);

        //

        /**
         *
         */

    }

}
