package eidolons.game.battlecraft.logic.battle.encounter;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.module.herocreator.logic.UnitLevelManager;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static main.content.enums.EncounterEnums.ENCOUNTER_TYPE;
import static main.content.enums.EncounterEnums.GROWTH_PRIORITIES;

//TODO  support WEIGHT-format: pick from unit pool until target-power is filled or
// apply growth per random
public class EncounterAdjuster {

    public static final int REGULAR_POWER = 100;
    public static final int ELITE_POWER = 100;
    public static final int BOSS_POWER = 100;
    public static final int POWER_GAP = 10;
    public static final int MAX_GROUPS = 4;
    public static final Integer DUNGEON_DEFAULT_BOSS_POWER_MOD = 125;
    public static final Integer ENCOUNTER_DEFAULT_BOSS_POWER_MOD = 120;

    private final MissionBattleMaster manager;
    public GenericEnums.DIFFICULTY difficulty;
    public Encounter encounter;
    public List<List<ObjType>> unitGroups;
    private List<ObjType> typeMap = new LinkedList<>();

    List<GROWTH_PRIORITIES> growthPriorities;
    public int unitLevel = 0;
    public int groups = 0;
    public int fillApplied = 0;
    public int groupsExtended = 0;

    public Integer target_power = 0;
    public Integer power = 0;
    Integer forcedPower;

    public EncounterAdjuster(MissionBattleMaster manager) {
        this.manager = manager;
        this.difficulty = manager.getOptionManager().getDifficulty();
    }

