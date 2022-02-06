package main.gui.components.table;

import eidolons.content.*;
import eidolons.content.values.DC_ValueManager;
import main.content.*;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ItemEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.TableDataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.StringComparison;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.gui.builders.EditViewPanel;
import main.gui.components.editors.AV_ImgChooser;
import main.launch.ArcaneVault;
import main.swing.generic.components.editors.*;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.misc.G_Table;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.entity.ConditionMaster;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TableMouseListener extends DefaultCellEditor implements MouseListener {

    static AV_ImgChooser imageChooser = new AV_ImgChooser();
    static ListEditor multiListEditor = new ListEditor(false);
    static ListEditor abilsListEditor = new ListEditor(false, DC_TYPE.ABILS);
    static TextEditor textEditor = new TextEditor();
    // static NumberEditor numberEditor = new NumberEditor();
    private static final Map<String, EDITOR> editorMap = new XLinkedMap<>();

    private static final XLinkedMap<String, String> groupFilterMap = new XLinkedMap<>();
    private static final XLinkedMap<String, String> subGroupFilterMap = new XLinkedMap<>();
    private final G_Table table;
    private final boolean second;
    private ValueEditor altHandler;
    JTextField tf;

    public TableMouseListener(G_Table table, boolean second) {
        super(new JTextField());
        tf = (JTextField) editorComponent;
        this.table = table;
        this.second = second;
        if (!second) {
            configureEditors();
        }
    }

    private boolean isIgnoreHeaderClick() {
        return true;
    }

    private void launchDefault(G_Table table, int row, int column, String value) {
        String valueName = table.getValueAt(row, TableDataManager.NAME_COLUMN).toString();
        // if (ContentManager.isParameter(valueName)) {
        // numberEditor.launch(table, row, column, value);
        // } else
        textEditor.launch(table, row, column, value, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ArcaneVault.setAltPressed(e.isAltDown());
        try {
            handleMouseClick(e);
        } catch (Exception ex) {
            main.system.ExceptionMaster.printStackTrace(ex);
        } finally {
            ArcaneVault.setAltPressed(false);
        }
        // Weaver.inNewThread(this, "handleMouseClick", e, MouseEvent.class);

    }

    public void handleMouseClick(MouseEvent e) {
        handleMouseClick(e, e.isAltDown());
    }

    public void handleMouseClick(MouseEvent e, boolean altDown) {

        int row = table.getSelectedRow();
        int column = table.getColumn(EditViewPanel.NAME).getModelIndex();

        if (table.getSelectedColumn() == column) {
            if (isIgnoreHeaderClick()) {
                return;
            }
        }

        Object valueAt = table.getValueAt(row, column);
        VALUE val = ContentValsManager.getValue(valueAt.toString());
        String value;
        ObjType selectedType = (second) ? ArcaneVault.getPreviousSelectedType() : ArcaneVault
                .getSelectedType();
        if (val != null) {
            value = selectedType.getValue(val);
        } else {
            value = table.getValueAt(row, 1).toString();
        }
        if (altHandler != null) {
            if (altHandler.checkClickProcessed(e, selectedType, val, value)) {
                return;
            }
        }

        // table.setRowSelectionInterval(row, row);
        if (altDown || e.isControlDown()
            // SwingUtilities.isRightMouseButton(e)
        ) {
            if (val instanceof PARAMETER && (val != PARAMS.FORMULA)) {
                new NumberEditor().launch(table, row, column, value, e);
                return;
            }

            textEditor.launch(table, row, column, value, getEditorByValueName(valueAt) == null);
            return;
        }
        EDITOR editor = getEditorByValueName(valueAt);
        // TODO lazy editor init!
        if (editor instanceof ListEditor) {
            ((ListEditor) editor).setBASE_TYPE(selectedType.getOBJ_TYPE_ENUM());
            ((ListEditor) editor).setEntity(selectedType);
        }
        if (editor != null) {
            try {
                editor.launch(table, row, column, value, e);
            } catch (NullPointerException ex) {
                main.system.ExceptionMaster.printStackTrace(ex);
                handleMouseClick(e, true);
            } catch (Exception ex) {
                main.system.ExceptionMaster.printStackTrace(ex);
            }
        } else {
            // decorators
            launchDefault(table, row, column, value);
        }
    }


    // TODO AV revamp - rework into functional/lazy style
    public static void configureEditors() {
        DC_ContentValsManager.setEditorMap(editorMap);

        editorMap.put(TableEditValueConsts.soundsetIdentifier, TableEditValueConsts.soundChooser);
        editorMap.put(G_PROPS.FULLSIZE_IMAGE.getName(), imageChooser);
        editorMap.put(TableEditValueConsts.imgIdentifier, imageChooser);
        editorMap.put(MACRO_PROPS.MAP_ICON.getName(), new AV_ImgChooser("global\\map\\icons\\places\\"));
        editorMap.put(PROPS.MAP_BACKGROUND.getName(), new AV_ImgChooser(null));
        editorMap.put(TableEditValueConsts.emblemIdentifier, imageChooser);
        editorMap.put(TableEditValueConsts.actIdentifier, multiListEditor);
        editorMap.put(TableEditValueConsts.pasIdentifier, abilsListEditor);
        // .getTYPEDcopy(OBJ_TYPES.ABILS));
        for (String id : TableEditValueConsts.SINGLE_ENUM_LIST_IDS) {
            id = StringMaster.format(id);
            editorMap.put(id, new ListEditor(SELECTION_MODE.SINGLE, true));
        }

        for (String id : TableEditValueConsts.MULTIPLE_ENUM_LIST_IDS) {
            id = StringMaster.format(id);
            ListEditor multiEnumListEditor = new ListEditor(true);
            multiEnumListEditor.setVarTypes(getMultiTypeVarTypes(id));
            Class<?> enumClass = null;
            if (isWeightedType(id)) {
                enumClass = EnumMaster.getEnumClass(id, DC_CONSTS.class, true);
                if (id.contains("Mastery Groups")) {
                    enumClass = DC_ValueManager.VALUE_GROUP.class;
                }
            } else {
                if (id.equalsIgnoreCase(PROPS.PARAMETER_BONUSES.getName())) {
                    enumClass = PARAMS.class;
                    multiEnumListEditor.setListData(DC_ContentValsManager.getBonusParamList());
                }
                if (id.equalsIgnoreCase(PROPS.ATTRIBUTE_BONUSES.getName())) {
                    enumClass = ATTRIBUTE.class;
                }
                if (enumClass != null) {
                    multiEnumListEditor.setVarTypes(ListMaster.toList(String.class));
                }
            }
            if (enumClass != null) {
                multiEnumListEditor.setVarTypesClass(enumClass);
                multiEnumListEditor.setEnumClass(enumClass);
            }
            editorMap.put(id, multiEnumListEditor);
        }
        int i = 0;
        for (String id : TableEditValueConsts.GROUP_FILTERED) {
            id = StringMaster.format(id);
            groupFilterMap.put(id, TableEditValueConsts.FILTER_GROUPS[i]);
            i++;
        }
        for (String id : TableEditValueConsts.SUBGROUP_FILTERED) {
            id = StringMaster.format(id);
            subGroupFilterMap.put(id, TableEditValueConsts.FILTER_SUBGROUPS[i]);
            i++;
        }
        i = 0;
        for (String id : TableEditValueConsts.VAR_MULTI_ENUM_LIST_IDS) {
            ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, true);
            if (TableEditValueConsts.VAR_ENUM_CLASS_LIST.length <= i) {
                listEditor.setVarTypesClass(VariableManager.STRING_VAR_CLASS);
            } else {
                listEditor.setEnumClass(TableEditValueConsts.VAR_ENUM_CLASS_LIST[i]);
                listEditor.setVarTypesClass(TableEditValueConsts.VAR_ENUM_CLASS_LIST[i]);
            }
            editorMap.put(id, listEditor);
            i++;
        }

        for (Object[] pair : TableEditValueConsts.VAR_MULTI_ENUM_PAIRS) {
            ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, true);
            Object o = pair[1];
            listEditor.setEnumClass((Class<?>) o);
            listEditor.setVarTypesClass((Class<?>) o);
            editorMap.put(pair[0].toString(), listEditor);
        }

        i = 0;
        for (String id : TableEditValueConsts.CONDITIONAL_MULTI_LIST_IDS) {
            ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, false,
                    TableEditValueConsts.CONDITIONAL_MULTI_TYPE_LIST[i]);
            listEditor.setConditions(TableEditValueConsts.TYPE_LIST_CONDITIONS[i]);
            for (String arg0 : TableEditValueConsts.MULTI_VAR_TYPE_IDS) {
                int j = 0;
                if (id.equals(arg0)) {

                    listEditor.setVarTypes(Arrays.asList(TableEditValueConsts.MULTI_VAR_TYPES[j]));

                }
                j++;
            }
            editorMap.put(id, listEditor);

            i++;
        }
        i = 0;
        for (String id : TableEditValueConsts.SINGLE_RES_LIST_IDS) {
            id = StringMaster.format(id);
            editorMap.put(id, new ListEditor(SELECTION_MODE.SINGLE, TableEditValueConsts.RES_KEYS[i]));
            i++;
        }

        i = 0;

        for (String id : TableEditValueConsts.SINGLE_RES_FOLDER_IDS) {
            id = StringMaster.format(id);
            final int index = i;
            editorMap.put(id, new FileChooser(true) {
                protected String getDefaultFileLocation() {
                    return PathFinder.getResPath() + TableEditValueConsts.RES_FOLDER_KEYS[index];
                }

            });
            i++;
        }

        i = 0;
        for (String id : TableEditValueConsts.SINGLE_RES_FILE_IDS) {
            id = StringMaster.format(id);
            final int index = i;
            editorMap.put(id, new FileChooser(false) {
                protected String getDefaultFileLocation() {
                    return PathFinder.getResPath() + TableEditValueConsts.RES_FILE_KEYS[index];
                }

            });
            i++;
        }

        i = 0;
        for (String id : TableEditValueConsts.MULTI_RES_FILE_IDS) {
            id = StringMaster.format(id);
            final int index = i;
            editorMap.put(id, new FileChooser(false, true) {
                protected String getDefaultFileLocation() {
                    return PathFinder.getResPath() + TableEditValueConsts.MULTI_RES_FILE_KEYS[index];
                }

            });
            i++;
        }

        for (VALUE val : TableEditValueConsts.SPRITE_IDS) {
            String id = StringMaster.format(val.name());
            editorMap.put(id, new FileChooser(false, true) {
                protected String getDefaultFileLocation() {
                    return PathFinder.getResPath() + TableEditValueConsts.SPRITE_PATH;
                }

            });
        }
        for (VALUE val : TableEditValueConsts.SOUND_IDS) {
            String id = StringMaster.format(val.name());
            editorMap.put(id,
                    new ListEditor(SELECTION_MODE.MULTIPLE,
                            true, GenericEnums.SOUND_CUE.class));
        }
        for (VALUE val : TableEditValueConsts.VFX_IDS) {
            String id = StringMaster.format(val.name());
            editorMap.put(id, new ListEditor(SELECTION_MODE.MULTIPLE, true, GenericEnums.VFX.class));
            //            editorMap.put(id, new FileChooser(false, true) {
            //                protected String getDefaultFileLocation() {
            //                    return PathFinder.getResPath() + VFX_PATH;
            //                }
            //            });
        }


        i = 0;
        for (String id : TableEditValueConsts.ENUM_LIST_IDS) {
            id = StringMaster.format(id);
            editorMap.put(id, new ListEditor(SELECTION_MODE.MULTIPLE, true, TableEditValueConsts.ENUM_LIST_CLASSES[i]));
            i++;
        }

        i = 0;
        for (String id : TableEditValueConsts.MULTI_TYPE_LIST_IDS) {
            id = StringMaster.format(id);
            addMultiTypeEditor(id, TableEditValueConsts.MULTI_TYPE_LIST[i]);
            i++;
        }
        i = 0;
        for (String id : TableEditValueConsts.SINGLE_TYPE_LIST_IDS) {
            id = StringMaster.format(id);
            editorMap.put(id, new ListEditor(SELECTION_MODE.SINGLE, false, TableEditValueConsts.SINGLE_TYPE_LIST[i]));
            i++;
        }
        // for (String id : textIdentifiers) {
        // editorMap.put(id, te);
        // }
        // prop enum browser, ...

        for (Object[] multiTypePair : TableEditValueConsts.MULTI_TYPE_PAIRS) {
            String name = (String) multiTypePair[0];
            DC_TYPE type = (DC_TYPE) multiTypePair[1];
            addMultiTypeEditor(name, type);
        }
    }

    private static void addMultiTypeEditor(String name, OBJ_TYPE type) {
        ListEditor listEditor = new ListEditor(SELECTION_MODE.MULTIPLE, false,
                type);
        listEditor.setConditions(getMultiTypeCondition(name));
        listEditor.setVarTypes(getMultiTypeVarTypes(name));
        if (isWeightedType(name)) {
            listEditor.setVarTypesClass(VariableManager.STRING_VAR_CLASS);
        }
        if (subGroupFilterMap.get(name) != null) {
            listEditor.setFilterSubgroup(subGroupFilterMap.get(name));
        }
        if (groupFilterMap.get(name) != null) {
            listEditor.setFilterGroup(groupFilterMap.get(name));
        }
        editorMap.put(name, listEditor);
    }

    private static List<Object> getMultiTypeVarTypes(String id) {
        if (isWeightedType(id)) {
            return ListMaster.toList(String.class);
        }
        return null;
    }

    private static Condition getMultiTypeCondition(String id) {
        if (isWeightedType(id)) {
            return new Conditions(new NotCondition(new StringComparison(StringMaster.getValueRef(
                    KEYS.MATCH, G_PROPS.WEAPON_TYPE), "" + ItemEnums.WEAPON_TYPE.NATURAL, true)),
                    ConditionMaster.getItemBaseTypeFilterCondition());
        }
        return null;
    }

    private static boolean isWeightedType(String id) {

        return StringMaster.format(id).contains("Repertoire")
                || id.contains("Plan") || id.contains("Mastery Groups") || id.contains("Priority");
    }

    public static EDITOR getEditorByValueName(Object valueAt) {
        return editorMap.get(valueAt);
    }


    @Override
    public void mouseEntered(MouseEvent e) {
        // e.getComponent().c

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
