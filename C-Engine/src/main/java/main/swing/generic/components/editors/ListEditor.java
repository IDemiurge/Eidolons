package main.swing.generic.components.editors;

import main.content.ContentManager;
import main.content.MACRO_CONTENT_CONSTS;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.ResourceManager;
import main.data.xml.XML_Reader;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.Game;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Err;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ListEditor implements EDITOR {

    private int columns = 2;
    private SELECTION_MODE mode;
    private boolean ENUM;
    private OBJ_TYPE TYPE;
    private OBJ_TYPE BASE_TYPE;
    private String subgroup;
    private Condition conditions;
    private Class<?> enumClass;
    private String res_name;
    private List<Object> varTypes;
    private Entity entity;
    private Game game;
    private Class<?> varTypesClass;
    private List<String> secondListData;
    private List<String> listData;
    private boolean listDataSet;
    private String filterSubgroup;
    private String filterGroup;

    public ListEditor(boolean ENUM, OBJ_TYPE TYPE) {
        this(SELECTION_MODE.MULTIPLE, ENUM, TYPE);
    }

    public ListEditor(SELECTION_MODE mode, boolean ENUM, OBJ_TYPE TYPE) {
        this.mode = mode;
        this.ENUM = ENUM;
        this.TYPE = TYPE;
    }

    public ListEditor(boolean ENUM) {
        this(SELECTION_MODE.MULTIPLE, ENUM);
    }

    public ListEditor(SELECTION_MODE mode, boolean ENUM) {
        this.mode = mode;
        this.ENUM = ENUM;
    }

    public ListEditor(SELECTION_MODE mode, boolean ENUM, Class<?> enumClass) {
        this(mode, ENUM);
        this.enumClass = enumClass;
    }

    public ListEditor(SELECTION_MODE mode, String res_name) {
        this.res_name = res_name;
        this.mode = mode;
    }

    @Override
    public void launch(JTable table, int row, int column, String value) {

        String name = (String) table.getValueAt(row, 0);
        String newValue = launch(value, name);
        if (newValue != null) {
            table.setValueAt(newValue, row, 1);
        }

    }

    @Override
    public String launch(String value, String name) {
        if (listData == null && !listDataSet) {
            if (res_name != null) {
                listData = ResourceManager.getFilesInFolder(res_name);
                // StringMaster.formatResList(listData);
                Collections.sort(listData);
            } else if (ENUM) {
                // TODO get enum class by name (constants)
                if (enumClass != null) {
                    listData = EnumMaster.getEnumConstantNames(enumClass);
                } else {
                    Class<?> ENUM_CLASS = EnumMaster.getEnumClass(name);

                    if (ENUM_CLASS == null)// TODO find
                    {
                        ENUM_CLASS = EnumMaster.getEnumClass(name, MACRO_CONTENT_CONSTS.class);
                    }
                    listData = EnumMaster.getEnumConstantNames(ENUM_CLASS);
                    StringMaster.formatList(listData);
                }
            } else {
                if (TYPE == null) {
                    TYPE = OBJ_TYPES.ABILS;

                    subgroup = name;
                    if (!XML_Reader.getSubGroups(TYPE.toString()).contains(subgroup)) {
                        Err.info("No subgroup found! - " + subgroup);
                    }
                    if (getBASE_TYPE() instanceof OBJ_TYPES) {
                        switch ((OBJ_TYPES) getBASE_TYPE()) {
                            case BF_OBJ:
                                TYPE = OBJ_TYPES.ACTIONS;
                                subgroup = null;
                                break;

                            case CHARS:
                                TYPE = OBJ_TYPES.ACTIONS;
                                subgroup = null;
                                break;
                            case UNITS:
                                TYPE = OBJ_TYPES.ACTIONS;
                                subgroup = null;
                                break;
                        }
                    }
                }
                listData = DataManager.getTypeNamesGroup(TYPE, subgroup);
                if (filterGroup != null) {
                    listData = DataManager.getTypeNamesGroup(TYPE, filterGroup);
                } else if (filterSubgroup != null) {
                    listData = DataManager.getTypesSubGroupNames(TYPE, filterSubgroup);
                }

                if (getConditions() != null) {
                    Ref ref = new Ref(Game.game, getEntity().getId());
                    listData = DataManager.toStringList(new Filter<ObjType>(ref,
                            getConditions()).filter(DataManager.toTypeList(listData, TYPE)));
                }
            }
        }
        ListChooser listChooser;

        if (mode == SELECTION_MODE.SINGLE) {
            listChooser = new ListChooser(listData, ENUM, TYPE);

        } else {

            secondListData = new LinkedList<String>();
            if (value != null) {
                if (!value.equals(ContentManager.getDefaultEmptyValue())) {
                    secondListData = ListMaster.toList(value.toString(), ENUM);
                }
            }

            listChooser = new ListChooser(listData, secondListData, ENUM, TYPE);

        }

        if (ENUM || TYPE == OBJ_TYPES.UNITS || TYPE == OBJ_TYPES.CHARS || TYPE == OBJ_TYPES.DEITIES) {
            columns = 1;
        }
        listChooser.setColumns(columns);
        listChooser.setVarTypes(getVarTypes());
        listChooser.setVarClass(getVarTypesClass());
        String newValue = listChooser.getString();
        return newValue;
    }

    public void setFilterSubgroup(String string) {
        filterSubgroup = string;
    }

    public void setFilterGroup(String string) {
        filterGroup = string;

    }

    public List<String> getSecondListData() {
        return secondListData;
    }

    public void setSecondListData(List<String> secondListData) {
        this.secondListData = secondListData;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Condition getConditions() {
        return conditions;
    }

    public void setConditions(Condition conditions) {
        this.conditions = conditions;
    }

    public List<Object> getVarTypes() {

        return varTypes;
    }

    public void setVarTypes(List<Object> varTypes) {
        this.varTypes = varTypes;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Game getGame() {
        return Game.game;
    }

    public OBJ_TYPE getBASE_TYPE() {
        return BASE_TYPE;
    }

    public void setBASE_TYPE(OBJ_TYPE TYPE) {
        BASE_TYPE = TYPE;
    }

    public synchronized Class<?> getVarTypesClass() {
        return varTypesClass;
    }

    public void setVarTypesClass(Class<?> varTypesClass) {
        this.varTypesClass = varTypesClass;
    }

    public Class<?> getEnumClass() {
        return enumClass;
    }

    public void setEnumClass(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    public List<String> getListData() {
        return listData;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
        listDataSet = true;
    }

}
