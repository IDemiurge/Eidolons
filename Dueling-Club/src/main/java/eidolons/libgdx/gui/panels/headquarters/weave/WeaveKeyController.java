package eidolons.libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import eidolons.game.core.EUtils;

/**
 * Created by JustMe on 7/1/2018.
 */
public class WeaveKeyController extends InputAdapter {

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:{
                EUtils.switchBackScreen();
                return true;
            }
        }
        return super.keyDown(keycode);
    }
}
