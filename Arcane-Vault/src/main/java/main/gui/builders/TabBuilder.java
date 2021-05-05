package main.gui.builders;

import main.content.C_OBJ_TYPE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.TableDataManager;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.data.xml.XML_File;
import main.data.xml.XML_Reader;
import main.entity.type.ObjType;
import main.handlers.util.TypeFilter;
import main.launch.ArcaneVault;
import main.launch.AvConsts;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.G_TabbedPanel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.Err;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.launch.CoreEngine;
import main.system.util.DialogMaster;
import main.utilities.workspace.Workspace;
import main.utilities.workspace.WorkspaceManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static main.system.ExceptionMaster.printStackTrace;

public class TabBuilder extends Builder implements ChangeListener {

    private static Class<?> TOP_ENUM_CLASS = DC_TYPE.class;
    int specialTabsAdded = 0;
    private List<String> tabNames;
    private final boolean top;
    private final String type;
    private PROPERTY groupingKey;
    private XLinkedMap<String, Component> tabmap;
    private boolean dirty = true;
    private G_TabbedPanel workspaceTab;
    private G_TabbedPanel searchTab;
    private List<Workspace> activeWorkspaces;
    private boolean levelEditor;
    private G_TabbedPanel autoWorkspaceTab;
    private int selectedIndex;
    private int prevIndex;

    public TabBuilder(List<String> tabNames, String key) {
        this(key);
        this.tabNames = tabNames;
    }

    public TabBuilder(String key) {
        this.top = (key == null);
        this.type = key;
        comp = new JTabbedPane();
        if (XML_Reader.isMacro()) {
            TOP_ENUM_CLASS = MACRO_OBJ_TYPES.class;
        }
    }

    @Override
    public void init() {

        List<Builder> list = new ArrayList<>();

        if (top) {
            // if (ArcaneVault.isTestMode())
            if (tabNames == null) {
                tabNames = new ArrayList<>(XML_Reader.getTypeMaps().keySet());
                ListMaster.removeNullElements(tabNames);
            }
            if (!isLevelEditor())
            // if (ArcaneVault.isMacroMode())
            {
                Collections.sort(tabNames, new EnumMaster<>().getEnumSorter(TOP_ENUM_CLASS));

                LogMaster.log(1,
                 "                                  <><><><><>  after sort " + tabNames);
            }
            for (String sub : tabNames) {
                TabBuilder tabBuilder = new TabBuilder(sub);
                tabBuilder.setLevelEditor(levelEditor);
                list.add(tabBuilder);
                // tabNames.add(generic);
            }

        } else {
            if (tabNames == null) {
                try {
                    tabNames = TableDataManager.getTreeTabSubGroups(type);
                } catch (Exception e) {
                    LogMaster.log(1, "Tab Sub Groups failed for " + type);
                    printStackTrace(e);
                    // throw new RuntimeException();
                    return;
                }
            }
            groupingKey = DataManager.getGroupingKey(type);
            if (!isLevelEditor())
            // if (ArcaneVault.isMacroMode())
            {
                try {
                    Chronos.mark("sortBySubgroupEnum " + groupingKey.getName());
                    sortBySubgroupEnum();
                    Chronos.logTimeElapsedForMark("sortBySubgroupEnum " + groupingKey.getName());

                    LogMaster.log(1, "sortBySubgroupEnum SUCCESS on "
                     + tabNames + " by " + groupingKey.getName());
                } catch (Exception e) {
                    printStackTrace(e);
                    LogMaster.log(1, "sortBySubgroupEnum FAILED on "
                     + tabNames + " by " + groupingKey.getName());
                }
            }
            for (String sub : tabNames) {
                OBJ_TYPE TYPE = ContentValsManager
                        .getOBJ_TYPE(type);
                List<ObjType> types = DataManager.getTypesGroup(TYPE, sub);
                list.add(new TreeViewBuilder(types, TYPE, sub));

            }
        }
        LogMaster.log(0, "Tabs: " + TableDataManager.getTreeTabGroups());

        infoArray = new String[tabNames.size()];

        tabNames.toArray(infoArray);

        builderArray = new Builder[tabNames.size()];
        list.toArray(builderArray);
        builders = new MapMaster<Builder, LayoutInfo>().constructMap(list,
         new ArrayList<>());
    }

    // override build()?

    private void sortBySubgroupEnum() {
        Class<?> ENUM_CLASS = EnumMaster.getEnumClass(groupingKey.getName());
        if (ENUM_CLASS != null) {
            Collections.sort(tabNames, new EnumMaster<>().getEnumSorter(ENUM_CLASS));
        }

    }

