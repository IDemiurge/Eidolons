package main.game.logic.dungeon.editor;

import main.content.values.properties.MACRO_PROPS;
import main.entity.obj.Obj;
import eidolons.game.battlecraft.logic.meta.scenario.ObjectiveMaster.OBJECTIVE_TYPE;
import main.swing.generic.components.editors.lists.ListChooser;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

public class ObjectiveHelper {

    public static void editObjectives(Obj mission) {
        List<String> objectiveTypes = StringMaster.openContainer(mission
                .getProperty(MACRO_PROPS.OBJECTIVE_TYPES));
        List<String> objectivesData = StringMaster.openContainer(mission
                .getProperty(MACRO_PROPS.OBJECTIVE_DATA));
        // assert equal size!
        // editObjective
        DialogMaster.ask("What to do about objectives?", true, "ADD", "REMOVE", "EDIT");
        Boolean add_remove_edit = (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.OPTION_DIALOG);
        if (add_remove_edit == null) {
            int index = 0;
            if (objectiveTypes.size() > 1) {
                index = DialogMaster.optionChoice(objectiveTypes.toArray(), // descriptive...
                        "Which objective to edit?");
                // -1
            }
            OBJECTIVE_TYPE type = new EnumMaster<OBJECTIVE_TYPE>().retrieveEnumConst(
                    OBJECTIVE_TYPE.class, objectiveTypes.get(index));
            String newData = objectivesData.get(index);
            // custom objective names!!! "Slay the Black Captain"
            if (DialogMaster.confirm("Edit string?")) {
                newData = DialogMaster.inputText("Edit data for " + objectiveTypes.get(index),
                        objectivesData.get(index));
            } else {
                newData = inputObjectiveData(type);
            }
            objectivesData.set(index, newData);
        } else {
            if (add_remove_edit) {
                OBJECTIVE_TYPE type = new EnumMaster<OBJECTIVE_TYPE>().retrieveEnumConst(
                        OBJECTIVE_TYPE.class, ListChooser.chooseEnum(OBJECTIVE_TYPE.class));
                String newData = inputObjectiveData(type);
                objectivesData.add(newData);
                objectiveTypes.add(type.toString());
            }
        }

        String data = StringMaster.constructContainer(objectivesData);
        String types = StringMaster.constructContainer(objectiveTypes);
        mission.setProperty(MACRO_PROPS.OBJECTIVE_DATA, data, true);
        mission.setProperty(MACRO_PROPS.OBJECTIVE_TYPES, types, true);
    }

    public static String inputObjectiveData(OBJECTIVE_TYPE type) {
        // multi objectives chain???
        switch (type) {
            case ITEM_ESCAPE:
            case RESCUE_ESCAPE:
            case ITEM:
            case BOSS:
                Obj obj = LevelEditor.getMouseMaster().pickObject();
                if (obj != null) {
                    return obj.getNameAndCoordinate();
                }
            case CAPTURE_HOLD:
                break;
            case CAPTURE_HOLD_TIME:
                break;
            case ENTER_AREA:
//				List<Coordinates>  = LevelEditor.getMapMaster().pickCoordinates();
//				if (
//						!= null)
//					return CoordinatesMaster.getStringFromCoordinates(
//					);
                break;
            case ESCAPE:
                break;
            case SURVIVE_TIME:
                return "" + DialogMaster.inputInt();
            default:
                break;

        }
        return "";
    }

    public static void editTriggers(Obj mission) {
        // createTrigger()

    }

    public static void editSubObjectives(Obj mission) {
        // TODO Auto-generated method stub

    }

}
