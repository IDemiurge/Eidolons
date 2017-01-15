package main.libgdx.anims.particles;

import java.util.List;
import java.util.Stack;

/**
 * Created by JustMe on 1/9/2017.
 */
public class EmitterMap {
    // мапа статичных эмитеров
    List<ParticleActor> emitters;


    List<ParticleEmitter> ambientFx;

    Stack<List<ParticleActor>> fxStack;

public EmitterMap(){

}
    public boolean contains(ParticleActor actor) {
        return emitters.contains(actor);
    }

    public void update() {
        addSmoke();
    }



    private void addSmoke() {
        Ambience smoke = new Ambience(PARTICLE_EFFECTS.SMOKE_TEST);
//    smoke.getEffect().setPosition(x, y);
        smoke.getEffect().start();
    }

    public List<ParticleActor> getEmitters() {
        return emitters;
    }

    public List<ParticleEmitter> getAmbientFx() {
        return ambientFx;
    }

    public Stack<List<ParticleActor>> getFxStack() {
        return fxStack;
    }
}
