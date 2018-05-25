package eidolons.libgdx.bf.mouse;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by JustMe on 5/24/2018.
 */
public class GlobalInputController implements InputProcessor {
    private static InputProcessor instance;

    public static InputProcessor getInstance() {
        if (instance==null )
            instance = new GlobalInputController();
        return instance;
    }

    public static void setInstance(InputProcessor instance) {
        GlobalInputController.instance = instance;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
