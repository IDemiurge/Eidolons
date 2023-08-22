package combat.sub;

import combat.BattleHandler;
import combat.battlefield.BattleField;
import combat.init.BattleSetup;
import combat.state.BattleData;
import combat.state.BattleState;
import elements.exec.EntityRef;
import framework.field.FieldPos;
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
    private BattleSetup battleSetup;
    private List<BattleHandler> handlers = new ArrayList<>();
    private BattleData battleData;
    private BattleField battleField;
    private BattleState battleState;
    private CombatEventHandler eventHandler;

    public BattleManager(BattleSetup battleSetup) {
        this.battleSetup = battleSetup;
        handlers.add(battleData = new BattleData(this));
        handlers.add(battleField = new BattleField(this));
        handlers.add(battleState = new BattleState(this));
        handlers.add(eventHandler = new CombatEventHandler(this));
        // what should we do with ALL handlers?
        // on game end? On event?
    }
    public void resetAll() {
        handlers.forEach(handler -> handler.reset());
    }

    public BattleData getData() {
        return battleData;
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
}
