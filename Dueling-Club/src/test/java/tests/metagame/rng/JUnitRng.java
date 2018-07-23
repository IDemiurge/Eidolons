package tests.metagame.rng;

import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import org.junit.Test;

/**
 * Created by JustMe on 7/23/2018.
 */
public class JUnitRng {
    @Test
        public void rngTest(){
        TileMap map = new LevelGenerator().generateTileMap(LevelGenerator.getLevelData(0));
    // a star alg?
        }
}
