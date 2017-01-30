package main.libgdx.anims.particles.controls;

import main.libgdx.GameScreen;
import main.libgdx.anims.particles.Emitter;
import main.libgdx.anims.particles.EmitterActor;

/**
 * Created by JustMe on 1/27/2017.
 */
public class EmitterController {
    static Emitter last;

    public static void save() {
//        EmitterPresetMaster.save(last);
    }

    public static void create(String path) {
        EmitterActor actor = new EmitterActor(path);
        GameScreen.getInstance().getAnimsStage().addActor(actor);


    }

    public static void modify() {

    }
}
