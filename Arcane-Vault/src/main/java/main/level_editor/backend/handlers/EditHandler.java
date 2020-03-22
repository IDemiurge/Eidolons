package main.level_editor.backend.handlers;

import eidolons.entity.obj.BattleFieldObject;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.components.DataTable;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.data.DataUnit;

public class EditHandler extends LE_Handler {

    public EditHandler(LE_Manager manager) {
        super(manager);
    }

    public void edit( ){
        edit(getSelectionHandler().getObject());
    }
        public void edit(BattleFieldObject object){
        ObjType newType = new ObjType(object.getType());
        edit(newType);
        operation(Operation.LE_OPERATION.EDIT, object.getType());
        object.applyType(newType);
    }

    private void edit(ObjType newType) {
        GuiEventManager.trigger(GuiEventType.LE_EDIT, newType);
//        WaitMaster.waitForInput()
    }

    public <T extends Enum<T>> void editDataUnit(DataUnit<T> dataUnit) {
        for (String s : dataUnit.getValues().keySet()) {

        }

    }

    public static DataTable.DataPair[] getDataPairs(Entity editEntity) {
        DataTable.DataPair[] pairs=new DataTable.DataPair[0];
        VALUE[] arrays = null;
        OBJ_TYPE TYPE = editEntity.getOBJ_TYPE_ENUM();
        if (TYPE instanceof DC_TYPE) {
            switch (((DC_TYPE) TYPE)) {
                case UNITS:
                case BF_OBJ:
                case ENCOUNTERS:
            }
        }

        return pairs;
    }

}
