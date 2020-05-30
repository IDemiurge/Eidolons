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
        LevelEditor.getManager().getAdvFuncs().initPlatforms(textContent);
    }

    @Override
    public void addMainEntrance(Location location, String text, boolean exit) {
        Integer id = Integer.valueOf(text);
        LevelEditor.getManager().getTransitHandler().addMain(id, exit);
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


        LinkedHashMap<Point, Module> grid = null;
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
        LevelEditor.getManager().getModuleHandler().setGrid(grid);
    }

    private LinkedHashMap<Point, Module> gridFromString(String s, Set<Module> modules) {
        LinkedHashMap<Point, Module> map = new LinkedHashMap<>();
        Iterator<Module> iterator = modules.iterator();
        for (String substring : ContainerUtils.openContainer(s, StringMaster.AND_SEPARATOR)) {
            Coordinates c = new Coordinates(substring);
            map.put(new Point(c.x, c.y), iterator.next());
        }
        return map;
    }

    protected void processTextMap(Location location) {
    }
}
