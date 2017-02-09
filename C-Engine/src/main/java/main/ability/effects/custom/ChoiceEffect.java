package main.ability.effects.custom;

import main.ability.effects.oneshot.MicroEffect;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.data.ability.OmittedConstructor;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

public class ChoiceEffect extends MicroEffect {

    private static final WAIT_OPERATIONS operation = WAIT_OPERATIONS.CUSTOM_SELECT;
    private List<String> listData;
    private List<ObjType> listTypeData;
    private OBJ_TYPE TYPE;

    public ChoiceEffect(String obj_type) {
        this.TYPE = OBJ_TYPES.getType(obj_type);
    }

    public ChoiceEffect() {

    }

    @OmittedConstructor
    public ChoiceEffect(List<ObjType> listData) {
        this.listTypeData = listData;
    }

    @OmittedConstructor
    public ChoiceEffect(OBJ_TYPE TYPE, List<String> listData) {
        this.TYPE = TYPE;
        this.listData = listData;
    }

    @Override
    public boolean applyThis() {
        // ref.getGroup().getObjects()
        // ref.setTarget(id);

        Obj obj = ref.getSourceObj();
        if (listData == null) {
            if (listTypeData == null && TYPE != null) {
                /**
                 * how to choose from a custom obj pool? ref.getGroup()
                 */
                listTypeData = DataManager.getTypes(TYPE);

            }
            listData = DataManager.toStringList(listTypeData);
        }
        String input = null;
        if (!getGame().isOffline()) {
            getGame().getCommunicator().getChoiceData();
        } else {
            input = ListChooser.chooseType(listData, TYPE);
        }
        if (input == null) {
            return false;
        }
        WaitMaster.receiveInput(operation, input);

        if (!getGame().isOffline()) {
            if (obj.isMine()) {
                getGame().getCommunicator().sendChoiceData(input);
            }
        }

        return true;
    }

}
