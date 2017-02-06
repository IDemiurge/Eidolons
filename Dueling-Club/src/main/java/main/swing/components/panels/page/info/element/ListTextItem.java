package main.swing.components.panels.page.info.element;

import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;

public class ListTextItem<E> extends TextCompDC {

    private static final int DEFAULT_FONT_SIZE = 48;
    private static final FONT DEFAULT_FONT_TYPE = FONT.AVQ;
    private boolean blocked;
    private boolean isSelected;
    private E value;
    private Integer forcedFontSize;

    public ListTextItem(VISUALS V, E value, boolean isSelected, boolean blocked,
                        Integer forcedFontSize) {
        super(V);
        this.value = value;
        this.blocked = blocked;
        this.forcedFontSize = forcedFontSize;
        this.isSelected = isSelected;
        if (blocked) {
            setDefaultFont(getBlockedFont());
        } else {
            setDefaultFont((isSelected) ? getSelectedFont() : getDefaultFont());
        }
        setBackground(ColorManager.BACKGROUND);
        setOpaque(true);
        text = getText();
    }

    public ListTextItem(VISUALS V, E value, boolean isSelected, boolean blocked) {
        this(V, value, isSelected, blocked, null);
    }

    @Override
    protected String getText() {
        if (value == null) {
            return "null";
        }
        return value.toString().replace("_", "");
        // StringMaster.getWellFormattedString().toUpperCase();
    }

    @Override
    protected Color getColor() {
        if (blocked) {
            return getBlockedColor();
        }
        if (isSelected) {
            return getSelectedColor();
        }
        return super.getColor();
    }

    private Color getSelectedColor() {
        return super.getColor();
    }

    private Color getBlockedColor() {
        return ColorManager.GREY;
    }

    @Override
    protected int getDefaultFontSize() {
        if (forcedFontSize != null) {
            return forcedFontSize;
        }
        return DEFAULT_FONT_SIZE;
    }

    @Override
    protected int getDefaultX() {
        return super.getDefaultX();
    }

    @Override
    protected int getDefaultY() {
        return super.getDefaultY() * 3 / 2;
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getFont(getDefaultFontType(), getDefaultFontSize(), Font.PLAIN);
    }

    private Font getBlockedFont() {
        return FontMaster.getFont(getDefaultFontType(), getDefaultFontSize(), Font.PLAIN);
    }

    private Font getSelectedFont() {
        return FontMaster.getFont(getDefaultFontType(), getDefaultFontSize(), Font.BOLD);
    }

    private FONT getDefaultFontType() {
        return DEFAULT_FONT_TYPE;
    }

}
