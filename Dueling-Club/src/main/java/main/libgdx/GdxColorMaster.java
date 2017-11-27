package main.libgdx;

import com.badlogic.gdx.graphics.Color;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.PARAMS;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.values.parameters.PARAMETER;
import main.system.graphics.ColorManager.FLAG_COLOR;

/**
 * Created by JustMe on 1/23/2017.
 */
public class GdxColorMaster {
    public static final Color OBSIDIAN = getColor(21, 11, 42, 1f);
    public static final Color NEUTRAL = getColor(100, 100, 120, 1f);
    public static final Color ALLY_COLOR = getColor(50, 150, 100, 1f);
    public static final Color ENEMY_COLOR = getColor(150, 100, 50, 1f);
    // new Color(55, 215, 65)
    public static final Color HEALTH = getColor(210, 100, 110, 1f);
    public static final Color ENDURANCE = getColor(210, 100, 110, 1f);
    public static final Color TOUGHNESS = getColor(65, 35, 15, 1f);
    public static final Color STAMINA = getColor(180, 150, 45, 1f);
    public static final Color ESSENCE = getColor(80, 30, 225, 1f);
    public static final Color FOCUS = getColor(10, 175, 200, 1f);
    public static final Color MORALE = getColor(150, 60, 180, 1f);
    public static final Color BEIGE = getColor(210, 180, 130, 1f);
    public static final Color DEEP_GRAY = getColor(120, 120, 120, 1f);
    public static final Color MILD_WHITE = getColor(240, 240, 240, 1f);
    public static final Color PURPLE = getColor(175, 85, 255, 1f);
    public static final Color LILAC = getColor(125, 65, 235, 1f);
    public static final Color CYAN = getColor(45, 225, 165, 1f);
    public static final Color GREEN = getColor(55, 255, 65, 1f);
    public static final Color YELLOW_GREEN = getColor(95, 205, 25, 1f);
    public static final Color RED = getColor(255, 25, 25, 1f);
    public static final Color CRIMSON = getColor(215, 15, 65, 1f);
    public static final Color BLUE = getColor(25, 25, 255, 1f);
    public static final Color YELLOW = getColor(165, 185, 55, 1f);
    public static final Color DARK_ORANGE = getColor(195, 105, 25, 1f);
    public static final Color ORANGE = getColor(225, 135, 55, 1f);
    public static final Color WHITE = new Color(1,1,1, 1f);
    public static final Color GOLDEN_WHITE = getColor(252, 238, 210, 1f);
    public static final Color BRONZE = getColor(175, 115, 25, 1f);
    public static final Color IRON = getColor(125, 100, 55, 1f);
    public static final Color STEEL = getColor(111, 115, 135, 1f);
    public static final Color MITHRIL = getColor(75, 145, 145, 1f);
    public static final Color PLATINUM = getColor(155, 115, 155, 1f);
    public static final Color MOON_SILVER = getColor(175, 115, 25, 1f);
    public static final Color DARK_STEEL = getColor(175, 115, 25, 1f);
    public static final Color WARP_STEEL = getColor(175, 115, 25, 1f);
    public static final Color CRYSTAL = getColor(175, 115, 25, 1f);
    public static final Color RED_IRON = getColor(175, 115, 25, 1f);
    public static final Color ADAMANTIUM = getColor(175, 115, 25, 1f);
    public static final Color METEORITE = getColor(175, 115, 25, 1f);
    public static final Color COLD_IRON = getColor(175, 115, 25, 1f);
    public static final Color GREY = getColor(115, 115, 125, 1f);
    public static final Color BACKGROUND_TRANSPARENT = getColor(0, 0, 0, 208);
    public static final Color BACKGROUND_MORE_TRANSPARENT = getColor(0, 0, 0, 158);
    public static final Color STANDARD_TEXT = GOLDEN_WHITE;
    private static final Color LIGHT_YELLOW = getColor(195, 205, 125, 1f);
    public static final Color COPPER = LIGHT_YELLOW;
    private static final Color BROWN = getColor(165, 155, 45, 1f);
    private static final Color DARK_GREEN = getColor(35, 155, 45, 1f);
    private static final Color DARK_BLUE = getColor(35, 45, 155, 1f);
    private static final Color MAGENTA = getColor(195, 55, 225, 1f);

