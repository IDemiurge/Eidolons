package eidolons.libgdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class StyleHolder {
    public static final FONT DEFAULT_FONT = FONT.MAIN;
    public static final FONT ALT_FONT = FONT.NYALA;
    public static final FONT DEFAULT_FONT_FLOAT_TEXT = FONT.MAIN;
    public static final int DEFAULT_FONT_SIZE_FLOAT_TEXT = 18;
    //    private static String FONT_CHARS = "";
//
//    static {
//
//        for (int i = 0x20; i < 0x7B; i++) FONT_CHARS += (char) i;
//        for (int i = 0x401; i < 0x452; i++) FONT_CHARS += (char) i;
//    }
    final static String FONT_CHARS = "абвгдежзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
    private static final String DISABLED = "_disabled";
    private static final String OVER = "_over";
    private static final String DOWN = "_down";
    private static final String UP = "_up";
    private static final String CHECKED = "_down";
    private static final int DEFAULT_SIZE = 14;
    private static final Color DEFAULT_COLOR = new Color(ColorManager.GOLDEN_WHITE.getRGB());
    static Map<STD_BUTTON, Map<LabelStyle, TextButtonStyle>> textButtonStyleMap = new HashMap<>();
    private static Label.LabelStyle defaultLabelStyle;
    private static Label.LabelStyle avqLabelStyle;
    private static TextButton.TextButtonStyle defaultTextButtonStyle;
    private static Map<FONT, Map<Color, Label.LabelStyle>> colorLabelStyleMap = new HashMap<>();
    private static Map<FONT, Map<Integer, Label.LabelStyle>> sizeLabelStyleMap = new HashMap<>();
    private static Map<FONT, Map<Pair<Integer, Color>, Label.LabelStyle>> sizeColorLabelStyleMap = new HashMap<>();
    private static TextButtonStyle defaultTabStyle;

    public static Label.LabelStyle getStyledLabelStyle(Label.LabelStyle style, boolean italic, boolean bold) {
        //TODO
        return null;
    }

    public static Label.LabelStyle getSizedLabelStyle(FONT fontStyle, Integer size) {
        return getSizedColoredLabelStyle(fontStyle, size, DEFAULT_COLOR);
    }

    public static Label.LabelStyle getSizedColoredLabelStyle(FONT fontStyle,
                                                             Integer size, Color color) {
        return getSizedColoredLabelStyle(0, fontStyle, size, color);
    }

    public static Label.LabelStyle getDebugLabelStyle() {
        return getSizedColoredLabelStyle(0.2f, FONT.MAIN, 15, GdxColorMaster.GOLDEN_WHITE);
    }

    public static Label.LabelStyle getSizedColoredLabelStyle(float adjustSizeCoef,
                                                             FONT fontStyle,
                                                             Integer size, Color color) {
        if (size>100)
            size = size/100;
        else //quick fix to enable static size
        if (adjustSizeCoef > 0)
            if (GdxMaster.getFontSizeMod() != 1)
            {
                int mod = Math.round(size * (GdxMaster.getFontSizeMod() - 1) * adjustSizeCoef);
                size += mod;
            }
        Map<Pair<Integer, Color>, LabelStyle> map = getSizedColoredLabelStyleMap(fontStyle, size, color);

        ImmutablePair<Integer, Color> pair = new ImmutablePair<>(size, color);

        if (!map.containsKey(pair)) {
            Label.LabelStyle style = new Label.LabelStyle
             (getFont(fontStyle, color, size), color);
            style.font.getData().markupEnabled = true;
            map.put(pair, style);
        }

        return map.get(pair);
    }

    private static Map<Pair<Integer, Color>, LabelStyle> getSizedColoredLabelStyleMap(FONT fontStyle, Integer size, Color color) {

        if (!sizeColorLabelStyleMap.containsKey(fontStyle)) {
            Map<Pair<Integer, Color>, LabelStyle> map = new HashMap<>();
            sizeColorLabelStyleMap.put(fontStyle, map);
            return map;
        }
        return sizeColorLabelStyleMap.get(fontStyle);
    }

    public static Label.LabelStyle getDefaultLabelStyle(Color color) {
        return getLabelStyle(DEFAULT_FONT, color);
    }

    public static Label.LabelStyle getAltLabelStyle(Color color) {
        return getLabelStyle(ALT_FONT, color);
    }

    public static Label.LabelStyle getLabelStyle(FONT font, Color color) {
        Map<Color, LabelStyle> map = getLabelStyleMap(font, color);
        if (!map.containsKey(color)) {
            Label.LabelStyle style = new Label.LabelStyle
             (getFont(font, DEFAULT_COLOR, getDefaultSize()), color);
            style.font.getData().markupEnabled = true;
            map.put(color, style);
        }
        return map.get(color);
    }

    private static Map<Color, LabelStyle> getLabelStyleMap(FONT font, Color color) {
        if (!colorLabelStyleMap.containsKey(font)) {
            Map<Color, LabelStyle> map = new HashMap<>();
            colorLabelStyleMap.put(font, map);
            return map;
        }
        return colorLabelStyleMap.get(font);
    }

    public static int getDefaultSize() {

        return Math.round(DEFAULT_SIZE * GdxMaster.getFontSizeMod());
    }

    private static BitmapFont getFont(FONT font, Color color, int size) {
        return getFont(font.path, color, size);
    }

    private static BitmapFont getFont(String fontpath, Color color, int size) {
        final String path = PathFinder.getFontPath() + fontpath;

        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
         new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = color;
        parameter.size = size;
        parameter.characters = FONT_CHARS;
        final BitmapFont bitmapFont = generator.generateFont(parameter);
        generator.dispose();
        return bitmapFont;
    }

    public static Label.LabelStyle getDefaultLabelStyle() {
        return getDefaultLabelStyle(DEFAULT_COLOR);
    }

    public static Label.LabelStyle getAVQLabelStyle() {
        return getLabelStyle(FONT.AVQ, DEFAULT_COLOR);
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
        if (defaultTextButtonStyle == null) {
            defaultTextButtonStyle = getTextButtonStyle(FONT.AVQ, DEFAULT_COLOR, 18);
        }
        return defaultTextButtonStyle;
    }

    public static TextButtonStyle getHqTextButtonStyle(STD_BUTTON button, int size) {
        return getTextButtonStyle(button, FONT.METAMORPH, DEFAULT_COLOR, size);
    }
    public static TextButton.TextButtonStyle getHqTextButtonStyle(
       int size) {
        return getTextButtonStyle(FONT.METAMORPH, DEFAULT_COLOR, size);
    }
        public static TextButton.TextButtonStyle getTextButtonStyle(
         FONT FONT, Color color, int size) {
        return getTextButtonStyle(null, FONT, color, size);
    }

    public static TextButton.TextButtonStyle getMenuTextButtonStyle(
      int size) {
        return getTextButtonStyle(STD_BUTTON.MENU, FontMaster.FONT.METAMORPH, DEFAULT_COLOR, size);
    }
        public static TextButton.TextButtonStyle getTextButtonStyle(
         STD_BUTTON button, FONT FONT, Color color, int size) {
        Map<LabelStyle, TextButtonStyle> map = textButtonStyleMap.get(button);
        LabelStyle labelStyle = getSizedColoredLabelStyle(FONT, size, color);
        TextButtonStyle style = null;
        if (map != null) {
            style = map.get(labelStyle);
        } else {
            map = new HashMap<>();
            textButtonStyleMap.put(button, map);
        }
        if (style != null)
            return style;

        style = new TextButtonStyle();
        if (button != null) {
            style.up = button.getTexture();
            if (button.isVersioned()) {
                style.down = button.getTextureDown();
                style.over = button.getTextureOver();
                style.disabled = button.getTextureDisabled();
                style.checked = button.getTextureChecked();
                style.checkedOver = button.getTextureCheckedOver();
            } else {
                style.down = button.getTexture();
                style.over = button.getTexture();
                style.disabled = button.getTexture();
            }
        }

        style.font = getFont(FONT, color, size);
        style.fontColor=new Color(color);
        style.disabledFontColor= new Color(color);
        style.checkedFontColor= new Color(color);
        style.overFontColor = new Color(color);
        style.downFontColor= new Color(color);

        style.fontColor.mul(0.95f);
        style.disabledFontColor.mul(0.65f);
        style.checkedFontColor.mul(1.05f);
        style.overFontColor.mul(1.11f);
        style.downFontColor.mul(1.15f);

        style.fontColor.a=1 ;
        style.disabledFontColor.a=1;
        style.checkedFontColor.a=1;
        style.overFontColor.a=1 ;
        style.downFontColor.a=1 ;

//        style.fontColor = DEFAULT_COLOR;
//        style.overFontColor = new Color(DEFAULT_COLOR).add(50, 50, 50, 0);
//        style.checkedFontColor = new Color(0xFF_00_00_FF);

        map.put(labelStyle, style);
        return style;
    }

    public static TextButtonStyle getTabStyle(TextButtonStyle style) {
        TextureRegion buttonTexture = TextureCache.getOrCreateR("/UI/components/infopanel/buttons.png");
        TextureRegion pressed = new TextureRegion(buttonTexture, 0, 0, 59, 28);
        TextureRegion released = new TextureRegion(buttonTexture, 60, 0, 59, 28);
        style.checked = style.down = new TextureRegionDrawable(pressed);
        style.up = new TextureRegionDrawable(released);
        return style;
    }

    public static TextButtonStyle getHqTabStyle( ) {
        TextButtonStyle  style = getTextButtonStyle(STD_BUTTON.HIGHLIGHT,
         FONT.METAMORPH, GdxColorMaster.GOLDEN_GRAY, 20);
       return style;
    }

    public static TextButtonStyle getDefaultTabStyle() {
        if (defaultTabStyle == null)
            defaultTabStyle = getTabStyle(getDefaultTextButtonStyle());
        return defaultTabStyle;
    }

    public static LabelStyle getHqLabelStyle(int fontSize) {
        return getSizedLabelStyle(FONT.METAMORPH,fontSize );
    }

}
