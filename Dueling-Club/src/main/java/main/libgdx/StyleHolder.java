package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.system.graphics.ColorManager;

import java.util.HashMap;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class StyleHolder {
    private static final String DISABLED = "_disabled";
    private static final String OVER = "_over";
    private static final String DOWN = "_down";
    private static final String UP = "_up";
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

    public static Button.ButtonStyle getCustomButtonStyle(String baseImagePath) {
        final int jpgEnd = baseImagePath.indexOf(".jpg");
        final int pngEnd = baseImagePath.indexOf(".png");
        String endString = null;
        String baseString = null;
        if (jpgEnd > 0) {
            endString = ".jpg";
            baseString = baseImagePath.replace(endString, "");
        }
        if (pngEnd > 0) {
            endString = ".png";
            baseString = baseImagePath.replace(endString, "");
        }

        final String disabledPath = baseString + DISABLED + endString;
        final String overPath = baseString + OVER + endString;
        final String downPath = baseString + DOWN + endString;
        final String upPath = baseString + UP + endString;

        final TextureRegion disabledTexture = getOrCreateR(disabledPath);
        final TextureRegion overTexture = getOrCreateR(overPath);
        final TextureRegion downTexture = getOrCreateR(downPath);
        final TextureRegion upTexture = getOrCreateR(upPath);

        Button.ButtonStyle style = new Button.ButtonStyle();
        style.disabled = new TextureRegionDrawable(disabledTexture);
        style.over = new TextureRegionDrawable(overTexture);
        style.down = new TextureRegionDrawable(downTexture);
        style.up = new TextureRegionDrawable(upTexture);

        return style;
    }
}
