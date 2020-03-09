package main.level_editor.struct.module;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.level_editor.gui.tree.data.LayeredData;
import main.level_editor.sim.LE_GameSim;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LE_Module implements LayeredData<LE_Zone> {

    Module module;
    private Set<LE_Zone> zones;

    public LE_Module(Module module) {
        this.module = module;
        List<LevelZone> zoneList = //module.getZones();
                LE_GameSim.getGame().getDungeonMaster().getDungeonLevel().getZones();
        zones = zoneList.stream().map(z -> new LE_Zone(z)).collect(Collectors.toSet());

    }

    @Override
    public Set<LE_Zone> getChildren() {
        return zones;
    }
}
