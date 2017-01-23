package main.libgdx.anims.phased;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.game.battlefield.PointX;
import main.libgdx.GameScreen;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AnimationManager.MouseItem;
import main.system.graphics.PhaseAnimation;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;

/**
 * Created by JustMe on 1/17/2017.
 */
public class PhaseAnimListener extends InputListener {


    private final PhaseAnim actor;
    private final PhaseAnimation anim;

    public PhaseAnimListener(PhaseAnim phaseAnim) {
        this.actor = phaseAnim;
        this.anim = phaseAnim.getAnim();
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        if (anim.contains(new PointX(x, y))) {
            if (anim.isWheelSupported()) {
                if (anim.isManualFlippingSupported()) {
                    boolean forward = amount < 0;
                    anim.pageFlipped(forward);
                    return true;
                }
            }
        }
        return false;
    }


//                if (anim.contains(e.getPoint())) {
//                    if (e.getClickCount() > 1) {
//
//                        if (anim.isPaused())
//                            anim.resume();
//                        else
//                            anim.pause();
//                        return true;
//                    }
//                    AnimPhase phase = anim.getPhase();
//                    if (phase != null)
//                        if (anim.subPhaseClosed()) {
//                            SoundMaster.playStandardSound(STD_SOUNDS.BACK);
//                            return true;


    public boolean checkClick(float x, float y, int button) {
        for (Rectangle rect : anim.getMouseMap().keySet()) {
            if (rect.contains(x, y)) {
                MouseItem item = anim.getMouseMap().get(rect);
                return itemClicked(item);
            }
        }
        Vector2 v = GameScreen.getInstance().getGridPanel().
                getVectorForCoordinateWithOffset(anim.getSourceCoordinates());
        return new Rectangle(
                (int) v.x, (int) v.y, anim.getW(), anim.getH()).contains(x, y);

    }

    private boolean itemClicked(MouseItem item) {

        if (item.getType() != null)
            switch (item.getType()) {

                case THUMBNAIL:
                    anim.toggleThumbnail();
                    break;
                case TOOLTIP:
//                        displayTooltip(anim, item);
                    break;
                case SUB_PHASE:
                    if (anim.getPhase().getType().isSubPhase())
                        return false;
                    if (item.getArg() == null) {
                        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
                        return true;
                    }
                    anim.subPhaseOpened(anim.getPhase((PHASE_TYPE) item.getArg()));
                    SoundMaster.playStandardSound(STD_SOUNDS.DIS__OPEN_MENU);
                    return true;
                case CONTROL_BACK:
                    anim.pageFlipped(false);
                    return true;
                case CONTROL_FORWARD:
                    anim.pageFlipped(true);
                    return true;

            }
        return false;


    }
}
