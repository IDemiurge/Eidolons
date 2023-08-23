package combat.state;

import combat.BattleHandler;
import combat.sub.BattleManager;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import framework.entity.field.FieldOmen;
import framework.entity.field.HeroUnit;
import framework.entity.field.Unit;
import framework.entity.sub.UnitAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleData extends BattleHandler {
    Map<Class<? extends Entity>, Map<Integer, Entity>> entityMaps = new HashMap<>();
    private int ID = 0;
    //including reserve?

    public BattleData(BattleManager battleManager) {
        super(battleManager);
        entityMaps.put(Unit.class, new HashMap<>());
        entityMaps.put(HeroUnit.class, new HashMap<>());
        entityMaps.put(UnitAction.class, new HashMap<>());
        entityMaps.put(FieldEntity.class, new HashMap<>());
        entityMaps.put(FieldOmen.class, new HashMap<>());
    }

    public <T extends Entity> Integer addEntity(T entity) {
        Integer id = ID++;
        entityMaps.get(getKeyClass(entity.getClass())).put(id, entity);
        return id;
    }

    private Class<? extends Entity> getKeyClass(Class<? extends Entity> aClass) {
        //some exceptions?
        return aClass;
    }

    public <T extends Entity> T getEntityById(Integer id, Class<T> entityClass) {
        return (T) entityMaps.get(entityClass).get(id);

    }
    @Override
    public void reset() {
        //for all entities? I'd limit this for now
        //what about buffs?
        //check continuous conditions
        for (Entity unit : getUnits()) {
            unit.toBase();
        }

    }

    public List<Unit> getUnits() {
        // return entityMaps.get(Unit.class).values().stream().collect(Collectors.toList());
        return getEntityList(Unit.class);
    }
    public <T extends Entity> List<T> getEntityList(Class<T> clazz) {
        return (List<T>) entityMaps.get(clazz).values().stream().collect(Collectors.toList());
    }
}
