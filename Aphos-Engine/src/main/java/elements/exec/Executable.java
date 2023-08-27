package elements.exec;

import elements.exec.effect.Effect;
import elements.exec.targeting.TargetGroup;
import elements.exec.targeting.Targeting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface Executable {
    List<Pair<Targeting, Effect>> getTargetedEffects();

    void execute(EntityRef ref);

    default boolean isMultiExec(){
        return getTargetedEffects().size() > 1;
    }
}
