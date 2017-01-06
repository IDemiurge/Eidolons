package main.system.text;

import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;

public class SmartText {
    private static final float FONT_SIZE = 14;
    private String text;
    private Color color;
    private Font font;

    public SmartText(String text, Color color) {
        this.text = text;
        this.color = color;
        font = FontMaster.getFont(FONT.MAIN, 15, Font.PLAIN);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getWidth() {
        return FontMaster.getStringWidth(getFont(), getText());
    }

    public int getHeight() {
        return FontMaster.getFontHeight(getFont());
    }

    public enum OBJ_COMP_TEXT {
        TOUGHNESS {
        },

    }

}
