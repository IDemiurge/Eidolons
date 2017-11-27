package main.game.battlecraft.ai.advanced.machine.train;

import main.data.filesys.PathFinder;
import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.advanced.machine.PriorityProfileManager;
import main.game.battlecraft.ai.advanced.machine.evolution.EvolutionMaster;
import main.game.battlecraft.ai.advanced.machine.profile.ProfileWriter;
import main.system.auxiliary.data.ArrayMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.math.FuncMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JustMe on 8/1/2017.
 */
public class AiTrainingRunner implements Runnable {
    private static final int POPULATION = 10;
    public static boolean running;
    public static boolean evolutionTestMode = false;
    static int instances = 1;
    static List<AiTrainingRunner> runners = new ArrayList<>();
    private final String[] args;
    AiTrainingParameters parameters;
    private PriorityProfile winner;

    public AiTrainingRunner(String[] args) {
        this.args = args;
    }

    private static String getDefaultData() {
        return "test data.txt";
    }

    public static void main(String[] args) {
        //run X instances simultaneously, repeat Y times before yielding winner Profile
        running = (true);
        CoreEngine.setGraphicsOff(true);
        if (!ArrayMaster.isNotEmpty(args)) {
            args =
             ArrayMaster.getFilledStringArray(instances, getDefaultData());
        }
        final String[] ARGS = args;
        DC_Engine.jarInit();
        DC_Engine.mainMenuInit();
        if (!evolutionTestMode)
            DC_Engine.gameStartInit();
        ProfileWriter.generateDefaultDataFiles();
        for (int i = 0; i < instances; i++) {
            final int index = i;
            String data = FileManager.readFile(getDataFile(ARGS[index]));
            AiTrainingRunner runner = new AiTrainingRunner(data.split(getDataInstanceSeparator()));
            runners.add(runner);
            new Thread(() -> {
                runner.run();
                runners.remove(runner);
                if (runners.isEmpty()) {
                    WaitMaster.receiveInput(WAIT_OPERATIONS.AI_TRAINING_FINISHED, true);
                }
            }, "AiTraining thread#" + i).start();
        }
        //wait();
        WaitMaster.waitForInput(WAIT_OPERATIONS.AI_TRAINING_FINISHED);
        handleWinningProfile();

        //meta handlers for testGame
    }

    private static void handleWinningProfile() {
        AiTrainingRunner topRunner = (AiTrainingRunner) FuncMaster.getGreatest(
         runners, r -> ((AiTrainingRunner) r).getWinner().getFitness());

        ProfileWriter.save(topRunner.getWinner());
    }

    public static String getItemSeparator() {
        return ",";
    }

    public static String getDataInstanceSeparator() {
        return "><";
    }

    public static String getSegmentSeparator() {
        return ";";
    }

    private static String getDataFile(String arg) {
        return PathFinder.getXML_PATH() + PathFinder.MICRO_MODULE_NAME +
         "//ai-data//training//init-data//" + arg;
    }

    @Override
    public void run() {
        parameters = new AiTrainingParameters(args[0].split(getItemSeparator()));
        AiTrainer trainer = new AiTrainer();
        AiTrainingCriteria criteria = new AiTrainingCriteria(args[1].split(getSegmentSeparator()));
        List<PriorityProfile> population =
         PriorityProfileManager.initProfiles(POPULATION);
        EvolutionMaster<PriorityProfile> evolutionMaster =
         new EvolutionMaster<PriorityProfile>(population) {
             @Override
             public void evolve(PriorityProfile profile) {
                 Float score = new Float(new Random().nextInt(100 + profile.getFitness()));
                 if (!evolutionTestMode) {
                     AiTrainingResult result = trainer.train(profile, criteria, parameters);
                     score = result
                      .getValue();
                     main.system.auxiliary.log.LogMaster.log(1,
                      "evaluated " + profile + "; score= " + score);

                 }
                 profile.addScore(score);
             }
         };
        evolutionMaster.nChildren = population.size();
        evolutionMaster.nParents = 2;
        evolutionMaster.nMutations = 1;
//       while()
        trial(15, evolutionMaster); //until gets a score higher than X
        winner = evolutionMaster.getFittest();
    }

    public PriorityProfile getWinner() {
        return winner;
    }

    private int trial(int stgLimit,
                      EvolutionMaster<PriorityProfile> evolutionMaster) {
        int result = 0;

        evolutionMaster.run();

        while (evolutionMaster.generation < 10000) {
            if (evolutionMaster.isStagnant(stgLimit))
                break;
            evolutionMaster.run();
        }

        result = evolutionMaster.avgFitness;

        return result;
    }

}
