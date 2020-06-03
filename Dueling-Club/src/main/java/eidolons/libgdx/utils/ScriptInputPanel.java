package eidolons.libgdx.utils;

import com.badlogic.gdx.Input;

public class ScriptInputPanel extends TextInputPanel {
    public ScriptInputPanel(String title, String text, String hint, Input.TextInputListener textInputListener) {
        super(title, text, hint, textInputListener);
    }

    @Override
    public boolean isPausing() {
        return false;
    }
}
