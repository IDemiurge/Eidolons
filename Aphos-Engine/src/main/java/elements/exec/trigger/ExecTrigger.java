package elements.exec.trigger;

import elements.exec.EntityRef;
import elements.exec.Executable;
import elements.exec.condition.Condition;

/**
 * Created by Alexander on 8/22/2023
 */
public class ExecTrigger implements Trigger<EntityRef> {

    //source - determines priority in sort()

    private final Condition condition;
    private final Executable executable;
    private Condition retainCondition;
    private EntityRef lastRef;
    private EntityRef targetRef;

    public ExecTrigger(Condition condition, Executable executable) {
        this.condition = condition;
        this.executable = executable;
        retainCondition = ref -> true;
        lastRef = new EntityRef(); //may lead to NPE's?
    }

    public void apply(EntityRef arg) {
        executable.execute(arg);
    }

    public Condition getCondition() {
        return condition;
    }

    public Executable getExecutable() {
        return executable;
    }

    public Condition getRetainCondition() {
        return retainCondition;
    }

    public void setRetainCondition(Condition retainCondition) {
        this.retainCondition = retainCondition;
    }

    public boolean check(EntityRef ref) {
        lastRef = ref;
        return condition.check(ref);
    }

    public void setTargetRef(EntityRef targetRef) {
        this.targetRef = targetRef;
    }

    public EntityRef getLastRef() {
        return lastRef;
    }
}