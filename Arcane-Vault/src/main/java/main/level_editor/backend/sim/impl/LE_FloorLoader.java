package main.level_editor.backend.sim.impl;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.netherflame.dungeons.model.assembly.ModuleGridMapper;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static main.system.auxiliary.log.LogMaster.log;

public class LE_FloorLoader extends FloorLoader {
    public LE_FloorLoader(DungeonMaster master) {
        super(master);
    }

    @Override
    protected boolean isModuleObjInitRequired(Module module) {
        return true;
    }

    @Override
    protected void initObjects(Module module) {
        Map<Integer, BattleFieldObject> map = module.initObjects();
        LevelEditor.getManager().getIdManager().addMap(map);

    }

    @Override
    protected void initPlatformData(Module module, String textContent) {
        super.initPlatformData(module, textContent);
        LevelEditor.getManager().getPlatformHandler().initPlatforms(textContent);
    }

    @Override
    public void addMainEntrance(Location location, String text, boolean exit) {
        if (NumberUtils.isInteger(text)) {
            Integer id = Integer.valueOf(text);
            LevelEditor.getManager().getTransitHandler().addMain(id, exit);
        }
    }

    @Override
    protected void processTransitPair(Integer id, Coordinates c, Location location) {
        LevelEditor.getManager().getTransitHandler().addTransit(id, c);
    }

    @Override
    protected void initEncounterGroups(String textContent) {
        LevelEditor.getManager().getAiHandler().initEncounterGroups(textContent);
    }

    @Override
    protected void checkModuleRemap(boolean forced, Location location) {

        boolean remap = false;
        Set<Module> modules = getMetaMaster().getModuleMaster().getModules();
        for (Module module : modules) {
            if (module.getOrigin() == null) {
                remap = true;
                break;
            }
        }
        if (!remap) {
            if (!forced) {
                return;
            }
        }

        Map<Point, Module> grid = null;
        String s = location.getData().getValue(LevelStructure.FLOOR_VALUES.module_grid);
        if (!s.isEmpty()) {
            grid = gridFromString(s, modules);
        }

        if (grid == null) {
            grid = new ModuleGridMapper().getOptimalGrid(modules);

            location.setInitialEdit(true);
            int w = ModuleGridMapper.maxWidth;
            int h = ModuleGridMapper.maxHeight;
            location.setWidth(w);
            location.setHeight(h);
            log(1, location + " w = " + w);
            log(1, location + " h = " + h);
        } else {
            ModuleGridMapper.calculateTotalSquareSize(grid);
            int w = ModuleGridMapper.width;
            int h = ModuleGridMapper.height;
            // int w =   location.getData().getIntValue(LevelStructure.FLOOR_VALUES.width);
            // int h =   location.getData().getIntValue(LevelStructure.FLOOR_VALUES.height);
            location.setWidth(w);
            location.setHeight(h);
            log(1, location + " w = " + w);
            log(1, location + " h = " + h);
            // boolean autoAdjust=true;
            // if (autoAdjust){
            //     ArrayList<Point> keys = new ArrayList<>(grid.keySet());
            //     Collections.shuffle(    new ArrayList<>(grid.values()) );
            // }
        }
        getBuilder().initLocationSize(location);
        s = location.getData().getValue(LevelStructure.FLOOR_VALUES.cell_spans);

        grid = modifyGridCells(grid, s);
        LevelEditor.getManager().getModuleHandler().setGrid(grid);
    }

    private Map<Point, Module> modifyGridCells(Map<Point, Module> grid, String s) {
        Iterator<Point> iterator = grid.keySet().iterator();
        Map newGrid = new LinkedHashMap<>(grid);
        for (String substring : ContainerUtils.openContainer(s, StringMaster.VERTICAL_BAR)) {
            Point p = iterator.next();
            Module module = grid.get(p);
            String[] split = substring.split("-");
            int xSpan = NumberUtils.getInt(split[0]);
            int ySpan = NumberUtils.getInt(split[1]);
            newGrid.put(p, module);
            for (int i = 1; i < xSpan; i++) {
                newGrid.put(new Point(p.x + i, p.y), module);
            }
            for (int i = 1; i < ySpan; i++) {
                newGrid.put(new Point(p.x, p.y + i), module);
            }
        }
        return newGrid;
    }

    private LinkedHashMap<Point, Module> gridFromString(String s, Set<Module> modules) {
        LinkedHashMap<Point, Module> map = new LinkedHashMap<>();
        Iterator<Module> iterator = modules.iterator();
        for (String substring : ContainerUtils.openContainer(s, StringMaster.VERTICAL_BAR)) {
            Coordinates c = new Coordinates(substring);
            map.put(new Point(c.x, c.y), iterator.next());
        }
        return map;
    }

    protected void processTextMap(Location location) {
    }

}
