package main.libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;

/**
 * Created by JustMe on 1/20/2017.
 */
public class FloatingText {

    private Label label;

    public FloatingText(String text, Color c) {
        label =
         new Label(text, StyleHolder.getDefaultLabelStyle()){
             @Override
             public void draw(Batch batch, float parentAlpha) {
act(Gdx.graphics.getDeltaTime());                 super.draw(batch, parentAlpha);
             }
         };
        label.setColor(c);
    }

    public void
    init(Stage stage, Vector2 origin, float x, float y, float duration) {
     label.setPosition(origin.x,origin.y);
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

        stage.addActor(label);
        label.addAction(parallelAction);
        label.addAction(afterAction);
        parallelAction .setTarget(label);
        afterAction .setTarget(label);

    }


}
