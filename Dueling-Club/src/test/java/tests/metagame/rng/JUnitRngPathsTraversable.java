package tests.metagame.rng;

import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import org.junit.Test;

/**
 * Created by JustMe on 7/23/2018.
 */
public class JUnitRngPathsTraversable extends JUnitRng{
    @Test
    public void rngTest() {
        super.rngTest();
        TileMap map = new LevelGenerator().generateTileMap(LevelDataMaker.getDefaultLevelData(0));
    }
}
