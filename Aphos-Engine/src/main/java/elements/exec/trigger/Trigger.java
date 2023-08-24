package elements.exec.trigger;

import elements.exec.Executable;
import elements.exec.condition.Condition;

/**
 * Created by Alexander on 8/22/2023
 */
public class Trigger {
    Condition condition;
    Executable executable;

    public Trigger(Condition condition, Executable executable) {
        this.condition = condition;
        this.executable = executable;
    }
}
