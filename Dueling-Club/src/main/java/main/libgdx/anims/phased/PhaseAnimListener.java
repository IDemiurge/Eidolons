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
//        TODO
    }
//    private boolean checkAnimationPageFlipped(MouseWheelEvent e) {
//        for (PhaseAnimation anim : gridComp.getGame().getAnimationManager().getAnimations()) {
//            if (anim.contains(e.getPoint())) {
//                if (anim.isWheelSupported()) {
//                    if (anim.isManualFlippingSupported()) {
//                        boolean forward = e.getWheelRotation() < 0;
//                        anim.pageFlipped(forward);
//                        return true;
//                    }
//                }
//            }
//        }
//
//        return false;
//    }

    //    public boolean checkAnimationClick(MouseEvent e) {
//        point = e.getPoint();
//        DequeImpl<PhaseAnimation> animations = new DequeImpl<>(gridComp.getGame().getAnimationManager()
//         .getAnimations());
//        animations.addAll(gridComp.getGame().getAnimationManager().getTempAnims());
//        if (SwingUtilities.isRightMouseButton(e)) {
//
//            for (PhaseAnimation anim : animations) {
//                if (checkToggleTooltip(anim, e))
//                    return true;
//
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
//                        }
//                }
//            }
//        }
//        for (PhaseAnimation anim : animations) {
//            if (anim.getMouseMap() != null)
//
//                for (Rectangle rect : anim.getMouseMap().keySet()) {
//                    if (rect.contains(point)) {
//                        MouseItem item = anim.getMouseMap().get(rect);
//                        return itemClicked(item, anim);
//                    }
//                }
//        }
//
//        return false;
//    }
//
    public boolean checkClick(float x, float y, int button) {
//        for ()
        
        return false;




























    }
}
