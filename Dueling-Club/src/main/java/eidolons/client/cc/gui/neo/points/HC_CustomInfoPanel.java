package eidolons.client.cc.gui.neo.points;

import eidolons.swing.components.panels.page.log.WrappedTextComp;

import java.awt.*;

public class HC_CustomInfoPanel extends WrappedTextComp {
    public static final VISUALS V = VISUALS.INFO_PANEL_HC;
    private static final int DEF_FONT_SIZE = 17;

    public HC_CustomInfoPanel() {
        this(V);
        text = "";
    }

    public HC_CustomInfoPanel(VISUALS V) {
        super(V);
    }

    @Override
    protected boolean isAutoWrapText() {
        return true;
    }

    @Override
    protected int getDefaultFontSize() {
        return DEF_FONT_SIZE;
    }

    @Override
    protected int getDefaultX() {
        return super.getDefaultX() + 5;
    }

    @Override
    protected int getDefaultY() {
        return super.getDefaultY() * 2;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

    }

}
