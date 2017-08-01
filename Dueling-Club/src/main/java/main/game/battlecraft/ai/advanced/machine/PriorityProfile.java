package main.game.battlecraft.ai.advanced.machine;

import java.util.Collection;
import java.util.Map;

/**
 * Created by JustMe on 7/31/2017.
 */
public class PriorityProfile {
    Collection<Float> constants;
    Map<AiConst, Float> map;

    public PriorityProfile(Map<AiConst, Float> map) {
        this.map = map;
    }

    public Collection<Float> getConstants() {
        return constants;
    }

    public Map<AiConst, Float> getMap() {
        return map;
    }
}
