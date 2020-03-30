package eidolons.game.battlecraft.logic.dungeon.location.struct.wrapper;

import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.data.tree.LayeredData;

import java.util.Set;
import java.util.stream.Collectors;

public class LE_Zone  implements LayeredData<LE_Block> {
    LevelZone zone;
    private Set<LE_Block> blocks;

    public LE_Zone(LevelZone zone) {
        this.zone = zone;
        blocks = zone.getSubParts().stream().map(block -> new LE_Block(block)).collect(Collectors.toSet());
    }

    public LevelZone getZone() {
        return zone;
    }

    @Override
    public Set<LE_Block> getChildren() {
        return blocks;
    }

    @Override
    public String toString() {
        return zone.toString();
    }
}
