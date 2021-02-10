package libgdx.particles;

import libgdx.anims.actions.ActionMaster;
import libgdx.gui.generic.GroupX;
import libgdx.particles.ParticlesSprite.PARTICLES_SPRITE;
import main.data.XLinkedMap;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.Map;

public class ParticlesSprites extends GroupX {
    private final Map<PARTICLES_SPRITE, ParticlesSprite> map = new XLinkedMap<>();

    public ParticlesSprites() {
        GuiEventManager.bind(GuiEventType.SET_PARTICLES_ALPHA, p -> {
            List args = (List) p.get();
            PARTICLES_SPRITE type = (PARTICLES_SPRITE) args.get(0);
            float alpha = (float) args.get(1);
            ParticlesSprite sprite = map.get(type);
            if (sprite == null) {
                map.put(type, sprite = new ParticlesSprite(type));
                addActor(sprite);
                sprite.getColor().a=0;
            }
            ActionMaster.addAlphaAction(sprite, 3, alpha);
        });
    }

    public static void doParticles(PARTICLES_SPRITE type, float alpha ){
        GuiEventManager.triggerWithParams(GuiEventType.SET_PARTICLES_ALPHA, type, alpha);
    }
    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
