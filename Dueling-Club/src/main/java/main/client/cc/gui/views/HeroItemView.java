package main.client.cc.gui.views;

import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.lists.VendorListsPanel;
import main.client.cc.gui.misc.BorderChecker;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;

import java.util.Comparator;
import java.util.List;

public abstract class HeroItemView extends HeroView implements BorderChecker, Runnable {
    protected VendorListsPanel vendorPanel;
    protected DC_PagedInfoPanel ip;
    protected ItemListManager itemManager;
    private boolean showAll;
    private boolean responsive;

    public HeroItemView(Unit hero, boolean responsive, boolean showAll) {
        super(hero);
        this.responsive = responsive;
        this.showAll = showAll;
        init();
    }

    protected int getPoolWidth() {
        return 5;
    }

    @Override
    public void refresh() {
        super.refresh();
        // ip.refresh();
        // panel.refresh();
    }

    public HC_TabPanel getTabbedPanel() {
        return vendorPanel.getTabs();
    }

    @Override
    public void init() {

        if (vendorPanel == null) {
            initInfoPanel();

            setItemManager(new ItemListManager(hero, getTYPE(), getPROP()));

            this.vendorPanel = generatePanel();
            this.vendorPanel.initialize();
        }
        add(vendorPanel, "id panel, pos ip.x2 0");
        // add/remove comp etc
        // add(ip, "id ip, pos 0 0");
        // add(poolComp, "id pool,pos 0 ip.y2");
        new Thread(this).start();
    }

    public VendorListsPanel getVendorPanel() {
        return vendorPanel;
    }

    @Override
    public void run() {
        vendorPanel.setBorderChecker(this);
        getItemManager().setView(this);
    }

    @SuppressWarnings("serial")
    protected VendorListsPanel generatePanel() {
        return new VendorListsPanel(hero, getTYPE(), getPROP(), isResponsive(), isShowAll(),
         getItemManager(), getTypeFilter()) {
            public Filter<ObjType> getSpecialFilter() {
                return getTypeFilter();
            }

            protected boolean checkSpecial(String name) {
                return isSpecial(name);
            }

            protected List<ObjType> getSpecialData() {
                return getAdditionalTypesData();
            }

            public Comparator<? super ObjType> getSorter() {
                return getItemSorter();

            }
        };
    }

    protected boolean isSpecial(String name) {
        return false;
    }

    protected Filter<ObjType> getTypeFilter() {
        if (getFilterConditions() != null) {
            return new Filter<>(hero.getGame(), getFilterConditions());
        }
        return null;
    }

    protected List<ObjType> getAdditionalTypesData() {
        return null;
    }

    protected Comparator<? super ObjType> getItemSorter() {
        if (getSortingParam() == null) {
            return null;
        }
        return new Comparator<ObjType>() {
            public int compare(ObjType o1, ObjType o2) {

                if (o1.getIntParam(getSortingParam()) > o2.getIntParam(getSortingParam())) {
                    return 1;
                }
                if (o1.getIntParam(getSortingParam()) == o2.getIntParam(getSortingParam())) {
                    return 0;
                }
                return -1;
            }
        };
    }

    protected PARAMETER getSortingParam() {
        return null;
    }

    protected Condition getFilterConditions() {
        return null;
    }

    protected void initInfoPanel() {
        ip = new DC_PagedInfoPanel(hero); // Paged TODO
    }

    protected abstract OBJ_TYPE getTYPE();

    protected abstract PROPERTY getPROP();

    public synchronized boolean isShowAll() {
        return showAll;
    }

    public synchronized void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public synchronized boolean isResponsive() {
        return responsive;
    }

    public synchronized void setResponsive(boolean responsive) {
        this.responsive = responsive;
    }

    public ItemListManager getItemManager() {
        return itemManager;
    }

    public void setItemManager(ItemListManager itemManager) {
        this.itemManager = itemManager;
    }

    protected Filter<ObjType> getSpecialTypeFilter() {
        return null;
    }

}
