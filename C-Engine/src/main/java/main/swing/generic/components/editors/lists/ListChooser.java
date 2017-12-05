package main.swing.generic.components.editors.lists;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.swing.components.PagedOptionsComp;
import main.swing.listeners.ListChooserFilterOptionListener;
import main.swing.listeners.ListChooserSortOptionListener;
import main.swing.listeners.ListChooserSortOptionListener.SORT_TEMPLATE;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ListChooser extends GenericListChooser<String> {

    private static boolean filterGenerated;
    private static ListChooser instance;
    private static boolean sortingDisabled;
    protected Class<?> ENUM_CLASS;
    private VALUE sortValue = G_PROPS.NAME;
    private PagedOptionsComp<SORT_TEMPLATE> sortOptionsComp;
    private PagedOptionsComp<?> filterOptionsComp;
    private Class<?> filterOptionClass;

    public ListChooser(List<String> listData, boolean ENUM, OBJ_TYPE TYPE) {
        this.mode = SELECTION_MODE.SINGLE;
        this.listData = listData;
        this.ENUM = ENUM;
        this.TYPE = TYPE;
        sortData();

    }

    public ListChooser(SELECTION_MODE mode, List<String> listData, OBJ_TYPE TYPE) {
        this(listData, false, TYPE);
        this.mode = mode;
    }

    public ListChooser(List<String> listData, List<String> secondListData, boolean ENUM,
                       OBJ_TYPE TYPE) {
        this.TYPE = TYPE;
        this.mode = SELECTION_MODE.MULTIPLE;
        this.listData = listData;
        this.secondListData = secondListData;
        this.ENUM = ENUM;
        sortData();

    }

    public ListChooser(SELECTION_MODE mode, List<String> listData, boolean ENUM) {
        this.mode = mode;
        this.listData = listData;
        this.ENUM = ENUM;
        sortData();
    }

    public ListChooser(SELECTION_MODE mode, Class<?> ENUM_CLASS) {
        this.mode = mode;
        this.ENUM = true;
        this.ENUM_CLASS = ENUM_CLASS;
    }

    public static void sortData(SORT_TEMPLATE t) {
        List<String> data = new ArrayList<>(instance.getListData());
        Collections.sort(data, SortMaster.getSorter(t));
        instance.getList().setData(data);

    }

    public static String chooseEnum(SELECTION_MODE MODE, List<String> data) {
        return new ListChooser(MODE, data, true).getString();
    }

    public static String chooseObj(List list, SELECTION_MODE MODE) {
        OBJ_TYPE T = ((Entity) list.get(0)).getOBJ_TYPE_ENUM();
        sortingDisabled = true;
        return new ListChooser(MODE, ListMaster.toNameList(list), T).getString();
    }

    public static String chooseEnum(Class<?> ENUM_CLASS, SELECTION_MODE MODE) {
        return new ListChooser(MODE, ENUM_CLASS).getString();

    }

    public static String chooseEnum(Class<?>... ENUM_CLASSES) {
        List<String> data = new ArrayList<>();
        for (Class<?> ENUM_CLASS : ENUM_CLASSES) {
            data.addAll(EnumMaster.getEnumConstantNames(ENUM_CLASS));
        }
        return new ListChooser(SELECTION_MODE.MULTIPLE, data, true).getString();

    }

    public static String chooseEnum(Class<?> ENUM_CLASS) {
        return chooseEnum(ENUM_CLASS, SELECTION_MODE.MULTIPLE);
    }

    public static ObjType chooseObjType(OBJ_TYPE TYPE) {
        return chooseObjType(TYPE, false);
    }

    public static ObjType chooseObjType(OBJ_TYPE TYPE, boolean filterGenerated) {
        ListChooser.setFilterGenerated(filterGenerated);
        String typeName = chooseType(TYPE);
        ListChooser.setFilterGenerated(false);
        if (typeName == null) {
            return null;
        }
        return DataManager.getType(typeName, TYPE);
    }

    public static String chooseType(OBJ_TYPE TYPE) {
        return chooseType(TYPE, SELECTION_MODE.SINGLE);
    }

    public static String chooseType(OBJ_TYPE TYPE, SELECTION_MODE mode) {
        List<ObjType> types = DataManager.getTypes(TYPE);
        List<ObjType> typesList = new ArrayList<>();
        if (filterGenerated) {
            for (ObjType t : types) {
                if (t.isGenerated()) {
                    continue;
                }
                typesList.add(t);
            }
        } else {
            typesList = types;
        }
        List<String> data = DataManager.toStringList(typesList);

        return new ListChooser(mode, data, TYPE).getString();

    }

    public static File chooseFile(String path) {
        return chooseFile(path, null, SELECTION_MODE.SINGLE);
    }

    public static String chooseFiles(String path, String filter) {
        return chooseFile(path, filter, SELECTION_MODE.MULTIPLE, true);
    }
    public static File chooseFile(String path, String filter, SELECTION_MODE mode) {
        return FileManager.getFile(path + "\\" +  chooseFile(path,filter,mode, false));
    }

        public static String chooseFile(String path, String filter, SELECTION_MODE mode, boolean dirs) {
            List<String> data = FileManager
                .getFileNames(FileManager.getFilesFromDirectory(path, dirs));
        if (filter != null) {
            for (String str : new ArrayList<>(data)) {
                if (!str.contains(filter)) {
                    data.remove(str);
                }
            }
        }
        String name = new ListChooser(mode, data, false).choose();
        if (name == null) {
            return null;
        }
        return   name ;
    }

    public static String chooseStrings(List<String> stringList) {
        return chooseStringMode(SELECTION_MODE.MULTIPLE, stringList);
    }

    public static String chooseStringMode(SELECTION_MODE mode, List<String> stringList) {
        return new ListChooser(mode, stringList, false).choose();
    }

    public static String chooseString(List<String> stringList) {
        return chooseStringMode(SELECTION_MODE.SINGLE, stringList);
    }

    public static String chooseType(OBJ_TYPE TYPE, String group) {
        return new ListChooser(DataManager.toStringList(DataManager.getTypesGroup(TYPE, group)),
                false, TYPE).getString();
    }

    public static ObjType chooseType_(OBJ_TYPE TYPE) {
        return chooseTypeFromSubgroup_(TYPE, null);
    }

    public static ObjType chooseTypeFromSubgroup_(OBJ_TYPE TYPE, String subgroup) {
        return DataManager.getType(new ListChooser((DataManager.getTypesSubGroupNames(TYPE,
                subgroup)), false, TYPE).getString(), TYPE);
    }

    public static ObjType chooseTypeFromGroup_(OBJ_TYPE TYPE, String subgroup) {
        return DataManager.getType(new ListChooser(
                (DataManager.getTypesGroupNames(TYPE, subgroup)), false, TYPE).getString(), TYPE);
    }

    public static ObjType chooseTypeObj_(OBJ_TYPE TYPE, String subgroup) {
        return DataManager.getType(new ListChooser((DataManager.getTypesSubGroupNames(TYPE,
                subgroup)), false, TYPE).getString(), TYPE);
    }

    public static ObjType chooseTypeObj(OBJ_TYPE TYPE, String group) {
        return DataManager.getType(new ListChooser((DataManager.getTypesGroupNames(TYPE, group)),
                false, TYPE).getString(), TYPE);
    }

    public static String chooseType(OBJ_TYPE TYPE, Ref ref, Condition heroCondition) {
        List<ObjType> data = (DataManager.getTypes(TYPE));
        data = new Filter<ObjType>(ref, heroCondition).filter(data);

        return new ListChooser(DataManager.toStringList(data), false, TYPE).getString();
    }

    public static ObjType chooseType_(List<ObjType> listData, OBJ_TYPE TYPE) {
        return DataManager.getType(chooseType(DataManager.toStringList(listData), TYPE), TYPE);
    }

    public static ObjType chooseType(List<ObjType> types) {
        if (types == null) {
            return null;
        }
        if (types.isEmpty()) {
            return null;
        }
        OBJ_TYPE TYPE = types.get(0).getOBJ_TYPE_ENUM();
        return DataManager.getType(chooseType(DataManager.toStringList(types), TYPE), TYPE);
    }

    public static String chooseTypes(OBJ_TYPE TYPE, String property) {
        return chooseTypes(TYPE, property, null);
    }

    public static String chooseTypesNoPool(OBJ_TYPE TYPE, String property) {
        return chooseTypes(TYPE, property, "no pool");
    }

    public static String chooseTypes(List<ObjType> types) {
        return chooseTypes(types.get(0).getOBJ_TYPE_ENUM(), DataManager.toStringList(types),
                new ArrayList<>());
    }

    public static List<ObjType> chooseTypes_(List<ObjType> types) {
        return DataManager.toTypeList(chooseTypes(types), types.get(0).getOBJ_TYPE_ENUM());
    }

    public static String chooseTypes(OBJ_TYPE TYPE, String property, String group) {
        List<String> listData = new ArrayList<>();
        if (group != "no pool") {
            listData = DataManager.toStringList(DataManager.getTypesGroup(TYPE, group));
        }
        List<String> secondListData = DataManager.toStringList(DataManager.toTypeList(property,
                TYPE));

        listData.removeAll(secondListData);
        // setDecorator(decorator)
        return chooseTypes(TYPE, listData, secondListData);

    }

    public static List<ObjType> chooseTypes_(OBJ_TYPE TYPE, List<String> listData,
                                             List<String> secondListData) {
        return DataManager.toTypeList(chooseTypes(TYPE, listData, secondListData), TYPE);
    }

    public static String chooseTypes(OBJ_TYPE TYPE, List<String> listData,
                                     List<String> secondListData) {
        return new ListChooser(listData, secondListData, false, TYPE).choose();
    }

    public static String chooseType(List<String> listData, OBJ_TYPE TYPE) {
        return new ListChooser(listData, false, TYPE).getString();
    }

    public static List<ObjType> chooseTypes_(OBJ_TYPE TYPE) {
        return chooseTypes_(TYPE, "", "");
    }

    public static List<ObjType> chooseTypes_(OBJ_TYPE TYPE, String property, String group) {
        return DataManager.toTypeList(chooseTypes(TYPE, property, group), TYPE);
    }

    public static boolean isFilterGenerated() {
        return filterGenerated;
    }

    public static void setFilterGenerated(boolean filterGenerated) {
        ListChooser.filterGenerated = filterGenerated;
    }

    public static void setSortingDisabled(boolean b) {
        sortingDisabled = b;
    }

    @Override
    public String choose() {
        instance = this;
        String result = null;
        try {
            result = super.choose();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        mods.clear();
        return result;
    }

    protected void initData() {
        if (ENUM_CLASS != null) {
            listData = EnumMaster.getEnumConstantNames(ENUM_CLASS);
        }
        sortData();

    }

    private void sortData() {
        if (sortingDisabled) {
            sortingDisabled = false;
            return;
        }
        if (getSortValue() != null && TYPE != null && !ENUM) {
            if (isGroupSorted()) {
                // if (TYPE == OBJ_TYPES.UNITS) {
                // Collections.sort(listData,
                // new EnumMaster<>().getEnumTypesSorter(false, TYPE));
                // new ListMaster<String>().inverseList(listData);
                // if (WorkspaceMaster.FILTER_UNITS_LIST) {
                // List<String> removeList = new ArrayList<>();
                // for (String s : listData) {
                // ObjType type = DataManager.getType(s,
                // OBJ_TYPES.UNITS);
                // if (!type.checkProperty(G_PROPS.WORKSPACE_GROUP))
                // removeList.add(s);
                // }
                // for (String s : removeList) {
                // listData.remove(s);
                // }
                // }
                // return;
                // }
                try {
                    Collections.sort(listData, new EnumMaster<>().getEnumTypesSorter(true, TYPE));
                    // Collections.sort(listData, new
                    // EnumMaster<>().getEnumTypesSorter(false, TYPE));
                } catch (Exception e) {
                    try {
                        Collections.sort(listData, new EnumMaster<>().getEnumTypesSorter(false,
                                TYPE));
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            } else {
                listData = SortMaster.sortByValue(listData, getSortValue(), TYPE);
            }
        } else {
            // Collections.sort(listData);
        }
        if (getTYPE() instanceof DC_TYPE) {
            checkSpecialSort((DC_TYPE) getTYPE());
        }
    }

    private void checkSpecialSort(DC_TYPE type) {
        switch (type) {
            case CHARS:

                break;
        }

    }

    private boolean isGroupSorted() {
        if (TYPE == DC_TYPE.ABILS) {
            return false;
        }
        return TYPE instanceof DC_TYPE;
        // return TYPE == OBJ_TYPES.SPELLS;
    }

    private VALUE getSortValue() {
        return sortValue;
    }

    protected void initList() {
        if (listData == null) {
            initData();
        }
        super.initList();
        if (mods.contains(LC_MODS.TEXT_DISPLAYED)) {
            list.setCellRenderer(list);
        }
    }

    @Override
    protected void initPanel() {
        super.initPanel();

        if (!ENUM) {
            ObjType type;
            if (!getListData().isEmpty()) {
                type = DataManager.getType(getListData().get(0), TYPE);
            } else {
                type = DataManager.getType(getSecondListData().get(0), TYPE);
            }
            ListInfoPanel ip = new ListInfoPanel(type);
            list.addListSelectionListener(ip);

            String id = "list1";
            if (getSecondList() != null) {
                id = "list2";
                secondList.addListSelectionListener(ip);

            }
            int h = getPanelHeight();
            int w = getPanelWidth();
            panel.setPanelSize(new Dimension(w, h));
            ip.setPanelSize(new Dimension(w / 2, h - 155));
            panel.add(ip, "id ip, pos " + id + ".x2 0");
            // addSortOptionComp();
            // addFilterOptionComp();
        }

        list.setSelectedIndex(0);
        list.requestFocusInWindow();

    }

    @Override
    protected int getPanelWidth() {
        return GuiManager.getScreenWidthInt() / 2;
    }

    protected void addSortOptionComp() {
        sortOptionsComp = new PagedOptionsComp<>("Sort by: ", SORT_TEMPLATE.class);
        sortOptionsComp.addListener(new ListChooserSortOptionListener());
        panel.add(sortOptionsComp, "id sortOptionsComp, pos ip.x ip.y2");
        sortOptionsComp.refresh();
    }

    protected void addFilterOptionComp() {
        if (getFilterOptionClass() != null) {
            filterOptionsComp = new PagedOptionsComp<>("Filter: ", getFilterOptionClass());
            filterOptionsComp.refresh();
            panel.add(filterOptionsComp, "id filterOptionsComp, pos ip.x sortOptionsComp.y2+12");

            filterOptionsComp.addListener(new ListChooserFilterOptionListener());

        }
    }

    private Class<?> getFilterOptionClass() {
        return filterOptionClass;
    }

    @Override
    protected String getMultiValue() {
        Enumeration<String> elements = ((DefaultListModel<String>) secondList.getModel())
                .elements();
        if (!elements.hasMoreElements()) {
            if (getSelectedItems(list).isEmpty()) {
                return "";
            } else {
                return StringMaster.constructContainer(list.getSelectedValuesList());
            }
        }
        List<String> values = new ArrayList<>();
        while (elements.hasMoreElements()) {
            values.add(elements.nextElement());
            // string += elements.nextElement() + StringMaster.getSeparator();
        }
        return StringMaster.constructContainer(values);
    }

    public enum SELECTION_MODE {
        SINGLE(ListSelectionModel.SINGLE_SELECTION),
        MULTIPLE(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        private int mode;

        SELECTION_MODE(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }
    }

}
