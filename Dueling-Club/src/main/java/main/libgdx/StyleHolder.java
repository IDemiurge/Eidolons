package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class StyleHolder {
    private static Label.LabelStyle defaultLabelStyle;

    public static Label.LabelStyle getDefaultLabelStyle() {
        if (defaultLabelStyle == null) {
            defaultLabelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        }

        return defaultLabelStyle;
    }
}
