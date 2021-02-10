package libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface OverlayPanel {
    boolean keyDown(int keyCode);

    boolean keyTyped(char character);

    boolean keyUp(int keyCode);

    default void show() {
        if (this instanceof Actor) {
            if (((Actor) this).getStage() instanceof StageWithClosable) {
                ((StageWithClosable) ((Actor) this).getStage()).setOverlayPanel(this);
            }
        }
    }
    default void hide() {
        if (this instanceof Actor) {
            if (((Actor) this).getStage() instanceof StageWithClosable) {
                ((StageWithClosable) ((Actor) this).getStage()).setOverlayPanel(null  );
            }
        }
    }
}