    @Override
    public JComponent build() {
        getTabbedPane().addChangeListener(this);

        init();
        int i = -1;
        tabmap = new XLinkedMap<>();

        for (Builder builder : builderArray) {

            i++;
            LogMaster.log(0,

             "building tab: " + builder.getClass().getSimpleName() + infoArray[i]);
            if (top) {

                int code = -1;
                try {
                    code = ContentValsManager.getTypeCode(infoArray[i]);
                } catch (Exception e) {
                    printStackTrace(e);
                }
                if (code == -1) {
                    continue;
                }
                Component component;

                component = builder.build();

                tabmap.put(infoArray[i], component);
                if (!ContentValsManager.getOBJ_TYPE(infoArray[i]).isHidden()) {
                    getTabbedPane().addTab(infoArray[i], component);
                }

            } else {
                addTab(infoArray[i], builder.build());

            }

        }
        if (top) {
            if (!levelEditor) {
                sortTabs();
            }
            if (WorkspaceManager.ADD_WORKSPACE_TAB_ON_INIT) {
                initWorkspaceTabs();
            }
        }

        ready = true;
        return comp;

    }

    private void sortTabs() {
        if (ArcaneVault.isSelectiveInit()) {
            return;
            // if (ArcaneVault.getTypes() != null) {
            // List<String> list =
            // StringMaster.openContainer(ArcaneVault.getTypes());
            // code = new SearchMaster<String>().getIndex(tabName, list);
            // }
        }
        for (String tabName : tabmap.keySet()) {
            if (ContentValsManager.getOBJ_TYPE(tabName).isHidden()) {
                continue;
            }
            Component component = tabmap.get(tabName);
            addTypeTab(tabName, component);

        }
    }

    private void addTypeTab(String tabName, Builder builder) {
        addTypeTab(tabName, builder.build());
        List<Builder> list = new ArrayList<>(builderArray.length + 1);
        for (Builder b : builderArray) {
            list.add(b);
        }
        list.add(builder );
        builderArray = list.toArray(new Builder[builderArray.length + 1]);
    }
    private void addTypeTab(String tabName, Component component) {
        int code = getTabbedPane().getTabCount();//ContentManager.getTypeCode(tabName);

        ImageIcon icon = ImageManager.getIcon(ContentValsManager.getTypeImage(tabName));

        String text = "";
        if (!ImageManager.isValidIcon(icon)) {
            text = ("" + tabName.charAt(0)).toUpperCase();
        }
        getTabbedPane().insertTab(text, icon, component, tabName, code
//                Math.min(code, getTabbedPane().getTabCount() - 1)
        );
        getTabbedPane().addChangeListener(this);
        tabmap.put(tabName, component);
    }

