package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import main.content.enums.EncounterEnums;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.MapConverter;
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

    public GroupAI createEncounterGroup(Encounter encounter, AiData data, List<Unit> units) {
        GroupAI group = new GroupAI();
        group.setEncounter(encounter);
        group.setMembers(new DequeImpl<>(units));
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
                new MapConverter<>("=", Strings.VERTICAL_BAR,
                        NumberUtils::getIntParse,
                        AiData::new
                )
                .build(nodeContents);
    }

    public AiData getAiData(Integer origId) {
        return encounterAiMap.get(origId);
    }
}
