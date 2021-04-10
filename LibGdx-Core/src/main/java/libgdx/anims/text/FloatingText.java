package libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.content.consts.VisualEnums;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMaster;
import libgdx.anims.actions.WaitAction;
import libgdx.bf.GridMaster;
import libgdx.gui.LabelX;
import libgdx.gui.generic.GroupX;
import libgdx.texture.TextureCache;
import main.game.bf.Coordinates;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/20/2017.
 */
public class FloatingText extends GroupX {

    private static final boolean DEBUG_MODE = false;
    private static final Integer DEFAULT_FONT_SIZE = 18;
    private static final float DEFAULT_TEXT_Y = -20;
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
    private LabelStyle fontStyle;
    private Image image;
    private LabelX label;
    Coordinates coordinates;
    private float stayFullDuration;
    private float fadeInDuration = 0;
    private float offsetX;
    private float offsetY;
    private Predicate stayFullCondition;
    private VisualEnums.TEXT_CASES aCase;
    private float maxWidth = 700;

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
        if (DEBUG_MODE) {
            debug();
//            main.system.auxiliary.log.LogMaster.log(1, getText() + " at " + getX() + " " + getY()+" color ="+getColor());
        }
//        new SearchMaster<MoveByAction>().findInstanceOf(MoveByAction.class, getActions());
        if (inverseAlpha) {
            parentAlpha = 1 - parentAlpha;
        }
        label.setX(offsetX);
        label.setY(offsetY + DEFAULT_TEXT_Y);
        if (image != null) {
            image.setX(offsetX);
            image.setY(offsetY);
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getText() + "; delay: " + delay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FloatingText) {
            if (((FloatingText) obj).getText().equals(getText())) {
                if (((FloatingText) obj).getDelay() == getDelay()) {
                    if (((FloatingText) obj).getDuration() == getDuration()) {
                        return true;
                    }
                }
            }
        }
        return super.equals(obj);
    }

    public FloatingText init() {
        Vector2 v = new Vector2(getX(), getY());
        if (coordinates != null) {
            v = GridMaster.getCenteredPos(coordinates);
        }
        return
                init(v, displacementX, displacementY, getDuration());
    }

    public FloatingText init(Vector2 origin, float x, float y, float duration) {
        SequenceAction alphaActionSequence = new SequenceAction();
        if (stayFullCondition != null) {
            alphaActionSequence.addAction(new WaitAction(stayFullCondition));
        }
        if (stayFullDuration != 0) {
            alphaActionSequence.addAction(new WaitAction(stayFullDuration));
        }
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

        Action parallelAction;
        if (delay != 0) {
            parallelAction = new DelayAction();
            ((DelayAction) parallelAction).setTime(delay);
            ((DelayAction) parallelAction).setAction(new ParallelAction(alphaActionSequence, moveByAction));
        } else {
            parallelAction = new ParallelAction(alphaActionSequence, moveByAction);
        }

        clear();
        if (image == null)
            if (imageSupplier != null) {
                if (!StringMaster.isEmpty(imageSupplier.get())) {
                    if (!ImageManager.isImage(imageSupplier.get())
                            ||
                            ImageManager.getImage(imageSupplier.get()).getWidth(null) >= 64)
                        image = new Image
                                (TextureCache.getOrCreateR(VISUALS.QUESTION.getImgPath()));

                    image = new Image(TextureCache.getOrCreateR(imageSupplier.get()));

//            image.setPosition(origin.x, origin.y);
                }
            }
        if (image != null)
            addActor(image);
        if (label == null) {
            label =
                    new LabelX(getText(), getFontStyle());
            label.setMaxWidth(getMaxWidth());
            if (label.getText().length > 20) {
                label.setZigZagLines(true);
            }
            label.setText(getText());
            label.setColor(c);
            label.setPosition(0, DEFAULT_TEXT_Y);
            addActor(label);
        }
        if (label != null)
            addActor(label);

        setPosition(origin.x, origin.y);

        getActions().clear();
        addAction(parallelAction);
        addAction(afterAction);
        parallelAction.setTarget(this);

//        if (!ActorMaster.getActionsOfClass(this, AfterAction.class).isEmpty()) {
//            remove();
//        } else
        afterAction.setTarget(this);
        setInitialized(true);
        return this;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public LabelX getLabel() {
        return label;
    }

    public String getText() {
        if (textSupplier != null) {
            if (textSupplier.get() != null)
                text = textSupplier.get();
        }
        return text;
    }

    public void setStayFullDuration(float stayFullDuration) {
        this.stayFullDuration = stayFullDuration;
    }

    public void setStayFullCondition(Predicate stayFullCondition) {
        this.stayFullCondition = stayFullCondition;
    }

    public void setText(String text) {
        this.text = text;
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

    public LabelStyle getFontStyle() {
        if (fontStyle == null)
            return StyleHolder.getSizedLabelStyle(
                    StyleHolder.DEFAULT_FONT_FLOAT_TEXT, StyleHolder.DEFAULT_FONT_SIZE_FLOAT_TEXT);
        return fontStyle;
    }

    public void setFontStyle(LabelStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setAlphaLoops(int alphaLoops) {
        this.alphaLoops = alphaLoops;
    }

    public void setInverseAlpha(boolean inverseAlpha) {
        this.inverseAlpha = inverseAlpha;
    }

    public void setC(Color c) {
        this.c = c;
    }

    public void setTextSupplier(Supplier<String> textSupplier) {
        this.textSupplier = textSupplier;
    }

    public void setImageSupplier(Supplier<String> imageSupplier) {
        this.imageSupplier = imageSupplier;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void added() {
        if (fadeInDuration != 0) {
            ActionMaster.addFadeInAction(this, fadeInDuration);
        }
    }

    public void setFadeInDuration(float fadeInDuration) {
        this.fadeInDuration = fadeInDuration;
    }

    @Override
    public float getFadeInDuration() {
        return fadeInDuration;
    }

    @Override
    protected float getFadeOutDuration() {
        return fadeInDuration;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public void setCase(VisualEnums.TEXT_CASES aCase) {
        this.aCase = aCase;
    }

    public VisualEnums.TEXT_CASES getCase() {
        return aCase;
    }
}
