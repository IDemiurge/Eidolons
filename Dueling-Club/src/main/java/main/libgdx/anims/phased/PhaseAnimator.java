package main.libgdx.anims.phased;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.system.GuiEventManager;

import java.util.LinkedList;
import java.util.List;

import static main.system.GuiEventType.SHOW_PHASE_ANIM;

/**
 * Created by JustMe on 1/5/2017.
 */
public class PhaseAnimator extends Group {
    private static PhaseAnimator instance;
    List<PhaseAnim> anims = new LinkedList<>();



    public void init() {
        GuiEventManager.bind(SHOW_PHASE_ANIM, (event) -> {

        });
//        DC_Game.game.getAnimationManager().drawAnimations();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void show() {
        clear();
        clearChildren();
        anims.forEach(anim -> {
            anim.update();
            addActor(anim);
//    anim.getAnim().getMouseMap()
        });
//            sprite = new TextureRegion(j2dTex);
        setVisible(true);
    }
    public List<PhaseAnim> getAnims() {
        return anims;
    }

    public void setAnims(List<PhaseAnim> anims) {
        this.anims = anims;
    }
    public static PhaseAnimator getInstance() {
        if (instance == null)
            instance = new PhaseAnimator();
        return
         instance;
    }
}
