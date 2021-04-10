package libgdx.particles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.content.consts.VisualEnums;
import libgdx.anims.sprite.SpriteX;
import libgdx.bf.Fluctuating;
import libgdx.gui.generic.GroupX;
import main.data.XLinkedMap;
import main.system.auxiliary.RandomWizard;

import java.util.Map;

public class ParticlesSprite extends GroupX {
    static {
    }

    VisualEnums.PARTICLES_SPRITE type;
    Map<SpriteX, Fluctuating> map = new XLinkedMap<>();

    public ParticlesSprite(VisualEnums.PARTICLES_SPRITE type) {
        this.type = type;
        init();
    }

    public void init() {

        for (int i = 0; i < type.overlap; i++) {
            SpriteX sprite = new SpriteX(type.path);
            if (type.flipping)
                sprite.setOnCycle(() ->
                        {
                            if (RandomWizard.random()) {
                                sprite.setFlipX(!sprite.getSprite().isFlipX());
                            } else
                                sprite.setFlipY(!sprite.getSprite().isFlipY());
                        }
                );
            if (type.reverse) {
                sprite.getSprite().setPlayMode(Animation.PlayMode.LOOP_REVERSED);
            }
            sprite.setFps(type.fps);
//            sprite.setFlipX();
//            sprite.setFlipY();
            sprite.setBlending(type.blending);
            float duration = type.duration = sprite.getSprite().getFrameNumber() / type.fps;
            sprite.act(i * duration / type.overlap);
            Fluctuating f = type.fluctuation == null ? null : new Fluctuating(type.fluctuation);
            map.put(sprite, f);
            addActor(sprite);
            sprite.getSprite().centerOnScreen();
            if (f != null) {
                addActor(f);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (SpriteX spriteX : map.keySet()) {
            spriteX.getSprite().setPlayMode(Animation.PlayMode.LOOP);
            spriteX.getSprite().centerOnScreen();
            Fluctuating fluctuating = map.get(spriteX);
            if (fluctuating != null) {
                spriteX.setFps((int) (((spriteX.getSprite().getOriginalFps() * fluctuating.getColor().a / 3))));
                if (fluctuating.isAlphaFluctuationOn()) {
                    float time = spriteX.getSprite().getStateTime();
                    float duration = type.duration / (type.fluctuation.max + type.fluctuation.min) / 2;
                    float perc = (time % duration) / (duration);
                    float coef = Math.max(
                            Math.abs(1 / type.overlap - (1 - perc)),
                            Math.abs(1 / type.overlap - perc));
                    spriteX.getColor().a =
//                            getSprite().setAlpha
                            (fluctuating.getColor().a * coef * getColor().a);
                }
            } else {
                spriteX.getColor().a = getColor().a;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, 1f);
    }
}
