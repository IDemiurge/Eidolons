package eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.tree.LayeredData;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LE_Module implements LayeredData<LE_Zone> {

    Module module;
    private Set<LE_Zone> zones;

    public LE_Module(Module module) {
        this.module = module;
        List<LevelZone> zoneList =  module.getZones();
        zones = new LinkedHashSet<>(zoneList.stream().map(z -> new LE_Zone(z)).collect(Collectors.toList()));

    }

    @Override
    public String toString() {
        return module.toString();
    }

    @Override
    public Set<LE_Zone> getChildren() {
        return zones;
    }

    public Module getModule() {
        return module;
    }
}
