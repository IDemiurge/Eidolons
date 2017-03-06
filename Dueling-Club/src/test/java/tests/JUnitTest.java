package tests;

import main.game.core.game.DC_Game;

import init.JUnitDcInitializer;

public abstract class JUnitTest {
    /*
    resources to load
    additional initialization

     */
    protected JUnitDcInitializer initializer;
    protected DC_Game game;

    public JUnitTest(JUnitDcInitializer initializer) {
        this.initializer = initializer;
        this.game= JUnitDcInitializer.game;
    }

    @org.junit.Before
    public abstract void setUp();

    @org.junit.Test
    public abstract void testUnitTest();
}

