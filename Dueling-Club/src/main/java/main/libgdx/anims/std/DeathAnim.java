package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.sprite.SpriteAnimation;

import java.util.List;

/**
 * Created by JustMe on 1/16/2017.
 */
public class DeathAnim extends ActionAnim {
    DC_HeroObj unit;

    public DeathAnim(Entity active, AnimData params) {
        super(active, params);
    }

    @Override
    public List<SpriteAnimation> getSprites() {
        return super.getSprites();
    }

    @Override
    protected Action getAction() {
        AlphaAction action = new AlphaAction();
        action.setAlpha(0);
        

        return super.getAction();
    }
}
