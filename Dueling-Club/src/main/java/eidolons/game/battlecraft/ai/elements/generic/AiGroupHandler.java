package eidolons.game.battlecraft.ai.elements.generic;

import main.entity.type.ObjType;

public class AiGroupHandler extends AiHandler{
    public AiGroupHandler(AiMaster master) {
        super(master);
        //TODO dc init fix
//        Map<Integer, String> map = getGame().getMetaMaster().getDungeonMaster().getDataMap(DataMap.ai);
//        Map<String, List<ObjType>> customGroupsMap = new LinkedHashMap<>();
//        for (Integer id : map.keySet()) {
//            ObjType objType = (ObjType) getGame().getMetaMaster().getDungeonMaster().getIdTypeMap().get(id);
//            if (objType.getOBJ_TYPE_ENUM()== DC_TYPE.ENCOUNTERS) {
//                AiData aiData = new AiData(map.get(id));
//                createEncounterGroup(objType,aiData);
//            } else {
//                MapMaster.addToListMap(customGroupsMap, map.get(id), objType);
//            }
//        }
    }

    private void createEncounterGroup(ObjType objType, AiData data) {

    }

}
