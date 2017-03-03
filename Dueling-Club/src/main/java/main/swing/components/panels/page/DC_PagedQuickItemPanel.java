package main.swing.components.panels.page;

import main.entity.item.DC_QuickItemObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.graphics.GuiManager;

import java.util.List;
import java.util.Set;

public class DC_PagedQuickItemPanel extends G_PagedListPanel<DC_QuickItemObj> {

    private static final int PAGE_SIZE = 8;
    private static final int WRAP = 2;
    private static final boolean vertical = true;
    private static final int VERSION = 3;
    private DC_Game game;

    public DC_PagedQuickItemPanel(DC_Game game) {
        super(PAGE_SIZE, vertical, VERSION);
        this.game = game;
    }

    @Override
    protected List<List<DC_QuickItemObj>> getPageData() {
        if (obj instanceof Unit) {
            DequeImpl<DC_QuickItemObj> items = ((Unit) obj)
                    .getQuickItems();
            List<List<DC_QuickItemObj>> list = new ListMaster<DC_QuickItemObj>()
                    .splitList(PAGE_SIZE, items);
            if (list.isEmpty()) {
                return list;
            }
            List<DC_QuickItemObj> lastList = list.get(list.size() - 1);
            ListMaster.fillWithNullElements(lastList, PAGE_SIZE);

            return list;
        }
        return null;
    }

    // @Override
    // public void setObj(Obj obj) {
    // Obj oldObj = getObj();
    // super.setObj(obj);
    // // if (obj != oldObj)
    // // dataChanged();
    // }

    @Override
    protected G_Component createEmptyPageComponent() {
        return new QuickItemPage(getObj(), game.getState(), WRAP, PAGE_SIZE,
                null);
    }

    @Override
    protected G_Component createPageComponent(List<DC_QuickItemObj> list) {
        return new QuickItemPage(getObj(), game.getState(), WRAP, PAGE_SIZE,
                list);
    }

    public void highlight(Set<Obj> set) {
        refresh();

    }

    public void highlightsOff() {
        refresh();

    }

    @Override
    public boolean isButtonsOnBothEnds() {
        return true;
    }

    protected boolean isDoubleButtons() {
        return false;
    }

    public int getPanelHeight() {
        return PAGE_SIZE / WRAP * GuiManager.getSmallObjSize();
    }

    public int getPanelWidth() {
        return GuiManager.getSmallObjSize() * WRAP;
    }

}
