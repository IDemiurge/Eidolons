package main.swing.generic.components.panels;

import main.content.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.swing.generic.components.G_Panel;
import main.swing.generic.misc.ValueBar;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BarPanel {
    G_Panel panel;
    List<PARAMETER> vals;
    List<Color> colors;
    private Map<PARAMETER, ValueBar> bars = new XLinkedMap<>();
    private Entity obj;

    public BarPanel(PARAMETER[] params, Color[] colors) {
        vals = new LinkedList<>(Arrays.asList(params));
        this.colors = new LinkedList<>(Arrays.asList(colors));
        // TODO GradientPaint
        initBars();
    }

    public void initBars() {
        panel = new G_Panel("flowy");
        int i = 0;
        for (PARAMETER v : vals) {
            ValueBar valueBar = new ValueBar(v, colors.get(i)) {
                public void refresh() {
                    super.refresh();
                    c_val = max_val;
                    max_val = 100;
                    percentage = c_val * MathMaster.MULTIPLIER / max_val;
                }

                protected PARAMETER getPercentageParameter(PARAMETER param) {
                    return null;
                }

            };
            bars.put(v, valueBar);
            panel.add(valueBar, "w " + getWidth() + ",h " + getHeight()
                    // getPos(i)
            );
            i++;
        }
    }

    protected String getWidth() {
        return GuiManager.getSquareCellSize() + "";
    }

    protected String getHeight() {
        return GuiManager.getSquareCellSize() + "/6";
    }

    public void refresh() {
        for (ValueBar bar : bars.values()) {
            bar.setObj(getObj());
            bar.refresh();
        }
    }

    public Entity getObj() {
        return obj;
    }

    public void setObj(Entity obj) {
        this.obj = obj;
    }

    public G_Panel getPanel() {
        return panel;
    }

    public List<PARAMETER> getVals() {
        return vals;
    }

    public List<Color> getColors() {
        return colors;
    }

    public Map<PARAMETER, ValueBar> getBars() {
        return bars;
    }
}
