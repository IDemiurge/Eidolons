package main.logic;

import main.ArcaneTower;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.VALUE.INPUT_REQ;
import main.content.ValueEditor;
import main.content.parameters.G_PARAMS;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.gui.SessionWindow.VIEW_OPTION;
import main.io.PromptMaster;
import main.launch.ArcaneVault;
import main.session.Session;
import main.session.SessionMaster;
import main.swing.generic.components.editors.lists.GenericListChooser.LC_MODS;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.components.editors.lists.ListObjChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.FilterMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.text.NameMaster;
import main.time.ZeitMaster;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CreationHelper implements ValueEditor {
    public static final String TASK_COMPILATION = "Task Group";

    public static void fillOut() {
//		if (mass) {
//			types = ListChooser.chooseTypes(TYPE, property, group);
//			for (ObjType type : types) {
//				fillOut(type);
//			}
//		}
    }

    private static void fillOut(ObjType type) {
        List<VALUE> requiredValues = getRequiredValues(type, false);
        requiredValues = new ListObjChooser<VALUE>().selectMulti(requiredValues); // TODO
        // tooltip
        // TODO allow adding other vals
        for (VALUE val : requiredValues) {
            String value = type.getValue(val);
            if (value.isEmpty()) {
                ListChooser.setTooltip("Set " + val.getName());
                value = getInput(val, type, type.getValue(val));
            }
            type.setValue(val, value);
        }
    }

    // TableMouseListener!
    public static ObjType create(AT_OBJ_TYPE TYPE) {
        ObjType type;
        // ArcaneVault.getSelectedType();
        // if (type == null)
        if (DialogMaster.confirm("Select template?")) {
            type = ListChooser.chooseType_(TYPE);
        } else {
            String name = DialogMaster.inputText("Name?");
            type = new ObjType(name, TYPE);
        }
        if (type == null) {
            return null;
        }
        fillOut(type);
        return type;
    }

    public static void newTask() {
        ObjType selected = ArcaneVault.getSelectedType();

        for (VALUE val : getRequiredValues(selected, false)) {
            String input = getInput(val, selected, selected.getValue(val));
        }
    }

    public static List<VALUE> getRequiredValues(Entity t, boolean extended) {
        AT_OBJ_TYPE TYPE = (AT_OBJ_TYPE) t.getOBJ_TYPE_ENUM();
        List<VALUE> list = new LinkedList<>();
        for (VALUE l : ContentManager.getValuesForType(t.getOBJ_TYPE(), false)) {
            if (extended) {
                if (l.isLowPriority()) {
                    continue;
                }
                list.add(l);
            } else if (l.isHighPriority()) {
                if (!(l instanceof G_PROPS)) {
                    if (!(l instanceof G_PARAMS)) {
                        list.add(l);
                    }
                }
            }
        }
        switch (TYPE) {
            case TASK:
                list.add(AT_PARAMS.GLORY);
                list.add(AT_PARAMS.TIME_ESTIMATED);
                break;
            case DAY:
            case DIRECTION:
            case GOAL:
            case SESSION:

        }
        if (TYPE.getChildValue() != null) {
            list.add(TYPE.getChildValue());
        }

        if (TYPE.getParentValue() != null) {
            list.add(TYPE.getParentValue());
        }

        if (TYPE.getGroupingKey() != G_PROPS.GROUP) {
            list.add(TYPE.getGroupingKey());
        }
        if (TYPE.getSubGroupingKey() != G_PROPS.GROUP) {
            list.add(TYPE.getSubGroupingKey());
        }

        return list;
    }

    public static Goal getAllSessionTasks(Session session) {
        ObjType type = new ObjType("All Session Tasks", AT_OBJ_TYPE.GOAL);
        type.setProperty(AT_PROPS.TASKS, DataManager.toString(SessionMaster.getAllTasks(session)));
        Goal goal = new Goal(type);
        return goal;
    }

    public static Goal getFilteredGoal(TASK_STATUS status) {
        ObjType type = new ObjType(TASK_COMPILATION, AT_OBJ_TYPE.GOAL);
        List<String> tasks = DataManager
                .convertObjToStringList((Collection<? extends Obj>) FilterMaster.filterByProp(
                        new LinkedList<>(ArcaneTower.getTasks()), AT_PROPS.TASK_STATUS
                                .getName(), status.toString()));
        if (status == TASK_STATUS.PINNED) {
            String name = ArcaneTower.getSessionWindow().getSession().getName();
            tasks = ListMaster.toStringList(FilterMaster.filterByProp(tasks,
                    AT_PROPS.SESSION.getName(), name, AT_OBJ_TYPE.TASK, false).toArray());
        }
        type.setProperty(AT_PROPS.TASKS, StringMaster.joinStringList(tasks, ";"));
        Goal goal = new Goal(type);
        return goal;
    }

    public static Goal getGroupGoal(VIEW_OPTION viewOption) {
        ObjType type = null;
        if (viewOption == VIEW_OPTION.CHOOSE_GROUP) {
            type = ListChooser.chooseTypeFromSubgroup_(AT_OBJ_TYPE.GOAL, "Task Group");
        }
        if (viewOption == VIEW_OPTION.GROUP_LAST) {
            type = (ObjType) ZeitMaster.getLatest(DataManager.getTypesGroup(AT_OBJ_TYPE.GOAL,
                    "Task Group"), AT_PARAMS.TIME_LAST_MODIFIED);
        }
        if (viewOption == VIEW_OPTION.NEW_GROUP) {
            String typeName = CreationHelper.TASK_COMPILATION + " from "
                    + TimeMaster.getDateString();
            typeName = NameMaster.getUniqueVersionedName(DataManager.getTypes(AT_OBJ_TYPE.GOAL),
                    typeName);
            type = new ObjType(typeName, AT_OBJ_TYPE.GOAL);

            ListChooser.addMod(LC_MODS.TEXT_DISPLAYED);
            StringMaster.constructEntityNameContainer(PromptMaster.taskPrompt());
            String types = ListChooser.chooseTypes(AT_OBJ_TYPE.TASK, "", "");
            if (types.isEmpty()) {
                return null;
            }
            type.setProperty(AT_PROPS.TASKS, types);
            // unique name if same day!
            type.setProperty(AT_PROPS.GOAL_TYPE, CreationHelper.TASK_COMPILATION);
            DataManager.addType(type);
            ArcaneTower.saveAll();
        }

        return (Goal) ArcaneTower.getSimulation().getInstance(type);
    }

    public static String getInput(VALUE val) {
        // return ListChooser.chooseEnum(MUSIC_TYPE.class);
        return getInput(val, null, null);
    }

    public static String getInput(VALUE val, Object object, Object object2) {
        return getInput(val, null, null, null);
    }

    public static String getInput(VALUE val, Entity entity, String value,
                                  INPUT_REQ preferredInputMode) {

        Class<?> ENUM_CLASS = EnumMaster.getEnumClass(val.getName());
        OBJ_TYPE TYPE = ContentManager.getOBJ_TYPE(val.getName());

        ListChooser.setTooltip("Input for " + val.getName());
        INPUT_REQ inputReq = preferredInputMode;
        if (inputReq == null) {
            inputReq = val.getInputReq();
            if (inputReq == null) {
                if (ENUM_CLASS != null) {
                    inputReq = INPUT_REQ.SINGLE_ENUM;
                }
            }
        }
        switch (inputReq) {
            case STRING:
                return DialogMaster.inputText("Set value for " + val.getName(), value);
            case INTEGER:
                return ""
                        + DialogMaster.inputInt("Set value for " + val.getName(), StringMaster
                        .getInteger(val.getDefaultValue()));
            case MULTI_ENUM:
                return ListChooser.chooseEnum(ENUM_CLASS, SELECTION_MODE.MULTIPLE);

            case MULTI_TYPE:
                return ListChooser.chooseType(TYPE, SELECTION_MODE.MULTIPLE);
            case SINGLE_ENUM:
                return ListChooser.chooseEnum(ENUM_CLASS, SELECTION_MODE.SINGLE);
            case SINGLE_TYPE:
                return ListChooser.chooseType(TYPE);
            case SINGLE_TYPE_VAR:
                return null;
            case MULTI_TYPE_VAR:
                return null;
            case MULTI_ENUM_VAR:
                return null;
            case SINGLE_ENUM_VAR:
                return null;
        }
        return null;
    }

    @Override
    public boolean checkClickProcessed(MouseEvent e, ObjType selectedType, VALUE val, String value) {
        if (val instanceof AT_PROPS) {
            String input = getInput(val, selectedType, value);
            if (input != null) {
                selectedType.setValue(val, input);
            }
            return true;
        }
        if (val instanceof AT_PARAMS) {

            String input = getInput(val, selectedType, value);
            if (input != null) {
                selectedType.setValue(val, input);
            }
            return true;
        }
        return false;
    }

}
