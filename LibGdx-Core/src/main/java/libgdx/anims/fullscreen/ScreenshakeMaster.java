package libgdx.anims.fullscreen;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.core.Core;
import main.entity.Ref;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ScreenshakeMaster {

    public static void shakeCamera(Ref ref, Damage damage) {
        boolean lethal = ref.getTargetObj().isDead();
        boolean meTarget = ref.getTargetObj() == Core.getMainHero();
        boolean me = ref.getSourceObj() == Core.getMainHero();

        Boolean vertical = null;
        if (me)
            if (ref.getSourceObj() instanceof Unit) {
                vertical = ((Unit) ref.getSourceObj()).getFacing().isVertical();
            }
        float intensity = damage.getAmount() / 10;
        if (lethal) {
            intensity += 40;
        }
        if (meTarget) {
            intensity += 20;
        }

        VisualEnums.ScreenShakeTemplate template = VisualEnums.ScreenShakeTemplate.SLIGHT;
        if (intensity > 80) {
            template = VisualEnums.ScreenShakeTemplate.BRUTAL;
        }
        if (intensity > 40) {
            template = VisualEnums.ScreenShakeTemplate.HARD;
        }
        if (intensity > 20) {
            template = VisualEnums.ScreenShakeTemplate.MEDIUM;
        }

        GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(0.25f+intensity/100, vertical, template));
        //        main.system.auxiliary.log.LogMaster.log(1, "HIT ANIM STARTED WITH REF: " + getRef());

    }
}
