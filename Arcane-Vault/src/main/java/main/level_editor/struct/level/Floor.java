package main.level_editor.struct.level;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.level_editor.LevelEditor;
import main.level_editor.functions.LE_Manager;
import main.level_editor.functions.model.LE_DataModel;
import main.level_editor.sim.LE_GameSim;
import main.level_editor.struct.boss.BossDungeon;
import main.system.data.DataUnitFactory;

import java.util.Set;

public class Floor {
//this is just a wrapper for a real Dungeon?
    String name;
//    FloorData data;
    LE_Manager manager;
    LE_GameSim game;
    BossDungeon bossDungeon;

    public Floor(String name, LE_GameSim game ) {
        this.name = name;
        this.game = game;
        manager =  new LE_Manager(this);
    }

    public LE_GameSim getGame() {
        return game;
    }

    public LE_Manager getManager() {
        return manager;
    }

    public String getName() {
        return name;
    }

    public Set<Module> getModules() {
        return game.getMetaMaster().getModuleMaster().getModules();
    }
}
