package main.swing.components.panels.page;

import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.DC_UnitObj;
import main.swing.components.panels.DC_UnitActionPanel;
import main.swing.components.panels.DC_UnitActionPanel.ACTION_DISPLAY_GROUP;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;

import java.util.LinkedList;
import java.util.List;

public class DC_PagedUnitActionPanel extends G_PagedListPanel<DC_UnitAction> {

    public static final int ARROW_VERSION = 3;
    private static final int PAGE_SIZE = DC_UnitActionPanel.SPECIAL_W;
    private static final int WRAP = 1;
    private ACTION_DISPLAY_GROUP group;

    public DC_PagedUnitActionPanel(ACTION_DISPLAY_GROUP actionGroup) {
        super(PAGE_SIZE, false, ARROW_VERSION);
        this.group = actionGroup;
    }

    @Override
    public boolean isButtonsOnBothEnds() {
        return true;
    }

    @Override
    protected boolean isDoubleButtons() {
        return false;
    }

    @Override
    protected List<List<DC_UnitAction>> getPageData() {
        // sort spells?
        if (getObj() == null) {
            return null;
        }
        List<DC_UnitAction> actions = ((DC_UnitObj) getObj()).getActionMap().get(group.getType());
        if (actions == null) {
            return null;
        }
        return splitList(actions);
    }

    @Override
    protected G_Component createEmptyPageComponent() {
        LinkedList<DC_UnitAction> list = new LinkedList<>();
        ListMaster.fillWithNullElements(list, PAGE_SIZE);
        return new DC_UnitActionPanel(list);
    }

    @Override
    protected G_Component createPageComponent(List<DC_UnitAction> list) {
        return new DC_UnitActionPanel(list);
    }

    public int getPanelHeight() {
        return GuiManager.getSmallObjSize() * WRAP;
    }

    public int getPanelWidth() {
        return PAGE_SIZE / WRAP * GuiManager.getSmallObjSize();
    }

}
