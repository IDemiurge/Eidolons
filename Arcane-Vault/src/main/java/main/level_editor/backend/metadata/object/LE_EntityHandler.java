package main.level_editor.backend.metadata.object;

import eidolons.content.data.BfObjData;
import eidolons.content.data.EntityData;
import eidolons.content.data.UnitData;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.encounter.EncounterData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.data.xml.XML_Converter;
import main.data.xml.XmlStringBuilder;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.data.MapMaster;
import main.system.data.DataUnit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LE_EntityHandler extends LE_Handler {

    Map<Integer, ObjType> customTypeMap = new HashMap<>();
    Map<Integer, EntityData> dataMap = new HashMap<>();
    //type names will be the same, so we will rely on this map in DC a lot!

    public LE_EntityHandler(LE_Manager manager) {
        super(manager);
    }

    public void edit(BattleFieldObject entity) {
        Integer id = getIdManager().getId(entity);
        EntityData data = dataMap.get(id);

        if (data == null) {
            data = createData(entity);
        }
        data.setValue("id", id + "");

        editData(data);
    }

    @Override
    public String getXml(Function<Integer, Boolean> idFilter) {

        String xml =
                dataMap.keySet().stream().filter(id -> idFilter.apply(id)).
                        map(
//                        id -> ImmutableMap.builder().put(id, dataMap.get(id).getValues())
                                id -> getDataString(id)).collect(Collectors.joining(MapMaster.DATA_MAP_SEPARATOR));
        return XML_Converter.wrap(FloorLoader.CUSTOM_TYPE_DATA, xml);
    }

    private String getDataString(Integer id) {
        return id +
                "=" + dataMap.get(id).getDataExcept("id");
    }

    @Override
    public String getDataMapString(Function<Integer, Boolean> idFilter) {
        XmlStringBuilder builder = new XmlStringBuilder();
        builder.append(getMapXml(idFilter, encounterDataMap, DataMap.encounters));
        builder.append(getMapXml(idFilter, dataMap, DataMap.custom_types));
        return builder.toString();
    }

    private String getMapXml(Function<Integer, Boolean> idFilter,
                             Map<Integer, ? extends DataUnit> encounterDataMap,
                             DataMap encounters) {
        XmlStringBuilder builder = new XmlStringBuilder();

        for (Integer integer : encounterDataMap.keySet()) {
            if (idFilter != null)
                if (idFilter.apply(integer)) {
                    continue;
                }
            builder.append(encounterDataMap.get(integer).getData());
        }
        return builder.wrap(encounters.name()).toString();
    }

    private EntityData createData(BattleFieldObject entity) {
        OBJ_TYPE type = entity.getOBJ_TYPE_ENUM();
        if (type instanceof DC_TYPE) {
            switch (((DC_TYPE) type)) {
                case ENCOUNTERS:
                    return new EncounterData(entity);
                case UNITS:
                    return new UnitData(entity);
                case BF_OBJ:
                    return new BfObjData(entity);


            }
        }
        return null;
    }

    public void modified(EntityData data) {
        int id = data.getIntValue("id");
        ObjType objType = customTypeMap.get(id);
        if (objType == null) {
            customTypeMap.put(id, objType = new ObjType(getIdManager().getObjectById(id).getType()));
        }
        if (data instanceof EncounterData) {
            for (String name : ((EncounterData) data).getValues().keySet()) {
                String val = data.getValue(name);
                objType.setValue(name, val);
            }
        }
    }

    Map<Integer, EncounterData> encounterDataMap = new LinkedHashMap<>();

    public void encounterRemoved(Integer id) {
        encounterDataMap.remove(id);
    }
    public void encounterAdded(Integer id, EncounterData encounterData) {
        encounterDataMap.put(id, encounterData);
    }
}
