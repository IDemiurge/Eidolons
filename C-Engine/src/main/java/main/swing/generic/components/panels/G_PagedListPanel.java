package main.swing.generic.components.panels;

import main.swing.generic.components.G_Component;
import main.swing.generic.components.list.G_List;
import main.system.auxiliary.GuiManager;

public abstract class G_PagedListPanel<E> extends G_PagePanel<E> {

    public G_PagedListPanel(int pageSize, boolean vertical, int version) {
        super(pageSize, vertical, version);
    }

    @Override
    public void setCurrentComponent(G_Component currentComponent) {
        super.setCurrentComponent(currentComponent);
        getList().clearSelection();

    }

    public G_List<E> getList() {
        return ((G_ListPanel<E>) currentComponent).getList();
    }

    public int getPanelHeight() {
        return getPageSize() / getWrap() * GuiManager.getSmallObjSize();
    }

    public int getPanelWidth() {
        return getItemSize() * getWrap();
    }

    @Override
    protected int getItemSize() {
        return GuiManager.getSmallObjSize();
    }

    public int getWrap() {
        return 1;
    }
}
