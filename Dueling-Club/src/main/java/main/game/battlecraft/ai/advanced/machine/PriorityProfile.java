package main.game.battlecraft.ai.advanced.machine;

import main.game.battlecraft.ai.advanced.machine.evolution.Evolvable;
import main.game.battlecraft.ai.advanced.machine.evolution.Mutatable;
import main.game.battlecraft.ai.advanced.machine.train.AiTrainingResult;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 7/31/2017.
 */
public class PriorityProfile implements Evolvable{
    Collection<Float> constants;
    Map<AiConst, Float> map;
    AiTrainingResult result;

    public PriorityProfile(Map<AiConst, Float> map) {
        this.map = map;
    }

    public Collection<Float> getConstants() {
        return constants;
    }

    public Map<AiConst, Float> getMap() {
        return map;
    }

    @Override
    public Evolvable schuffleParents(Set<Evolvable> parents, boolean b) {
        return null;
    }

    public AiTrainingResult getResult() {
        return result;
    }

    @Override
    public int compareTo(Evolvable o) {
        if (o instanceof PriorityProfile){
            return result.compareTo(((PriorityProfile) o).getResult());
        }
        return 0;
    }

    @Override
    public int compare(Evolvable o1, Evolvable o2) {
        return 0;
    }

    @Override
    public Mutatable mutate() {
        return null;
    }
}
