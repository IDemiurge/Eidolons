package main.swing.components;

import main.swing.SwingMaster;
import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import java.awt.*;

public class TextComp extends G_Panel {
    private static final Dimension NULL_DIMENSION = new Dimension(10, 10);
    protected String text = "";
    protected int x;
    protected int y;
    protected Font defaultFont;
    protected boolean permanent;
    protected Dimension defaultSize;
    protected int fontSize;
    // protected Color color = ColorManager.GOLDEN_WHITE;
    protected Font standardFont;
    protected FONT standardFontType;
    protected Color color;

    public TextComp(ComponentVisuals V, String text) {
        this(V, text, FontMaster.SIZE);
    }

    public TextComp(ComponentVisuals V, String text, int fontSize) {
        this(V, text, fontSize, null);
    }

    public TextComp(ComponentVisuals V, String text, int fontSize, FONT type) {
        // super(V);
        // this.fontSize = fontSize;
        // setDefaultFont(getDefaultFont());
        // if (text != null) {
        // this.text = text;
        // this.permanent = true;
        // }
        // x = getDefaultX();
        // y = getDefaultY();
        // panelSize = getDefaultSize();

        super(V);
        this.fontSize = fontSize;
        standardFontType = type;

        if (text != null) {
            this.text = text;
            this.permanent = true;
        }
        x = getDefaultX();
        y = getDefaultY();

        // if (getVisuals() != null)
        // if (getVisuals().getImage() != null)
        // defaultSize = new Dimension(getVisuals().getImage().getWidth(null),
        // getVisuals()
        // .getImage().getHeight(null));
        // if (defaultSize == null) {
        // if (text != null)
        // defaultSize = initSizeFromText(text);
        //
        // }
        // if (defaultSize == null)
        // defaultSize = new Dimension(10, 10);

        resetSize(text);

        if (standardFontType != null) {
            setDefaultFont(getStandardFont());
        } else {
            setDefaultFont(getDefaultFont());
        }
    }

    public TextComp(Color color) {
        this(null, "");
        this.color = color;
    }

    public TextComp() {
        this(null, "");

    }

    public TextComp(ComponentVisuals V) {
        this(V, null);
    }

    public TextComp(String text) {
        this(null, text);
    }

    public TextComp(String tooltip, Color color) {
        this(null, tooltip);
        this.color = color;
    }

    public static Font getDefaultFontAll() {
        return FontMaster.getDefaultFont();
    }

    protected Color getColor() {
        if (color != null) {
            return color;
        }
        return ColorManager.getTextColor();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    protected Dimension initDefaultSize() {
        return null;
    }

    public Dimension initSizeFromText(String text) {

        return new Dimension(FontMaster.getStringWidth(getDefaultFont(), text) * 3 / 2, FontMaster
         .getFontHeight(getDefaultFont()) * 2);
    } // getText() may cause exceptions

    public void initalizeSizeFromGetText() {
        setDefaultSize(initSizeFromText(getText()));
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

    protected Dimension getDefaultSize() {
        if (getVisuals() != null) {
            if (getVisuals().getImage() != null) {
                return new Dimension(getVisuals().getImage().getWidth(null), getVisuals()
                 .getImage().getHeight(null));
            }
        }
        return new Dimension(getPanelWidth(), getPanelHeight());
    }

    public void setDefaultSize(Dimension defaultSize) {
        this.defaultSize = defaultSize;
    }

    @Override
    public int getPanelWidth() {
        return FontMaster.getStringWidth(getDefaultFont(), text) * 3 / 2;
    }

    @Override
    public int getPanelHeight() {
        return FontMaster.getFontHeight(getDefaultFont()) * 2;
    }

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

    protected Font getDefaultFont() {
        if (defaultFont != null) {
            return defaultFont;
        }
        if (getDefaultFontSize() != 0) {
            return FontMaster.getDefaultFont(getDefaultFontSize());
        }
        return getDefaultFontAll();

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

    public void refresh() {
        resetSize(text);
        if (isPaintBlocked() || permanent) {
            return;
        }
        text = getText();
        repaint();
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

    public String getTextDisplayed() {
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
        g.drawString(text, x, y);
    }

    protected boolean isPaintBlocked() {
        return false;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

}
