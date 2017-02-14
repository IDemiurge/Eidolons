package main.client.cc.gui.lists;

import main.client.cc.gui.misc.BorderChecker;
import main.client.cc.gui.neo.tabs.HC_TabPanel;
import main.client.cc.gui.neo.tabs.TabChangeListener;
import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.ARMOR_TYPE;
import main.content.CONTENT_CONSTS.ITEM_SHOP_CATEGORY;
import main.content.CONTENT_CONSTS.ITEM_TYPE;
import main.content.CONTENT_CONSTS.WORKSPACE_GROUP;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.xml.XML_Reader;
import main.elements.Filter;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.system.FilterMaster;
import main.system.auxiliary.Chronos;

import java.awt.*;
import java.util.*;
import java.util.List;

// used for SPELLS, ITEMS and SKILLS (maybe even mercs!)
public class VendorListsPanel extends G_Panel implements TabChangeListener {
    protected static final Dimension VENDOR_DIMENSION = new Dimension(550, 885);
    protected static final int DEFAULT_PAGE_SIZE = 6;
    protected static final String[] HIDDEN_ITEM_GROUPS_DEFAULT = {"Custom",};
    protected static final String[] HIDDEN_GROUPS = {"Natural", "Perk", "Exotic",};
    protected static final String[] HIDDEN_SUBGROUPS = {"Multiclass", "Void",};

    protected DC_HeroObj hero;
    protected boolean responsive;
    protected Filter<ObjType> filter;
    protected boolean showAll;
    protected Map<String, G_Panel> tabMap = new XLinkedMap<>();
    protected BorderChecker borderChecker;
    protected Map<String, Map<String, HC_PagedListPanel>> listMaps = new XLinkedMap<>();
    protected int pageSize = DEFAULT_PAGE_SIZE;
    protected Comparator<? super ObjType> sorter;
    protected Filter<ObjType> specialFilter;
    protected String selectedTabName;
    OBJ_TYPE TYPE;
    PROPERTY prop;
    PROPERTY listDividingProp;
    HC_TabPanel tabs;
    int itemSize;
    int rowsPerList = 2;
    ItemListManager manager;

    public VendorListsPanel(DC_HeroObj hero, OBJ_TYPE TYPE, PROPERTY prop, boolean responsive,
                            boolean showAll, ItemListManager manager) {
        this(hero, TYPE, prop, responsive, showAll, manager, null);
    }

    public VendorListsPanel(DC_HeroObj hero, OBJ_TYPE TYPE, PROPERTY prop, boolean responsive,
                            boolean showAll, ItemListManager manager, Filter<ObjType> filter) {
        this.hero = hero;
        this.TYPE = TYPE;
        this.prop = prop;
        this.listDividingProp = TYPE.getSubGroupingKey();
        this.responsive = responsive;
        this.setShowAll(showAll);
        this.manager = manager;
        this.filter = filter;
        // initSorter(); done via anonymous class in HeroItemView
    }

    public void initialize() {
        initTabs();
        addComponents();

    }

    protected void initSorter() {
        if (TYPE.getParam() != null) {
            sorter = new Comparator<ObjType>() {
                @Override
                public int compare(ObjType o1, ObjType o2) {
                    if (TYPE.getParam() == null) {
                        return 0;
                    }
                    int i1 = o1.getIntParam(TYPE.getParam());
                    int i2 = o2.getIntParam(TYPE.getParam());
                    if (i1 < i2) {
                        return -1;
                    }
                    if (i1 > i2) {
                        return 1;
                    }
                    return 0;
                }
            };
        }

    }

    @Override
    public void tabSelected(String name) {
        selectedTabName = name;
        initTab(selectedTabName);
    }

    @Override
    public void tabSelected(int index) {

    }

    public String getSelectedTabName() {
        if (selectedTabName == null) {
            return getTabGroups().get(0);
        }
        return selectedTabName;
    }

    public Dimension getMaximumSize() {
        return VENDOR_DIMENSION;
    }

