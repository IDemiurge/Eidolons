package main.swing.components.panels.page.log;

import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;
import java.util.List;

public class TextPage extends WrappedTextComp {

    private DC_PagedLogPanel logPanel;

    public TextPage(List<String> list, DC_PagedLogPanel logPanel) {
        this(list, logPanel, null, null, null, null);
    }

    public TextPage(List<String> list, DC_PagedLogPanel logPanel, List<Integer> gaps,
                    List<Font> fonts, List<Color> colors, List<Boolean> centring) {
        super(VISUALS.INFO_PANEL, gaps, colors, fonts, centring);
        this.logPanel = logPanel;
        setTextLines(list);
        addMouseListener(logPanel);
    }

    @Override
    public Font getFont() {
        if (logPanel == null)
            return FontMaster.getDefaultFont();
        return logPanel.getFont();
    }

    @Override
    protected int getDefaultY() {
        return getLineHeight() * 3 / 2 + STD_COMP_IMAGES.ARROW_3_DOWN.getImg().getHeight(null);
    }

    @Override
    protected int getDefaultX() {
        return STD_COMP_IMAGES.ARROW_3_LEFT.getImg().getWidth(null) * 3 / 2;
    }

    @Override
    protected boolean isCentering() {
        return false;
    }

    private boolean isLineNumberPainted() {
        return true;
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
        if (isLineNumberPainted()) {
            String str = (logPanel.getCurrentIndex() + 1) + "/" + (logPanel.getPageData().size());
            Font font = FontMaster.getFont(FONT.MAIN, 18, Font.PLAIN);
            graphics.setFont(font);
            int w = FontMaster.getStringWidth(font, str);
            int h = FontMaster.getFontHeight(font);
            int x = getWidth() - w - 20;
            int y = getHeight() - h - 5;
            graphics.drawString(str, x, y);
        }

        // Point pen = new Point(10, 20);
        // Graphics2D g2d = (Graphics2D)graphics;
        // FontRenderContext frc = g2d.getFontRenderContext();
        //
        // // let styledText be an AttributedCharacterIterator containing at
        // least
        // // one character
        //
        // LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
        // float wrappingWidth = getSize().width - 15;
        //
        // while (measurer.getPosition() < fStyledText.length()) {
        //
        // TextLayout initComps = measurer.nextLayout(wrappingWidth);
        //
        // pen.y += (initComps.getAscent());
        // float dx = initComps.isLeftToRight() ?
        // 0 : (wrappingWidth - initComps.getAdvance());
        //
        // initComps.draw(graphics, pen.x + dx, pen.y);
        // pen.y += initComps.getDescent() + initComps.getLeading();
        // }
    }

}
