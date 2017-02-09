package main.swing.generic.misc;

import main.content.ContentManager;
import main.content.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.ColorManager;
import main.system.math.MathMaster;

import java.awt.*;

public class ValueBar extends G_Panel {

    protected static final int COLOR_TRANSPARENCY = 50;
    protected Color color;
    protected PARAMETER param;
    protected Entity obj;
    protected PARAMETER c_param;
    protected PARAMETER perc_param;
    protected Integer c_val;
    protected Integer max_val;
    protected Integer percentage = MathMaster.PERCENTAGE;
    boolean numericRepresentation = false;

    public ValueBar(PARAMETER param, Color color) {
        this.param = param;
        this.c_param = ContentManager.getCurrentParam(param);
        this.perc_param = getPercentageParameter(param);
        this.color = color;
    }

    protected PARAMETER getPercentageParameter(PARAMETER param) {
        return ContentManager.getPercentageParam(param);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (getObj() == null) {
            return;
        }
        int transparency = COLOR_TRANSPARENCY
                + MathMaster.getCentimalPercent(percentage);
        // MathManager.getFractionValue(256, percentage)

        g.setColor(ColorManager.getTranslucent(color, transparency));
        // g.setColor(color);
        if (!numericRepresentation) {
            // gradient + shadowing + show numeric value
            int w = MathMaster.getFractionValue(this.getWidth(), percentage);
            int h = getHeight();
            g.drawRect(0, 0, w, h);
            g.fillRect(0, 0, w, h);

            g.setColor(ColorManager.WHITE);
            g.drawLine(w, h, w, 0);
            g.drawString(param.getName() + ": " + c_val + "/" + max_val, 0,
                    this.getHeight() / 2);
        } else {
            g.drawString(
                    param + ": " + MathMaster.getCentimalPercent(percentage)
                            + "%", 0, this.getHeight() / 2);

        }
    }

    @Override
    public void refresh() {
        if (getObj() == null) {
            return;
        }

        c_val = getObj().getIntParam(c_param);
        max_val = getObj().getIntParam(param);

        if (!(obj instanceof Obj)) {
            percentage = MathMaster.PERCENTAGE;
            c_val = max_val;
        } else {
            // ((Obj) getObj()).resetPercentages();
            if (perc_param != null) {
                percentage = getObj().getIntParam(perc_param);
            }

        }
        super.refresh();
    }

    public Entity getObj() {
        return obj;
    }

    public void setObj(Entity obj2) {
        this.obj = obj2;
    }

}
