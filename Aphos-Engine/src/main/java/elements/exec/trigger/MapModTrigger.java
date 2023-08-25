package elements.exec.trigger;

import elements.exec.condition.Condition;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Alexander on 8/25/2023
 */
public class MapModTrigger implements Trigger<Map> {
    Consumer<Map> mapModifier;
    private Condition condition;

    public MapModTrigger(Consumer<Map> mapModifier) {
        this.mapModifier = mapModifier;
    }

    @Override
    public void apply(Map arg) {
        mapModifier.accept(arg);
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    public MapModTrigger setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }
}
