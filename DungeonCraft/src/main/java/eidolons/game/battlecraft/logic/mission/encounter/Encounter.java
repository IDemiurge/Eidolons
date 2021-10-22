package eidolons.game.battlecraft.logic.mission.encounter;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.mission.encounter.reinforce.Reinforcer;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.LinkedList;
import java.util.List;

import static main.content.enums.EncounterEnums.ENCOUNTER_TYPE;

public class Encounter extends DC_Obj {
//TODO DOES NOT EXIST BEYOND SPAWNING!
    private Integer power;
    private List<ObjType> types = new LinkedList<>();
    private ENCOUNTER_TYPE waveType;
    private EncounterSpawner.ENCOUNTER_STATUS status;

    private AiData aiData;
    private  GroupAI groupAI;
    private Reinforcer reinforcer;
    private  boolean spawned;
    private Integer origId;
    private boolean adjustmentProhibited;

    public Encounter(ObjType waveType, DC_Game game, Ref ref, DC_Player player, Coordinates c) {
        super(waveType, player, game, ref);
        reinforcer = new Reinforcer(this);
        setCoordinates(c);
    }

    public Encounter(ObjType encounterType, Coordinates coordinates) {
        this(encounterType, DC_Game.game, new Ref(), DC_Game.game.getPlayer(false), coordinates);
    }

    public void setAi(GroupAI groupAi) {
        for (Unit unit : getUnits()) {
            unit.getUnitAI().setGroupAI(groupAi);
        }
    }

    public EncounterSpawner.ENCOUNTER_STATUS getStatus() {
        return status;
    }

    public void setStatus(EncounterSpawner.ENCOUNTER_STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        // unitMap
        return getNameAndCoordinate() + " with " + getUnits();
    }


    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        ref.setSource(getId());
    }

    public Integer getPower() {
//        if (power == 0)
//            power = PowerCalculator.getPower(type, null);
        return power;
    }
    public boolean isBoss(ObjType type) {
//        return StringMaster.compare(type.getName(), getProperty(PROPS.BOSS_TYPE), true);
        return false;
    }

    public Reinforcer getReinforcer() {
        return reinforcer;
    }

    public int getCurrentUnitNumber() {
        return getUnits().size();
    }
    public int getUnitNumber() {
        return types.size();
    }

    public List<ObjType> getTypes() {
        if (!ListMaster.isNotEmpty(types)) {
            return DataManager.toTypeList( getPresetGroupTypes(), DC_TYPE.UNITS);
        }
        return types;
    }

    public AiData getAiData() {
        return aiData;
    }

    public GroupAI getGroupAI() {
        return groupAI;
    }

    public int getMaxUnitsPerGroup() {
        return getIntParam(PARAMS.MAX_UNIT_PER_GROUP);
    }

    public ENCOUNTER_TYPE getWaveType() {
        if (waveType == null) {
            waveType = new EnumMaster<ENCOUNTER_TYPE>().retrieveEnumConst(ENCOUNTER_TYPE.class,
                    getProperty(G_PROPS.ENCOUNTER_TYPE));
        }
        return waveType;
    }


    public String getExtendedGroupTypes() {
        return getProperty(PROPS.EXTENDED_PRESET_GROUP);
    }

    public String getPresetGroupTypes() {
        return getProperty(PROPS.PRESET_GROUP);
    }

    public String getShrunkenGroupTypes() {
        return getProperty(PROPS.SHRUNK_PRESET_GROUP);
    }


    public void setPower(Integer power) {
        this.power = power;
    }

    public ENCOUNTER_TYPE getEncounterType() {
        return new EnumMaster<ENCOUNTER_TYPE>().retrieveEnumConst(ENCOUNTER_TYPE.class,
                getProperty(G_PROPS.ENCOUNTER_TYPE));
    }

    public DequeImpl<Unit> getUnits() {
        return groupAI.getMembers();
    }

    public int getPreferredPower() {
        return getIntParam(PARAMS.POWER_BASE);
    }

    public void setOrigId(Integer origId) {
        this.origId = origId;
    }

    public Integer getOrigId() {
        return origId;
    }

    public Unit getLeader() {
        return groupAI.getLeader();
    }

    public void setAiData(AiData aiData) {
        this.aiData = aiData;
    }

    public void setGroupAI(GroupAI groupAI) {
        this.groupAI = groupAI;
    }

    public void setReinforcer(Reinforcer reinforcer) {
        this.reinforcer = reinforcer;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }

    public void addMembers(List<Unit> units) {
        for (Unit unit : units) {
            groupAI.add(unit);
        }
    }

    public void setTypes(List<ObjType> types) {
        this.types = types;
    }

    public void setAdjustmentProhibited(boolean adjustmentProhibited) {
        this.adjustmentProhibited = adjustmentProhibited;
    }

    public boolean isAdjustmentProhibited() {
        return adjustmentProhibited;
    }
}
