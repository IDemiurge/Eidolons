package main.libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.texture.TextureManager;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/20/2017.
 */
public class FloatingText extends Group {

    private String text;
    private Color c;
    private Supplier<String> textSupplier;
    private Supplier<String> imageSupplier;
int alphaLoops=1;
boolean inverseAlpha;
    float   displacementX;
    float   displacementY;
    private float   duration;
    private float delay;

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

        if (inverseAlpha)
            parentAlpha=1-parentAlpha;
        super.draw(batch, parentAlpha);
    }

    public void addToStage(Stage animsStage) {
        addToStage(animsStage, new Vector2(getX(), getY()));
    }
    public void addToStage(Stage animsStage, Vector2 vector2) {
        init(animsStage, vector2, displacementX, displacementY, getDuration());
    }
    public FloatingText
    init(Stage stage, Vector2 origin, float x, float y, float duration) {
        SequenceAction alphaActionSequence = new SequenceAction();
        for (int i = alphaLoops; i>0;i--){
            AlphaAction fadeOutAction = new AlphaAction();
            fadeOutAction.setAlpha(  0.0f);
            fadeOutAction.setDuration(duration/alphaLoops);
            alphaActionSequence.addAction(fadeOutAction);
            if (alphaLoops==1) break;
            i--; // twice
            AlphaAction fadeInAction = new AlphaAction();
            fadeOutAction.setAlpha(  1);
            fadeOutAction.setDuration(duration/alphaLoops);
            alphaActionSequence.addAction(fadeInAction);
        }
        MoveByAction moveByAction = new MoveByAction();
        moveByAction.setAmount(x, y);
        moveByAction.setDuration(duration);

        RemoveActorAction removeAction = new RemoveActorAction();
        AfterAction afterAction = new AfterAction();
        afterAction.setAction(removeAction);

         Action parallelAction = null ;
if (delay!=0){
    parallelAction=new DelayAction();
    ((DelayAction)parallelAction).setTime(delay);
    ((DelayAction)parallelAction).setAction(new ParallelAction(alphaActionSequence, moveByAction));
}else
    parallelAction=new ParallelAction(alphaActionSequence, moveByAction);

        if (imageSupplier != null) {
            Image image = new Image(TextureManager.getOrCreate(imageSupplier.get()));
            addActor(image);
//            image.setPosition(origin.x, origin.y);
        }
        if (textSupplier != null) {
            text = textSupplier.get();
        }
        Label label =
                new Label(text, StyleHolder.getDefaultLabelStyle());
        label.setColor(c);
        addActor(label);

        setPosition(origin.x, origin.y);
        stage.addActor(this);
        addAction(parallelAction);
        addAction(afterAction);
        parallelAction.setTarget(this);
        afterAction.setTarget(this);

        return this;
    }

    public void setDisplacementX(float displacementX) {
        this.displacementX = displacementX;
    }

    public void setDisplacementY(float displacementY) {
        this.displacementY = displacementY;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getDuration() {
        return duration;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public float getDelay() {
        return delay;
    }

    public void setPosition(Vector2 origin) {
        setPosition(origin.x, origin.y);
    }

}
