package main.test.libgdx.sfx;

import com.badlogic.gdx.tools.particleeditor.ParticleEditor;
import main.libgdx.anims.particles.ParticleEffect;
import main.system.auxiliary.secondary.ReflectionMaster;

/**
 * Created by JustMe on 2/4/2017.
 */
public class MyParticleEditor extends ParticleEditor {
    private static MyParticleEditor instance;

    public MyParticleEditor() {
        super();
        new ReflectionMaster().setValue("effect", new ParticleEffect(), this);
        instance = this;
    }

    public static void main(String[] args) {
        ParticleEditor.main(args);

//        EffectPanel myEffectPanel = new EffectPanel(instance){
//
//        };
//        emittersPanel.add(effectPanel);
//
//        new ReflectionMaster().setValue("effect", new ParticleEffect() , this);
    }
}
