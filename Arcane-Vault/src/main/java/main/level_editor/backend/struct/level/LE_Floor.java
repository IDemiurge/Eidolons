 package main.level_editor.backend.struct.level;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.sim.LE_GameSim;

import java.util.Objects;
import java.util.Set;

public class LE_Floor extends Floor {
    String name;
    LE_Manager manager;
    LE_GameSim game;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LE_Floor le_floor = (LE_Floor) o;
        return Objects.equals(name, le_floor.name) &&
                Objects.equals(game, le_floor.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, game);
    }

    public LE_Floor(ObjType type, LE_GameSim game) {
        super(type);
        this.name = type.getName();
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

    @Override
    public String toString() {
        return "Floor: " + name;
    }

    public Location getWrapper() {
        return game.getDungeonMaster().getFloorWrapper();
    }

    public Module getDefaultModule() {
        return game.getMetaMaster().getModuleMaster().getBase();
    }
}
