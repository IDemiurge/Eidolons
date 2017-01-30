package main.libgdx.anims.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.content.CONTENT_CONSTS2.SFX;

/**
 * Created by JustMe on 1/10/2017.
 */
public class Ambience extends EmitterActor {
    public Ambience(SFX fx) {
        super(fx);
    }

    public enum AMBIENT_SFX {
        MIST,


    }

    @Override
    public void draw(Batch spriteBatch, float delta) {
      getEffect(). modifyParticles();
        super.draw(spriteBatch, delta);
    }
}
