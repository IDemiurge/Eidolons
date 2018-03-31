package eidolons.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by JustMe on 11/25/2017.
 */
public interface Closable {

    default void close() {
        ((Actor) this).setVisible(false);
    }

    default void open() {
        ((StageWithClosable) ((Actor) this).getStage()).closeDisplayed();
        ((StageWithClosable) ((Actor) this).getStage()).setDisplayedClosable(this);
        ((Actor) this).setVisible(true);
    }

}
