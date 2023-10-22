package combat;

import combat.battlefield.BattleField;
import combat.state.BattleEntities;
import combat.state.BattleState;
import combat.sub.BattleManager;
import combat.sub.BattleStatistics;
import elements.exec.EntityRef;
import framework.entity.Entity;
import framework.entity.field.Unit;
import logic.execution.ActionExecutor;
import logic.execution.event.combat.CombatEventHandler;
import logic.execution.event.combat.CombatEventType;

import java.util.function.Consumer;

/**
 * Created by Alexander on 8/21/2023
 */
public abstract class BattleHandler {
    protected BattleManager manager;

    public BattleHandler(BattleManager battleManager) {
        this.manager = battleManager;
    }

    public void battleStarts(){
    }
    public void battleEnds(){
    }
    public void reset(){
    }
    public void afterReset(){
    }
    public void newRound(){
    }

    public void roundEnds() {
    }
    public void resetAll() {
        manager.resetAll();
    }

    public void forEach(Consumer<Unit> func) {
        getEntities().getUnits().forEach(unit -> func.accept(unit));
    }

    public BattleEntities getData() {
        return manager.getEntities();
    }

    public BattleField getField() {
        return manager.getField();
    }

    public BattleState getBattleState() {
        return manager.getBattleState();
    }
    // manager delegates getters

    public BattleEntities getEntities() {
        return manager.getEntities();
    }

    public CombatEventHandler getEventHandler() {
        return manager.getEventHandler();
    }

    public ActionExecutor getExecutor() {
        return manager.getExecutor();
    }

    public void event(CombatEventType type, EntityRef ref, Object... args) {
        manager.event(type, ref, args);
    }

    public Unit getUnitById(Integer id) {
        return manager.getUnitById(id);
    }

    public <T extends Entity> T getById(Integer id, Class<T> entityClass) {
        return manager.getById(id, entityClass);
    }

    public BattleStatistics stats() {
        return manager.stats();
    }

}