    public Dimension getMinimumSize() {
        return VENDOR_DIMENSION;
    }

    public Dimension getPreferredSize() {
        return VENDOR_DIMENSION;
    }

    protected List<String> getTabGroups() {

        return DataManager.getTabsGroup(TYPE);
    }

    protected void initTabs() {
        tabs = new HC_TabPanel();
        tabs.setENUM(DataManager.getGroupingClass(TYPE));
        tabs.setPageSize(pageSize);
        tabs.setPanelSize(VENDOR_DIMENSION);
        List<String> tabGroup = getTabGroups();
        for (String tabName : tabGroup) {
            if (checkTab(tabName)) {
                addTab(tabName);
            }
        }
        tabs.setChangeListener(this);
        if (tabGroup.isEmpty()) {
            return;
        }
        initTab(tabGroup.get(0));
        tabs.select(0);
        selectedTabName = tabGroup.get(0);
    }

    public boolean checkTab(String tabName) {
        for (String tab : HIDDEN_GROUPS) {
            if (tabName.equalsIgnoreCase(tab)) {
                return false;
            }
        }

        return true;
    }

    protected void addTab(String title) {
        G_Panel comp = new G_Panel();
        tabMap.put(title, comp);
        tabs.addTab(title, comp);
    }

    public boolean checkList(String name, boolean potential) {
        return hero.checkItemGroup(prop, listDividingProp, name, potential, TYPE);

    }

    public List<String> getCurrentListGroup() {
        return getListGroup(selectedTabName);
    }

    protected List<String> getListGroup(String key) {
        // if (Arrays.asList(ItemGenerator.CUSTOM_GROUPS).contains(key))
        if (key.equalsIgnoreCase(OBJ_TYPES.JEWELRY.getName())) {
            return Arrays.asList(DataManager.CUSTOM_JEWELRY_GROUPS);
        }

        List<String> group = new LinkedList<>(XML_Reader.getTreeSubGroupMap().get(key));

        return group;
    }

    public Map<String, HC_PagedListPanel> initListMap(String tabName) {
        List<String> group = getListGroup(tabName);
        LinkedList<String> specialLists = new LinkedList<>();
        Map<String, HC_PagedListPanel> map = new XLinkedMap<>();
        for (String listName : group) {
            if (checkSpecial(listName)) {
                specialLists.add(listName);
                continue;
            }

            if (!responsive) {
                if (!checkList(listName, false)) {
                    continue;
                }
            }
            if (!showAll) {
                if (!checkList(listName, true)) {
                    continue;
                }
            }

            List<String> types = DataManager.getTypesSubGroupNames(TYPE, listName);
            if (Launcher.ILYA_MODE) {
                if (TYPE == OBJ_TYPES.SPELLS || TYPE == OBJ_TYPES.SKILLS) {
                    FilterMaster.filterByProp(types, G_PROPS.WORKSPACE_GROUP.getName(), ""
                            + WORKSPACE_GROUP.DESIGN, TYPE, true);
                    FilterMaster.filterByProp(types, G_PROPS.WORKSPACE_GROUP.getName(), ""
                            + WORKSPACE_GROUP.IMPLEMENT, TYPE, true);

                    FilterMaster.filterByProp(types, PROPS.ITEM_SHOP_CATEGORY.getName(), ""
                            + ITEM_SHOP_CATEGORY.SPECIAL, TYPE, true);
                    // so you see, it wouldn't be hard to filter the items for
                    // each shop in macro
                }
            }
            if (types == null) {
                // types = DataManager.getTypeGroupNames(TYPE, tabName); another
                // shot in the leg!
                continue;
            }
            if (types.isEmpty()) {
                continue;
            }
            List<ObjType> data;

            // if (checkSpecial(listName))
            // data = getSpecialData();
            // else {

            OBJ_TYPE T = TYPE;
            if (T instanceof C_OBJ_TYPE) {
                if (tabName.equalsIgnoreCase(OBJ_TYPES.JEWELRY.getName())) {
                    T = OBJ_TYPES.JEWELRY;
                }
                if (tabName.equalsIgnoreCase(ITEM_TYPE.ALCHEMY.toString())) {
                    T = OBJ_TYPES.ITEMS;
                }
                // if (TYPE != OBJ_TYPES.SPELLS)
                if (tabName.equalsIgnoreCase(ARMOR_TYPE.LIGHT.toString())
                        || tabName.equalsIgnoreCase(ARMOR_TYPE.HEAVY.toString())) {
                    T = OBJ_TYPES.ARMOR;
                }
            }
            data = DataManager.toTypeList(types, T); // TODO is there a
            // better fix?

            if (getFilter() != null) {
                data = getFilter().filter(data);
            }

            if (getSpecialFilter() != null) {
                data = getSpecialFilter().filter(data);
            }
            // }

            if (getSorter() != null) {
                try {
                    Collections.sort(data, getSorter());
                } catch (Exception e) {
                    e.printStackTrace();
                    // e.printStackTrace();
                }
            }

            putList(listName, data, map);
        }

        for (String listName : specialLists) {
            List<ObjType> data = getSpecialData();
            if (getSorter() != null) {
                Collections.sort(data, getSorter());
            }
            putList(listName, data, map);
        }
        return map;
    }

