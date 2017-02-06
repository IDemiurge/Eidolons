package main.swing.components.panels;

import main.entity.obj.Obj;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;

public class NameComponent extends WrappedTextComp {

    private Obj obj;

    public NameComponent() {
        super(VISUALS.BF_NAME_COMP, true);
    }

    @Override
    protected String getText() {
        if (getObj() == null) {
            return "?";
        }
        String nameIfKnown = getObj().getNameIfKnown();
        if (!getObj().getName().equals(nameIfKnown)) {
            return nameIfKnown;
        }
        return getObj().getDisplayedName();
    }

    private Obj getObj() {
        return obj;
    }

    public void setObj(Obj obj) {
        this.obj = obj;
    }

    @Override
    protected int getDefaultY() {
        return 0;
    }

    protected int getOffsetY() {
        int size = 0;
        if (getTextLines() != null) {
            size = getTextLines().size();
        }
        return Math.max(0, (3 - size)) * getLineHeight() + size * 2;
    }

    @Override
    protected int getWrapLength() {
        return 16;
    }

    @Override
    protected Color getColor() {
        return ColorManager.GOLDEN_WHITE;
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getFont(FONT.MAIN, 16, Font.PLAIN);
    }

    @Override
    protected boolean isCentering() {
        return true;
    }

    @Override
    public void wrapTextLines() {
        super.wrapTextLines();
    }
}
