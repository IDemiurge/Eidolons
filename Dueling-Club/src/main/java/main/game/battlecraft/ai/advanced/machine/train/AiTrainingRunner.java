package main.game.battlecraft.ai.advanced.machine.train;

/**
 * Created by JustMe on 8/1/2017.
 */
public class AiTrainingRunner {
  AiTrainingParameters parameters;

    public AiTrainingRunner(String[] args) {
        new AiTrainingParameters(args );
    }

    public static void main(String[] args) {
        new AiTrainingRunner(args);

        //meta handlers for testGame
    }

}
