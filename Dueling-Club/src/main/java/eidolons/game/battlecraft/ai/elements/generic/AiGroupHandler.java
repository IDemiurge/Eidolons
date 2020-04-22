package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.battle.encounter.Encounter;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.enums.EncounterEnums;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AiGroupHandler extends AiHandler {
    protected List<GroupAI> groups;
    private Map<Integer, AiData> encounterAiMap;


    public AiGroupHandler(AiMaster master) {
        super(master);
    }

    public GroupAI createEncounterGroup(Encounter encounter, AiData data) {
        GroupAI group = new GroupAI(encounter.getLeader());
        EncounterEnums.UNIT_GROUP_TYPE type = data.getType();
        group.setType(type);
        if (data.getArg() == null) {

        } else {
            group.setArg(data.getArg());
        } //how to init it? }

        LevelBlock block = getGame().getDungeonMaster().getStructureMaster().findBlockById(
                data.getIntValue(AiData.AI_VALUE.block_id));

        group.setBlock(block);
        UnitAI.AI_BEHAVIOR_MODE behavior = data.getBehavior();
        if (behavior == null) {
//            behavior = getBehavior(type);
        }
        group.setBehavior(behavior);

        return group;
    }

    public List<GroupAI> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupAI> groups) {
        this.groups = groups;
    }

    public void initEncounterGroups(String textContent) {

    }

    public Map<Integer, AiData> getEncounterAiMap() {
        return encounterAiMap;
    }

    public void initAiData(String nodeContents, boolean custom) {
        if (custom){
            //TODO it's reverse - it's dataUnit=[ids]!
        }
        encounterAiMap = new LinkedHashMap<>();
        for (String sub : ContainerUtils.openContainer(nodeContents)) {
            String[] split = sub.split("=");
            Integer id = NumberUtils.getInteger(split[0]);
            AiData data = new AiData(split[1]);
            encounterAiMap.put(id, data);
        }
    }

    public AiData getAiData(Integer origId) {
        return encounterAiMap.get(origId);
    }
}
