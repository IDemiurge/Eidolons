package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.ValueContainer;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class CostsPanel extends TablePanel {

    private Cell costsPanel;

    public CostsPanel() {
        addElement(new ValueContainer(getOrCreateR("UI\\components\\infopanel\\action_cost_header.png")))
         .fill(1, 0).expand(1, 0).top();
        row();
        final CostTableTooltip tooltip = new CostTableTooltip();
        tooltip.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
        costsPanel = addElement(tooltip)
         .fill(1, 0).expand(1, 0).top();

        addElement(null);//plug element

    }

    @Override
    public void clear() {
        costsPanel.getActor().clear();
    }

    @Override
    public void setUserObject(Object userObject) {
        costsPanel.getActor().setUserObject(userObject);
    }
}
