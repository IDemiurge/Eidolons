package main.level_editor.metadata.object;

import eidolons.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;

import java.util.HashMap;
import java.util.Map;

public class LE_IdManager {

    private static int ID = 0;
    Map<Integer, Obj> objects = new HashMap<>();

    public void objectRemoved(Integer id) {
        objects.remove(id);
    }

    public void setObjIds(Map<Integer, Obj> objIdMap) {
        ID = objIdMap.size();
        objects = objIdMap;
    }
    public void objectRemoved(Obj obj) {
        for (Integer integer : objects.keySet()) {
            if (objects.get(integer) == obj){
                objects.remove(integer);
                return;
            }

        }
    }
    public void objectCreated(Obj obj) {
        Integer id = ID++;
        objects.put(id, obj);

    }

    public Obj getObjectById(Integer id) {
        return objects.get(id);
    }

    public Integer getId(BattleFieldObject obj) {
        for (Integer integer : objects.keySet()) {
            if (objects.get(integer) == obj){
                return integer;
            }

        }
        return null;
    }

}
