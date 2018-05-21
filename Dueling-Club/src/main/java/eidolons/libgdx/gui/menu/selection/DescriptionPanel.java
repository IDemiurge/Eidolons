package eidolons.libgdx.gui.menu.selection;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextPanel;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/30/2017.
 */
public class DescriptionPanel extends ScrollTextPanel {
    public DescriptionPanel() {
    }
    @Override
    protected String getBgPath() {
        return null;
    }

    @Override
    public void initBg() {

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
    protected float getDefaultWidth() {
        return GdxMaster.adjustSize(430);
    }
}
