package main.game.battlecraft.ai.advanced.machine.evolution;

import main.system.SortMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;

import java.util.*;

/**
 * Created by JustMe on 8/2/2017.
 */
public class EvolutionMaster<T extends Evolvable> {
    public int nChildren;
    public int nMutations;
    public int nParents;
    public int generation;
    public int avgFitness;
    public int maxFitness;
    private List<T> population = new LinkedList<>();
    private List<T> control = new LinkedList<>();
    private T fittest;
    private int controlSince;

    public EvolutionMaster(List<T> population) {
        this.population = population;
    }

    private T compete(T a, T b) {
        int result = a.compareTo(b);
        if (result > 0)
            return a;
        else
            return b;
    }

    private T getChild(T parent) {

        T child = getChildFromParents(parent);
        for (int i = 0; i < nMutations; i++) {
            child.mutate();

        }

        return child;

    }


    private T getChildFromParents(T parent) {

        Set<T> parents = new HashSet<>();
        while (parents.size() < nParents) {
            parents.add(
             (T) RandomWizard.getRandomListObject(population));
        }

        T child = (T) parent.shuffleParents((Set<Evolvable>) parents, true);

        return child;

    }

    private T chooseParent() {

        Collections.sort(population, population.get(0));

        // TODO move the index part into getIndex method
        return getIndexLinearRanking(population);

    }

    private T getIndexLinearRanking(List<T> pop) {
        int totalRanks = 0;
        int nElements = pop.size();

        for (int i = 0; i < nElements; i++) {
            totalRanks += i;
        }
        Random random = new Random();

        int dice = random.nextInt(totalRanks);

        int currentBoundary = 0;

        for (int i = nElements; i > 0; i--) {
            currentBoundary += i;
            if (dice < currentBoundary) {
                return pop.get(nElements - i);
            }

        }

        if (true)
            throw new RuntimeException("failed to write proper linear ranking index calculator");

        return null;

    }

    public void run() {
        List<T> children = new LinkedList<>();
        for (int i = 0; i < nChildren; i++) {
            children.add(getChild(chooseParent()));
        }

        for (int i = 0; i < nChildren; i++) {
            T competitor = (T) RandomWizard.getRandomListObject(population);
            population.remove(competitor);
            population.add(compete(children.get(i), competitor));
        }

        population.forEach(child -> evolve(child));
        updateMaxAvgFitness();
        SortMaster.sortByExpression(population, sub -> ((T) sub).getFitness());
        setFittest(population.get(0));

        generation++;
        main.system.auxiliary.log.LogMaster.log(
         LOG_CHANNEL.AI_TRAINING, generation + " generation: " + StringMaster.build("population=", ""
           + population, "maxFitness=", "" + maxFitness, "fittest=",
          "nChildren" + nChildren, "nParents" + nParents, "nMutations" + nMutations,
          "fittest" + fittest));
        if (generation > 9000)
            return;
        if (!control.containsAll(population)) {
            setControl();
        }

    }

    // overriden  !
    public void evolve(T t) {
        t.mutate();

    }

    public T getFittest() {
        return fittest;
    }

    public void setFittest(T fittest) {
        this.fittest = fittest;
    }

    public boolean isStagnant(int thresholdStagnant) {
        return (control.containsAll(population) && (generation - controlSince) > thresholdStagnant);
    }

    private void setControl() {
        control.clear();
        control.addAll(population);
        controlSince = generation;
    }

    public void updateMaxAvgFitness() {

        avgFitness = 0;
        maxFitness = Integer.MIN_VALUE;
        for (Evolvable e : population) {
            int fitness = e.getFitness();
            if (fitness > maxFitness) {

                maxFitness = fitness;

            }
            avgFitness += fitness;
        }
        avgFitness /= population.size();

    }
}
