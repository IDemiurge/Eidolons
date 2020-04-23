package main.swing.generic.components.editors;

import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.macro.MACRO_CONTENT_CONSTS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.xml.XML_Reader;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.Err;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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
    private MouseEvent mouseEvent;

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
    public void launch(JTable table, int row, int column, String value, MouseEvent e) {
        this.mouseEvent = e;
        String name = (String) table.getValueAt(row, 0);
        String newValue = launch(value, name);
        if (newValue != null) {
            table.setValueAt(newValue, row, 1);
        }

    }

    @Override
    public String launch(String value, String name) {
        if (getFilter()!=null || (listData == null && !listDataSet)) {
            if (res_name != null) {
                listData = FileManager.getFileNames(FileManager.getFilesFromDirectory(res_name, false));
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
                    ContainerUtils.formatList(listData);
                }
            } else {
                if (TYPE == null) {
                    TYPE = DC_TYPE.ABILS;

                    subgroup = name;
                    if (!XML_Reader.getSubGroups(TYPE.toString()).contains(subgroup)) {
                        Err.info("No subgroup found! - " + subgroup);
                    }
                    if (getBASE_TYPE() instanceof DC_TYPE) {
                        switch ((DC_TYPE) getBASE_TYPE()) {
                            case BF_OBJ:
                                TYPE = DC_TYPE.ACTIONS;
                                subgroup = null;
                                break;

                            case CHARS:
                                TYPE = DC_TYPE.ACTIONS;
                                subgroup = null;
                                break;
                            case UNITS:
                                TYPE = DC_TYPE.ACTIONS;
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
                if (getFilter() != null) {
                    listData = new ArrayList<>(listData);
                    listData.removeIf(s -> !getFilter().test(s));
                }
            }
        }
        ListChooser listChooser;


        if (mode == SELECTION_MODE.SINGLE) {
            listChooser = new ListChooser(listData, ENUM, TYPE);

        } else {

            secondListData = new ArrayList<>();
            if (value != null) {
                if (!value.equals(ContentValsManager.getDefaultEmptyValue())) {
                    secondListData = ListMaster.toList(value, ENUM);
                }
            }

            listChooser = new ListChooser(listData, secondListData, ENUM, TYPE);

        }

        if (ENUM || TYPE == DC_TYPE.UNITS || TYPE == DC_TYPE.CHARS || TYPE == DC_TYPE.DEITIES) {
            columns = 1;
        }
        listChooser.setColumns(columns);
        listChooser.setVarTypes(getVarTypes());
        listChooser.setVarClass(getVarTypesClass());
        String newValue = listChooser.getString();
        return newValue;
    }

    private Predicate<String> getFilter() {
        if (mouseEvent != null) {
            if (mouseEvent.isShiftDown()) {
//                listData=    new ArrayList<>() ;
//                return null;
                return s->true;
            }
        }
        if (BASE_TYPE instanceof DC_TYPE) {
            switch (((DC_TYPE) BASE_TYPE)) {
                case ENCOUNTERS:
                    if (TYPE == DC_TYPE.UNITS) {
                        return getEncounterUnitFilter(entity);
                    }
                    break;
            }
        }

        return null;
    }

    private Predicate<String> getEncounterUnitFilter(Entity encounter) {
        return (name) -> {
            ObjType type = DataManager.getType(name, DC_TYPE.UNITS);

            PROPERTY[] property = {
                    G_PROPS.ENCOUNTER_GROUP,
                    G_PROPS.UNIT_GROUP,
                    G_PROPS.ENCOUNTER_SUBGROUP,
                    G_PROPS.GROUP,
            };
            for (PROPERTY property1 : property) {
                for (PROPERTY property2 : property) {
                    if (type.getProperty(property1).equalsIgnoreCase(encounter.getProperty(property2))) {
                        if (!type.getProperty(property1).isEmpty()) {
                            return true;
                        }
                    }
                }
            }

            return false;
        };
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
