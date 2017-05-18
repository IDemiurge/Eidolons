package main.game.battlecraft.logic.battle.arena;

import main.client.cc.logic.UnitLevelManager;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.EncounterEnums;
import main.content.enums.EncounterEnums.ENCOUNTER_TYPE;
import main.content.enums.EncounterEnums.GROWTH_PRIORITIES;
import main.data.DataManager;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.BattleHandler;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.BattleOptions;
import main.game.battlecraft.logic.battle.universal.BattleOptions.DIFFICULTY;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.Spawner;
import main.game.battlecraft.logic.dungeon.arena.ArenaPositioner;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNELS;
import main.system.graphics.GuiManager;
import main.system.math.MathMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//TODO  support WEIGHT-format: pick from unit pool until target-power is filled or 
// apply growth per random 
public class WaveAssembler extends BattleHandler<ArenaBattle> {

    public static final int REGULAR_POWER = 100;
    public static final int ELITE_POWER = 100;
    public static final int BOSS_POWER = 100;
    public static final int POWER_GAP = 10;
    public static final int MAX_GROUPS = 4;
    public static final Integer DUNGEON_DEFAULT_BOSS_POWER_MOD = 125;
    public static final Integer ENCOUNTER_DEFAULT_BOSS_POWER_MOD = 120;

    public ArenaPositioner positioner;
    public BattleOptions options;
    public Wave wave;
    public DIFFICULTY difficulty;
    public List<List<ObjAtCoordinate>> unitGroups;
    public int unitLevel = 0;
    public int groups = 0;
    public Integer target_power = 0;
    public Integer power = 0;
    public int fillApplied = 0;
    public int groupsExtended = 0;
    public Dungeon dungeon;
    List<GROWTH_PRIORITIES> growthPriorities;
    // public Map<ObjType, Coordinates> typeMap;
    List<ObjAtCoordinate> typeMap = new LinkedList<>();
    Integer forcedPower;

    public WaveAssembler(BattleMaster<ArenaBattle> master) {
        super(master);

    }


