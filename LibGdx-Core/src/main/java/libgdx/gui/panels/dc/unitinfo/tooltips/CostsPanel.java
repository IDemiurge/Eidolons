package libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.TablePanel;
import libgdx.texture.TextureCache;

import static libgdx.texture.TextureCache.getOrCreateR;

public class CostsPanel extends TablePanel {

    private Cell costsPanel;

    public CostsPanel() {
        addElement(new ValueContainer(TextureCache.getOrCreateR("ui/components/infopanel/action_cost_header.png")))
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
