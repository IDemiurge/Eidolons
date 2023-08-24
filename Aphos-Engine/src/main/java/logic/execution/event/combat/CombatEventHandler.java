package logic.execution.event.combat;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.exec.trigger.Trigger;
import elements.exec.trigger.TriggerList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class CombatEventHandler extends BattleHandler {

    Map<CombatEventType, TriggerList> map= new HashMap<>(); //sometimes it's important to sort() triggers correctly

    public CombatEventHandler(BattleManager battleManager) {
        super(battleManager);
    }

    public void addTrigger(Trigger trigger, CombatEventType eventType){
        TriggerList triggerList = map.get(eventType);
        if (triggerList==null){
            triggerList = new TriggerList();
        }
        triggerList.add(trigger);
    }

    public EventResult handle(CombatEvent combatEvent) {
        combatEvent.getRef();
        //maybe instead of checking ALL triggers we should have maps like we did in bind/trigger sys?
        EventResult result = new EventResult();
        // checkResult(result);  some more triggers?
        return result;
    }
    /*
    should we support some of that bind/trigger from Libgdx?
     */

    /*
    check triggers


     */
}
