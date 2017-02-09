package main.client.cc.gui.neo.points;

import main.content.PARAMS;
import main.content.VALUE;
import main.entity.Entity;
import main.swing.components.panels.page.log.WrappedTextComp;

import java.awt.*;

public class HC_InfoTextPanel extends WrappedTextComp {
    public static final VISUALS V = VISUALS.INFO_PANEL_HC;
    protected boolean showCost = true;
    protected VALUE value;
    protected Entity hero;

    public HC_InfoTextPanel(Entity hero, VALUE param) {
        this(V, hero, param);
    }

    public HC_InfoTextPanel(VISUALS V, Entity hero, VALUE param) {
        super(V);
        this.hero = hero;
        this.value = param;

    }

    @Override
    protected boolean isAutoWrapText() {
        return true;
    }

    @Override
    protected int getDefaultFontSize() {
        return 17;
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
    public void refresh() {
        if (value == null) {
            return;
        }
        text = null;
        if (value instanceof PARAMS) {
            text = getSpecialDescription((PARAMS) value);
        }
        if (text == null) {
            text = value.getDescription();
        }
        super.refresh();
        textLines.add(0, value.getName());
    }

    protected String getSpecialDescription(PARAMS param) {
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (showCost) {
            // TODO
        }
    }

    public VALUE getValue() {
        return value;
    }

    public void setValue(VALUE param) {
        this.value = param;
        refresh();
    }

}