    public void addSearchTopTab() {
        searchTab = new G_TabbedPanel(new Dimension(AvConsts.TREE_WIDTH,
         AvConsts.TABLE_HEIGHT - AvConsts.TREE_HEIGHT / 20));
        searchTab.getTabs().addChangeListener(this);
        try {
            getTabbedPane().insertTab("", ImageManager.STD_IMAGES.SEARCH.getIcon(),

             searchTab, "Searches", getTabbedPane().getTabCount());
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    public void addWorkspaceTopTab() {
        // workspace.getGroup()
        workspaceTab = new G_TabbedPanel(new Dimension(AvConsts.TREE_WIDTH,
         AvConsts.TABLE_HEIGHT - AvConsts.TREE_HEIGHT / 20));
        workspaceTab.getTabs().addChangeListener(this);
        getTabbedPane().insertTab("", new ImageIcon(STD_IMAGES.CROSS.getImage()), workspaceTab,
         "Workspaces", getTabbedPane().getTabCount());
        // ImageManager.getSizedIcon(ArcaneVault.ICON_PATH, 32)
    }

    public void addAutoWorkspaceTab(Workspace ws) {
        // workspace.getGroup()
        autoWorkspaceTab = new G_TabbedPanel(new Dimension(AvConsts.TREE_WIDTH,
         AvConsts.TABLE_HEIGHT - AvConsts.TREE_HEIGHT / 20));
        autoWorkspaceTab.getTabs().addChangeListener(this);
        ws.setTabComp(autoWorkspaceTab);
        getTabbedPane().insertTab("", new ImageIcon(VISUALS.GEARS.getImage()), autoWorkspaceTab,
         "Auto Workspaces", getTabbedPane().getTabCount());

        autoWorkspaceTab.add(generateWorkspaceTree(ws), "pos 0 0 " + AvConsts.TREE_WIDTH + " "
         + AvConsts.TABLE_HEIGHT + "-" + AvConsts.TREE_HEIGHT + "/20");

    }

    public void initWorkspaceTabs() {
        // TODO Workspace ws =
        // ArcaneVault.getWorkspaceManager().initAutoWorkspace();
        // if (ws != null) {
        // addAutoWorkspaceTab(ws);
        // }
        ArcaneVault.getWorkspaceManager().initDefaultWorkspaces();
        ArcaneVault.getWorkspaceManager().initSearches();

        // for (Workspace workspace : ArcaneVault.getWorkspaceManager()
        // .getActiveWorkspaces()) {
        // addWorkspaceTab(workspace);
        // }
    }

    public void addWorkspaceTab(Workspace workspace) {
        if (getActiveWorkspaces().contains(workspace)) {
            return;
        }
        if (workspace.isSearch()) {
            if (searchTab == null) {
                addSearchTopTab();
            }
        } else if (workspaceTab == null) {
            addWorkspaceTopTab();
        }
        G_Panel tabComp = new G_Panel();
        tabComp.add(generateWorkspaceTree(workspace), "pos 0 0 " + AvConsts.TREE_WIDTH + " "
         + AvConsts.TABLE_HEIGHT + "-" + AvConsts.TREE_HEIGHT + "/20");
        (workspace.isSearch() ? searchTab : workspaceTab).addTab(tabComp, workspace.getName());
        workspace.setTabComp(tabComp);
    }

    private void initSelectedWorkspace(G_TabbedPanel tab) {
        G_Panel tabComp = (G_Panel) tab.getTabs().getSelectedComponent();
        Workspace workspace = ArcaneVault.getWorkspaceManager().getWorkspaceByTab(tabComp);
        if (workspace != null) {
            tabComp.removeAll();
            Component generateWorkspaceTree = generateWorkspaceTree(workspace);
            tabComp.add(generateWorkspaceTree, "pos 0 0 " + AvConsts.TREE_WIDTH + " "
             + AvConsts.TABLE_HEIGHT + "-" + AvConsts.TREE_HEIGHT + "/20");
            tabComp.revalidate();

            workspace.setDirty(false);
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!top) {
            if (!isLevelEditor()) {
                if (ArcaneVault.getMainBuilder().isNodesDirty()) {
                    rebuildTab();
                }
            }
        }

        getTreeBuilder().getInfoTree().refresh();

        // getSelectedTabName()
        if (getTabbedPane().getSelectedComponent() instanceof G_TabbedPanel) {
            G_TabbedPanel tab = (G_TabbedPanel) getTabbedPane().getSelectedComponent();
            // if (workspaceTab != null) {
            if (tab.getTabs().getSelectedComponent() != null
             && (e.getSource() == tab.getTabs() || getTabbedPane().getSelectedComponent() == tab)) {
                Workspace workspace = ArcaneVault.getWorkspaceManager().getWorkspaceByTab(
                 (G_Panel) tab.getTabs().getSelectedComponent());
                if (workspace != null) {
                    if (workspace.isDirty()) {
                        initSelectedWorkspace(tab);
                    }
                }
            }
        }
    }

    private void rebuildTab() {
        int index = getTabbedPane().getSelectedIndex();
        builderArray[index].build(); // if types had been added or removed...
        // TODO
        // new TreeViewBuilder(typesDoc, generic, type)
    }

    private Component generateWorkspaceTree(Workspace workspace) {
        return new TreeViewBuilder(workspace).build();
    }

    // public void addSearchTab(List<ObjType> list) {
    // if (searchTab==null){
    // addSearchTopTab();
    // }
    // component =
    //
    // searchTab.getTabs().addTab(title, component);
    //
    // }
    //
    // private void addSearchTopTab() {
    // // TODO Auto-generated method stub
    //
    // }
    public void removeTab(int i) {
        ((JTabbedPane) this.comp).removeTabAt(i);

    }

    private void addTab(String title, JComponent comp) {
        Icon icon = null;
        if (type.equalsIgnoreCase("Skills")) {
            icon = ImageManager.getMasteryGroupIcon(title);
            title = "";
        }
        ((JTabbedPane) this.comp).addTab(title, icon, comp);

    }

    public String getSelectedTabName() {

        if (!ready || !top) {
            return "";
        }

        try {
            String name = MapMaster.getInvertedMap(tabmap).get(
             getTabbedPane().getSelectedComponent());
            // workspace
            return name;
        } catch (Exception ex) {
            printStackTrace(ex);
            return "";
        }
    }

    public JTree getTree() {
        JTree tree;
        if (top) {
            if (getTabbedPane().getSelectedIndex() >= builderArray.length) {
                G_Panel tabComp = (G_Panel) getTabbedPane().getSelectedComponent();
                tree = (JTree) tabComp.getComponent(0);
            } else {
                tree = ((TabBuilder) builderArray[getTabbedPane().getSelectedIndex()]).getTree();
            }

        } else {
            int index = getTabbedPane().getSelectedIndex();
            TreeViewBuilder selectedTree = ((TreeViewBuilder) builderArray[index]);
            tree = selectedTree.getTree();
        }
        if (tree == null) {
            Err.info("NULL TREE OBJECT!");

        }
        return tree;
    }

    // TODO mouselistener on filter tabs?
    public void removeSelectedTab() {
        if (top) {
            ((TabBuilder) getBuilderArray()[getSelectedIndex()]).removeTab(getSelectedIndex());
        } else {
            removeTab(getSelectedIndex());
        }
    }

    public TreeViewBuilder getPrevTreeBuilder() {
        return getTreeBuilder(true);
    }
    public TreeViewBuilder getTreeBuilder( ) {
        return getTreeBuilder(false);
    }
    public TreeViewBuilder getTreeBuilder(boolean prev) {
        int selectedIndex =prev? prevIndex: getSelectedIndex( );
        if (top) {
            return ((TabBuilder) getBuilderArray()[selectedIndex]).getTreeBuilder();
        }
        return (TreeViewBuilder) getBuilderArray()[selectedIndex];
    }

    public int getSelectedIndex() {
        //TODO this is shit - overwrites on GETT!!!! listen instead!!
        prevIndex  = selectedIndex;
        selectedIndex = ((JTabbedPane) comp).getSelectedIndex();
        return selectedIndex;

    }

    @Override
    public void refresh() {
        if (!top) {
            if (isDirty()) {
                if (TypeFilter.isFiltering()) {
                    addTab(TypeFilter.TAB_TITLE + TypeFilter.filterN, TypeFilter.getTree());
                }
                setDirty(false);
            }
        }

    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean b) {
        dirty = b;
    }

    @Override
    public void reload() {

    }

    @Override
    public void dataChanged() {

    }

    public JTabbedPane getTabbedPane() {
        return (JTabbedPane) comp;
    }

    public List<Workspace> getActiveWorkspaces() {
        if (activeWorkspaces == null) {
            activeWorkspaces = new ArrayList<>();
        }
        return activeWorkspaces;
    }

    public G_TabbedPanel getWorkspaceTab() {
        return workspaceTab;
    }

    public TabBuilder getSubTabs(int code) {
        if (!top) {
            return null;
        }
        return (TabBuilder) getBuilderArray()[code];
    }

    public void removeFilter() {

    }

    public void addFilter(TypeFilter filter) {
        if (top) {
            if (filter.getTYPE() instanceof C_OBJ_TYPE) {

            }
            for (Builder sub : builders.isEmpty() ? builderArray : builders.keySet().toArray(new Builder[builders.size()])) {

                TabBuilder builder = (TabBuilder) sub;
                if (builder.getType().equalsIgnoreCase(filter.getTYPE().getName())) {
                    builder.addFilter(filter);
                    break;
                }
            }
            // getSubTabs(filter.getTYPE().getCode()).addFilter(filter);
        } else {
            List<ObjType> types = filter.getTypes();
            if (!types.isEmpty()) {
                getTabbedPane().addTab("F", new TreeViewBuilder(new Workspace("F", types, true)).build());
            } else {
                DialogMaster.error("No types matching " + filter.getCondition());
            }
        }

    }

    public void addTab(Class<?> CLASS, String name) {
        // if (XML_Reader.getXmlMap().containsKey(name))
        // return;
        String path = PathFinder.getTYPES_PATH();
        if (CLASS == MACRO_OBJ_TYPES.class) {
            path = PathFinder.getMACRO_TYPES_PATH();
        } else {
            if (DC_TYPE.getType(name).isTreeEditType())
                CoreEngine.compileReflectionMap();
        }
        File file = FileManager.getFile(path + name + ".xml");
        List<File> files = new ArrayList<>();
        if (file.isFile()) {
            files.add(file);
        } else {
            files = FileManager.getFiles(path, name, ".xml");
        }
        for (File sub : files) {
            XML_File xmlFile = XML_Reader.readFile(sub);
            XML_Reader.loadMap(xmlFile.getType().getName(),
             xmlFile.getContents());
        }
        TabBuilder tabBuilder = new TabBuilder(name);
        builders.put(tabBuilder, null);

        addTypeTab(name, tabBuilder);

    }

    public boolean isLevelEditor() {
        return levelEditor;
    }

    public void setLevelEditor(boolean levelEditor) {
        this.levelEditor = levelEditor;
    }

    public List<String> getTabNames() {
        return tabNames;
    }

    public boolean isTop() {
        return top;
    }

    public String getType() {
        return type;
    }

    public PROPERTY getGroupingKey() {
        return groupingKey;
    }

    public XLinkedMap<String, Component> getTabmap() {
        return tabmap;
    }

}
