package combat.state;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.stats.UnitParam;
import elements.stats.generic.StatConsts;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import framework.entity.field.FieldOmen;
import framework.entity.field.HeroUnit;
import framework.entity.field.Unit;
import framework.entity.sub.UnitAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleEntities extends BattleHandler {
    Map<Class<? extends Entity>, Map<Integer, Entity>> entityMaps = new HashMap<>();
    private int ID = 0;

    public BattleEntities(BattleManager battleManager) {
        super(battleManager);
        entityMaps.put(Unit.class, new HashMap<>());
        entityMaps.put(HeroUnit.class, new HashMap<>());
        entityMaps.put(UnitAction.class, new HashMap<>());
        entityMaps.put(FieldEntity.class, new HashMap<>());
        entityMaps.put(FieldOmen.class, new HashMap<>());
    }

    ///////////////// region INIT METHODS ///////////////////
    public <T extends Entity> Integer addEntity(T entity) {
        Integer id = ID++;
        entityMaps.get(getKeyClass(entity.getClass())).put(id, entity);
        return id;
    }

    //endregion
    ///////////////// region GETTERS ///////////////////
    private Class<? extends Entity> getKeyClass(Class<? extends Entity> aClass) {
        //some exceptions?
        return aClass;
    }

    public <T extends Entity> T getEntityById(Integer id, Class<T> entityClass) {
        return (T) entityMaps.get(entityClass).get(id);
    }

    public List<Unit> getUnits() {
        // return entityMaps.get(Unit.class).values().stream().collect(Collectors.toList());
        return getEntityList(Unit.class);
    }

    public <T extends Entity> List<T> getEntityList(Class<T> clazz) {
        return (List<T>) entityMaps.get(clazz).values().stream().collect(Collectors.toList());
    }

    public List<Entity> getMergedEntityList(Class... clazz) {
        List<Entity> merged = new ArrayList<>();
        for (Class aClass : clazz) {
            merged.addAll(entityMaps.get(aClass).values().stream().collect(Collectors.toList()));
        }
        return merged;
    }

    public List<Entity> getFieldEntities() {
        return getMergedEntityList(FieldEntity.class, Unit.class);
    }

    //endregion

    ///////////////// region UPDATE METHODS ///////////////////

    @Override
    public void newRound() {
        for (Unit unit : getUnits()) {
            restoreRoundlyValues(unit);
        }

    }

    private void restoreRoundlyValues(Unit unit) {
        // saved | max | cur
        for (UnitParam param : StatConsts.roundlyParams) {
            // if (broken) //disabled regen?
            //     continue;
            int cur = unit.getInt(param); //what for? calc saved?
            int saved = unit.getInt(param.getName() + "_saved"); //sanity/faith?
            int max = unit.getInt(param.getName() + "_max");

            unit.setValue(param, max);
            unit.addCurValue(param, saved);

        }
    }

    @Override
    public void reset() {
        //for all entities? I'd limit this for now
        //what about buffs?
        //check continuous conditions
        for (Unit unit : getUnits()) {
            unit.toBase();
        }
    }

    //endregion

}
