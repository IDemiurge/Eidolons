package main.system.text;

import main.system.auxiliary.FontMaster;

import java.awt.*;

public class ImageLine {
    private Image image;
    private boolean wrap;
    private int offsetY;
    private int offsetX;
    private String text;

    // private TextItem textItem;
    // TODO font/color?
    public ImageLine(Image image, String text, boolean wrap, int offsetX, int offsetY) {
        this.image = image;
        this.text = text;
        this.wrap = wrap;
        this.offsetY = offsetY;
        this.offsetX = offsetX;
    }

    public int getHeight() {
        return Math.max(offsetY + getImage().getHeight(null), FontMaster.getFontHeight(getFont()));
    }

    public int getWidth() {
        return offsetX + getImage().getWidth(null)
                + FontMaster.getStringWidth(getFont(), getText());
    }

    public Font getFont() {
        return TextItem.getDefaultTextItemFont();
    }

    public String getText() {
        return text;
    }

    public Image getImage() {
        return image;
    }

    public boolean isWrap() {
        return wrap;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

}
