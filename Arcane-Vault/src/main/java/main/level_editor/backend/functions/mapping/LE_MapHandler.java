package main.level_editor.backend.functions.mapping;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Collection;

public class LE_MapHandler extends LE_Handler {

    public static final int DEFAULT_MAX_WIDTH = 100;
    public static final int DEFAULT_MAX_HEIGHT = 100;
    private Coordinates offset;
    private Coordinates previousOffset;



    public LE_MapHandler(LE_Manager manager) {
        super(manager);
    }

    public void initModuleSize(Module module) {
//        Set<BattleFieldObject> objects = getObjects(module);
        //non-block objects?!

        Collection<Coordinates> coordinates = new ArrayList<>();
        for (LevelZone zone : module.getZones()) {
            for (LevelBlock subPart : zone.getSubParts()) {
                coordinates.addAll(subPart.getCoordinatesSet());

            }

        }
        int w = CoordinatesMaster.getWidth(coordinates);
        int h = CoordinatesMaster.getHeight(coordinates);
//        padding

        module.getData().setValue(LevelStructure.MODULE_VALUE.width, w);
        module.getData().setValue(LevelStructure.MODULE_VALUE.height, h);
    }

    public Coordinates getModulePlacement(Module module) {
        return null;
    }

    public void reloadAndIncreaseSize() {

    }


    private void offsetChanged() {

        reload();
    /*
    how to do this graphically?
    removeAll(), units_created() ?
    alternative:
    if we always create 100x100 and have max 4 modules each in its corner... and handle them separately...
    will we still need to change offset?
     */

    }

    private void reload() {
        DequeImpl<BattleFieldObject> objects = getGame().getBfObjects();
        //overlaying?!

//        getObjHandler().removeAll();
//        getObjHandler().addAll();
    }

    public void changeOffset() {

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



}