    public static Color getColor(java.awt.Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 1);
    }
        public static Color getColor(int r, int b, int g, float a) {
        return new Color(r / 100, b / 100, g / 100, a);
    }

    public static Color getDamageTypeColor(DAMAGE_TYPE dmgType) {
        switch (dmgType) {

            case POISON:
                return DARK_GREEN;
            case FIRE:
                return Color.ORANGE;
            case COLD:
                return DARK_BLUE;
            case LIGHTNING:
                return BLUE;
            case ACID:
                return GREEN;
            case SONIC:
                return MAGENTA;
            case LIGHT:
                return LIGHT_YELLOW;
            case ARCANE:
                return Color.CYAN;
            case CHAOS:
                return CRIMSON;
            case SHADOW:
                return LILAC;
            case HOLY:
                return GOLDEN_WHITE;
            case DEATH:
                return GREY;
            case PSIONIC:
                return Color.PURPLE;
            case MAGICAL:
                return Color.BLUE;
        }
        return Color.RED;
    }
    public static Color getParamColor(PARAMETER param) {
        if (param instanceof PARAMS) {
            switch (((PARAMS) param)) {
                case C_MORALE:
                    return MORALE;
                case C_FOCUS:
                    return FOCUS;
                case C_ENDURANCE:
                    return ENDURANCE;
                case C_STAMINA:
                    return STAMINA;
                case C_ESSENCE:
                    return ESSENCE;
                case C_TOUGHNESS:
                    return TOUGHNESS;
                case C_INITIATIVE:
                case C_ENERGY:
                case C_N_OF_ACTIONS:
            }
        }

        return Color.WHITE;
    }

    public static Color getColor(FLAG_COLOR flagColor) {
        if (flagColor==null )
        return Color.BROWN;
        switch (flagColor) {
            case BLUE:
                return Color.BLUE;
            case CYAN:
                return Color.CYAN;
            case GREEN:
                return Color.GREEN;
            case DARK_GREEN:
                return DARK_GREEN;
            case PURPLE:
                return PURPLE;
            case RED:
                return Color.RED;
            case CRIMSON:
                return CRIMSON;
            case YELLOW:
                return Color.YELLOW;
            case BROWN:
                return Color.BROWN;
            case ORANGE:
                return Color.ORANGE;
            case BLACK:
                return Color.BLACK;
            case GRAY:
                return Color.GRAY;
            case WHITE:
                return Color.WHITE;
        }
        return null;
    }

    public static Color darker(Color color, float perc) {
        perc = 1-perc;
        return  new Color(color.r*perc,color.g*perc,color.b*perc,1);
    }
    public static Color lighter(Color color, float perc) {
        perc = 1+perc;
        return  new Color(color.r*perc,color.g*perc,color.b*perc,1);
    }
        public static Color darker(Color color) {
        return  new Color(color.r*0.7f,color.g*0.7f,color.b*0.7f,1);
    }
    public static Color lighter(Color color) {
        return  new Color(color.r*1.2f,color.g*1.2f,color.b*1.2f,1);
    }

    public static Color getColorForTheme(COLOR_THEME color) {

        switch (color) {
            case BLUE:
                return new Color(0.7f, 0.8f, 1f, 1);
            case GREEN:
                return new Color(0.7f, 0.9f, 0.7f, 1);
            case RED:
                return new Color(1f, 0.7f, 0.7f, 1);
            case DARK:
                return new Color(0.6f, 0.5f, 0.7f, 1);
            case LIGHT:
                return new Color(1f, 1f, 1f, 1);
            case YELLOW:
                return new Color(1, 0.9f, 0.7f, 1);
            case PURPLE:
                return new Color(0.8f, 0.7f, 0.9f, 1);
        }
        return null;
    }
}
