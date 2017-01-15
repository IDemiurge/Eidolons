package main.libgdx.anims.std;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.entity.Entity;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;


/**
 * Created by JustMe on 1/9/2017.
 */
public class ActionAnim extends Anim {


    public ActionAnim(Entity active, AnimData params) {
        super(active, params);
    }

    @Override
    public DC_ActiveObj getActive() {
        return (DC_ActiveObj) super.getActive();
    }

    public void addEffectAnims(Effect effect) {
//        effect.get
        //damage,
//        Anim anim = new Anim();
//        add(anim);
//        addActor(a);
    }

    public void addAbilityAnims(Ability ability) {

    }
}