    protected boolean checkSpecial(String name) {
        return false;
    }

    protected List<ObjType> getSpecialData() {
        return null;
    }

    protected void putList(String listName, List<ObjType> data, Map<String, HC_PagedListPanel> map) {
        HC_PagedListPanel list =
                // new HeroListPanel(listName, hero, responsive,
                // vertical, rowsPerList, itemSize, data);
                new HC_PagedListPanel(HC_LISTS.VENDOR, hero, manager, data, listName);
        list.setTYPE(TYPE);
        list.setBorderChecker(getBorderChecker());
        map.put(listName, list);
        manager.add(list);
    }

    public void resetTab(String title) {
        listMaps.remove(title);
        tabMap.get(title).removeAll();
        initTab(title);
        tabMap.get(title).revalidate();
    }

    protected void initTab(String title) {
        Map<String, HC_PagedListPanel> map = listMaps.get(title);
        if (map == null) {
            Chronos.mark(title);
            map = initListMap(title);
            Chronos.logTimeElapsedForMark(title);
            G_Panel tab = tabMap.get(title);

            for (String listName : map.keySet()) {

                tab.add(map.get(listName), "wrap");
            }
            listMaps.put(title, map);
        } else {
            try {
                map.values().iterator().next().getCurrentList().getList().setSelectedIndex(0);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

    }

    protected void addComponents() {
        add(tabs, "id tabs, pos 0 0");
        addControls();

    }

    protected void addControls() {
        // TODO Auto-generated method stub

    }

    public BorderChecker getBorderChecker() {
        return borderChecker;
    }

    public void setBorderChecker(BorderChecker borderChecker) {
        this.borderChecker = borderChecker;
        for (Map<String, HC_PagedListPanel> map : listMaps.values()) {
            for (HC_PagedListPanel list : map.values()) {
                list.setBorderChecker(this.borderChecker);
            }
        }
    }

    public Filter<ObjType> getSpecialFilter() {
        return specialFilter;
    }

    public Filter<ObjType> getFilter() {
        return filter;
    }

    public void setFilter(Filter<ObjType> filter) {
        this.filter = filter;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public synchronized boolean isResponsive() {
        return responsive;
    }

    public synchronized void setResponsive(boolean responsive) {
        this.responsive = responsive;
    }

    public synchronized OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public synchronized void setTYPE(OBJ_TYPE tYPE) {
        TYPE = tYPE;
    }

    public Comparator<? super ObjType> getSorter() {
        return sorter;
    }

    public void setSorter(Comparator<? super ObjType> sorter) {
        this.sorter = sorter;
    }

    public HC_TabPanel getTabs() {
        return tabs;
    }

}
