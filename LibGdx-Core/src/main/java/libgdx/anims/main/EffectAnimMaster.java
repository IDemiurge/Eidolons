package libgdx.anims.main;

import libgdx.anims.Animation;
import libgdx.anims.construct.AnimConstructor;
import main.ability.effects.Effect;

/**
 * Created by JustMe on 11/15/2018.
 */
public class EffectAnimMaster {

    AnimMaster master;

    public EffectAnimMaster(AnimMaster master) {
        this.master = master;
    }

    protected void initEffectAnimation(Effect effect) {
        Animation anim = AnimConstructor.getEffectAnim(effect);
        if (anim == null) {
            return;
        }
        // CompositeAnim parentAnim =AnimMaster.getParentAnim(effect.getRef());
        // if (parentAnim != null) {
        //     LogMaster.log(LogMaster.ANIM_DEBUG, anim + " created for: " + parentAnim);
        //     parentAnim.addEffectAnim(anim, effect); //TODO}
        // } else {
            master.getDrawer().attachedAnims.add(anim);// when to start()?
            anim.start();
        // }
    }

}
