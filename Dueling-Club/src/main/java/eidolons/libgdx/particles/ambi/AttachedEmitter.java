package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.particles.VFX;
import eidolons.libgdx.screens.CustomSpriteBatch;

/**
 * Created by JustMe on 11/16/2018.
 */
public class AttachedEmitter extends Ambience {
    public AttachedEmitter(VFX preset) {
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
