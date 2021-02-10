package libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.graphics.OrthographicCamera;
import libgdx.bf.mouse.InputController;

/**
 * Created by JustMe on 6/28/2018.
 */
public class WeaveInputController extends InputController{
    public WeaveInputController(OrthographicCamera camera) {
        super(camera);
    }

    @Override
    protected WeaveScreen getScreen() {
        return WeaveScreen.getInstance();
    }

    @Override
    protected float getWidth() {
        return 16000;
    }

    @Override
    protected float getHeight() {
        return 15000;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
