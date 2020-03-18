package main.level_editor.backend.sim;

import eidolons.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;

import java.util.LinkedHashMap;
import java.util.Map;

public class LE_IdManager {

    private static int ID = 0;
    Map<Integer, Obj> objMap = new LinkedHashMap<>();

    public void objectRemoved(Integer id) {
        objMap.remove(id);
    }

    public void setObjIds(Map<Integer, Obj> objIdMap) {
        ID = objIdMap.size();
        objMap = objIdMap;
    }

    public Integer objectRemoved(Obj obj) {
        for (Integer integer : objMap.keySet()) {
            if (objMap.get(integer) == obj) {
                objMap.remove(integer);
                return integer;
            }

        }
        return null;
    }

    public Integer objectCreated(Obj obj) {
        Integer id = ID++;
        objMap.put(id, obj);
        return id;
    }

    public Obj getObjectById(Integer id) {
        return objMap.get(id);
    }

    public Integer getId(BattleFieldObject obj) {
        for (Integer integer : objMap.keySet()) {
            if (objMap.get(integer) == obj) {
                return integer;
            }

        }
        return null;
    }

    public Map<Integer, Obj> getObjMap() {
        return objMap;
    }
}
