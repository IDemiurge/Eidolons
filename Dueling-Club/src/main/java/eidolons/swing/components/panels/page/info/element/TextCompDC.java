package eidolons.swing.components.panels.page.info.element;

import main.swing.SwingMaster;
import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import java.awt.*;
import java.beans.Transient;

public class TextCompDC extends G_Panel {
    private static final Dimension NULL_DIMENSION = new Dimension(10, 10);
    protected String text = "";
    protected int x;
    protected int y;
    protected boolean permanent;
    protected Dimension defaultSize;
    private Font defaultFont;
    private int fontSize;
    private Font standardFont;
    private FONT standardFontType;
    private Color textColor;

    public TextCompDC(ComponentVisuals V, String text) {
        this(V, text, FontMaster.SIZE);
    }

    public TextCompDC(ComponentVisuals V, String text, int fontSize) {
        this(V, text, fontSize, null);
    }

    public TextCompDC(ComponentVisuals V, String text, int fontSize, FONT type, Color textColor) {
        this(V, text, fontSize, type);
        this.textColor = textColor;
    }

    public TextCompDC(ComponentVisuals V, String text, int fontSize, FONT type) {
        super(V);
        this.fontSize = fontSize;
        standardFontType = type;

        if (text != null) {
            this.text = text;
            this.permanent = true;
        }
        x = getDefaultX();
        y = getDefaultY();

        resetSize(text);

        if (standardFontType != null) {
            setDefaultFont(getStandardFont());
        } else {
            setDefaultFont(getDefaultFont());
        }
    }

    public TextCompDC() {
        this(null, null);

    }

    public TextCompDC(ComponentVisuals V) {
        this(V, null);
    }

    @Override
    @Transient
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        return defaultSize;
    }

    public void setDefaultSize(Dimension defaultSize) {
        this.defaultSize = defaultSize;
    }

    private boolean isValidSize(Dimension size) {
        return SwingMaster.isSizeGreaterThan(size, NULL_DIMENSION);
    }

    private void resetSize(String text) {
        // GraphicsManager.isValidSize(defaultSize)
        if (isValidSize(defaultSize)) {
            return;
        }
        if (getVisuals() != null) {
            if (getVisuals().getImage() != null) {
                defaultSize = new Dimension(getVisuals().getImage().getWidth(null), getVisuals()
                 .getImage().getHeight(null));
            }
        }
        if (isValidSize(defaultSize)) {
            return;
        }

        if (text != null) {
            defaultSize = initSizeFromText(text);
        } else {
            defaultSize = initSizeFromText(getText());
        }

        if (isValidSize(defaultSize)) {
            return;
        }

        if (defaultSize == null) {
            defaultSize = NULL_DIMENSION;
        }
    }

    public void initalizeSizeFromGetText() {
        setDefaultSize(initSizeFromText(getText()));
    }

    public Dimension initSizeFromText(String text) {

        return new Dimension(FontMaster.getStringWidth(getDefaultFont(), text) * 3 / 2, FontMaster
         .getFontHeight(getDefaultFont()) * 2);
    } // getText() may cause exceptions

    protected int getDefaultY() {
        return
         // (getVisuals().getHeight() -
         FontMaster.getFontHeight(getDefaultFont())
         // * 3 / 2
         // ) / 2
         ;
    }

    protected int getDefaultX() {
        return getCenteredX(text);
    }

    protected Font getStandardFont() {

        if (standardFont != null) {
            return standardFont;
        }
        if (getDefaultFontSize() != 0) {
            standardFont = FontMaster.getFont(getFontType(), getDefaultFontSize(), Font.PLAIN);
        } else {
            standardFont = FontMaster.getFont(getFontType(), FontMaster.MEDIUM_FONT_SIZE,
             Font.PLAIN);
        }
        return standardFont;
    }

    protected FONT getFontType() {
        if (standardFont == null) {
            return FONT.MAIN;
        }
        return standardFontType;
    }

    protected Font getDefaultFont() {
        if (defaultFont != null) {
            return defaultFont;
        }
        if (getDefaultFontSize() != 0) {
            defaultFont = FontMaster.getDefaultFont(getDefaultFontSize());
        } else {
            defaultFont = FontMaster.getDefaultFont();
        }
        return defaultFont;

    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    protected int getDefaultFontSize() {
        return fontSize;
    }

    protected String getPrefix() {
        return "";
    }

    protected Color getColor() {
        if (textColor != null) {
            return textColor;
        }
        return ColorManager.GOLDEN_WHITE;
    }

    public void refresh() {
        resetSize(text);
        if (isPaintBlocked() || permanent) {
            return;
        }
        text = getText();
        repaint();
    }

    protected boolean isCentering() {
        return true;
    }

    protected int recalculateX() {
        if (!isCentering()) {
            return getDefaultX();
        }
        int newX = getCenteredX(text);
        if (newX < 0) {
            return 0;
        }
        return newX;
    }

    @Override
    public int getHeight() {
        if (visuals != null) {
            return visuals.getHeight();
        }
        if (super.getHeight() == 0) {
            return FontMaster.getFontHeight(getDefaultFont());
        }
        return super.getHeight();
    }

    @Override
    public int getWidth() {
        if (visuals != null) {
            return visuals.getWidth();
        }
        if (super.getWidth() == 0) {
            return FontMaster.getStringWidth(getDefaultFont(), text);
        }
        return super.getWidth();
    }

    protected int getCenteredY() {
        return (getHeight() - FontMaster.getFontHeight(getDefaultFont()))
         // + FontMaster.getFontHeight(defaultFont)
         ;
    }

    protected int getCenteredX(String text) {
        return (getWidth() - FontMaster.getStringWidth(getDefaultFont(), text)) / 2;
    }

    public String getTextString() {

        return text;
    }

    protected String getText() {
        return text;
    }

    public synchronized void setText(String text) {
        this.text = text;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isPaintBlocked()) {
            return;
        }

        g.setFont(getDefaultFont());
        g.setColor(getColor());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        x = recalculateX();
        if (isPaintText()) {
            g.drawString(text, x, y);
        }
    }

    protected boolean isPaintText() {
        return true;
    }

    protected boolean isPaintBlocked() {
        return false;
    }

}
