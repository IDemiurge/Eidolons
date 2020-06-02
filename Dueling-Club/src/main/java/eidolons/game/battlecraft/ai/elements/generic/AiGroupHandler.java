package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.enums.EncounterEnums;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapBuilder;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiGroupHandler extends AiHandler {
    protected List<GroupAI> groups=    new ArrayList<>() ;
    private Map<Integer, AiData> encounterAiMap = new HashMap<>();


    public AiGroupHandler(AiMaster master) {
        super(master);
    }

    public GroupAI createEncounterGroup(Encounter encounter, AiData data) {
        GroupAI group = new GroupAI(encounter.getLeader());
        group.setEncounter(encounter);
        group.setMembers(new DequeImpl<>(encounter.getUnits()));
        if (data == null) {
            data = new AiData(true, EncounterEnums.UNIT_GROUP_TYPE.CROWD, null );
        }
        EncounterEnums.UNIT_GROUP_TYPE type = data.getType();
        group.setType(type);
        if (data.getArg() == null) {

        } else {
            group.setArg(data.getArg());
        } //how to init it? }

        LevelBlock block = getGame().getDungeonMaster().getStructMaster().findBlockById(
                data.getIntValue(AiData.AI_VALUE.block_id));

        group.setBlock(block);
        UnitAI.AI_BEHAVIOR_MODE behavior = data.getBehavior();
        if (behavior == null) {
//            behavior = getBehavior(type);
        }
        group.setBehavior(behavior);
        groups.add(group);
        return group;
    }

    public List<GroupAI> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupAI> groups) {
        this.groups = groups;
    }

    public void initEncounterGroups(String textContent) {
        initAiData(textContent, false);
    }

    public Map<Integer, AiData> getEncounterAiMap() {
        return encounterAiMap;
    }

    public void initAiData(String nodeContents, boolean custom) {
        if (custom){
            //TODO it's reverse - it's dataUnit=[ids]!
        }
        encounterAiMap =
                new MapBuilder<>("=", StringMaster.VERTICAL_BAR,
                        s -> NumberUtils.getInteger(s),
                        s -> new AiData(s)
                )
                .build(nodeContents);
    }

    public AiData getAiData(Integer origId) {
        return encounterAiMap.get(origId);
    }
}
