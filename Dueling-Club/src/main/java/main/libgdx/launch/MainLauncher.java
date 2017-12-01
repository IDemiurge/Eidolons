package main.libgdx.launch;

import main.game.battlecraft.DC_Engine;

/**
 * Created by JustMe on 11/30/2017.
 */
public class MainLauncher extends GenericLauncher{
    public static void main(String[] args) {
        new MainLauncher().start();

    }

    @Override
    protected void engineInit() {
        super.engineInit();
        DC_Engine.dataInit();
    }
}
