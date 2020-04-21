package main.level_editor.backend.functions.mapping;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.data.xml.XML_Converter;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LE_TransitHandler extends LE_Handler {

    private Map<Integer, Integer> moduleTransitMap = new LinkedHashMap<>(); //entrance obj to entrance obj
    private Integer unpaired;
    private Integer addingExitFor;
    public List<Entrance> entrances = new ArrayList<>();

    public LE_TransitHandler(LE_Manager manager) {
        super(manager);
    }

    public void addTransit(Integer id, Coordinates c ) {
        Obj objectByCoordinate = getGame().getObjectByCoordinate(c);
        if (objectByCoordinate instanceof Entrance) {
            Integer id1 = getIdManager().getId((BattleFieldObject) objectByCoordinate);
            addTransit(id, id1);
        } else {
            main.system.auxiliary.log.LogMaster.log(1,"NO EXIT: " +c);
        }
    }
        public void addTransit(Integer id, Integer id2) {
        moduleTransitMap.put(id, id2);

    }

    public void edit(Entrance object) {
        Integer id = getIdManager().getId(object);
        if (moduleTransitMap.containsKey(id)) {
            if (EUtils.waitConfirm("Make one-way?")) {
                moduleTransitMap.remove(id);
                //option to have no exit-object?
            }
            return;
        }
        if (EUtils.waitConfirm("Make main entrance?")) {
            for (Entrance entrance : entrances) {
                if (entrance.isMainEntrance()) {
                    //confirm remove
                    entrance.setMainEntrance(false);
                }
            }
            object.setMainEntrance(true);
            return;
        }
        if (EUtils.waitConfirm("Make main exit?")) {
            for (Entrance entrance : entrances) {
                if (entrance.isMainExit()) {
                    //confirm remove
                    entrance.setMainExit(false);
                }
            }
            object.setMainExit(true);
            return;
        }
    }

    public String getXml(Function<Integer, Boolean> idFilter) {
        StringBuilder xmlBuilder = new StringBuilder();
        StringBuilder builder = new StringBuilder();

        for (Integer id : moduleTransitMap.keySet()) {
            Integer pairId = moduleTransitMap.get(id);
            if (!idFilter.apply(id)) {
                if (!idFilter.apply(pairId))
                    continue;
            }
            BattleFieldObject entrance = getIdManager().getObjectById(id);
            BattleFieldObject pair = getIdManager().getObjectById(pairId);
            boolean oneWay = false; //TODO
            if (oneWay) {
                builder.append(id).append("->").append(pair.getCoordinates()).append(";");
            } else {
                builder.append(id).append("->").append(pair.getCoordinates()).append(";");
                builder.append(pairId).append("->").append(entrance.getCoordinates()).append(";");
            }
        }
        xmlBuilder.append(XML_Converter.wrap(FloorLoader.TRANSIT_IDS, builder.toString()));

        Integer id = null;
        Integer id2 = null;
        for (Entrance entrance : entrances) {
            if (entrance.isMainExit()) {
                id = getIdManager().getId(entrance);
            }
            if (entrance.isMainEntrance()) {
                id2 = getIdManager().getId(entrance);
            }
        }
        if (id2 == null) {
            EUtils.showInfoText("No main entrance!");
        } else
            xmlBuilder.append(XML_Converter.wrap(FloorLoader.MAIN_ENTRANCE, id2.toString()));

        if (id == null) {
            EUtils.showInfoText("No main exit!");
        } else
            xmlBuilder.append(XML_Converter.wrap(FloorLoader.MAIN_EXIT, id.toString()));

        return
                xmlBuilder.toString();
    }

    public void entranceRemoved(Entrance obj) {
        entrances.remove(obj);
        addingExitFor = null;
        Integer id = moduleTransitMap.remove(getIdManager().getId(obj));
        if (id == null) {
            id = moduleTransitMap.remove(moduleTransitMap.get(getIdManager().getId(obj)));
        }
        if (id == null) {
            return;
        }
        unpaired = (id);
        EUtils.showInfoText("Unpaired entrance: "
                + getIdManager().getObjectById(id).getNameAndCoordinate());
        //TODO remove the other end?
//        entranceAdded(getIdManager().getObjectById(id));
    }

    public void objAdded(BattleFieldObject obj) {
        if (obj instanceof Entrance) {
            entranceAdded((Entrance) obj);
        } else {
            if (addingExitFor != null) {
                unpaired= addingExitFor;
            }
            addingExitFor = null;
        }
    }

    public void entranceAdded(Entrance obj) {
        entrances.add(obj);
        if (unpaired != null) {
            if (EUtils.waitConfirm("Pair with "
                    + getIdManager().getObjectById(unpaired).getNameAndCoordinate())) {
                addingExitFor = unpaired;
                unpaired=null ;
            }
        }
        Integer id = getIdManager().getId(obj);
        if (addingExitFor != null) {
            moduleTransitMap.put(addingExitFor, id);
            moduleTransitMap.put( id , addingExitFor);
            addingExitFor = null;
            return;
        }
        EUtils.showInfoText("Add an exit");
        addingExitFor = id;
    }

}
