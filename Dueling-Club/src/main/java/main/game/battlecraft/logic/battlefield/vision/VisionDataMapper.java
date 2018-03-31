package main.game.battlecraft.logic.battlefield.vision;

import main.data.XLinkedMap;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;

import java.util.Map;

/**
 * Created by JustMe on 3/30/2018.
 */
public class VisionDataMapper<T> {

    Map<Unit, Map<BattleFieldObject, T>> map = new XLinkedMap<>();

    public VisionDataMapper() {
    }

    public void reset() {

    }
    public void log() {
        for (Unit unit : map.keySet()) {
            log(unit);
        }
    }
        public void log(Unit unit) {
            main.system.auxiliary.log.LogMaster.log(1,unit + "'s "+toString());
            for (BattleFieldObject object : map.get(unit).keySet()) {
                main.system.auxiliary.log.LogMaster.log(1,object + " has "
                 + map.get(unit).get(object));

        }
    }


    public void set(Unit source, BattleFieldObject object,
                    T outlineType) {
        getMap(source).put(object, outlineType);
    }

    public T get(Unit source, BattleFieldObject object) {
        return getMap(source).get(object);
    }

    private Map<BattleFieldObject, T> getMap(Unit source) {
        Map<BattleFieldObject, T> map = this.map.get(source);
        if (map == null) {
            map = new XLinkedMap<>();
            this.map.put(source, map);
        }
        return map;
    }
}
