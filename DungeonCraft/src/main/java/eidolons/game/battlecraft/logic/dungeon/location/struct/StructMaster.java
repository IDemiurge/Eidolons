package eidolons.game.battlecraft.logic.dungeon.location.struct;

import com.google.inject.internal.util.ImmutableMap;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.exploration.dungeons.struct.IStruct;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import eidolons.game.exploration.dungeons.struct.LevelStruct;
import eidolons.game.exploration.dungeons.struct.LevelZone;
import main.content.enums.DungeonEnums;
import main.game.bf.Coordinates;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StructMaster extends DungeonHandler {

    Set<LevelBlock> blocks;
    Set<LevelZone> zones;

    public StructMaster(DungeonMaster master) {
        super(master);
    }

    public LevelStruct getLowestStruct(Coordinates c) {
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
            blocks.addAll(zone.getSubParts());
        }
        return blocks;
    }

    public Set<LevelBlock> getBlocks() {
        if (blocks == null) {
            blocks = new LinkedHashSet<>();
            for (Module module : getModules()) {
                for (LevelZone zone : module.getZones()) {
                    blocks.addAll(zone.getSubParts());
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
                zones.addAll(module.getSubParts());
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
    public LevelBlock findBlockByName(String name) {
        for (LevelBlock block : getBlocks()) {
            if (block.getName().equalsIgnoreCase(name)) {
                return block;
            }
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
        LevelStruct struct = getLowestStruct(c);
        if (struct instanceof LevelBlock) {
            if (struct.getCellPattern() != null) {

                Map<Coordinates, Integer> patternMap = struct.getPatternMap();
                if (patternMap == null) {
                    patternMap = createPatternMap(struct);
                    struct.setPatternMap(patternMap);
                }
                return patternMap.get(c);
            }
        }
        return 0;
    }

    private Map<Coordinates, Integer> createPatternMap(IStruct struct) {
//TODO cell vs alt cell is better?
        Map<Coordinates, Integer> map = new LinkedHashMap<>();
        int width = CoordinatesMaster.getWidth(struct.getCoordinatesSet());
        int height = CoordinatesMaster.getHeight(struct.getCoordinatesSet());
        Function<Coordinates, Integer> func =CellMaster.getFunc(width, height , struct.getCellPattern());

        Set<Pair<Coordinates, Integer>> entries =
                struct.getCoordinatesSet().stream().map(c -> new ImmutablePair<>(c, func.apply(c)))
                        .collect(Collectors.toSet());
        for (Pair<Coordinates, Integer> entry : entries) {
            ImmutableMap.builder().put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private boolean isPatternsOn() {
        return false;
    }

    public DungeonEnums.CELL_SET getCellType(int i, int j) {
        LevelStruct struct = getLowestStruct(Coordinates.get(i, j));
        if (struct == null) {
            return DungeonEnums.CELL_SET.beige;
        }
        return struct.getCellSet();
    }

}
