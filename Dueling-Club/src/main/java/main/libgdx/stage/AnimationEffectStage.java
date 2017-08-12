package main.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import main.game.core.game.DC_Game;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.particles.ParticleManager;
import main.libgdx.anims.phased.PhaseAnimator;

public class AnimationEffectStage extends Stage {
    private   ParticleManager particleManager;

    public AnimationEffectStage() {
        particleManager = new ParticleManager(this);
        addActor(particleManager);
        new PhaseAnimator(this);
    }

    @Override
    public void draw() {
        super.draw();
        if (AnimMaster.isOn()) {
//            phaseAnimsStage.draw();
//            animsStage.draw();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (DC_Game.game != null) {
            if (DC_Game.game.getAnimationManager() != null) {
                DC_Game.game.getAnimationManager().updateAnimations();
            }
        }
    }
}
