package eidolons.game.module.dungeoncrawl.generator.test;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_GEN_FLAG;

/**
 * Created by JustMe on 8/2/2018.
 */
public class LevelTester {

    private static final int N = 100;
    int tries;
    private LevelData data;
    private GenerationStats stats;

    public LevelTester(int tries) {
        this.tries = tries;
    }

    public static void main(String[] args) {
        new LevelTester(N).run();


    }

    private void run() {
         data = generateInitialData();
        while (true) {
            //proper evolution alg?
            stats = new GenerationStats();
            mutateData();
                        for (int i = 0; i < N; i++) {
                            DungeonLevel level= LevelGenerator.generateForData(data);
                            LevelValidator.validateForTester(stats, level);

                        }
                        int value = stats.rate();
            //            String content = getContentToWrite(stats, data, value);
            //            writeData();
        }
    }

    private void mutateData() {
        double variants = Math.pow(2, LEVEL_GEN_FLAG.values().length);
        for (LEVEL_GEN_FLAG flag : LEVEL_GEN_FLAG.values()) {

        }
//        data.setValue(flag, value);
    }

    private LevelData generateInitialData() {
        return null;
    }



}
