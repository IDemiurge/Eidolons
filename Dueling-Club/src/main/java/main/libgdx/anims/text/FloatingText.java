package main.libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
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
        super.draw(batch, parentAlpha);
    }

    public FloatingText
    init(Stage stage, Vector2 origin, float x, float y, float duration) {
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(0.0f);
        alphaAction.setDuration(duration);

        MoveByAction moveByAction = new MoveByAction();
        moveByAction.setAmount(x, y);
        moveByAction.setDuration(duration);

        RemoveActorAction removeAction = new RemoveActorAction();
        AfterAction afterAction = new AfterAction();
        afterAction.setAction(removeAction);

        ParallelAction parallelAction = new ParallelAction(alphaAction, moveByAction);


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


}
