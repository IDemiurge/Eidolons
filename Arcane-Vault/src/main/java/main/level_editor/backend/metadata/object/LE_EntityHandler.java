package main.level_editor.backend.metadata.object;

import eidolons.content.data.BfObjData;
import eidolons.content.data.EntityData;
import eidolons.content.data.UnitData;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.encounter.EncounterData;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

import java.util.HashMap;
import java.util.Map;

public class LE_EntityHandler extends LE_Handler {

    Map<Integer, ObjType> customTypeMap=new  HashMap<>();
    Map<Integer, EntityData> dataMap = new HashMap<>();
        //type names will be the same, so we will rely on this map in DC a lot!

    public LE_EntityHandler(LE_Manager manager) {
        super(manager);
    }

    public void edit(BattleFieldObject entity){
        Integer id = getIdManager().getId(entity);
        EntityData data = dataMap.get(id);

        if (data == null) {
            data = createData(entity);
        }
        data.setValue("id", id+"");

//      TODO   LE_Screen.getInstance().getGuiStage().getEntityEditDialog().setUserObject(data);

    }

    private EntityData createData(BattleFieldObject entity) {
        OBJ_TYPE type = entity.getOBJ_TYPE_ENUM();
        if (type instanceof DC_TYPE) {
            switch (((DC_TYPE) type)) {
                case ENCOUNTERS:
                    return new EncounterData(entity);
                case UNITS:
                    return new UnitData(entity);
                case BF_OBJ:
                    return new BfObjData(entity);


            }
        }
        return null;
    }

    public void modified(EntityData data){
        int id = data.getIntValue("id");
        ObjType objType = customTypeMap.get(id);
        if (objType == null) {
            customTypeMap.put(id, objType = new ObjType(getIdManager().getObjectById(id).getType()));
        }
        if (data instanceof EncounterData) {
            for (String name : ((EncounterData) data).getValues().keySet()) {
                String val = data.getValue(name);
                objType.setValue(name, val);
            }
        }
    }
}
