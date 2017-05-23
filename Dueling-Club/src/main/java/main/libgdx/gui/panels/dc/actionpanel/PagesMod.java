package main.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.Input;

public enum PagesMod {
    NONE(0),
    ALT(Input.Keys.ALT_LEFT),
    SHIFT(Input.Keys.SHIFT_LEFT),
    CTRL(Input.Keys.CONTROL_LEFT);
    private static PagesMod[] values;

    static {
        values = values();
    }

    private int keyCode;

    PagesMod(int keyCode) {
        this.keyCode = keyCode;
    }

    public static PagesMod[] getValues() {
        return values;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
