package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimData;

/**
 * Created by JustMe on 1/16/2017.
 */
public class DeathAnim extends ActionAnim {
    DC_HeroObj unit;
    DEATH_ANIM template;
    public DeathAnim(Entity active, AnimData params) {
        super(active, params);
        unit = (DC_HeroObj) getRef().getTargetObj();
        template = getTemplate(getActive(), unit);

    }



    @Override
    protected Action getAction() {
        AlphaAction action = ActorMaster.addFadeAction(getActor());
        action.setDuration(duration);
        return action;
    }

    private DEATH_ANIM getTemplate(DC_ActiveObj active, DC_HeroObj unit) {
        getRef().getEvent().getRef().getDamageType();
        return DEATH_ANIM.FADE;
    }

    @Override
    public void start() {
//        addSfx();
        //skull / grave?
        super.start();
        add();
    }

    public enum DEATH_ANIM {
        FADE, FLASH,
        EXPLODE,
        BURN,
        COLLAPSE,
        ATOMIZE, SHATTER,;
        String spritePath;
    }

}
