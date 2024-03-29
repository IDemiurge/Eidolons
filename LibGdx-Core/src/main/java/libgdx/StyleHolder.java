package libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.assets.AssetEnums;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import libgdx.assets.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster.FONT;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StyleHolder {
    public static final FONT DEFAULT_FONT = FONT.MAIN;
    public static final FONT ALT_FONT = FONT.NYALA;
    public static final FONT DEFAULT_FONT_FLOAT_TEXT = FONT.MAIN;
    public static final int DEFAULT_FONT_SIZE_FLOAT_TEXT = 18;
    final static String FONT_CHARS = "абвгдежзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|//?-+=()*&.;:,{}\"´`'<>";
    private static final int DEFAULT_SIZE = 17;
    private static final Color DEFAULT_COLOR = new Color(ColorManager.GOLDEN_WHITE.getRGB());
    private static final float SMART_FONT_SIZE_COEF = 0.15f;
    public static Boolean HIERO_ON = null;
    static ObjectMap<STD_BUTTON, ObjectMap<LabelStyle, TextButtonStyle>> textButtonStyleMap = new ObjectMap<>();
    static ObjectMap<FONT, List<Integer>> hieroMap = new ObjectMap<>();
    static ObjectMap<FONT, ObjectMap<Integer, LabelStyle>> hieroStyleMap = new ObjectMap<>();
    private static TextButtonStyle defaultTextButtonStyle;
    private static final ObjectMap<FONT, ObjectMap<Color, LabelStyle>> colorLabelStyleMap = new ObjectMap<>();
    private static final ObjectMap<FONT, ObjectMap<Integer, LabelStyle>> sizeLabelStyleMap = new ObjectMap<>();
    private static final ObjectMap<FONT, ObjectMap<Pair<Integer, Color>, LabelStyle>> sizeColorLabelStyleMap = new ObjectMap<>();
    private static ScrollPaneStyle scrollStyle;
    private static TextButtonStyle dialogueReplyStyle;
    private static LabelStyle defaultHiero;
    private static LabelStyle defaultInfoStyle;
    private static TabbedPane.TabbedPaneStyle horTabStyle;
    private static Menu.MenuStyle menuStyle;
    private static LabelStyle hugeStyle;

    static {
        for (FONT font : FONT.values()) {
            List<Integer> list;
            hieroMap.put(font, list = new ArrayList<>());
            hieroStyleMap.put(font, new ObjectMap<>());
            String path = getHieroPath(font);
            for (File file : FileManager.getFilesFromDirectory(path, false)) {
                String size = StringMaster.cropFormat(file.getName().replace(
                        StringMaster.getStringBeforeNumerals(file.getName()), "")).trim();
                list.add(NumberUtils.getInt(size));
            }

        }
    }
    //TODO

    // public static final Set<ImmutablePair<FONT, Integer>> hieroFonts = ImmutableSet.of(
    //     new ImmutablePair<>(FONT.MAIN, 17),
    //     new ImmutablePair<>(FONT.METAMORPH, 18),
    //     new ImmutablePair<>(FONT.MAGIC, 20),
    //     new ImmutablePair<>(FONT.AVQ, 17)
    //         );
    //
    // public static final Set<ImmutablePair<FONT, Integer>> stdFonts = ImmutableSet.of(
    //         new ImmutablePair<>(FONT.AVQ, 20),
    //         new ImmutablePair<>(FONT.MAIN, 16)
    // );

    public static void preload(){



    }

    public static Boolean isHieroOn() {
        if (HIERO_ON == null) {
            HIERO_ON = Math.abs(GdxMaster.getFontSizeMod() - 1) < 0.6f;
        }
        return HIERO_ON;
    }

    public static LabelStyle getStyledLabelStyle(LabelStyle style, boolean italic, boolean bold) {
        //TODO
        return null;
    }

    public static LabelStyle getSizedLabelStyle(FONT fontStyle, Integer size) {
        if (fontStyle == FONT.METAMORPH) {
            return getHqLabelStyle(size);
        }
        return getSizedColoredLabelStyle(fontStyle, size, DEFAULT_COLOR);
    }

    public static LabelStyle getSizedColoredLabelStyle(FONT fontStyle,
                                                       Integer size, Color color) {
        return getSizedColoredLabelStyle(SMART_FONT_SIZE_COEF, fontStyle, size, color);
    }

    public static LabelStyle getSizedColoredLabelStyle(float i, FONT fontStyle,
                                                       Integer size) {
        return getSizedColoredLabelStyle(i, fontStyle, size, GdxColorMaster.PALE_GOLD);
    }

    public static LabelStyle getDebugLabelStyleLarge() {
        return getSizedColoredLabelStyle(FONT.MAIN, 28, GdxColorMaster.PALE_GOLD);
    }

    public static LabelStyle getDebugLabelStyle() {
        return getSizedColoredLabelStyle(FONT.MAIN, 18, GdxColorMaster.PALE_GOLD);
    }

    public static LabelStyle getSizedColoredLabelStyle(float adjustSizeCoef,
                                                       FONT fontStyle,
                                                       Integer size, Color color) {
        if (size > 100)
            size = size / 100;
        else //quick fix to enable static size
            if (adjustSizeCoef > 0)
                if (GdxMaster.getFontSizeMod() != 1 && GdxMaster.getFontSizeMod() != 0) {
                    int mod = Math.round(size * (GdxMaster.getFontSizeMod() - 1) * adjustSizeCoef);
                    size += mod;
                }
        ObjectMap<Pair<Integer, Color>, LabelStyle> map = getSizedColoredLabelStyleMap(fontStyle, size, color);

        ImmutablePair<Integer, Color> pair = new ImmutablePair<>(size, color);

        if (!map.containsKey(pair)) {
            LabelStyle style = new LabelStyle
                    (getFont(fontStyle, color, size), color);
            style.font.getData().markupEnabled = true;
            map.put(pair, style);
        }

        return map.get(pair);
    }

    private static ObjectMap<Pair<Integer, Color>, LabelStyle> getSizedColoredLabelStyleMap(FONT fontStyle, Integer size, Color color) {

        if (!sizeColorLabelStyleMap.containsKey(fontStyle)) {
            ObjectMap<Pair<Integer, Color>, LabelStyle> map = new ObjectMap<>();
            sizeColorLabelStyleMap.put(fontStyle, map);
            return map;
        }
        return sizeColorLabelStyleMap.get(fontStyle);
    }

    public static LabelStyle getDefaultLabelStyle(Color color) {
        return getLabelStyle(DEFAULT_FONT, color);
    }

    public static LabelStyle getAltLabelStyle(Color color) {
        return getLabelStyle(ALT_FONT, color);
    }

    public static LabelStyle getLabelStyle(FONT font, Color color) {
        ObjectMap<Color, LabelStyle> map = getLabelStyleMap(font );
        if (!map.containsKey(color)) {
            LabelStyle style = new LabelStyle
                    (getFont(font, DEFAULT_COLOR, getDefaultSize()), color);
            style.font.getData().markupEnabled = true;
            map.put(color, style);
        }
        return map.get(color);
    }

    private static ObjectMap<Color, LabelStyle> getLabelStyleMap(FONT font ) {
        if (!colorLabelStyleMap.containsKey(font)) {
            ObjectMap<Color, LabelStyle> map = new ObjectMap<>();
            colorLabelStyleMap.put(font, map);
            return map;
        }
        return colorLabelStyleMap.get(font);
    }

    public static int getDefaultSize() {
        return GdxMaster.adjustFontSize(DEFAULT_SIZE);
    }

    private static BitmapFont getFont(FONT font, Color color, int size) {
        if (font == null) {
            return null;
        }
        if (font== FONT.MAIN && size==getDefaultSize()) {
            return getHieroFontMain();
        }
        //        Integer i = getHieroClosestSize(font, size);
        //        boolean hiero = i != null && font.isHieroSupported() && HIERO_ON;
        //        if (hiero) {
        //            return getFont(getHieroPath(font), color, i, true);
        //        }
        return getFont(font.path, color, size, false);
    }

    private static String getHieroPath(FONT font) {
        return PathFinder.getFontsHieroPath() + "/" + font.name().toLowerCase();
    }

    private static String getHieroPath(FONT font, int size, boolean image) {
        return (image
                ? PathFinder.getHieroImagePath()
                : PathFinder.getFontsHieroPath()) + "/" + font.name().toLowerCase() + "/" + font.name().toLowerCase() + " " + size;
    }

    private static Integer getHieroClosestSize(FONT font, int size) {
        size += 2;
        int sizeGap = 3;
        int smallestDiff = Integer.MAX_VALUE;
        Integer closest = null;
        for (Integer i : hieroMap.get(font)) {
            if (Math.abs(i - size) < smallestDiff) {
                smallestDiff = Math.abs(i - size);
                closest = i;
            }

        }
        return closest;
    }

    private static BitmapFont getFont(String fontpath, Color color, int size, boolean hiero) {
        String path = (hiero ? fontpath
                : PathFinder.getFontPath()
        ) + fontpath;
        if (hiero) {
            path =
                    StringMaster.cropFormat(path) + ".fnt";
        }
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(GDX.file(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.color = color;
        parameter.size = size;
        parameter.characters = FONT_CHARS;
        if (hiero) {
            TextureRegion tex = new TextureRegion(new Texture(PathFinder.getHieroImagePath() + "/high.png"));
            FreeTypeBitmapFontData data = new FreeTypeBitmapFontData();
            data.fontFile = GDX.file(path);
            data.imagePaths = new String[]{
                    StringMaster.cropFormat(path) + ".png"
            };

            return new BitmapFont(data, tex, true);
        } else {
            BitmapFont bitmapFont = generator.generateFont(parameter);
            generator.dispose();
            return bitmapFont;
        }
    }

    public static BitmapFont getHieroFontMain() {
        return getHieroFont(FONT.MAIN, 17);
        // TextureRegion tex = TextureCache.getOrCreateR(PathFinder.getHieroImagePath() + "/main/main 16.png");
        // return new BitmapFont(GDX.file(PathFinder.getFontPath() + "hiero/main/main 16.fnt"), tex);
    }

    public static BitmapFont getHieroFontMagic() {
        return getHieroFont(FONT.MAGIC, 20);
        // TextureRegion tex = TextureCache.getOrCreateR(PathFinder.getHieroImagePath() + "/magic/magic 20.png");
        // return new BitmapFont(GDX.file(PathFinder.getFontPath() + "hiero/magic/magic 20.fnt"), tex);
    }

    public static BitmapFont getHieroFontHigh() {
        TextureRegion tex = TextureCache.getOrCreateR(PathFinder.getHieroImagePath() + "high/high 22.png", false,
                AssetEnums.ATLAS.UI_BASE);
        return new BitmapFont(GDX.file(PathFinder.getFontPath() + "hiero/high/high 22.fnt"), tex);
    }

    public static BitmapFont getHieroFontHQ(int fontSize) {
        return getHieroFont(FONT.METAMORPH, fontSize);
    }

    public static BitmapFont getHieroFont(FONT font, int fontSize) {
        Integer size = getHieroClosestSize(font, fontSize);
        if (size == null) {
            return getFont(font, GdxColorMaster.getDefaultTextColor(), fontSize);
        }
        TextureRegion tex = TextureCache.getOrCreateR(getHieroPath(font, size,true) +
                ".png", false, AssetEnums.ATLAS.UI_BASE);
        return new BitmapFont(GDX.file(getHieroPath(font, size,false) +
                ".fnt"), tex);

    }

    public static BitmapFont getHugeHieroFont() {
        return getHieroFont(FONT.HUGE, 46);
    }

    public static LabelStyle getDefaultLabelStyle() {
        return getDefaultLabelStyle(DEFAULT_COLOR);
    }

    public static LabelStyle getAVQLabelStyle(int size) {
        return getSizedLabelStyle(FONT.AVQ, size);
    }

    public static LabelStyle getAVQLabelStyle() {
        return getLabelStyle(FONT.AVQ, DEFAULT_COLOR);
    }

    public static TextButtonStyle getDefaultTextButtonStyle() {
        if (defaultTextButtonStyle == null) {
            defaultTextButtonStyle = getTextButtonStyle(FONT.AVQ, DEFAULT_COLOR, 18);
        }
        return defaultTextButtonStyle;
    }

    public static TextButtonStyle getHqTextButtonStyle(STD_BUTTON button, int size) {
        return getTextButtonStyle(button, FONT.METAMORPH, DEFAULT_COLOR, size);
    }

    public static TextButtonStyle getHqTextButtonStyle(
            int size) {
        return getTextButtonStyle(FONT.METAMORPH, DEFAULT_COLOR, size);
    }

    public static TextButtonStyle getTextButtonStyle(
            FONT FONT, Color color, int size) {
        return getTextButtonStyle(STD_BUTTON.EMPTY, FONT, color, size);
    }

    public static TextButtonStyle getMenuTextButtonStyle(
            int size) {
        return getTextButtonStyle(STD_BUTTON.MENU,
                FONT.METAMORPH, DEFAULT_COLOR, size);
    }

    public static TextButtonStyle getButtonStyle(
            STD_BUTTON button, FONT font) {
        return getTextButtonStyle(button, font, GdxColorMaster.getDefaultTextColor(),
                20);
    }

    public static TextButtonStyle getTextButtonStyle(TextButtonStyle style, STD_BUTTON btnStyle) {
        style = new TextButtonStyle(style);
        initBtnStyle(style, btnStyle);
        return style;
    }

    public static TextButtonStyle getTextButtonStyle(
            STD_BUTTON button, FONT FONT, Color color, int size) {
        ObjectMap<LabelStyle, TextButtonStyle> map = textButtonStyleMap.get(button);
        LabelStyle labelStyle;
        if (FONT == null) {
            labelStyle = new LabelStyle();
        } else
            labelStyle = getSizedColoredLabelStyle(FONT, size, color);
        TextButtonStyle style = null;
        if (map != null) {
            style = map.get(labelStyle);
        } else {
            map = new ObjectMap<>();
            textButtonStyleMap.put(button, map);
        }
        if (style != null)
            return style;

        style = new TextButtonStyle();
        initBtnStyle(style, button);
        if (style.font == null)
            style.font = getFont(FONT, color, size);

        if (style.font == null)
            return style;
        style.fontColor = new Color(color);
        style.disabledFontColor = new Color(color);
        style.checkedFontColor = new Color(color);
        style.overFontColor = new Color(color);
        style.downFontColor = new Color(color);

        style.fontColor.mul(0.95f);
        style.disabledFontColor.mul(0.65f);
        style.checkedFontColor.mul(1.05f);
        style.overFontColor.mul(1.11f);
        style.downFontColor.mul(1.15f);

        style.fontColor.a = 1;
        style.disabledFontColor.a = 1;
        style.checkedFontColor.a = 1;
        style.overFontColor.a = 1;
        style.downFontColor.a = 1;

        //        style.fontColor = DEFAULT_COLOR;
        //        style.overFontColor = new Color(DEFAULT_COLOR).add(50, 50, 50, 0);
        //        style.checkedFontColor = new Color(0xFF_00_00_FF);

        map.put(labelStyle, style);
        return style;
    }

    private static void initBtnStyle(TextButtonStyle style, STD_BUTTON button) {

        if (button != STD_BUTTON.EMPTY) {
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
        if (button == STD_BUTTON.MENU) {
            if (isHieroOn()) {
                style.font = getHieroFontMagic();
            }
        }
    }

    public static TextButtonStyle getHqTabStyle() {
        return getTextButtonStyle(STD_BUTTON.TAB_HIGHLIGHT_COLUMN,
                FONT.METAMORPH, GdxColorMaster.GOLDEN_GRAY, 20);
    }

    public static TextButtonStyle getHqTabStyleEmpty() {
        return getTextButtonStyle(STD_BUTTON.TAB_HIGHLIGHT_COLUMN,
                FONT.METAMORPH, GdxColorMaster.GOLDEN_GRAY, 20);
    }


    public static LabelStyle getHqLabelStyle(int fontSize) {
        if (true) {
            LabelStyle style = hieroStyleMap.get(FONT.METAMORPH).get(fontSize);
            if (style == null) {
                style = new LabelStyle(getHieroFontHQ(fontSize), GdxColorMaster.getDefaultTextColor());
                hieroStyleMap.get(FONT.METAMORPH).put(fontSize, style);
            }
            return style;
        }
        return getSizedColoredLabelStyle(FONT.METAMORPH, fontSize, DEFAULT_COLOR);
    }


    public static ScrollPaneStyle getScrollStyle() {
        if (scrollStyle == null) {
            scrollStyle = new ScrollPaneStyle(
                    null,
                    //             NinePatchFactory.getLightDecorPanelFilledDrawable(),
                    NinePatchFactory.getScrollH(),
                    NinePatchFactory.getScrollKnobH(),
                    NinePatchFactory.getScrollV(),
                    NinePatchFactory.getScrollKnobV()
            );
        }
        return scrollStyle;
    }

    public static TextButtonStyle getDialogueReplyStyle() {
        if (dialogueReplyStyle == null)
            dialogueReplyStyle = getTextButtonStyle(STD_BUTTON.HIGHLIGHT_ALT, FONT.MAIN, GdxColorMaster.PALE_GOLD, 20);

        //TODO old... anything useful?
        if (dialogueReplyStyle == null) {
            dialogueReplyStyle = new TextButtonStyle();
            TextButtonStyle style = dialogueReplyStyle;
            BitmapFont font = getFont(FONT.MAIN, GdxColorMaster.PALE_GOLD, 20);

            Color color = GdxColorMaster.PALE_GOLD;
            style.font = font;

            color = GdxColorMaster.lighter(color);
            style.overFontColor = color;
            color = GdxColorMaster.lighter(color);
            style.downFontColor = color;

            color = GdxColorMaster.PALE_GOLD;
            color = GdxColorMaster.darker(color);
            style.checkedOverFontColor = color;
            color = GdxColorMaster.darker(color);
            style.checkedFontColor = color;
            color = GdxColorMaster.darker(color);
            style.disabledFontColor = color;
        }

        return dialogueReplyStyle;
    }

    public static LabelStyle getDefaultHiero() {
        if (defaultHiero == null)
            defaultHiero = new LabelStyle(getHieroFontHigh(), GdxColorMaster.getDefaultTextColor());
        return defaultHiero;
    }

    public static LabelStyle getDefaultInfoStyle() {
        if (defaultInfoStyle == null) {
            defaultInfoStyle = getHqLabelStyle(16);
        }
        return defaultInfoStyle;
    }

    public static LabelStyle getHugeStyle() {
        if (hugeStyle == null) {
            hugeStyle = getDefaultLabelStyle(Color.WHITE);
            hugeStyle.font = getHugeHieroFont();
        }
        return hugeStyle;
    }

    public static LabelStyle getStyle(VisualEnums.LABEL_STYLE style) {
        return getSizedColoredLabelStyle(style.font, style.size, style.color);
    }

    public static LabelStyle newStyle(LabelStyle style) {
        return new LabelStyle(style);
    }

    public static TabbedPane.TabbedPaneStyle getHorTabStyle() {
        if (horTabStyle == null) {
            horTabStyle = VisUI.getSkin().get(TabbedPane.TabbedPaneStyle.class);
            horTabStyle.vertical = false;

            horTabStyle.background = NinePatchFactory.getLightPanelFilledDrawable();
        }
        return horTabStyle;
    }

    public static Menu.MenuStyle getMenuStyle() {
        if (menuStyle == null) {
            menuStyle = new Menu.MenuStyle(VisUI.getSkin().get(Menu.MenuStyle.class));
            TextButtonStyle s = getMenuBtnStyle();
            menuStyle.openButtonStyle.font = s.font;
            //            =new VisTextButton.VisTextButtonStyle(
            //                    s.up,
            //                    s.down,
            //                    s.checked,
            //                    s.font
            //            );
        }
        return menuStyle;
    }

    public static MenuItem.MenuItemStyle getMenuBtnStyle() {
        MenuItem.MenuItemStyle menuItemStyle = new MenuItem.MenuItemStyle(VisUI.getSkin().get(MenuItem.MenuItemStyle.class));
        menuItemStyle.font = getButtonStyle(STD_BUTTON.MENU, FONT.MAGIC).font;
        return menuItemStyle;
    }

}
