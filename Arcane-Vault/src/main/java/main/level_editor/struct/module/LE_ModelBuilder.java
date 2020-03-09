package main.level_editor.struct.module;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.level_editor.struct.level.Floor;

public class LE_ModelBuilder {

    public void buildFloorModel(Floor floor){
        for (Module module : floor.getModules()) {
            // init zones?

        }
        floor.getGame().getDungeonMaster().getDungeonLevel().getZones();


    }
}
