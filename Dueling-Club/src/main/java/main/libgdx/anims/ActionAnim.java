package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.ability.Ability;
import main.ability.effects.Effect;
import main.entity.Entity;

import java.util.List;


/**
 * Created by JustMe on 1/9/2017.
 */
public class ActionAnim extends Anim {
    List<Anim> anims;
    public ActionAnim(Entity active, Anim... subAnims) {
        super(active, null);
    }


    /*
    damage info
    costs
    icon

     */

    public boolean draw(Batch batch ) {

        anims.forEach(a-> {
            addActor(a);
         draw(batch) ; });

        return true;
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
