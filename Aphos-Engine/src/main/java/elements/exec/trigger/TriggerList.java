package elements.exec.trigger;

import elements.exec.EntityRef;
import system.log.result.EventResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class TriggerList {

    private List<Trigger> triggers = new ArrayList<>();
    //make part of triggers via common interface

    public void cleanUp(){
        //TODO
        triggers.removeIf(trigger ->
                trigger instanceof PassiveTrigger ||
                !trigger.getRetainCondition().check(trigger.getTargetRef()));
    }
    public void sort(){
        //need to know each trigger's SOURCE then!
        //passives first? depends on active unit?
    }

    public void add(Trigger trigger) {
        triggers.add(trigger);
    }

    public void check(EntityRef ref, Map argMap, EventResult result) {
        //add argmap to ref? or what is it for?
        for (Trigger trigger : triggers) {

            if (trigger.checkVsOriginalRef(ref)) {
                if (trigger instanceof MapModTrigger){
                    trigger.apply(argMap);
                } else {
                    trigger.apply(ref);
                }
                // result.log()

            }
        }
    }
}
