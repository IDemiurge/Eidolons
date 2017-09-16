package main.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Group;
import main.game.core.game.DC_Game;
import main.libgdx.anims.particles.ParticleManager;

public class AnimationEffectStage extends Group {
    private   ParticleManager particleManager;

    public AnimationEffectStage(){
        particleManager = new ParticleManager( );
        addActor(particleManager.getEmitterMap());
        particleManager.getEmitterMap().setX(300);
//        new PhaseAnimator(null );
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
