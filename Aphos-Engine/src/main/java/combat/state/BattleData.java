package combat.state;

import combat.BattleHandler;
import combat.sub.BattleManager;
import framework.entity.Entity;
import framework.entity.field.Unit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleData extends BattleHandler {
    Map<Class<? extends Entity>, Map<Integer, Entity>> entityMaps;
    //including reserve?

    public BattleData(BattleManager battleManager) {
        super(battleManager);
    }

    public void addEntity(Integer id, Entity entity) {
        entityMaps.get(entity.getClass()).put(id, entity);
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

    public List<Entity> getUnits() {
        return entityMaps.get(Unit.class).values().stream().collect(Collectors.toList());
    }
}
