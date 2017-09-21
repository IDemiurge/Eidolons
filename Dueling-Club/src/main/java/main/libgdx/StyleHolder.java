package main.libgdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.data.filesys.PathFinder;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster.FONT;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class StyleHolder {
    private static final String DISABLED = "_disabled";
    private static final String OVER = "_over";
    private static final String DOWN = "_down";
    private static final String UP = "_up";
    private static final String CHECKED = "_down";
    public static final FONT DEFAULT_FONT = FONT.MAIN;
    public static final FONT ALT_FONT = FONT.NYALA;
    private static final int DEFAULT_SIZE = 14;
    private static Label.LabelStyle defaultLabelStyle;
    private static Label.LabelStyle avqLabelStyle;
    private static final Color DEFAULT_COLOR = new Color(ColorManager.GOLDEN_WHITE.getRGB());
    private static TextButton.TextButtonStyle defaultTextButtonStyle;
    private static Map<Color, Label.LabelStyle> colorLabelStyleMap = new HashMap<>();
    private static Map<Integer, Label.LabelStyle> sizeLabelStyleMap = new HashMap<>();
    private static Map<Pair<Integer,Color>, Label.LabelStyle> sizeColorLabelStyleMap = new HashMap<>();

    public static Label.LabelStyle getStyledLabelStyle(Label.LabelStyle style, boolean italic, boolean bold) {
        //TODO
        return null;
    }
    public static Label.LabelStyle getSizedLabelStyle(FONT fontStyle, Integer size) {
        return getSizedColoredLabelStyle(fontStyle, size, DEFAULT_COLOR);
    }
        public static Label.LabelStyle getSizedColoredLabelStyle(FONT fontStyle, Integer size, Color color) {
            ImmutablePair<Integer, Color> pair = new ImmutablePair<>(size, color);
        if (!sizeColorLabelStyleMap.containsKey(pair)) {
            Label.LabelStyle style = new Label.LabelStyle
             (getFont(fontStyle, color, size ), color);
            style.font.getData().markupEnabled = true;
            sizeColorLabelStyleMap.put(pair, style);
        }
        return sizeColorLabelStyleMap.get(pair);
    }
    public static Label.LabelStyle getDefaultLabelStyle(Color color) {
        return getLabelStyle(DEFAULT_FONT, color);
    }

    public static Label.LabelStyle getAltLabelStyle(Color color) {
        return getLabelStyle(ALT_FONT, color);
    }
        public static Label.LabelStyle getLabelStyle(FONT font, Color color) {
        if (!colorLabelStyleMap.containsKey(color)) {
            Label.LabelStyle style = new Label.LabelStyle
             (getFont(font, DEFAULT_COLOR, DEFAULT_SIZE ), color);
            style.font.getData().markupEnabled = true;
            colorLabelStyleMap.put(color, style);
        }
        return colorLabelStyleMap.get(color);
    }
    private static BitmapFont getFont(FONT  font, Color color, int size) {
        return getFont(font.path, color, size);
    }

        private static BitmapFont getFont(String fontpath, Color color, int size) {
        final String path = PathFinder.getFontPath() +fontpath;

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
         new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;
        parameter.size = size;
        final BitmapFont bitmapFont = generator.generateFont(parameter);
        generator.dispose();
        return bitmapFont;
    }

    public static Label.LabelStyle getDefaultLabelStyle() {
        return getDefaultLabelStyle(DEFAULT_COLOR);
    }

    public static Label.LabelStyle getAVQLabelStyle() {
       return  getLabelStyle(FONT.AVQ, DEFAULT_COLOR);
//        if (avqLabelStyle == null) {
//            avqLabelStyle = new Label.LabelStyle(new BitmapFont(
//             new FileHandle(
//              PathFinder.getFontPath()+ FONT.AVQ.path
//             )
//            ),
//             DEFAULT_COLOR);
//        }
//        return avqLabelStyle;
    }

    public static TextButton.TextButtonStyle getDefaultTextButtonStyle() {
        return getTextButtonStyle(FONT.AVQ, DEFAULT_COLOR, 18 );
    }
        public static TextButton.TextButtonStyle getTextButtonStyle(
         FONT FONT, Color color, int size) {
        if (defaultTextButtonStyle == null) {
            defaultTextButtonStyle = new TextButton.TextButtonStyle();
            defaultTextButtonStyle.font =getFont(FONT, color, size);// new BitmapFont();
            defaultTextButtonStyle.fontColor = DEFAULT_COLOR;
            defaultTextButtonStyle.overFontColor = new Color(DEFAULT_COLOR).add(50, 50, 50, 0);
            defaultTextButtonStyle.checkedFontColor = new Color(0xFF_00_00_FF);
        }

        return defaultTextButtonStyle;
    }

    public static TextButton getMainMenuButton(String text) {
        final TextButton.TextButtonStyle customButtonStyle = StyleHolder.getCustomButtonStyle("UI/red_button.png");
        customButtonStyle.checkedFontColor = Color.WHITE;
        return new TextButton(text, customButtonStyle);
    }

    public static TextButton.TextButtonStyle getCustomButtonStyle(String baseImagePath) {
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
        final String checkedPath = baseString + CHECKED + endString;

        TextButton.TextButtonStyle style = getTextButtonStyle(DEFAULT_FONT, DEFAULT_COLOR, 18);

        File f = new File(disabledPath);
        boolean isExists = false;
        if (f.exists()) {
            isExists = true;
            style.disabled = new TextureRegionDrawable(getOrCreateR(disabledPath));
        }
        f = new File(overPath);
        if (f.exists()) {
            isExists = true;
            style.over = new TextureRegionDrawable(getOrCreateR(overPath));
        }
        f = new File(downPath);
        if (f.exists()) {
            isExists = true;
            style.down = new TextureRegionDrawable(getOrCreateR(downPath));
        }
        f = new File(upPath);
        if (f.exists()) {
            isExists = true;
            style.up = new TextureRegionDrawable(getOrCreateR(upPath));
        }
        f = new File(checkedPath);
        if (f.exists()) {
            isExists = true;
            style.checked = new TextureRegionDrawable(getOrCreateR(checkedPath));
        }

        if (!isExists) {
            style.up = new TextureRegionDrawable(getOrCreateR(baseImagePath));
        }

        return style;
    }
}
