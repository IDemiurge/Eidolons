package main.libgdx.gui.panels.dc.actionpanel.tooltips;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSource;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.CostTableTooltip;
import main.libgdx.gui.tooltips.ToolTip;

public class ActionCostTooltip extends ToolTip {

    private Cell name;
    private Cell costTable;

    public ActionCostTooltip() {
        name = addElement(null);
        row();
        costTable = addElement(new CostTableTooltip());

        setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));
    }

    @Override
    public void updateAct(float delta) {
        final ActionCostSource sources = (ActionCostSource) getUserObject();

        name.setActor(sources.getName());
    }


}
