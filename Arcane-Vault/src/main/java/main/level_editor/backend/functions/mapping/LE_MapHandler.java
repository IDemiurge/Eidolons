package main.level_editor.backend.functions.mapping;

import eidolons.game.battlecraft.logic.dungeon.module.Module;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

public class LE_MapHandler extends LE_Handler {

    Coordinates offset;
    Coordinates previousOffset;


    public LE_MapHandler(LE_Manager manager) {
        super(manager);
    }

    private void offsetChanged() {
    /*
    how to do this graphically?
    removeAll(), units_created() ?
    alternative:
    if we always create 100x100 and have max 4 modules each in its corner... and handle them separately...
    will we still need to change offset?

     */

    }
    public void changeOffset(){

    }

    public Coordinates getOffset(Coordinates c) {
        return c.getOffset(getOffset());
    }
        public Coordinates getOffset() {
        return offset;
    }

    public void setOffset(Coordinates offset) {
        previousOffset = this.offset;
        this.offset = offset;
        offsetChanged();
    }


    public void resize(Module module, int newWidth, int newHeight) {
//        Point gridPos = moduleGrid.get(module);
//        int offsetX = newWidth - module.getWidth();
//        int offsetY = newHeight - module.getHeight();
//
//        for (Module module1 : moduleGrid.keySet()) {
//            Point pos = moduleGrid.get(module1);
////            offset = new Point(pos.x - );
//        }
        // sort it so that we offset the farthest is displaced first , by x then by y
    }

    public void remapAll() {
        Coordinates offset;
        for (Module module :getModuleHandler().getModules()) {
            //module should have outer walls and void border
        }
    }
}
