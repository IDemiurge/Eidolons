package elements.exec;

import elements.exec.effect.Effect;
import elements.exec.targeting.Targeting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ActionExecutable implements Executable{

    List<Pair<Targeting, Effect>> targetedEffects;

    public ActionExecutable(List<Pair<Targeting, Effect>> targetedEffects) {
        this.targetedEffects = targetedEffects;
    }

    @Override
    public List<Pair<Targeting, Effect>> getTargetedEffects() {
        return targetedEffects;
    }


}
