package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.adventure.map.travel.encounter.EncounterMaster;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

import static main.content.enums.EncounterEnums.ENCOUNTER_TYPE;

public class Encounter extends DC_Obj {

    private Integer power;
    private List<Unit> units = new LinkedList<>();

    private ENCOUNTER_TYPE waveType;
    private EncounterSpawner.ENCOUNTER_STATUS status;

    AiData aiData;
    GroupAI groupAI;

    public Encounter(ObjType waveType, DC_Game game, Ref ref, DC_Player player) {
        super(waveType, player, game, ref);
    }

    public Encounter(EncounterData data) {
        this(DataManager.getType(data.getValue(EncounterData.ENCOUNTER_VALUE.type), DC_TYPE.ENCOUNTERS),
                DC_Game.game, new Ref(), DC_Game.game.getPlayer(false));
    }


    public void setAi(GroupAI groupAi) {
        for (Unit unit : units) {
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
        String string = getNameAndCoordinate() + " with " + units;
        return string;
    }

    @Override
    public Coordinates getCoordinates() {
        return groupAI.getLeader().getCoordinates();
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        ref.setSource(getId());
    }

    public boolean isBoss(ObjType type) {
        return StringMaster.compare(type.getName(), getProperty(PROPS.BOSS_TYPE), true);
    }

    public int getUnitNumber() {
        return getIntParam(PARAMS.UNIT_NUMBER);
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

    public Integer getPower() {
        if (power == 0)
            power = EncounterMaster.getPower(type, null);
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public ENCOUNTER_TYPE getEncounterType() {
        return new EnumMaster<ENCOUNTER_TYPE>().retrieveEnumConst(ENCOUNTER_TYPE.class,
                getProperty(G_PROPS.ENCOUNTER_TYPE));
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }


    public int getPreferredPower() {
        return getIntParam(PARAMS.POWER_BASE);
    }

}
