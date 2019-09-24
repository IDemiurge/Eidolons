package eidolons.libgdx.particles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.Fluctuating;
import eidolons.libgdx.gui.generic.GroupX;
import main.content.enums.GenericEnums;
import main.data.XLinkedMap;
import main.system.auxiliary.RandomWizard;

import java.util.Map;

public class ParticlesSprite extends GroupX {
    static {
    }

    public enum PARTICLES_SPRITE {
        ASH(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.INVERT_SCREEN,
                12, 2, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        ASH_THICK(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.INVERT_SCREEN,
                14, 3, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
//        MIST(true, "sprites/particles/mist.txt", GenericEnums.BLENDING.SCREEN,
//                10, 2, null),
//        BLACK_MIST(true, "sprites/particles/mist.txt", GenericEnums.BLENDING.INVERT_SCREEN,
//                10, 2, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        SNOW(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.SCREEN,
                14, 2, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        SNOW_THICK(false, "sprites/particles/snow.txt", GenericEnums.BLENDING.SCREEN,
                15, 3, GenericEnums.ALPHA_TEMPLATE.OVERLAYS),
        ;


        PARTICLES_SPRITE(boolean flipping, String path, GenericEnums.BLENDING blending, int fps, int overlap, GenericEnums.ALPHA_TEMPLATE fluctuation) {
            this.flipping = flipping;
            this.path = path;
            this.blending = blending;
            this.fps = fps;
            this.overlap = overlap;
            this.fluctuation = fluctuation;
        }

        public boolean reverse;
        public boolean flipping;
        public String path;
        public GenericEnums.BLENDING blending;
        int fps;
        int overlap;
        boolean changeFps;

        GenericEnums.ALPHA_TEMPLATE fluctuation;
        int eachNisFlipX = 0;
        public int duration;
    }

    PARTICLES_SPRITE type;
    Map<SpriteX, Fluctuating> map = new XLinkedMap<>();

    public ParticlesSprite(PARTICLES_SPRITE type) {
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
