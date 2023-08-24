package elements.exec.trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 8/22/2023
 */
public class TriggerList {

    private List<Trigger> triggers = new ArrayList<>();

    public void cleanUp(){

    }
    public void sort(){

    }

    public void add(Trigger trigger) {
        triggers.add(trigger);
    }
}
