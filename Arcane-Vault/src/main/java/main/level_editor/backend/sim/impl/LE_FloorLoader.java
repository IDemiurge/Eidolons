package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.mapping.ModuleGridMapper;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Set;

public class LE_FloorLoader extends FloorLoader {
    public LE_FloorLoader(DungeonMaster<Location> master) {
        super(master);
    }

    @Override
    protected void processTransitPair(Integer id, Integer id2, Location location) {
        LevelEditor.getManager().getTransitHandler().addTransit(id, id2);
    }

    @Override
    protected void checkModuleRemap(boolean forced) {

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


        LinkedHashMap<Point, Module> grid = new ModuleGridMapper().getOptimalGrid(modules);

        LevelEditor.getManager().getModuleHandler().setGrid(grid);


    }
}
