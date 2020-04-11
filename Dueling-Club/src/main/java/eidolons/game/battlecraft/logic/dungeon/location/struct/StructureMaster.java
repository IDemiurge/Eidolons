package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.game.bf.Coordinates;

import java.util.LinkedHashSet;
import java.util.Set;

public class StructureMaster extends DungeonHandler<Location> {


    public StructureMaster(DungeonMaster master) {
        super(master);
    }

    public LevelStruct findLowestStruct(Coordinates c) {
        LevelBlock block = findBlock(c);
        if (block == null) {
            return findModule(c);
        }
        return block;
    }

    private LevelStruct findModule(Coordinates c) {
        for (Module module :  getModules()) {
            if (module.getCoordinatesSet().contains(c)) {
                return module;
            }
        }
        return null;
    }

    private LevelBlock findBlock(Coordinates c) {
        for (LevelBlock block : getBlocks()) {
            if (block.getCoordinatesSet().contains(c)) {
                return block;
            }
        }
        return null;
    }

    public Set<LevelBlock> getBlocks() {
        Set<LevelBlock> blocks = new LinkedHashSet<>();
        for (Module module : getModules()) {
            for (LevelZone zone : module.getZones()) {
                for (LevelBlock block : zone.getSubParts()) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public Set<LevelZone> getZones() {
        Set<LevelZone> zones = new LinkedHashSet<>();
        for (Module module : getModules()) {
            for (LevelZone zone : module.getSubParts()) {
                zones.add(zone);
            }
        }
        return zones;
    }
}
