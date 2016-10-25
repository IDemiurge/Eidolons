package main.swing.components.panels.page.log;

import main.entity.Ref;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.ListMaster;
import main.system.text.TextWrapper;

import java.awt.*;
import java.util.List;

/*
 * Has main text and wrapped lines 
 */

public class WrappedTextComp extends TextCompDC {
    protected List<String> textLines;
    private boolean autoWrapText;
    private List<Integer> gaps;
    private List<Font> fonts;
    private List<Boolean> centring;
    private List<Color> colors;

    public WrappedTextComp(VISUALS v, boolean autoWrapText) {
        this(v);
        this.autoWrapText = autoWrapText;

    }

    public WrappedTextComp(VISUALS v, boolean autoWrapText, Integer gaps, Color colors, Font fonts,
                           Boolean centring) {
        this(v, new ListMaster<Integer>().getList(gaps), new ListMaster<Color>().getList(colors),
                new ListMaster<Font>().getList(fonts), new ListMaster<Boolean>().getList(centring));
        this.autoWrapText = autoWrapText;
    }

    public WrappedTextComp(VISUALS v, List<Integer> gaps, List<Color> colors, List<Font> fonts,
                           List<Boolean> centring) {
        super(v);
        this.gaps = gaps;
        this.fonts = fonts;
        this.colors = colors;
        this.centring = centring;
        if (isAutoWrapText())
            wrapTextLines();
    }

    public WrappedTextComp(VISUALS v) {
        this(v, null, null, null, null);

    }

    public void wrapTextLines() {
        // int wrapLength = getWrapLength();// getStringWidth(font,
        // text = WordUtils. //is it enough
        try {
            text = getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        textLines = TextWrapper.wrap(text, getWrapLength(), getRef());

    }

    protected Ref getRef() {
        return null;
    }

    // public Dimension getPreferredSize() {
    // if (visuals !=null)
    // return super.getPreferredSize();
    // textLines.size()*getLineHeight();
    // return null;
    // }
    @Override
    public void refresh() {
        super.refresh();
        if (isAutoWrapText())
            wrapTextLines();
    }

    protected int getWrapLength() {
        return FontMaster.getStringLengthForWidth(getDefaultFont(), (int) getPreferredSize()
                .getWidth());
    }

    protected boolean isAutoWrapText() {
        return autoWrapText;
    }

    protected boolean isPaintText() {
        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        x = recalculateX();
        int y2 = y + getOffsetY();
        int x2 = x;
        int i = 0;
        if (ListMaster.isNotEmpty(getTextLines()))
            for (String str : getTextLines()) {
                y2 = paintLine(g, str, x2, y2, i);
                i++;
            }

        // AttributedCharacterIterator paragraph = text.getIterator();
        // paragraphStart = paragraph.getBeginIndex();
        // paragraphEnd = paragraph.getEndIndex();
        // FontRenderContext frc = g2d.getFontRenderContext();
        // lineMeasurer = new LineBreakMeasurer(paragraph, frc);
    }

    protected int paintLine(Graphics g, String str, int x2, int y2, int i) {
        g.setFont(getFont(i));
        g.setColor(getColor(i));
        if (isCentering(i)) {
            x2 = getCenteredX(str);
        }
        g.drawString(str, x2, y2);
        return y2 + getLineHeight(i);

    }

    protected int getOffsetY() {
        return 0;
    }

    protected boolean isCentering() {
        return false;
    }

    protected int getLineHeight(int lineNumber) {
        Font font = getFont(lineNumber);
        return FontMaster.getFontHeight(font) + getGap(lineNumber);
    }

    private Color getColor(int lineNumber) {
        if (!ListMaster.isNotEmpty(colors))
            return getColor();
        if (colors.size() <= lineNumber)
            return colors.get(colors.size() - 1);
        return colors.get(lineNumber);
    }

    protected boolean isCentering(int lineNumber) {
        if (!ListMaster.isNotEmpty(centring))
            return isCentering();
        if (centring.size() <= lineNumber)
            return centring.get(centring.size() - 1);
        return centring.get(lineNumber);
    }

    private int getGap(int lineNumber) {
        if (!ListMaster.isNotEmpty(gaps))
            return 0;
        if (gaps.size() <= lineNumber)
            return gaps.get(gaps.size() - 1);
        return gaps.get(lineNumber);
    }

    private Font getFont(int lineNumber) {

        if (!ListMaster.isNotEmpty(fonts))
            return getFont();
        if (fonts.size() <= lineNumber)
            return fonts.get(fonts.size() - 1);
        return fonts.get(lineNumber);
    }

    protected int getLineHeight() {
        return FontMaster.getFontHeight(getFont())
                // * 5 / 4
                ;
    }

    @Override
    public Dimension getPanelSize() {
        if (panelSize == null)
            return initSizeFromTextLines();
        return super.getPanelSize();
    }

    public Dimension initSizeFromTextLines() {
        return new Dimension(getMaxLineWidth(), getTextLines().size()
                * FontMaster.getFontHeight(getDefaultFont()) * 3 / 2);
    }

    private int getMaxLineLength() {
        int maxLength = 0;
        for (String sub : getTextLines()) {
            if (sub.length() > maxLength)
                maxLength = sub.length();
        }
        return maxLength;
    }

    private int getMaxLineWidth() {
        int maxWidth = 0;
        for (String sub : getTextLines()) {
            int width = FontMaster.getStringWidth(getDefaultFont(), sub);
            if (width > maxWidth)
                maxWidth = width;
        }
        return maxWidth;
    }

    @Override
    protected int getDefaultX() {
        return getLineHeight();
    }

    @Override
    protected int getDefaultY() {
        return getLineHeight();
    }

    public synchronized List<String> getTextLines() {
        return textLines;
    }

    public synchronized void setTextLines(List<String> textLines) {
        this.textLines = textLines;
    }

    public void setWrapText(boolean wrapText) {
        this.autoWrapText = wrapText;
    }

}
