package combat.sub;

import combat.Battle;
import combat.BattleHandler;
import combat.battlefield.BattleField;
import combat.init.BattleInitializer;
import combat.init.BattleSetup;
import combat.state.BattleEntities;
import combat.state.BattleState;
import elements.exec.EntityRef;
import framework.entity.Entity;
import framework.entity.field.Unit;
import logic.execution.event.combat.CombatEvent;
import logic.execution.event.combat.CombatEventHandler;
import logic.execution.event.combat.CombatEventType;
import logic.execution.event.combat.EventResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleManager {
    private final BattleInitializer battleInit;
    private final BattleSetup battleSetup;
    private final List<BattleHandler> handlers = new ArrayList<>();
    private final BattleEntities battleEntities;
    private final BattleField battleField;
    private final BattleState battleState;
    private final CombatEventHandler eventHandler;

    public BattleManager(BattleSetup battleSetup) {
        this.battleSetup = battleSetup;
        handlers.add(battleEntities = new BattleEntities(this));
        handlers.add(battleField = new BattleField(this));
        handlers.add(battleState = new BattleState(this));
        handlers.add(eventHandler = new CombatEventHandler(this));
        handlers.add(battleInit = new BattleInitializer(this, battleSetup));
        // what should we do with ALL handlers?
        // on game end? On event?
    }
    public void resetAll() {
        handlers.forEach(handler -> handler.reset());
    }
    public void newRound() {
        handlers.forEach(handler -> handler.newRound());
    }
    public void battleStarts() {
        handlers.forEach(handler -> handler.battleStarts());
    }
    public void battleEnds() {
        handlers.forEach(handler -> handler.battleEnds());
    }

    public BattleEntities getEntities() {
        return battleEntities;
    }

    public BattleField getField() {
        return battleField;
    }

    public BattleSetup getSetup() {
        return battleSetup;
    }

    public BattleState getBattleState() {
        return battleState;
    }

    public CombatEventHandler getEventHandler() {
        return eventHandler;
    }

    public void event(CombatEventType type, EntityRef ref, Object... args) {
        Map<Class, Object> map=  system.MapMaster.toClassMap(args);
        CombatEvent event= new CombatEvent(type, ref,  map);
        EventResult result = eventHandler.handle(event);
        //wazzup here?
    }
    //////////////////SHORTCUTS

    public Unit getUnitById(Integer id) {
        return getById(id, Unit.class);
    }

    public <T extends Entity> T getById(Integer id, Class<T> entityClass) {
        return getEntities().getEntityById(id, entityClass);
    }
    //endregion

    public static BattleManager combat(){
        return Battle.current.getManager();
    }
}
