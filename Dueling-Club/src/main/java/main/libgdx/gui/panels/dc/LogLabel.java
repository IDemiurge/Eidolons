package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LogLabel extends Label {
    public LogLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }

    public LogLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public LogLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    public LogLabel(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }

    public LogLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        getParent().setSize(getWidth(), getHeight());
    }
}
