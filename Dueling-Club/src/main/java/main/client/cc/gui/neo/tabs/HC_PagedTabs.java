package main.client.cc.gui.neo.tabs;

import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;

import java.util.List;

public class HC_PagedTabs extends G_PagePanel<HC_Tab> {
    private static final int VERSION = 4;
    private HC_TabPanel tabPanel;

    public HC_PagedTabs(List<HC_Tab> tabs, HC_TabPanel tabPanel, int pageSize) {
        super(pageSize, false, VERSION);
        this.data = tabs;
        this.tabPanel = tabPanel;
    }

    public HC_PagedTabs(List<HC_Tab> tabs, HC_TabPanel tabPanel) {
        this(tabs, tabPanel, tabPanel.getPageSize());
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    // @Override
    // public void refresh() {
    // super.refresh();
    // currentComponent.refresh();
    // }
    @Override
    protected G_Component createPageComponent(List<HC_Tab> list) {
        return new TabPage(list, tabPanel);
    }

    public int getPageSize() {
        return tabPanel.getPageSize();
    }

    ;

    @Override
    protected void resetData() {
        pageData = getPageData();
    }

    @Override
    protected List<List<HC_Tab>> getPageData() {
        return splitList(data);
    }

    @Override
    protected boolean isAddControlsAlways() {
        return false;
    }

    public int getPanelWidth() {
        return getPageSize() * tabPanel.getTAB().getWidth();
    }

    public int getPanelHeight() {
        return tabPanel.getTAB().getHeight();
    }

    // @Override
    // protected int getControlDisplacementX2() {
    // return -2 * arrowWidth;
    // }

    @Override
    protected int getArrowOffsetY() {
        return super.getArrowOffsetY();
    }

    @Override
    protected int getArrowOffsetY2() {
        return getArrowOffsetY();
    }

}
