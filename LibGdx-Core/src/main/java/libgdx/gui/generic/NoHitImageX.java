package libgdx.gui.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.bf.generic.ImageContainer;

public class NoHitImageX extends ImageContainer {
    public NoHitImageX(String path) {
        super(path);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }
}
