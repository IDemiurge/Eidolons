package tests;

import tests.init.JUnitDcInitializer;
import main.game.core.game.DC_Game;
import org.junit.Before;

/**
 * Created by JustMe on 3/27/2017.
 */
public class GenericTest {

    protected JUnitDcInitializer judi;
    protected DC_Game game;


    @Before
    public void init() {
        judi = new JUnitDcInitializer();
        game = judi.game;
    }


}
