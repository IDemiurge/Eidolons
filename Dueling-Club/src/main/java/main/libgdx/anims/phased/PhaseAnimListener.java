package main.libgdx.anims.phased;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.system.graphics.PhaseAnimation;

/**
 * Created by JustMe on 1/17/2017.
 */
public class PhaseAnimListener extends ClickListener {


    private final PhaseAnim actor;
    private final PhaseAnimation anim;

    public PhaseAnimListener(PhaseAnim phaseAnim) {
        this.actor =phaseAnim;
        this.anim =phaseAnim.getAnim();
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
       anim.setThumbnail(! anim.isThumbnail());
       actor.dirty=true;
//       anim.getMouseMap() TODO
    }
}
