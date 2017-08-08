package main.game.battlecraft.ai.advanced.machine;

import main.game.battlecraft.ai.advanced.machine.evolution.Evolvable;
import main.game.battlecraft.ai.advanced.machine.evolution.Mutatable;
import main.game.battlecraft.ai.advanced.machine.profile.ProfileChooser.AI_PROFILE_GROUP;
import main.game.battlecraft.ai.advanced.machine.train.AiTrainingResult;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.MapMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 7/31/2017.
 */
public class PriorityProfile implements Evolvable {
    Collection<Float> constants;
    Map<AiConst, Float> map;
    AiTrainingResult result;
    AI_PROFILE_GROUP group;
    //accumulate over multiple train sessions
    int score = 0;

    public PriorityProfile(PriorityProfile parent) {
        this.map = new MapMaster<AiConst, Float>().cloneLinkedHashMap(parent.getMap());
        this.group = parent.getGroup();
    }

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
    public Evolvable shuffleParents(Set<Evolvable> parents, boolean b) {
        PriorityProfile parent = (PriorityProfile) RandomWizard.getRandomListObject(
         new LinkedList<>(parents));
        return (Evolvable) parent.mutate();
    }

    @Override
    public String toString() {
        return "Profile score=" + score;
    }

    @Override
    public Integer getFitness() {
        return score;
    }

    public void addScore(float score) {
        this.score += score;
    }

    public AiTrainingResult getResult() {
        return result;
    }

    @Override
    public int compareTo(Evolvable o) {
        return compare(this, o);
    }

    @Override
    public int compare(Evolvable arg0, Evolvable arg1) {
        if (arg0.getFitness() > arg1.getFitness()) {
            return 1;
        } else if (arg0.getFitness() < arg1.getFitness()) {
            return -1;
        }
        return 0;
    }

    @Override
    public Mutatable mutate() {
        return ProfileMutator.getMutated(this);
    }

    public AI_PROFILE_GROUP getGroup() {
        return group;
    }

    public void setGroup(AI_PROFILE_GROUP group) {
        this.group = group;
    }
}
