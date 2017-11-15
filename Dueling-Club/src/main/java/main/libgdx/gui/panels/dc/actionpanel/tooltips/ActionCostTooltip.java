package main.libgdx.gui.panels.dc.actionpanel.tooltips;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.entity.active.DC_ActiveObj;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSource;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltip;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.CostTableTooltip;

public class ActionCostTooltip extends ActionTooltip {

    ValueContainer description;
    private Cell name;
    private Cell costTable;

    public ActionCostTooltip(DC_ActiveObj el) {
        super(el);
        name = addElement(null);
        row();
        costTable = addElement(new CostTableTooltip());

        setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));
    }


    @Override
    public void updateAct(float delta) {
        final ActionCostSource sources = (ActionCostSource) getUserObject();
        name.setActor(sources.getName());
        if (getActions().size > 0) {
            if (description != null) {
                description.remove();
            }
            return;
        }
        Action action = getDescriptionAction();
        addAction(action);
    }

    private Action getDescriptionAction() {
        final ActionCostSource sources = (ActionCostSource) getUserObject();
        DelayAction addAfter = new DelayAction();
        if (description == null)
            description = sources.getDescription();
        Action add = new Action() {
            @Override
            public boolean act(float delta) {
                if (getActor() instanceof Group) {
                    ((TablePanel) getActor()).row();
                     ((TablePanel) getActor()).addElement(description);
                }
                return true;
            }
        };
        //TODO add move up action!
        add.setActor(this);
        addAfter.setAction(add);
        addAfter.setDuration(1);
        addAfter.setTarget(this);
        return addAfter;
    }


}
