package main.game.core.game;

import main.game.battlecraft.logic.dungeon.location.LocationMaster;

/**
 * Created by JustMe on 5/10/2017.
 */
public class ScenarioGame extends DC_Game {

    @Override
    public LocationMaster getDungeonMaster() {
        return (LocationMaster) super.getDungeonMaster();
    }
}
