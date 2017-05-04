package main.libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/20/2017.
 */
public class FloatingText extends Group {

    int alphaLoops = 1;
    boolean inverseAlpha;
    float displacementX;
    float displacementY;
    private String text;
    private Color c;
    private Supplier<String> textSupplier;
    private Supplier<String> imageSupplier;
    private float duration;
    private float delay;
    private boolean initialized;

    public FloatingText(String text, Color c) {
        this.text = text;
        this.c = c;
    }

    public FloatingText(Supplier<String> textSupplier, Color c) {
        this(textSupplier.get(), c);
        this.textSupplier = textSupplier;
    }

    public FloatingText(Supplier<String> textSupplier, Supplier<String> imageSupplier, Color c) {
        this(textSupplier, c);
        this.imageSupplier = imageSupplier;
    }

    public void draw(Batch batch, float parentAlpha) {
        act(Gdx.graphics.getDeltaTime());

//        new SearchMaster<MoveByAction>().findInstanceOf(MoveByAction.class, getActions());
        if (inverseAlpha) {
            parentAlpha = 1 - parentAlpha;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getText() + "; delay: " + delay;
    }



    public FloatingText init() {
        return
        init(new Vector2(getX(), getY()), displacementX, displacementY, getDuration());
    }
    public FloatingText init( Vector2 origin, float x, float y, float duration) {
        SequenceAction alphaActionSequence = new SequenceAction();
        for (int i = alphaLoops; i > 0; i--) {
            AlphaAction fadeOutAction = new AlphaAction();
            fadeOutAction.setAlpha(0.0f);
            fadeOutAction.setDuration(duration / alphaLoops);
            alphaActionSequence.addAction(fadeOutAction);
            if (alphaLoops == 1) {
                break;
            }
            i--; // twice
            AlphaAction fadeInAction = new AlphaAction();
            fadeOutAction.setAlpha(1);
            fadeOutAction.setDuration(duration / alphaLoops);
            alphaActionSequence.addAction(fadeInAction);
        }
        MoveByAction moveByAction = new MoveByAction();
        moveByAction.setAmount(x, y);
        moveByAction.setDuration(duration);

        RemoveActorAction removeAction = new RemoveActorAction();
        AfterAction afterAction = new AfterAction();
        afterAction.setAction(removeAction);

        Action parallelAction = null;
        if (delay != 0) {
            parallelAction = new DelayAction();
            ((DelayAction) parallelAction).setTime(delay);
            ((DelayAction) parallelAction).setAction(new ParallelAction(alphaActionSequence, moveByAction));
        } else {
            parallelAction = new ParallelAction(alphaActionSequence, moveByAction);
        }


        if (imageSupplier != null) {
            if (!StringMaster.isEmpty(imageSupplier.get())) {
                Image image = new Image(TextureCache.getOrCreateR(imageSupplier.get()));
                addActor(image);
//            image.setPosition(origin.x, origin.y);
            }
        }

        Label label =
                new Label(getText(), StyleHolder.getDefaultLabelStyle());
        label.setColor(c);
        label.setPosition(0, -20);
        addActor(label);

        setPosition(origin.x, origin.y);
        addAction(parallelAction);
        addAction(afterAction);
        parallelAction.setTarget(this);
        afterAction.setTarget(this);
        setInitialized(true);
        return this;
    }

    public String getText() {
        if (textSupplier != null) {
            text = textSupplier.get();
        }
        return text;
    }

    public void setDisplacementX(float displacementX) {
        this.displacementX = displacementX;
        setInitialized(false);
    }

    public void setDisplacementY(float displacementY) {
        this.displacementY = displacementY;
        setInitialized(false);
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
        setInitialized(false);
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
        setInitialized(false);
    }

    public void setPosition(Vector2 origin) {
        setPosition(origin.x, origin.y);
        setInitialized(false);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