    public void assembleWave(Wave wave) {
        try {
            assembleWave(wave, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {
        this.options = master.getOptionManager().getOptions();
        this.difficulty = options.getDifficulty();
        dungeon = getMaster().getDungeon();
        positioner = (ArenaPositioner) getMaster().getPositioner();

    }

    public void assembleWave(Wave wave, boolean adjustPower, boolean presetCoordinate) {


        groupsExtended = 0;
        fillApplied = 0;
        groups = 0;
        positioner = positioner;
        this.wave = wave;
        typeMap = new LinkedList<>();
        unitGroups = new LinkedList<>();
        // map?
        initPower();
        applyAddGroup();
        if (!ListMaster.isNotEmpty(unitGroups)) {
            return;
        }
        if (adjustPower) {
            if (checkPowerAdjustmentNeeded()) {
                try {
                    adjustPower();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        wave.setUnitMap(typeMap);
        wave.setPower(power);
        // what about string output?

    }

    private void initPower() {
        int percentage = getPowerPercentage(wave);
        percentage = percentage * difficulty.getPowerPercentage() / 100;
        // Integer base_power = wave.getBasePower(); // min/max?
        // power = base_power;
        if (wave.getPreferredPower() != 0) {
            target_power = wave.getPreferredPower() * percentage / 100;
        } else {
//      TODO       target_power = options.getBattleLevel() * percentage / 100;
        }
    }

    private void adjustPower() {
        growthPriorities = getPriorities(wave);
        int index = 0;
        Loop.startLoop(1000);
        while (checkPowerAdjustmentNeeded() && !Loop.loopEnded()) {
            GROWTH_PRIORITIES priority = growthPriorities.get(index);
            index++;
            if (index >= growthPriorities.size()) {
                index = 0;
            }
            try {
                applyGrowth(priority);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void applyGrowth(GROWTH_PRIORITIES priority) {
        LogMaster.log(LOG_CHANNELS.WAVE_ASSEMBLING, priority + " is being applied" + "; Power = "
                + power);
        switch (priority) {
            case EXTEND:
                applyExtend();
                break;
            case FILL:
                if (!applyFill()) {
                    break; // TODO
                }
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
        String presetGroupTypes = wave.getPresetGroupTypes();
        if (shrunk_or_extended != null) {
            presetGroupTypes = (shrunk_or_extended) ? wave.getShrunkenGroupTypes() : wave
                    .getExtendedGroupTypes();
        }
        List<ObjType> types = DataManager.toTypeList(presetGroupTypes,
                C_OBJ_TYPE.UNITS_CHARS);
        if (groups >= MAX_GROUPS) {
            return false;
        }
        if (checkPowerExceeding(power + calculatePower(types, true))) {
            if (shrunk_or_extended == null) {
                return addGroup(true);
            }
            if (!shrunk_or_extended) {
                return addGroup(null);
            }
            if (groups != 0) {
                return false;
            }
        }
        positioner.setMaxSpacePercentageTaken(Spawner.MAX_SPACE_PERC_CREEPS);
        List<ObjAtCoordinate> group = positioner.getCoordinatesForUnitGroup(types, wave, unitLevel);
        typeMap.addAll(group);
        unitGroups.add(group);
        groups++;
        return true;
    }

    public void resetPositions(Wave wave) {
        List<ObjType> types = wave.getUnitMap().stream().map(t -> t.getType()).collect(Collectors.toList());
        positioner.setMaxSpacePercentageTaken(Spawner.MAX_SPACE_PERC_CREEPS);
        List<ObjAtCoordinate> group =
                positioner.getCoordinatesForUnitGroup(types, wave, wave.getUnitLevel());
        wave.setUnitMap(group);
    }

    public void applyLevel() {
        // TODO foreach unit add level and break if exceeding power
        unitLevel++;
        wave.setUnitLevel(unitLevel);
        for (ObjAtCoordinate type : typeMap) {
            ObjType newType = new UnitLevelManager().getLeveledType(type.getType(), 1, true);
            type.setType(newType);
            if (!checkPowerAdjustmentNeeded()) {
                return;
            }
        }
    }

    public boolean applyFill() {
        // TODO for each group, add a (next) unit from filler pool
        int i = 0;
        for (List<ObjAtCoordinate> group : unitGroups) {
            ObjType objType = getFillingType();
            if (objType == null) {
                return false;
            }
            FACING_DIRECTION side;
            try {
                side = positioner.getSides().get(i);
            } catch (Exception e) {
                // if (group.size() == 0)
                return false;
                // Coordinates c = (Coordinates) group.keySet().toArray()[0];
                // if (c == null)
                // return false;
                // side = positioner.getFacingForEnemy(c);
            }
            i++;
            Coordinates c = null;
            try {
                c = positioner.getCoordinatesForNewUnitInGroup(side, objType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (c == null) {
                return false;
            }
            ObjAtCoordinate objAtCoordinate = new ObjAtCoordinate(objType, c);
            group.add(objAtCoordinate);
            typeMap.add(objAtCoordinate);

            if (fillApplied >= getMaxFillNumber() || !checkPowerAdjustmentNeeded()) {
                return false;
            }
        }
        return true;

    }

    public int getMaxFillNumber() {
        return GuiManager.getCellNumber() - typeMap.size() - getMaster().getGame().getUnits().size();
    }

    public ObjType getFillingType() {
        fillApplied = 0;
        String list = wave.getProperty(PROPS.FILLER_TYPES);
        ObjType objType = null;

        if (RandomWizard.isWeightMap(list) || list.contains("@")) {
            objType = RandomWizard.getObjTypeByWeight(list, DC_TYPE.UNITS);
        }

        if (objType == null) {
            List<ObjType> fillingTypes = DataManager
                    .toTypeList(list, C_OBJ_TYPE.UNITS_CHARS);
            if (fillingTypes.isEmpty()) {
                return null;
            }
            if (fillApplied >= fillingTypes.size()) {
                fillApplied = 0;
            }
            objType = fillingTypes.get(fillApplied);
        }
        if (objType == null) {
            return null;
        }
        fillApplied++;
        return objType;
    }

    private void removeGroup(int index) {
        List<ObjAtCoordinate> group = unitGroups.get(index);
        typeMap.removeAll(group);
        unitGroups.remove(group);
        groups--;
        if (wave.getBlock() != null) {
            positioner.blockGroupRemoved(wave.getBlock(), group);
        } else {
            positioner.sideRemoved();
        }
    }

    private boolean applyExtend() {
        int index = groups - groupsExtended - 1;
        if (index < 0 || index >= unitGroups.size()) {
            return false;
        }
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
            // power += DC_Formulas.getPowerFromUnitLevel(level);
        }
        return power;
    }

    private boolean checkPowerAdjustmentNeeded() {
        int power = calculatePower(DataManager.toTypeList(typeMap), false);
        if (getTargetPower() <= power) {
            return false;
        }
        int diff = (getTargetPower() - power) * 100 / getTargetPower();
        LogMaster.log(LOG_CHANNELS.WAVE_ASSEMBLING, wave.getName() + "' Power = " + power
                + " vs target of " + getTargetPower() + ", diff = " + diff);
        return diff > POWER_GAP;

    }

    public int getTargetPower() {
        if (forcedPower != null) {
            return forcedPower;
        }
        return target_power;
    }

    private boolean checkPowerExceeding(int i) {
        if (getTargetPower() >= i) {
            return false;
        }
        int diff = (i - getTargetPower()) * 100 / i;
        LogMaster.log(LOG_CHANNELS.WAVE_ASSEMBLING, i + " vs target of " + getTargetPower()
                + ", diff = " + diff);
        return diff > POWER_GAP;
    }

    private List<GROWTH_PRIORITIES> getPriorities(Wave wave) {
        return new EnumMaster<GROWTH_PRIORITIES>().getEnumList(GROWTH_PRIORITIES.class, wave
                .getProperty(PROPS.GROWTH_PRIORITIES));
    }

    public int getPowerPercentage(Wave wave) {
        ENCOUNTER_TYPE TYPE = wave.getWaveType();
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

        Integer mod = wave.getIntParam(PARAMS.POWER_MOD);
        if (mod == 0) {
            if (wave.getEncounterType() == EncounterEnums.ENCOUNTER_TYPE.BOSS) {
                mod = ENCOUNTER_DEFAULT_BOSS_POWER_MOD;
            } else {
                mod = 100;
            }
        }
        power = MathMaster.applyMod(power, mod);

//        dungeon = manager.getGame().getDungeonMaster().getDungeon();
        if (dungeon != null) {
            mod = dungeon.getIntParam(PARAMS.POWER_MOD);
            if (mod == 0) {
                if (dungeon.isBoss()) {
                    mod = DUNGEON_DEFAULT_BOSS_POWER_MOD;
                } else {
                    mod = 100;
                }
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

}
