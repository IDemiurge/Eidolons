package main.system.auxiliary;

import main.content.CONTENT_CONSTS.ASPECT;
import main.content.CONTENT_CONSTS.CLASS_GROUP;
import main.content.CONTENT_CONSTS.SKILL_GROUP;
import main.content.properties.G_PROPS;
import main.entity.type.ObjType;

import java.awt.*;

public class ColorManager {

    public static final Color OBSIDIAN = new Color(21, 11, 42);
    public static final Color NEUTRAL = new Color(100, 100, 120);
    public static final Color ALLY_COLOR = new Color(50, 150, 100);
    public static final Color ENEMY_COLOR = new Color(150, 100, 50);
    // new Color(55, 215, 65)
    public static final Color HEALTH = new Color(210, 100, 110);
    public static final Color ENDURANCE =  new Color(210, 100, 110);
    public static final Color TOUGHNESS = new Color(65, 35, 15);
    public static final Color STAMINA = new Color(180, 150, 45);
    public static final Color ESSENCE = new Color(80, 30, 225);
    public static final Color FOCUS = new Color(10, 175, 200);
    public static final Color MORALE = new Color(150, 60, 180);
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color BEIGE = new Color(210, 180, 130);
    public static final Color DEEP_GRAY = new Color(120, 120, 120);
    public static final Color MILD_WHITE = new Color(240, 240, 240);
    public static final Color PURPLE = new Color(175, 85, 255);
    public static final Color LILAC = new Color(125, 65, 235);
    public static final Color CYAN = new Color(45, 225, 165);
    public static final Color GREEN = new Color(55, 255, 65);
    public static final Color YELLOW_GREEN = new Color(95, 205, 25);
    public static final Color RED = new Color(255, 25, 25);
    public static final Color CRIMSON = new Color(215, 15, 65);
    public static final Color BLUE = new Color(25, 25, 255);
    public static final Color YELLOW = new Color(165, 185, 55);
    public static final Color DARK_ORANGE = new Color(195, 105, 25);
    public static final Color ORANGE = new Color(225, 135, 55);
    public static final Color WHITE = new Color(225, 235, 245);
    public static final Color GOLDEN_WHITE = new Color(252, 238, 210);
    public static final Color BRONZE = new Color(175, 115, 25);
    public static final Color IRON = new Color(125, 100, 55);
    public static final Color STEEL = new Color(111, 115, 135);
    public static final Color MITHRIL = new Color(75, 145, 145);
    public static final Color PLATINUM = new Color(155, 115, 155);
    public static final Color MOON_SILVER = new Color(175, 115, 25);
    public static final Color DARK_STEEL = new Color(175, 115, 25);
    public static final Color WARP_STEEL = new Color(175, 115, 25);
    public static final Color CRYSTAL = new Color(175, 115, 25);
    public static final Color RED_IRON = new Color(175, 115, 25);
    public static final Color ADAMANTIUM = new Color(175, 115, 25);
    public static final Color METEORITE = new Color(175, 115, 25);
    public static final Color COLD_IRON = new Color(175, 115, 25);
    public static final Color BLACK = Color.black;
    public static final Color BACKGROUND = BLACK;
    public static final Color GREY = new Color(115, 115, 125);
    public static final Color BACKGROUND_TRANSPARENT = new Color(0, 0, 0, 208);
    public static final Color BACKGROUND_MORE_TRANSPARENT = new Color(0, 0, 0, 158);
    public static final Color STANDARD_TEXT = GOLDEN_WHITE;
    private static final Color LIGHT_YELLOW = new Color(195, 205, 125);
    public static final Color COPPER = LIGHT_YELLOW;
    private static final Color BROWN = new Color(165, 155, 45);
    private static final Color DARK_GREEN = new Color(35, 155, 45);
    private static final Color DARK_BLUE = new Color(35, 45, 155);
    private static final Color MAGENTA = new Color(195, 55, 225);
    private static Color currentColor = OBSIDIAN;

