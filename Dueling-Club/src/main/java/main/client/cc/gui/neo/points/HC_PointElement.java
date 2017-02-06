package main.client.cc.gui.neo.points;

import main.content.VALUE;
import main.swing.components.panels.page.info.element.ParamElement;
import main.system.auxiliary.ColorManager;

import java.awt.*;

import static main.client.cc.gui.neo.points.HC_PointComp.DEFAULT_FONT_SIZE;

public class HC_PointElement extends ParamElement {

    protected boolean integerValue;
    protected boolean shortened;
    private Integer fontSize;

    public HC_PointElement(boolean shortened, boolean integerValue, VALUE v, VISUALS V) {
        super(v, V);
        y = getDefaultY();
        this.shortened = shortened;
        this.integerValue = integerValue;
    }

    public HC_PointElement(boolean integerValue, VALUE v, VISUALS V) {
        super(v, V);
        y = getDefaultY();
        this.integerValue = integerValue;
    }

    @Override
    protected String getText() {
        if (shortened) {
            return "";
        }
        return super.getText();
    }

    @Override
    protected Color getColor() {
        return ColorManager.getHC_DefaultColor();
    }

    @Override
    protected int getDefaultY() {
        return super.getDefaultY() * 3 / 2;
    }

    protected int getDefaultX() {
//        if (icon == null)
//            return super.getDefaultX();
        return 52;
    }

    @Override
    protected int getDefaultX2() {
        return 0;
//        return super.getDefaultX2() - arrowWidth;
    }

    @Override
    protected int getDefaultFontSize() {
        if (fontSize == null) {
            fontSize = DEFAULT_FONT_SIZE;
        }
        return fontSize;
    }
}


