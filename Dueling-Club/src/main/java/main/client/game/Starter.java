package main.client.game;

import main.game.DC_Game;

//fast
public class Starter {
    boolean host;
    String mapData;
    String partyData;
    DC_Game game;

    public void pickParty() {
        // group, party,
    }

    public void pickMap() {

    }

    public void init() {
        // initOptions(); -
        game.init();
        game.start(true);

    }

}
