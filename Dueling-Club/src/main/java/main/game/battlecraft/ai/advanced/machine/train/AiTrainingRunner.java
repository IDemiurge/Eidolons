package main.game.battlecraft.ai.advanced.machine.train;

import main.data.filesys.PathFinder;
import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.ai.advanced.machine.PriorityProfile;
import main.game.battlecraft.ai.advanced.machine.evolution.EvolutionMaster;
import main.game.battlecraft.ai.advanced.machine.profile.ProfileWriter;
import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.launch.GameLauncher;
import main.system.auxiliary.data.FileManager;

import java.util.List;

/**
 * Created by JustMe on 8/1/2017.
 */
public class AiTrainingRunner {
    public static boolean running;
    static int instances = 1;
    AiTrainingParameters parameters;

    public AiTrainingRunner(String[] args) {
        parameters = new AiTrainingParameters(args);
        DC_Game game = new GameLauncher(GAME_SUBCLASS.TEST).initGame();
        AiTrainer trainer = new AiTrainer(game.getAiManager());
        AiTrainingCriteria criteria = new AiTrainingCriteria();
        List<PriorityProfile> population =
         initProfiles();
        EvolutionMaster<PriorityProfile> evolutionMaster =
         new EvolutionMaster<PriorityProfile>(population){
             @Override
             public void evolve(PriorityProfile profile) {
                 trainer.train(profile, criteria, parameters);
             }
         };
        evolutionMaster.nChildren = population.size();
        evolutionMaster.nParents = 10;
//       while()
        trial(15, evolutionMaster); //until gets a score higher than X
        PriorityProfile profile = evolutionMaster.getFittest();
        ProfileWriter.save(profile);
//         trainer.train();
    }

    public static void main(String[] args) {
        //default launch?
        running = (true);
        DC_Engine.jarInit();
        DC_Engine.mainMenuInit();
        DC_Engine.gameStartInit();
        for (int i = 0; i < instances; i++) {
            final int index = i;
            new Thread(() -> {
                String data = FileManager.readFile(getDataFile(args[index]));
                new AiTrainingRunner(data.split(getDataSeparator()));
            }, "AiTraining thread#" + i).start();
        }

        //meta handlers for testGame
    }

    private List<PriorityProfile> initProfiles() {
        return null;
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


    private static String getDataSeparator() {
        return ";";
    }

    private static String getDataFile(String arg) {
        return PathFinder.getXML_PATH() + PathFinder.MICRO_MODULE_NAME + "ai-data//training//init-data//" + arg;
    }
}
