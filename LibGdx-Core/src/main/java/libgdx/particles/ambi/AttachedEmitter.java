package libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import libgdx.screens.CustomSpriteBatch;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 11/16/2018.
 */
public class AttachedEmitter extends Ambience {
    public AttachedEmitter(GenericEnums.VFX preset) {
        super(preset);
    }

    @Override
    public void draw(Batch spriteBatch, float delta) {
        super.draw(spriteBatch, delta);
        if (spriteBatch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) spriteBatch).resetBlending();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
//        if (checkTimer> checkAfter){
//            checkPause();
//        }
    }
}
