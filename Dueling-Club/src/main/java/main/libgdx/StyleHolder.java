package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.system.graphics.ColorManager;

import java.util.HashMap;
import java.util.Map;

public class StyleHolder {
    private static Label.LabelStyle defaultLabelStyle;
    private static Label.LabelStyle avqLabelStyle;
    private static Color defaultColor = new Color(ColorManager.GOLDEN_WHITE.getRGB());

    private static TextButton.TextButtonStyle defaultTextButtonStyle;

    private static Map<Color, Label.LabelStyle> colorLabelStyleMap = new HashMap<>();

    public static Label.LabelStyle getDefaultLabelStyle(Color color) {
        if (!colorLabelStyleMap.containsKey(color)) {
            Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), color);
            style.font.getData().markupEnabled = true;
            colorLabelStyleMap.put(color, style);
        }
        return colorLabelStyleMap.get(color);
    }

    public static Label.LabelStyle getDefaultLabelStyle() {
        return getDefaultLabelStyle(defaultColor);
    }

    public static Label.LabelStyle getAVQLabelStyle() {
        if (avqLabelStyle == null) {
            avqLabelStyle = new Label.LabelStyle(new BitmapFont(
//             new FileHandle(
//              PathFinder.getFontPath()+ FONT.AVQ.path
//             )
            ),
                    defaultColor);
        }
        return avqLabelStyle;
    }

    public static TextButton.TextButtonStyle getTextButtonStyle() {
        if (defaultTextButtonStyle == null) {
            defaultTextButtonStyle = new TextButton.TextButtonStyle();
            defaultTextButtonStyle.font = new BitmapFont();
            defaultTextButtonStyle.fontColor = defaultColor;
            defaultTextButtonStyle.overFontColor = new Color(defaultColor).add(50, 50, 50, 0);
            defaultTextButtonStyle.checkedFontColor = new Color(0xFF_00_00_FF);
        }

        return defaultTextButtonStyle;
    }
}
