package main.level_editor.backend.functions.mapping;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.EUtils;
import main.data.xml.XML_Converter;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LE_MapHandler extends LE_Handler {

    private Coordinates offset;
    private Coordinates previousOffset;

    private Map<Integer, Integer> moduleTransitMap = new LinkedHashMap<>(); //entrance obj to entrance obj
    private List<Integer> oneWayExits = new LinkedList<>();
    private Integer addingExitFor;


    public LE_MapHandler(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void afterLoaded() {
        super.afterLoaded();
    }

    public void addTransit(Integer id, Integer id2) {
        moduleTransitMap.put( id, id2);

    }
    public String getXml() {
        StringBuilder xmlBuilder = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        for (Integer integer : moduleTransitMap.keySet()) {
            //should we support random/multi?
            builder.append(integer).append("->").append(moduleTransitMap.get(integer)).append(";");
        }
        xmlBuilder.append(XML_Converter.wrap(FloorLoader.TRANSIT_IDS, builder.toString()));

        for (Integer integer : oneWayExits) {
            builder.append(integer).append(";");
        }
        xmlBuilder.append(XML_Converter.wrap(FloorLoader.TRANSIT_ONE_END, builder.toString()));

        return
                XML_Converter.wrap(FloorLoader.TRANSITS, xmlBuilder.toString());
    }

    public void entranceRemoved(BattleFieldObject obj) {
        addingExitFor = null;
        Integer id = moduleTransitMap.remove(getIdManager().getId(obj));
        if (id == null) {
            id = moduleTransitMap.remove(moduleTransitMap.get(getIdManager().getId(obj)));
        }
        entranceAdded(getIdManager().getObjectById(id));
    }

    public void entranceAdded(BattleFieldObject obj) {
        if (addingExitFor != null) {
            moduleTransitMap.put(addingExitFor, getIdManager().getId(obj));
            addingExitFor = null;
            return;
        }
        boolean oneWay; //can be altered via trigger scripts}

        EUtils.info("Add an exit");
        addingExitFor = getIdManager().getId(obj);
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

    public void remapAll() {
        Coordinates offset;
        for (Module module : getModuleHandler().getModules()) {
            //module should have outer walls and void border
        }
    }

}
