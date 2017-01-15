package main.libgdx.anims.particles;

import main.content.CONTENT_CONSTS2.SFX;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap {

    List<ParticleInterface> emitters;

    List<ParticleInterface> animationFx;
    List<ParticleInterface> ambientFx;

    Stack<List<ParticleInterface>> fxStack;

public EmitterMap(){

    GuiEventManager.bind(GuiEventType.EMITTER_ANIM_CREATED, p -> {
        ParticleAnimation
                anim = new ParticleAnimation();
        animationFx.add(anim);

    });
}

    public boolean contains(ParticleInterface actor) {
        return emitters.contains(actor);
    }

    public void update() {
        addSmoke();
    }

    public void updateAnimFx() {
        animationFx.forEach(fx -> {
            if (fx.isRunning())
                fx.start();
            fx.updatePosition(2);

        });
    }

    private void addSmoke() {
        Ambience smoke = new Ambience(SFX.SMOKE_TEST);
//    smoke.getEffect().setPosition(x, y);
        smoke.getEffect().start();
    }

    public List<ParticleInterface> getEmitters() {
        return emitters;
    }

    public List<ParticleInterface> getAnimationFx() {
        return animationFx;
    }

    public List<ParticleInterface> getAmbientFx() {
        return ambientFx;
    }

    public Stack<List<ParticleInterface>> getFxStack() {
        return fxStack;
    }
}
