package main.libgdx.anims.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;

/**
 * Created by JustMe on 1/20/2017.
 */
public class FloatingText {

    private Label label;
    private float duration, x, y;

    public FloatingText(String text, Color c) {
        label =
         new Label(text, StyleHolder.getDefaultLabelStyle());
        label.setColor(c);
    }

    public void
    init(Stage stage) {
        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setAlpha(0);
        alphaAction.setDuration(duration);

        MoveByAction moveByAction = new MoveByAction();
        moveByAction.setAmount(x, y);
        moveByAction.setDuration(duration);

        RemoveAction removeAction = new RemoveAction();
        AfterAction afterAction = new AfterAction();
        afterAction.setAction(removeAction);

        label.addAction(new ParallelAction(alphaAction, moveByAction));
        label.addAction(afterAction);

        stage.addActor(label);
    }


}
