package libgdx.gui.dungeon.panels.dc.actionpanel.tooltips;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.feat.active.ActiveObj;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.dc.actionpanel.datasource.ActionCostSource;
import libgdx.gui.dungeon.panels.dc.actionpanel.datasource.ActionCostSourceImpl;
import libgdx.gui.dungeon.panels.dc.unitinfo.tooltips.ActionTooltip;
import libgdx.gui.dungeon.panels.dc.unitinfo.tooltips.CostTableTooltip;

public class ActionCostTooltip extends ActionTooltip {

    private Cell name;
//    ValueContainer description;
//    private Cell costTable;

    public ActionCostTooltip(ActiveObj el) {
        super(el);
        name = addElement(null);
        row();
        addElement(new CostTableTooltip());

        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));

        setUserObject(new ActionCostSourceImpl(el));
    }

    @Override
    public float getPrefHeight() {
        return super.getPrefHeight();

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
