package eidolons.game.battlecraft.ai.elements.generic;

import eidolons.game.battlecraft.ai.UnitAI;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import java.util.Set;
import java.util.stream.Collectors;

import static main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;

public class AiData extends DataUnit<AiData.AI_VALUE> {
    static Integer ID=0;
    Integer id;

    private UNIT_GROUP_TYPE type;
    private Set<Integer> ids;
    private boolean encounter;
    private  Integer leader;
    private Object arg;

    private Integer customRange;
   private boolean blockBound;

    public AiData(String text) {
        super(text);
        id = ID++;
    }

    public AiData(boolean encounter, UNIT_GROUP_TYPE type, Integer leader) {
        this("");
        this.setType(type);
        this.setEncounter(encounter);
        this.setLeader(leader);
    }

    public AiData(Integer id) {
        this("");
        setLeader(id);
        setEncounter(true);
        setType(UNIT_GROUP_TYPE.IDLERS);
    }
    public UnitAI.AI_BEHAVIOR_MODE getBehavior() {
        return getEnum(getValue(AI_VALUE.behavior), UnitAI.AI_BEHAVIOR_MODE.class);
    }

    public UNIT_GROUP_TYPE getType() {
        if (type == null) {
            type = new EnumMaster<UNIT_GROUP_TYPE>().retrieveEnumConst(UNIT_GROUP_TYPE.class,
                    getValue(AI_VALUE.type));
        }
        return type;
    }

    public void setType(UNIT_GROUP_TYPE type) {
        this.type = type;
        setValue(AI_VALUE.type, type);
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
        setValue(AI_VALUE.ids,
                ids.stream().map(Object::toString).collect(Collectors.joining(";")));

    }

    public boolean isEncounter() {
        return encounter;
    }

    public void setEncounter(boolean encounter) {
        this.encounter = encounter;
        setValue(AI_VALUE.encounter, encounter);
    }

    public Integer getLeader() {
        return leader;
    }

    public void setLeader(Integer leader) {
        this.leader = leader;
        setValue(AI_VALUE.leader, leader);
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
        setValue(AI_VALUE.arg, arg);
    }

    public Integer getCustomRange() {
        return customRange;
    }

    public void setCustomRange(Integer customRange) {
        this.customRange = customRange;
        setValue(AI_VALUE.customRange, customRange);
    }

    public boolean isBlockBound() {
        return blockBound;
    }

    public void setBlockBound(boolean blockBound) {
        this.blockBound = blockBound;
        setValue(AI_VALUE.blockBound, blockBound);
    }


    public enum AI_VALUE{
        ids,
        type,
        encounter,
        leader,
        arg,
        customRange,
        blockBound,
        pursuit_limit,
        awareness_coef,
        block_id, behavior

    }

}
