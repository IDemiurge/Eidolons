package eidolons.libgdx.bf.mouse;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveMaster;

/**
 * Created by JustMe on 5/24/2018.
 */
public class GlobalInputController implements InputProcessor {
    private static InputProcessor instance;

    public static InputProcessor getInstance() {
        if (instance == null)
            instance = new GlobalInputController();
        return instance;
    }

    public static void setInstance(InputProcessor instance) {
        GlobalInputController.instance = instance;
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Keys.F3:
                WeaveMaster.openWeave();
                return true;
            case Keys.F4:
                if (Eidolons.getScope() == SCOPE.MENU)
                    return false;

                Eidolons.exitToMenu();
                return true;
        }
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
