package eidolons.libgdx.gui.menu.selection;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextPanel;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/30/2017.
 */
public class DescriptionPanel extends ScrollTextPanel {
    public DescriptionPanel() {
        super();
    }
    @Override
    protected String getBgPath() {
        return null;
    }

    @Override
    public void initBg() {

    }

    @Override
    public void init() {
        super.init();
//        scrollPanel.pad(10, 10, 10, 10);
//        scrollPanel.getTable().padLeft(20);
    }

    @Override
    public void layout() {
        super.layout();
        scrollPanel.initScrollListener();
    }

    @Override
    protected int getFontSize() {
        return 18;
    }

    @Override
    protected FONT getFontStyle() {
        return FONT.MAIN;
    }

    @Override
    protected float getDefaultHeight() {
        return 650;
    }

    @Override
    protected float getTextLineWidth() {
        return getWidth()*0.86f;
    }

    @Override
    protected float getDefaultWidth() {
        return GdxMaster.adjustSize(415);
    }
}
