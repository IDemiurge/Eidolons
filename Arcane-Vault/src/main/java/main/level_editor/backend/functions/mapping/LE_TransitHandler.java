package main.level_editor.backend.functions.mapping;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.core.EUtils;
import main.data.xml.XML_Converter;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LE_TransitHandler extends LE_Handler {

    private Map<Integer, Integer> moduleTransitMap = new LinkedHashMap<>(); //entrance obj to entrance obj
    private List<Integer> oneWayExits = new LinkedList<>();
    private Integer addingExitFor;

    public LE_TransitHandler(LE_Manager manager) {
        super(manager);
    }

    public void addTransit(Integer id, Integer id2) {
        moduleTransitMap.put(id, id2);

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
            boolean oneWay=false; //TODO
            if (oneWay){
                builder.append(id).append("->").append(pair.getCoordinates()).append(";");
            } else {
                builder.append(id).append("->").append(pair.getCoordinates()).append(";");
                builder.append(pairId).append("->").append(entrance.getCoordinates()).append(";");
            }
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

        EUtils.showInfoText("Add an exit");
        addingExitFor = getIdManager().getId(obj);
    }

}
