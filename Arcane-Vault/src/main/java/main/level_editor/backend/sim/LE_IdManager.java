package main.level_editor.backend.sim;

import eidolons.entity.obj.BattleFieldObject;
import main.system.SortMaster;

import java.util.LinkedHashMap;
import java.util.Map;

public class LE_IdManager {

    private static int ID = 0;
    Map<Integer, BattleFieldObject> objMap = new LinkedHashMap<>();

    public void objectRemoved(Integer id) {
        objMap.remove(id);
    }

    public void setObjIds(Map<Integer, BattleFieldObject> objIdMap) {
        Integer max = objIdMap.keySet().stream().sorted(SortMaster.getNaturalIntegerComparator(true)).findFirst().orElse(-1 );
        ID = max;
        ID++;
        objMap = objIdMap;
    }

    public Integer objectRemoved(BattleFieldObject obj) {
        for (Integer integer : objMap.keySet()) {
            if (objMap.get(integer) == obj) {
                objMap.remove(integer);
                return integer;
            }

        }
        return null;
    }

    public Integer objectCreated(BattleFieldObject obj) {

        Integer id = ID++;
        objMap.put(id, obj);
        return id;
    }

    public BattleFieldObject getObjectById(Integer id) {
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

    public Map<Integer, BattleFieldObject> getObjMap() {
        return objMap;
    }

    public void addMap(Map<Integer, BattleFieldObject> map) {
        objMap.putAll(map);
    }
}
