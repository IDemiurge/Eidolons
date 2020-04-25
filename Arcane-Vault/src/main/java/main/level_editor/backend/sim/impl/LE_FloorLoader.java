package main.level_editor.backend.sim.impl;

import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.netherflame.dungeons.model.assembly.ModuleGridMapper;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Set;

import static main.system.auxiliary.log.LogMaster.log;

public class LE_FloorLoader extends FloorLoader {
    public LE_FloorLoader(DungeonMaster<Location> master) {
        super(master);
    }

    @Override
    protected void processTransitPair(Integer id, Coordinates c, Location location ) {
        LevelEditor.getManager().getTransitHandler().addTransit(id,       c );
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


        LinkedHashMap<Point, Module> grid = new ModuleGridMapper().getOptimalGrid(modules);

        location.setInitialEdit(true);
        int w = ModuleGridMapper.maxWidth;
        int h = ModuleGridMapper.maxHeight;
        location.setWidth(w);
        location.setHeight(h);
        log(1,location+ " w = " + w );
        log(1,location+ " h = " + h );
        getBuilder().initLocationSize( location);
        LevelEditor.getManager().getModuleHandler().setGrid(grid);


    }
}
