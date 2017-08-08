package main.game.battlecraft.ai.advanced.machine.evolution;

import java.util.Comparator;
import java.util.Set;

/**
 * Created by JustMe on 8/2/2017.
 */
public interface Evolvable extends Mutatable,
 Comparator<Evolvable>, Comparable<Evolvable> {
    Evolvable shuffleParents(Set<Evolvable> parents, boolean b);

    Integer getFitness();
}
