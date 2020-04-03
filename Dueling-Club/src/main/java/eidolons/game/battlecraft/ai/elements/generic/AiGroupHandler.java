package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.game.battlecraft.logic.dungeon.universal.data.DataMap;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.system.auxiliary.data.MapMaster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AiGroupHandler extends AiHandler{
    public AiGroupHandler(AiMaster master) {
        super(master);
        Map<Integer, String> map = getGame().getMetaMaster().getDungeonMaster().getDataMap(DataMap.ai);
        Map<String, List<ObjType>> customGroupsMap = new LinkedHashMap<>();
        for (Integer id : map.keySet()) {
            ObjType objType = (ObjType) getGame().getMetaMaster().getDungeonMaster().getIdTypeMap().get(id);
            if (objType.getOBJ_TYPE_ENUM()== DC_TYPE.ENCOUNTERS) {
                AiData aiData = new AiData(map.get(id));
                createEncounterGroup(objType,aiData);
            } else {
                MapMaster.addToListMap(customGroupsMap, map.get(id), objType);
            }

        }
    }

    private void createEncounterGroup(ObjType objType, AiData data) {

    }

}
