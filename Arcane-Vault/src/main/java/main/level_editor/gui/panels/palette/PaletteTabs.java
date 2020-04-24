package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;
import eidolons.libgdx.StyleHolder;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XmlNodeMaster;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PaletteTabs extends TabbedPane implements TabbedPaneListener {

    private static final String GROUP_PREF = "---";
    private final PaletteTypesTable holder;
    private final int groupTabsEnd;
    private final List<PaletteTab> currentTabs;
    Map<PaletteTab, List<PaletteTab>> tabMap = new LinkedHashMap<>();
    private Tab lastGroupTab;

    public PaletteTabs(PaletteTypesTable holder, OBJ_TYPE TYPE ) {
        super(StyleHolder.getHorTabStyle());
        this.holder = holder;
        addListener(this);
        getTable().setWidth(1200);
        getTabsPane().setWidth(600);
        List<String> tabs = DataManager.getTabGroups(TYPE);
        tabs.add("Custom");
        groupTabsEnd = tabs.size();
        for (String tabName : tabs) {
            PaletteTab tab;
            getTabs().add(tab = new PaletteTab(StringMaster.wrap(GROUP_PREF, tabName), null));
            addTab(tab, 0);

            List<PaletteTab> groupTabs = new ArrayList<>();

            tabMap.put(tab, groupTabs);
            Set<String> tabNames = null;
            if (tabs.get(tabs.size() - 1) == tabName) {
                List<File> files = FileManager.getFilesFromDirectory(PathFinder.getEditorWorkspacePath(),
                        false);
                for (File file : files) {
                    String data = FileManager.readFile(file);
                    data=  data.split("METADATA:")[0];
                    Document doc = XML_Converter.getDoc(data);
                    List<ObjType> types = new ArrayList<>();
                    for (Node n : XmlNodeMaster.getNodeListFromFirstChild(doc, true)) {
                    String s = XmlNodeMaster.getNodeList (n, true).stream().map(
                            node -> XML_Formatter.restoreXmlNodeName(node.getNodeName()
                            )).collect(Collectors.joining(";"));
                        types.addAll(DataManager.toTypeList(s , C_OBJ_TYPE.BF_OBJ_LE));
                    }
                    groupTabs.add(new PaletteTab(file.getName(), types));
                }
                break;
            }
            tabNames = DataManager.getSubGroups(tab.getTabTitle().substring(GROUP_PREF.length(), tab.getTabTitle().length() - GROUP_PREF.length()));
            for (String subGroup : tabNames) {
                List<ObjType> types = DataManager.getTypesSubGroup(TYPE, subGroup);
                groupTabs.add(new PaletteTab(subGroup, types));
            }

        }
        currentTabs = new ArrayList<>();
    }

    @Override
    public TabbedPaneTable getTable() {
        return super.getTable();
    }

    @Override
    public void switchedTab(Tab tab) {

        if (tab instanceof PaletteTab) {
            if (tab.getTabTitle().contains(GROUP_PREF)) {
                if (lastGroupTab != null) {
                    for (Tab subTab : currentTabs) {// tabMap.get(lastGroupTab)) {
                        try {
                            remove(subTab);
                            getTabs().removeValue(subTab, true);
                            main.system.auxiliary.log.LogMaster.log(1, ">>> Removed tab: " + subTab.getTabTitle());
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }
                    currentTabs.clear();
                }
                List<PaletteTab> newTabs = tabMap.get(tab);
                for (PaletteTab newTab : newTabs) {
                    addTab(newTab, groupTabsEnd);
                    getTabs().add(newTab);
                    currentTabs.add(newTab);
                    main.system.auxiliary.log.LogMaster.log(1, ">>> Added tab: " + newTab.getTabTitle());
                }
//                switchTab(newTabs.get(0));
                lastGroupTab = tab;
            } else
                holder.setUserObject(((PaletteTab) tab).getTypes());
        }
    }

    @Override
    protected void addTab(Tab tab, int index) {
        super.addTab(tab, index);
    }


    @Override
    public void removedTab(Tab tab) {


    }

    @Override
    public void removedAllTabs() {

    }


    public class PaletteTab extends Tab {


        private String name;
        List<ObjType> types;

        public PaletteTab(String name, List<ObjType> types) {
            this.name = name;
            this.types = types;
        }

        @Override
        public boolean isCloseableByUser() {
            return false;
        }

        public List<ObjType> getTypes() {
            return types;
        }

        @Override
        public String getTabTitle() {
            return name;
        }

        @Override
        public Table getContentTable() {
            return null;
        }
    }
}
