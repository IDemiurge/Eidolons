package main.io;

import main.ArcaneTower;
import main.content.VALUE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.file.CaptureParser;
import main.logic.*;
import main.logic.util.AT_SortMaster;
import main.session.Session;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListObjChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class PromptMaster {

    private static final int DEFAULT_DURATION = 90;

    public static void fillOut(ArcaneEntity entity, boolean emptyOnly) {
        // entity.getOBJ_TYPE_ENUM();
        for (VALUE t : CreationHelper.getRequiredValues(entity, false)) {
            String value = entity.getValue(t);
            if (!StringMaster.isEmpty(value)) {
                if (emptyOnly) {
                    continue;
                }
            }
            String input = CreationHelper.getInput(t, entity, value);
            if (!StringMaster.isEmpty(input)) {
                entity.setValue(value, input, true);
            } else if (DialogMaster.confirm("Done?")) {
                break;
            } else {
                entity.setValue(value, input, true);
            }

        }
    }

    public static void add(ArcaneEntity entity) {
        AT_OBJ_TYPE TYPE = AT_OBJ_TYPE.getChildType(entity.getOBJ_TYPE_ENUM());
        String property = entity.getProperty(TYPE.getChildValue());
        String children = ListChooser.chooseTypes(TYPE, property);
        entity.setProperty(TYPE.getChildValue(), children, true);
    }

    public static void preSessionPrompt(Session session, boolean alt) {
        if (alt) {
            String captureData = DialogMaster.inputText("Input Capture text...");
            CaptureParser.initSessionCapture(session, captureData);
            return;
        }
        // SessionMaster.getSessions()
        ObjType directionType = DataManager.getType(session.getProperty(AT_PROPS.DIRECTION),
                AT_OBJ_TYPE.DIRECTION);
        if (directionType == null) {
            if (ArcaneTower.isTestMode()) {
                directionType = DataManager.getTypes(AT_OBJ_TYPE.DIRECTION).get(0);
            } else {
                directionType = directionPrompt(session);
                if (directionType == null) {
                    return;
                }

                taskPrompt(session);
                goalPrompt(session);
            }
        }

        Direction direction = (Direction) ArcaneTower.getSimulation().getInstance(directionType);
        session.setDirection(direction);
        // session.setGoals(session.getDirection().getGoals());
        session.setTasks(ArcaneTower.getTasks());

        // int minutes =
        // DialogMaster.inputInt("Preset session length? (minutes)",
        // DEFAULT_DURATION);
        // setTimer(session);
    }

    public static boolean afterSessionPrompt(Session session) {
        VALUE[] values = new VALUE[]{

        };
        for (VALUE v : values) {
            String input = CreationHelper.getInput(v, session.getType(), null);
            if (input == null) {
                return false;
            }
        }
        return true;
    }

    private static void goalPrompt(Session session) {
        List<Goal> list = session.getDirection().getGoals();
        list = new ListObjChooser<Goal>().selectMulti(list);
        session.setGoals(list);
    }

    private static void taskPrompt(Session session) {
        session.setTasks(taskPrompt());
    }

    public static List<Task> taskPrompt(boolean filterOut, TASK_STATUS... filteredStatuses) {
        List<Task> list = new ArrayList<>(ArcaneTower.getTasks());
        for (TASK_STATUS f : filteredStatuses) {
            for (Task task : new ArrayList<>(list)) {
                boolean result = task.getStatusEnum() == f;
                if (filterOut) {
                    if (result) {
                        list.remove(task);
                    }
                }
                if (!filterOut) {
                    if (!result) {
                        list.remove(task);
                    }
                }
            }
        }
        AT_SortMaster.sortTasks(list);
        ListObjChooser<Task> listObjChooser = new ListObjChooser<>();
        return listObjChooser.selectMulti(list);
    }

    public static List<Task> taskPrompt() {
        return taskPrompt(false);

    }

    public static Direction chooseDirection(Session session) {
        Direction direction = (Direction) ArcaneTower.getSimulation().getInstance(
                directionPrompt(session));
        return (direction);
    }

    public static ObjType directionPrompt(Session session) {
        return ListChooser.chooseType_(AT_OBJ_TYPE.DIRECTION);

    }

}