    public static Color getTranslucent(Color color, int i) {
        if (i > 255)
            i = 255;
        if (i < 0)
            i = 0;
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), i);
    }

    public static Color getDarkerColor(Color color, int percent) {

        return new Color(Math.max(color.getRed() * (100 - percent) / 100, 0), Math.max(
                color.getGreen() * (100 - percent) / 100, 0), Math.max(color
                .getBlue()
                * (100 - percent) / 100, 0), color.getAlpha());
    }

    /**
     * reduce hue/saturation by 0-100%. 100 means grayscale
     *
     * @param color
     * @param i
     * @return
     */
    public static Color getPalerColor(Color color, int i) {

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int average = (red + green + blue) / 3;

        return color;
    }

    public static Color getInvertedColor(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    public static Color getAltAspectColor(ObjType type) {
        return getAspectColor(type, true);
    }

    public static Color getAspectColor(ObjType type) {
        return getAspectColor(type, false);
    }

    public static Color getAspectColor(ObjType type, boolean alt) {
        ASPECT aspect = new EnumMaster<ASPECT>().retrieveEnumConst(ASPECT.class, type
                .getProperty(G_PROPS.ASPECT));
        if (aspect == null)
            aspect = ASPECT.NEUTRAL;
        switch (aspect) {
            case ARCANUM:
                return (alt) ? MAGENTA : CYAN;
            case CHAOS:
                return (alt) ? RED : ORANGE;
            case DARKNESS:
                return (alt) ? DARK_BLUE : PURPLE;
            case DEATH:
                return (alt) ? DARK_GREEN : CRIMSON;
            case LIFE:
                return (alt) ? BROWN : YELLOW_GREEN;
            case LIGHT:
                return (alt) ? LIGHT_YELLOW : BLUE;
            case NEUTRAL:
                return (alt) ? BEIGE : YELLOW;

        }
        return (alt) ? BEIGE : YELLOW;

    }

    public static Color getColorForMastery(SKILL_GROUP arg) {
        switch (arg) {
            case ARCANE_ARTS:
                return CYAN;
            case BODY_MIND:
                return BEIGE;
            case CHAOS_ARTS:
                return COLORS.ORANGE.getColor();
            case CRAFT:
                return COLORS.ORANGE.getColor();
            case DARK_ARTS:
                return COLORS.LILAC.getColor();
            case DEATH_ARTS:
                return COLORS.CRIMSON.getColor();
            case DEFENSE:
                return COLORS.ORANGE.getColor();
            case HOLY_ARTS:
                return COLORS.YELLOW.getColor();
            case LIFE_ARTS:
                return COLORS.GREEN.getColor();
            case MISC:
                return COLORS.GOLDEN_WHITE.getColor();
            case OFFENSE:
                return ORANGE;
            case SPELLCASTING:
                return COLORS.BLUE.getColor();
            case WEAPONS:
                return RED_IRON;
            default:
                break;

        }
        return null;
    }

    public static Color getColorForClass(CLASS_GROUP arg) {
        switch (arg) {
            case ACOLYTE:
                return YELLOW;
            case FIGHTER:
                return ORANGE;
            case HERMIT:
                return BEIGE;
            case KNIGHT:
                return GOLDEN_WHITE;
            case MULTICLASS:
                return WHITE;
            case RANGER:
                return GREEN;
            case ROGUE:
                return BRONZE;
            case SORCERER:
                return LILAC;
            case TRICKSTER:
                return CYAN;
            case WIZARD:
                return BLUE;
        }
        return WHITE;
    }

    public static Color getStandardColor(Boolean negative) {
        if (negative == null)
            return GOLDEN_WHITE;
        if (negative)
            return CRIMSON;
        return GREEN;
    }

    public static Color getCurrentColor() {
        return currentColor;
    }

    public static void setCurrentColor(Color currentColor) {
        ColorManager.currentColor = currentColor;
    }

    public static Color getTranslucent() {
        return new Color(0, 0, 0, 0);
    }

    public static Color getHC_DefaultColor() {
        return GOLDEN_WHITE;
    }

    public static Color getTextColor() {
        return GOLDEN_WHITE;
    }

    public static com.badlogic.gdx.graphics.Color getGdxColor(Color c) {
        return new com.badlogic.gdx.graphics.Color(
         c.getRGB()
        );
    }

    public enum FLAG_COLOR {
        BLUE(new Color(25, 25, 255)),
        CYAN(new Color(45, 225, 165)),
        GREEN(new Color(55, 255, 65)),
        PURPLE(new Color(175, 85, 255)),
        RED(new Color(255, 25, 25)),
        YELLOW(new Color(165, 185, 55)),
        BROWN(new Color(165, 155, 45)),
        ORANGE(new Color(225, 135, 55)),
        BLACK(new Color(25, 25, 35)),
        WHITE(new Color(225, 235, 245)),;

        private Color color;

        FLAG_COLOR(Color c) {
            this.color = c;
        }

        public Color getColor() {
            return color;
        }
    }

    public enum COLORS {

        OBSIDIAN(new Color(21, 11, 42)),
        NEUTRAL(new Color(100, 100, 120)),
        ALLY_COLOR(new Color(50, 150, 100)),
        ENEMY_COLOR(new Color(150, 100, 50)),
        HEALTH(new Color(210, 100, 110)),
        ENDURANCE(ColorManager.HEALTH),
        TOUGHNESS(new Color(65, 35, 15)),
        STAMINA(new Color(180, 150, 45)),
        ESSENCE(new Color(80, 30, 225)),
        FOCUS(new Color(10, 175, 200)),
        MORALE(new Color(150, 60, 180)),

        TRANSPARENT(new Color(0, 0, 0, 0)),
        BEIGE(new Color(210, 180, 130)),
        DEEP_GRAY(new Color(120, 120, 120)),
        MILD_WHITE(new Color(240, 240, 240)),

        PURPLE(new Color(175, 85, 255)),
        LILAC(new Color(125, 65, 235)),
        CYAN(new Color(45, 225, 165)),
        GREEN(new Color(55, 255, 65)),
        YELLOW_GREEN(new Color(95, 205, 25)),
        RED(new Color(255, 25, 25)),
        CRIMSON(new Color(215, 15, 65)),
        BLUE(new Color(25, 25, 255)),
        YELLOW(new Color(165, 185, 55)),
        DARK_ORANGE(new Color(195, 105, 25)),
        ORANGE(new Color(225, 135, 55)),
        WHITE(new Color(225, 235, 245)),
        GOLDEN_WHITE(new Color(252, 238, 210)),

        LIGHT_YELLOW(new Color(195, 205, 125)),
        BROWN(new Color(165, 155, 45)),
        DARK_GREEN(new Color(35, 155, 45)),
        DARK_BLUE(new Color(35, 45, 155)),
        MAGENTA(new Color(195, 55, 225)),

        COPPER(ColorManager.LIGHT_YELLOW),
        BRONZE(new Color(175, 115, 25)),
        IRON(new Color(125, 100, 55)),
        STEEL(new Color(111, 115, 135)),
        MITHRIL(new Color(75, 145, 145)),
        PLATINUM(new Color(155, 115, 155)),

        MOON_SILVER(new Color(175, 115, 25)),
        DARK_STEEL(new Color(175, 115, 25)),
        WARP_STEEL(new Color(175, 115, 25)),
        CRYSTAL(new Color(175, 115, 25)),
        RED_IRON(new Color(175, 115, 25)),
        ADAMANTIUM(new Color(175, 115, 25)),
        METEORITE(new Color(175, 115, 25)),
        COLD_IRON(new Color(175, 115, 25)),
        BLACK(Color.black),
        BACKGROUND(ColorManager.BLACK),
        GREY(new Color(115, 115, 125)),;

        private Color color;

        COLORS(Color c) {
            this.color = c;
        }

        public Color getColor() {
            return color;
        }

    }

    public enum COLOR_VARIANTS_JEWELRY {
        YELLOW_FACE(2), BRIMSTONE(3), MOONLIGHT(4), WRAITH(5), POISON(6), BLUE_ARCANE(7), WARP(8),;
        private int code;

        COLOR_VARIANTS_JEWELRY(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

    }

}
