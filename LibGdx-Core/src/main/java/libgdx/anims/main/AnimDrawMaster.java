package libgdx.anims.main;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import libgdx.anims.Animation;
import libgdx.anims.CompositeAnim;
import libgdx.anims.sprite.FadeSprite;
import libgdx.screens.CustomSpriteBatch;
import main.game.logic.event.Event;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static libgdx.anims.main.AnimMaster.isOn;

/**
 * Created by JustMe on 11/15/2018.
 */
public class AnimDrawMaster extends Group {

    DequeImpl<CompositeAnim> leadQueue = new DequeImpl<>(); //if more Action Stacks have been created before leadAnimation is finished
    CompositeAnim leadAnimation; // wait for it to finish before popping more from the queue
    DequeImpl<Animation> attachedAnims = new DequeImpl<>();
    private FadeSprite fadeTest;
    private boolean drawing;
    private boolean drawingPlayer;

    public AnimDrawMaster(AnimMaster animMaster) {
    }


    public Boolean getParallelDrawing() {
        return true; //don't turn that off.... unless you want chaos and madness
    }

    public void setParallelDrawing(Boolean parallelDrawing) {
    }

    public boolean isDrawing() {
        return drawing;
    }

    private CompositeAnim next() {
        if (leadQueue.isEmpty()) {
            leadAnimation = null;
            if (drawing) {
                drawing = false;
                WaitMaster.receiveInput(WAIT_OPERATIONS.ANIMATION_QUEUE_FINISHED, true);
                //                GuiEventManager.trigger(GuiEventType.ANIMATION_QUEUE_FINISHED);
            }
            drawingPlayer = false;
            return null;
        }

        //TODO Stack: counter atk will animated first - last in first out :(
        CompositeAnim firstAnim = leadQueue.peekFirst();
        if (firstAnim != null)
            if (firstAnim.isWaitingForNext()) {
                return null;
            }
        leadAnimation = leadQueue.removeFirst();

        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ANIM_DEBUG, "next animation: " + leadAnimation +
                "; " +
                leadQueue.size() +
                " in Queue= " + leadQueue);
        //        leadAnimation.resetRef();
        return leadAnimation;
    }

    protected void add(CompositeAnim anim) {
        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ANIM_DEBUG, "ANIMATION ADDED   " + anim);
        if (anim == leadAnimation) {
            return;
        }
        if (leadQueue.contains(anim)) {
            return;
        }
        CompositeAnim firstAnim = leadQueue.peekFirst();
        if (firstAnim == null)
            leadQueue.add(anim);
        else if (firstAnim.isWaitingForNext()) {
            firstAnim.setWaitingForNext(false);
            leadQueue.addFirst(anim);
        } else
            leadQueue.add(anim);
    }

    private void startNext() {
        leadAnimation = next();
        if (leadAnimation != null) {
            leadAnimation.start();
        }
    }


    public void addAttached(Animation anim) {
        attachedAnims.add(anim);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawAnims(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
    }

    public void drawAnims(Batch batch, float parentAlpha) {
        if (!isOn()) {
            return;
        }
//        continuousAnims.values().forEach(a -> {
//            if (!a.getBuff().isDead()) {
//                if (a.isRunning()) {
//                    a.draw(batch);
//                }
//            }
//        });
        attachedAnims.removeIf((Animation a) -> !a.isRunning());
        DequeImpl<Animation> animations = attachedAnims;
        for (int i = 0, animationsSize = animations.size(); i < animationsSize; i++) {
            Animation attachedAnim = animations.get(i);
            attachedAnim.tryDraw(batch);
        }

        if (leadAnimation == null) {
            startNext();
        }

        if (leadAnimation != null) {
            drawing = true;
            boolean result = tryDrawAnimation(batch, leadAnimation);

            if (!result) {
                startNext();
            }
        }
        // not turned on
        if (getParallelDrawing()) {
            DequeImpl<CompositeAnim> compositeAnims =  (leadQueue);
            for (int i = 0, compositeAnimsSize = compositeAnims.size(); i < compositeAnimsSize; i++) {
                CompositeAnim a = compositeAnims.get(i);
                tryDrawAnimation(batch, a);
            }

            leadQueue.removeIf(CompositeAnim::isFinished);
        }

        super.draw(batch, parentAlpha);

    }

    private boolean tryDrawAnimation(Batch batch, CompositeAnim anim) {
        boolean result = false;
        if (ExplorationMaster.isExplorationOn())
            if (anim.getActive_() != null)
                if (anim.getActive_().getOwnerObj().isMine())
                    if (anim.getActive_().getOwnerObj().isMainHero())
                        drawingPlayer = true;

        try {
//         TODO    if (!anim.isRunning()) {
//                if (!anim.isFinished()) {
//                    anim.start();
//                }
//            }
            result = anim.tryDraw(batch);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            anim.finished();
        }
        if (ExplorationMaster.isExplorationOn())
            if (anim.getActive_() != null)
                if (anim.getActive_().getOwnerObj().isMine())
                    if (anim.getActive_().getOwnerObj().isMainHero())
                        drawingPlayer = result;

        return result;
    }

    public void onDone(Event event, EventCallback callback, EventCallbackParam param) {
        AnimMaster.getParentAnim(event.getRef()).onDone(callback, param);
    }

    public boolean isDrawingPlayer() {
        return drawingPlayer;
    }

    public void setDrawingPlayer(boolean drawingPlayer) {
        this.drawingPlayer = drawingPlayer;
    }

    public void cleanUp() {
        if (leadAnimation != null)
            leadAnimation.finished();
        leadQueue.forEach(CompositeAnim::finished);
        leadAnimation = null;
        leadQueue.clear();
    }

    public void interrupt() {
        leadAnimation.interrupt();
    }

    public CompositeAnim getLeadAnimation() {
        return leadAnimation;
    }

    public Animation findAnimation(DC_ActiveObj action) {
        if (leadAnimation != null)
            if (leadAnimation.getActive() == action) {
                return leadAnimation;
            }
        for (CompositeAnim anim : leadQueue) {
            if (anim.getActive() == action) {
                return anim;
            }
        }
        return null;
    }
}

//        if (FADE_SPRITE_TEST) {
//            try {
//                Coordinates c = Eidolons.getPlayerCoordinates();
//                SpriteAnimation animation = SpriteAnimationFactory.getSpriteAnimation("sprite shadow.png");
//                addActor(fadeTest = new FadeSprite(animation));
//                fadeTest.setBlending(BLENDING.SCREEN);
//                Vector2 v = GridMaster.getCenteredPos(c);
//                fadeTest.setPosition(v.x, v.y);
//
//                c = c.getOffsetByX(1);
//                animation = SpriteAnimationFactory.getSpriteAnimation("sprite dark.png");
//                addActor(fadeTest = new FadeSprite(animation));
//                fadeTest.setBlending(BLENDING.SCREEN);
//                v = GridMaster.getCenteredPos(c);
//                fadeTest.setPosition(v.x, v.y);
//            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
//            }
//        }