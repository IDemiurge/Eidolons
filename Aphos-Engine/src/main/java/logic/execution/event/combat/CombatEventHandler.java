package logic.execution.event.combat;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import system.log.result.EffectResult;
import elements.exec.trigger.Trigger;
import elements.exec.trigger.MapModTrigger;
import elements.exec.trigger.TriggerList;
import system.log.result.EventResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Alexander on 8/22/2023
 */
public class CombatEventHandler extends BattleHandler {

    Map<CombatEventType, TriggerList> map= new HashMap<>(); //sometimes it's important to sort() triggers correctly

    public CombatEventHandler(BattleManager battleManager) {
        super(battleManager);
    }

    public void addTrigger(Trigger conditional, CombatEventType eventType){
        TriggerList triggerList = map.get(eventType);
        if (triggerList==null){
            triggerList = new TriggerList();
            map.put(eventType, triggerList);
        }
        triggerList.add(conditional);
    }
    public EventResult fire(CombatEventType damageBeingDealt, EntityRef ref, Object args) {
        EventResult handle = handle(new CombatEvent(damageBeingDealt, ref, null ));
        return handle;
    }

    @Override
    public void reset() {
        for (CombatEventType eventType : map.keySet()) {
            map.get(eventType).cleanUp();
        }
    }

    public EventResult handle(CombatEvent combatEvent) {
        //maybe instead of checking ALL triggers we should have maps like we did in bind/trigger sys?
        EventResult result = new EventResult();
        TriggerList triggerList = map.get(combatEvent.getType());
        if (triggerList != null) {
            triggerList.check(combatEvent.getRef(), combatEvent.getArgMap(), result);
        }
        // checkResult(result);  some more triggers?
        return result;
    }

    public void addModifier(CombatEventType eventType, Condition condition, Consumer<Map> mod) {
        addTrigger(new MapModTrigger(mod).setCondition(condition), eventType);
    }

    public void afterEvents(EffectResult result) {

        // result
    }

    /*
    should we support some of that bind/trigger from Libgdx?
     */

    /*
    check triggers


     */
}
