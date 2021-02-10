package libgdx.anims.fullscreen;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.core.Eidolons;
import main.entity.Ref;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ScreenshakeMaster {

    public static void shakeCamera(Ref ref, Damage damage) {
        boolean lethal = ref.getTargetObj().isDead();
        boolean meTarget = ref.getTargetObj() == Eidolons.getMainHero();
        boolean me = ref.getSourceObj() == Eidolons.getMainHero();

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

        Screenshake.ScreenShakeTemplate template = Screenshake.ScreenShakeTemplate.SLIGHT;
        if (intensity > 80) {
            template = Screenshake.ScreenShakeTemplate.BRUTAL;
        }
        if (intensity > 40) {
            template = Screenshake.ScreenShakeTemplate.HARD;
        }
        if (intensity > 20) {
            template = Screenshake.ScreenShakeTemplate.MEDIUM;
        }

        GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, new Screenshake(0.25f+intensity/100, vertical, template));
        //        main.system.auxiliary.log.LogMaster.log(1, "HIT ANIM STARTED WITH REF: " + getRef());

    }
}
