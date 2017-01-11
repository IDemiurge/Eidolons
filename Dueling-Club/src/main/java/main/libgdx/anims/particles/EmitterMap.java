package main.libgdx.anims.particles;

import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap {

    List<ParticleActor> emitters;

    List<ParticleActor> animationFx;
    List<ParticleActor> ambientFx;

    Stack<List<ParticleActor>> fxStack;

public EmitterMap(){

    GuiEventManager.bind(GuiEventType.EMITTER_ANIM_CREATED, p -> {
        ParticleAnimation
        anim= new ParticleAnimation();
        animationFx.add(anim);

    });
}
    public boolean contains(ParticleActor actor) {
        return emitters.contains(actor);
    }

    public void update() {
        addSmoke();
    }

    public void updateAnimFx() {
    animationFx.forEach(fx->{
        if (fx.isRunning())
            fx.start();
        fx.updatePosition(2);

    });
    }

    private void addSmoke() {
        Ambience smoke = new Ambience(PARTICLE_EFFECTS.SMOKE_TEST);
//    smoke.getEffect().setPosition(x, y);
        smoke.getEffect().start();
    }

    public List<ParticleActor> getEmitters() {
        return emitters;
    }

    public List<ParticleActor> getAnimationFx() {
        return animationFx;
    }

    public List<ParticleActor> getAmbientFx() {
        return ambientFx;
    }

    public Stack<List<ParticleActor>> getFxStack() {
        return fxStack;
    }
}
