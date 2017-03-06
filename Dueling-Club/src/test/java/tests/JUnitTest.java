package java.tests;

import main.game.core.game.DC_Game;

import java.init.JUnitDcInitializer;

public abstract class JUnitTest {
    /*
    resources to load
    additional initialization

     */
    protected JUnitDcInitializer initializer;
    protected DC_Game game;

    public JUnitTest(JUnitDcInitializer initializer) {
        this.initializer = initializer;
        this.game=initializer.game;
    }

    @org.junit.Before
    public abstract void setUp();

    @org.junit.Test
    public abstract void testUnitTest();
}