    public void assembleWave(Encounter encounter) {
        try {
            assembleWave(encounter, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void assembleWave(Encounter encounter, boolean adjustPower, boolean presetCoordinate) {
        this.encounter = encounter;
        groupsExtended = 0;
        fillApplied = 0;
        groups = 0;
        unitGroups = new LinkedList<>();
        // map?
        initPower();
        applyAddGroup();
        if (!ListMaster.isNotEmpty(unitGroups))
        {
            main.system.auxiliary.log.LogMaster.log(1," empty encounter! " );
            return;
        }
        if (adjustPower)
            if (checkPowerAdjustmentNeeded())
                try {
                    adjustPower();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        encounter.setPower(power);

    }

    private void initPower() {
        int percentage = getPowerPercentage(encounter);
        percentage = percentage * difficulty.getPowerPercentage() / 100;
        // Integer base_power = wave.getBasePower(); // min/max?
        // power = base_power;
        if (encounter.getPreferredPower() != 0)
            target_power = encounter.getPreferredPower() * percentage / 100;
//        else
//            target_power = manager.getBattleLevel() * percentage / 100;
    }

    private void adjustPower() {
        growthPriorities = getPriorities(encounter);
        int index = 0;
        Loop.startLoop(1000);
        while (checkPowerAdjustmentNeeded() && !Loop.loopEnded()) {
            GROWTH_PRIORITIES priority = growthPriorities.get(index);
            index++;
            if (index >= growthPriorities.size())
                index = 0;
            try {
                applyGrowth(priority);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void applyGrowth(GROWTH_PRIORITIES priority) {
        LogMaster.log(LOG_CHANNEL.WAVE_ASSEMBLING, priority + " is being applied" + "; Power = "
                + power);
        switch (priority) {
            case EXTEND:
                applyExtend();
                break;
            case FILL:
                if (!applyFill())
                    break; // TODO
                break;
            case GROUP:
                applyAddGroup();
                break;
            case LEVEL:
                applyLevel();
                break;

        }

    }

    public void applyAddGroup() {
        if (!addGroup(null)) {
            addGroup(true);
        }
    }

    public boolean addGroup(Boolean shrunk_or_extended) {
        String presetGroupTypes = encounter.getPresetGroupTypes();
        if (shrunk_or_extended != null)
            presetGroupTypes = (shrunk_or_extended) ? encounter.getShrunkenGroupTypes() : encounter
                    .getExtendedGroupTypes();
        List<ObjType> types = DataManager.toTypeList(presetGroupTypes,
                C_OBJ_TYPE.UNITS_CHARS);
        if (groups >= MAX_GROUPS)
            return false;
        if (checkPowerExceeding(power + calculatePower(types, true))) {
            if (shrunk_or_extended == null)
                return addGroup(true);
            if (!shrunk_or_extended)
                return addGroup(null);
            if (groups != 0)
                return false;
        }

        unitGroups.add(types);
        groups++;
        return true;
    }

    public void applyLevel() {
        // TODO foreach unit add level and break if exceeding power
        for (ObjType type : getTypeMap()) {
            ObjType newType = new UnitLevelManager().getLeveledType(type, 1, true);
            type.setType(newType);
            if (!checkPowerAdjustmentNeeded()) {
                return;
            }
        }
    }

    public boolean applyFill() {
        // TODO for each group, add a (next) unit from filler pool
        for (List<ObjType> group : unitGroups) {
            ObjType objType = getFillingType();
            if (objType == null)
                return false;

            group.add(objType);

            if (fillApplied >= getMaxFillNumber() || !checkPowerAdjustmentNeeded())
                return false;
        }
        return true;

    }

    public int getMaxFillNumber() {
        //TODO block size heuristic...
        return 123;
    }

    public ObjType getFillingType() {
        fillApplied = 0;
        String list = encounter.getProperty(PROPS.FILLER_TYPES);
        ObjType objType = null;

        if (RandomWizard.isWeightMap(list) || list.contains("@"))
            objType = RandomWizard.getObjTypeByWeight(list, DC_TYPE.UNITS);

        if (objType == null) {
            List<ObjType> fillingTypes = DataManager
                    .toTypeList(list, C_OBJ_TYPE.UNITS_CHARS);
            if (fillingTypes.isEmpty())
                return null;
            if (fillApplied >= fillingTypes.size()) {
                fillApplied = 0;
            }
            objType = fillingTypes.get(fillApplied);
        }
        if (objType == null)
            return null;
        fillApplied++;
        return objType;
    }

    private void removeGroup(int index) {
        unitGroups.remove(index);
        groups--;
    }

    private boolean applyExtend() {
        int index = groups - groupsExtended - 1;
        if (index < 0 || index >= unitGroups.size())
            return false;
        removeGroup(index);
        addGroup(false);
        groupsExtended++;
        return true;
    }

    public int calculatePower(Collection<ObjType> unitTypes, boolean addLevelUps) {
        int power = 0;
        for (ObjType type : unitTypes) {
            power += type.getIntParam(PARAMS.POWER);
            // if (addLevelUps)
            //TODO  power += DC_Formulas.getPowerFromUnitLevel(level);
        }
        return power;
    }

    private boolean checkPowerAdjustmentNeeded() {
        int power = calculatePower(DataManager.toTypeList(getTypeMap()), false);
        if (getTargetPower() <= power)
            return false;
        int diff = (getTargetPower() - power) * 100 / getTargetPower();
        LogMaster.log(LOG_CHANNEL.WAVE_ASSEMBLING, encounter.getName() + "' Power = " + power
                + " vs target of " + getTargetPower() + ", diff = " + diff);
        return diff > POWER_GAP;

    }

    public int getTargetPower() {
        if (forcedPower != null)
            return forcedPower;
        return target_power;
    }

    private boolean checkPowerExceeding(int i) {
        if (getTargetPower() >= i)
            return false;
        int diff = (i - getTargetPower()) * 100 / i;
        LogMaster.log(LOG_CHANNEL.WAVE_ASSEMBLING, i + " vs target of " + getTargetPower()
                + ", diff = " + diff);
        return diff > POWER_GAP;
    }

    private List<GROWTH_PRIORITIES> getPriorities(Encounter encounter) {
        return new EnumMaster<GROWTH_PRIORITIES>().getEnumList(GROWTH_PRIORITIES.class, encounter
                .getProperty(PROPS.GROWTH_PRIORITIES));
    }

    public int getPowerPercentage(Encounter encounter) {
        ENCOUNTER_TYPE TYPE = encounter.getWaveType();
        int power = 0;
        switch (TYPE) {
            case BOSS:
                power = BOSS_POWER;
            case ELITE:
                power = ELITE_POWER;
            case REGULAR:
                power = REGULAR_POWER;
            case CONTINUOUS:
                break;

        }

        Integer mod = encounter.getIntParam(PARAMS.POWER_MOD);
        if (mod == 0) {
            if (encounter.getEncounterType() == ENCOUNTER_TYPE.BOSS)
                mod = ENCOUNTER_DEFAULT_BOSS_POWER_MOD;
            else
                mod = 100;
        }
        power = MathMaster.applyMod(power, mod);

        Dungeon dungeon = manager.getGame().getDungeonMaster().getDungeon();
        if (dungeon != null) {
            mod = dungeon.getIntParam(PARAMS.POWER_MOD);
            if (mod == 0) {
                if (dungeon.isBoss())
                    mod = DUNGEON_DEFAULT_BOSS_POWER_MOD;
                else
                    mod = 100;
            }
            power = MathMaster.applyMod(power, mod);
        }
        return power;

    }

    public Integer getForcedPower() {
        return forcedPower;
    }

    public void setForcedPower(Integer forcedPower) {
        this.forcedPower = forcedPower;
    }

    public List<ObjType> getTypeMap() {
        return unitGroups.stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
