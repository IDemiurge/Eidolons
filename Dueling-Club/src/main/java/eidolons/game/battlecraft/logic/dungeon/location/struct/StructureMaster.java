package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class StructureMaster extends DungeonHandler {

    Set<LevelBlock> blocks;
    Set<LevelZone> zones;

    public StructureMaster(DungeonMaster master) {
        super(master);
    }

    public LevelStruct findLowestStruct(Coordinates c) {
        LevelStruct block = findBlock(c);
        if (block == null) {
            block = findModule(c);
        }
        if (block == null) {
            return getFloorWrapper();
        }
        return block;
    }

    private LevelStruct findModule(Coordinates c) {
        for (Module module : getModules()) {
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

    public Set<LevelBlock> getCurrentBlocks() {
        Set<LevelBlock> blocks = new LinkedHashSet<>();
        for (LevelZone zone : getModule().getZones()) {
            for (LevelBlock block : zone.getSubParts()) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public Set<LevelBlock> getBlocks() {
        if (blocks == null) {
            blocks = new LinkedHashSet<>();
            for (Module module : getModules()) {
                for (LevelZone zone : module.getZones()) {
                    for (LevelBlock block : zone.getSubParts()) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public void modelChanged() {
        blocks = null;
        zones = null;
    }

    public Set<LevelZone> getZones() {
        if (zones == null) {
            zones = new LinkedHashSet<>();
            for (Module module : getModules()) {
                for (LevelZone zone : module.getSubParts()) {
                    zones.add(zone);
                }
            }
        }
        return zones;
    }

    public LevelStruct findStructWithin(LevelStruct struct, Coordinates c) {
        if (struct.getChildren() != null)
            for (Object o : struct.getChildren()) {
                if (o instanceof LevelStruct) {
                    LevelStruct child = (LevelStruct) o;
                    LevelStruct sub = findStructWithin(child, c);
                    if (sub != null) {
                        return sub;
                    }
                }
            }
        if (struct.getCoordinatesSet().contains(c)) {
            return struct;
        }
        return null;
    }

    public LevelBlock findBlockById(Integer id) {
        for (LevelBlock block : getBlocks()) {
            if (block.getId() == id) {
                return block;
            }
        }
        return null;
    }

    public int getCellVariant(int i, int j) {
        if (isPatternsOn()) {
            return getCellPatternVariant(i, j);
        }
        return 0;
    }

    private int getCellPatternVariant(int i, int j) {
        Coordinates c = Coordinates.get(i, j);
        LevelStruct struct = findLowestStruct(c);
        if (struct instanceof LevelBlock) {
            if (struct.getCellPattern() != null) {

                Map<Coordinates, Integer> patternMap = struct.getPatternMap();
                if (patternMap == null) {
                    patternMap = create(struct);
                    struct.setPatternMap(patternMap);
                }
                return patternMap.get(c);
            }
        }
        return 0;
    }

    private Map<Coordinates, Integer> create(LevelStruct struct) {
//TODO cell vs alt cell is better?
        Map<Coordinates, Integer> map = new LinkedHashMap<>();
//        struct.getCoordinatesSet().stream().; apply function?
        switch (struct.getCellPattern()) {
            case CROSS:


        }

        return map;
    }

    private boolean isPatternsOn() {
        return false;
    }

    public DungeonEnums.CELL_IMAGE getCellType(int i, int j) {
        LevelStruct struct = findLowestStruct(Coordinates.get(i, j));
        if (struct == null) {
            return DungeonEnums.CELL_IMAGE.tiles;
        }
        return struct.getCellType();
    }
}
