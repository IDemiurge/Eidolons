package main.level_editor.functions.mapping;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LE_MappingHandler extends LE_Handler {
//let's assume maximum of 4 modules, so we have a simple 2x2 grid, ok? damn, I did want to have 5 in some..
    //

    Map<Module, Point> moduleGrid = new LinkedHashMap<>();

    public LE_MappingHandler(LE_Manager manager) {
        super(manager);
    }

    public void resize(Module module, int newWidth, int newHeight) {
        Point gridPos = moduleGrid.get(module);
        int offsetX = newWidth - module.getWidth();
        int offsetY = newHeight - module.getHeight();

        for (Module module1 : moduleGrid.keySet()) {
            Point pos = moduleGrid.get(module1);
//            offset = new Point(pos.x - );
        }
        // sort it so that we offset the farthest is displaced first , by x then by y
    }

    public void resetModuleBorders() {
        // is it always necessary?
        for (Module module : getModules()) {
            //indestructible walls...
        }
    }
        public void remapAll() {
        Coordinates offset;
        for (Module module : getModules()) {
            //module should have outer walls and void border
        }
    }

    private List<Module> getModules() {
        return getGame().getMetaMaster().getModuleMaster().getModules();
    }

    public void offsetModule() {

    }
}
