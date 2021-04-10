package eidolons.entity;

import main.entity.Entity;
import main.system.auxiliary.data.MapMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 4/22/2018.
 */
public class SimCache {

    Map<Entity, Entity> map = new HashMap<>();
    private static SimCache simCache = new SimCache();

    public SimCache( ) {
    }

    public static SimCache getInstance() {
        return simCache;
    }

    public void addSim(Entity real,Entity sim) {
          map.put(real, sim);
    }
    public Entity getSim(Entity real) {
        return map.get(real);
    }
    public Entity getReal(Entity sim) {
        return (Entity) MapMaster .getKeyForValue_(map, sim);
    }

    public Entity getById(String subString) {
        Integer id = Integer.valueOf(subString);
        for (Entity entity : map.keySet()) {
            if (entity.getId().equals(id)) {
                return entity;
            }

        }
        return null;
    }
}
