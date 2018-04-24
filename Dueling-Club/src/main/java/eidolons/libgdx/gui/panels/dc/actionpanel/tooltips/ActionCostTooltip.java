package eidolons.libgdx.gui.panels.dc.actionpanel.tooltips;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSource;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSourceImpl;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltip;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.CostTableTooltip;

public class ActionCostTooltip extends ActionTooltip {

    private Cell name;
//    ValueContainer description;
//    private Cell costTable;

    public ActionCostTooltip(DC_ActiveObj el) {
        super(el);
        name = addElement(null);
        row();
        addElement(new CostTableTooltip());

        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));

         setUserObject(new ActionCostSourceImpl(el));
    }


    @Override
    public void updateAct(float delta) {
        final ActionCostSource sources = (ActionCostSource) getUserObject();
        name.setActor(sources.getName());
//        if (getActions().size > 0) {
//            if (description != null) {
//                description.remove();
//            }
//            return;
//        }
        if (!isAnimated()) {

            row();
            try {
                addElement(getDescription());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            return;
        }
        Action action = getDescriptionAction();
        addAction(action);
    }

    public boolean isAnimated() {
        return false;
    }

    private Action getDescriptionAction() {
        DelayAction addAfter = new DelayAction();
        Action add = new Action() {
            @Override
            public boolean act(float delta) {
                if (getActor() instanceof Group) {
                    ((TablePanel) getActor()).row();
                    ((TablePanel) getActor()).addElement(getDescription());
                }
                return true;
            }
        };
        //TODO add move up action!
        add.setActor(this);
        addAfter.setAction(add);
        addAfter.setDuration(0.7f);
        addAfter.setTarget(this);
        return addAfter;
    }

    public ValueContainer getDescription() {
        return ((ActionCostSource) getUserObject()).getDescription();
    }
}
