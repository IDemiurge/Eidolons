package main.game.logic.arena;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.EncounterEnums;
import main.content.enums.EncounterEnums.ENCOUNTER_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.elements.conditions.StringComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.logic.battle.BattleOptions;
import main.game.logic.battle.BattleOptions.ARENA_GAME_OPTIONS;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.generic.Positioner;
import main.game.meta.skirmish.SkirmishMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArenaBattleConstructor {
    public static final ENCOUNTER_TYPE[] default_encounter_sequence_1 = {EncounterEnums.ENCOUNTER_TYPE.REGULAR,
            EncounterEnums.ENCOUNTER_TYPE.ELITE,
            // ENCOUNTER_TYPE.REGULAR,
    };
    // let's switch on DIFFICULTY - neophyte could have r/r/e/b, avatar -
    // r/e/r/e/b + e/r/e/b
    // beating a dungeons on high difficulty should be something you do with a
    // Victorous party for fun...
    public static final ENCOUNTER_TYPE[] default_encounter_sequence_2 = {
            // ENCOUNTER_TYPE.ELITE,
            EncounterEnums.ENCOUNTER_TYPE.BOSS};

    public static final ENCOUNTER_TYPE[] boss_encounter_sequence_1 = {EncounterEnums.ENCOUNTER_TYPE.REGULAR,
            EncounterEnums.ENCOUNTER_TYPE.ELITE, EncounterEnums.ENCOUNTER_TYPE.REGULAR, EncounterEnums.ENCOUNTER_TYPE.ELITE, EncounterEnums.ENCOUNTER_TYPE.BOSS};
    public static final ENCOUNTER_TYPE[] boss_encounter_sequence_2 = {EncounterEnums.ENCOUNTER_TYPE.BOSS};

    // public static final ENCOUNTER_TYPE[] default_encounter_sequence_1 = {
    // ENCOUNTER_TYPE.REGULAR, ENCOUNTER_TYPE.REGULAR,
    // ENCOUNTER_TYPE.ELITE, };
    // // perhaps a pause here?
    // public static final ENCOUNTER_TYPE[] default_encounter_sequence_2 = {
    // ENCOUNTER_TYPE.REGULAR, ENCOUNTER_TYPE.ELITE, ENCOUNTER_TYPE.BOSS };
    public static final ENCOUNTER_TYPE[][] default_sequences = {default_encounter_sequence_1,
            default_encounter_sequence_2};
    public static final ENCOUNTER_TYPE[][] boss_sequences = {boss_encounter_sequence_1,
            boss_encounter_sequence_1};

    private static final Integer REGULARS_ROUNDS_TO_FIGHT = 4;
    private static final Integer REGULARS_ROUNDS_TO_FIGHT_MAX = 5;
    private static final Integer ELITE_ROUNDS_TO_FIGHT = 5;
    private static final Integer ELITE_ROUNDS_TO_FIGHT_MAX = 7;
    private static final Integer BOSS_ROUNDS_TO_FIGHT = 5;
    private static final Integer BOSS_ROUNDS_TO_FIGHT_MAX = 8;
    private static final int ALT_ENCOUNTER_DEFAULT_CHANCE = 50;
    private static final Integer BOSS_DUNGEON_SPAWN_DELAY_MOD = 65;
    private ArenaManager manager;
    private DC_Game game;
    private BattleOptions arenaOptions;
    private ENCOUNTER_TYPE[][] sequences;
    private int index = 0;
    private Boolean alt;
    private Positioner positioner;
    private boolean encounter;
    private List<Coordinates> usedSpawnCoordinates;
    private boolean sideSpawnTestMode = true;

    public ArenaBattleConstructor(ArenaManager manager) {
        this.manager = manager;
        this.game = manager.getGame();
        this.arenaOptions = manager.getArenaOptions();
        sequences = default_sequences;
        positioner = new Positioner();
    }

    public void init() {
    }

    public void setEncounterSequence(Map<Wave, Integer> waveSequence) {
        manager.getSpawnManager().setScheduledWaves(waveSequence);
        setEncounter(true);
    }

    public boolean construct() {
        usedSpawnCoordinates = new LinkedList<>();
        setIndex(0);
        if (isEncounter()) {
            return false;
        }
        if (sequences.length <= getIndex()) {
            return false;
        }
        Map<Wave, Integer> waves = constructWaveSequence(getEncounterTypes(getIndex()));
        if (waves.isEmpty()) {
            return false;
        }
        manager.getSpawnManager().setScheduledWaves(waves);
        manager.getSpawnManager().setPositioner(positioner);

        String encounters = "";
        for (Wave type : waves.keySet()) {
            encounters += type.getName() + " on " + waves.get(type) + ", ";
        }
        encounters = encounters.substring(0, encounters.length() - 2);
        if (Eidolons.DEV_MODE) {
            game.getLogManager().log("Encounters scheduled: " + encounters);
        }
        return true;
    }

    private ENCOUNTER_TYPE[] getEncounterTypes(int i) {
        if (getDungeon().isBoss()) {
            return boss_sequences[i];
        }
        return sequences[i];
    }

    // forDungeon()

    private boolean checkAltEncounter() {
        // TODO constant chance? per-dungeon chance? per-difficulty? toggle per
        // attempt?
        //
        return RandomWizard.chance(ALT_ENCOUNTER_DEFAULT_CHANCE);
    }

    public Map<Wave, Integer> constructWaveSequence(ENCOUNTER_TYPE[] encounter_sequence) {

        Integer round = getRoundNumber();
        if (SkirmishMaster.isSkirmish()) {
            return SkirmishMaster.constructWaveSequences(round);
        }

        Map<Wave, Integer> map = new XLinkedMap<>();

        if (!getDungeon().getProperty(PROPS.ENCOUNTER_GROUPS).isEmpty()) {
            return constructEncounterGroup();
        }

        if (alt != null) {
            alt = !alt;
        } else {
            alt = checkAltEncounter();
        }
        List<ObjType> waves;

        waves = getWaveTypes();
        List<ObjType> waveBuffer = new LinkedList<>(waves);

        for (ENCOUNTER_TYPE type : encounter_sequence) {
            if (waveBuffer.isEmpty()) {
                waveBuffer = DataManager.getTypesGroup(DC_TYPE.ENCOUNTERS, StringMaster
                        .getWellFormattedString(type.toString()));
            }
            waves = new LinkedList<>(waveBuffer);

            Conditions conditions = new Conditions(getEncounterTypeCondition(type)
//         TODO     ,getPlayableCondition()
            );

            List<ObjType> filteredWaves = new Filter<ObjType>(game, conditions).filter(waves);
            if (!filteredWaves.isEmpty()) {
                waves = filteredWaves;
            } else {
                continue;
            }
            ObjType waveType = getWaveType(waves, type);

            waveType = new ObjType(waveType);
            FACING_DIRECTION side = positioner.nextSide();
            waveType.setProperty(PROPS.SPAWNING_SIDE, side.getName());

            Coordinates c = pickSpawnCoordinateForWave(type, round, waveType);

            Wave wave = new Wave(c, waveType, game, new Ref(game), game.getPlayer(false));
            wave.initUnitMap();
            map.put(wave, round);
            if (Eidolons.DEV_MODE) {
                game.getLogManager().logInfo(wave.toString() + " on round #" + round);
            }
            LogMaster.log(1, wave.toString() + " on round #" + round);

            round += getRoundsToFight(waveType, type); // TODO subtract from
            // total pool

        }
        setIndex(getIndex() + 1);
        return map;
    }

    private Coordinates pickSpawnCoordinateForWave(ENCOUNTER_TYPE type, Integer round,
                                                   ObjType waveType) {
        return pickSpawnCoordinateForWave(type, round, waveType, false);
    }

    private Coordinates pickSpawnCoordinateForWave(ENCOUNTER_TYPE type, Integer round,
                                                   ObjType waveType, boolean recursion) {
        if (sideSpawnTestMode) {
            return null;
        }
        int minDistance = Integer.MAX_VALUE;
        int maxDistance = 0;
        Map<Coordinates, Point> map = new HashMap<>();
        for (String substring : StringMaster.openContainer(getDungeon().getProperty(
                PROPS.ENCOUNTER_SPAWN_POINTS))) {
            Coordinates coordinates = new Coordinates(substring);
            if (usedSpawnCoordinates.contains(coordinates)) {
                continue;
            }
            for (Obj member : game.getPlayer(true).getControlledUnits()) {
                // summoned?
                int distance = PositionMaster.getDistance(member.getCoordinates(), coordinates);
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
            map.put(coordinates, new Point(minDistance, maxDistance));
        }
        if (map.isEmpty()) {
            if (recursion) {
                return getDefaultSpawnCoordinate(type, round, waveType);
            }
            usedSpawnCoordinates.clear();
            return pickSpawnCoordinateForWave(type, round, waveType, true);
        }
        Coordinates pick = null;
        int minDistanceToOptimal = Integer.MAX_VALUE;

        // TODO OR JUST GET MIN DISTANCE AND MAKE SURE IT'S CLOSEST TO
        // AVERAGE!!!

        for (Coordinates coordinates : map.keySet()) {
            minDistance = map.get(coordinates).x;
            maxDistance = map.get(coordinates).y;
            int distanceToOptimal = Math.max(0, getOptimalMinDistance() - minDistance);
            distanceToOptimal += Math.max(0, maxDistance - getOptimalMaxDistance());

            if (distanceToOptimal < minDistanceToOptimal) {
                minDistanceToOptimal = distanceToOptimal;
                pick = coordinates;
            }
        }
        usedSpawnCoordinates.add(pick);
        return pick;
    }

    private Coordinates getDefaultSpawnCoordinate(ENCOUNTER_TYPE type, Integer round,
                                                  ObjType waveType) {
        // ENEMY_SPAWN_COORDINATES TODO
        return getDungeon().getDefaultEnemyCoordinates();
    }

    private int getOptimalMaxDistance() {
        return getDungeon().getSquare() / 100 + 3;
    }

    private int getOptimalMinDistance() {
        return getDungeon().getSquare() / 250 + 1;
    }

    private StringComparison getEncounterTypeCondition(ENCOUNTER_TYPE type) {
        return new StringComparison(type.toString(), StringMaster.getValueRef(KEYS.MATCH,
                G_PROPS.ENCOUNTER_TYPE), true);
    }

    private StringComparison getPlayableCondition() {
        return new StringComparison(StringMaster.PLAYABLE, StringMaster.getValueRef(KEYS.MATCH,
                G_PROPS.GROUP), true);
    }

    private List<ObjType> getWaveTypes() {
        List<ObjType> waves;
        // if (SkirmishMaster.isSkirmish()) {
        // return SkirmishMaster.getWaveTypes();
        // }
        ObjType dungeonType = getDungeon().getType();
        waves = DataManager.toTypeList(dungeonType.getProperty(alt ? PROPS.ALT_ENCOUNTERS
                : PROPS.ENCOUNTERS), DC_TYPE.ENCOUNTERS);
        // TODO need a better preCheck...
        if (waves.size() < 3) {
            waves = DataManager.toTypeList(dungeonType
                            .getProperty(!alt ? PROPS.ALT_ENCOUNTERS : PROPS.ENCOUNTERS),
                    DC_TYPE.ENCOUNTERS);
        }
        return waves;
    }

    private Dungeon getDungeon() {
        return game.getDungeonMaster().getDungeon();
    }

    private Map<Wave, Integer> constructEncounterGroup(String group) {
        // TODO Auto-generated method stub
        return null;
    }

    private int getRoundNumber() {
        return game.getState().getRound()
                + StringMaster.getInteger(arenaOptions
                .getValue(ARENA_GAME_OPTIONS.TURNS_TO_PREPARE));
    }

    private Map<Wave, Integer> constructEncounterGroup() {
        String prop = getDungeon().getProperty(PROPS.ENCOUNTER_GROUPS);
        String group = new RandomWizard<String>().getObjectByWeight(prop, String.class);
        return constructEncounterGroup(group);
    }

    private ObjType getWaveType(List<ObjType> waves, ENCOUNTER_TYPE type) {
        ObjType waveType;
        while (true) {
            waveType = waves.get(RandomWizard.getRandomListIndex(waves));
            // could roll by power if I can getOrCreate the target power...

            if (checkEncounter(waveType)) {
                break;
            }
        }
        return waveType;
    }

    private boolean checkEncounter(ObjType waveType) {
        return true;
//     TODO    return waveType.checkGroup(StringMaster.PLAYABLE);
    }

    public Integer getRoundsToFight(ObjType waveType) {

        return getRoundsToFight(waveType, new EnumMaster<ENCOUNTER_TYPE>().retrieveEnumConst(
                ENCOUNTER_TYPE.class, waveType.getProperty(G_PROPS.ENCOUNTER_TYPE)));
    }

    public Integer getRoundsToFight(ObjType waveType, ENCOUNTER_TYPE type) {

        int amount = 0;
        // if (game.getDungeonMaster().getDungeon() != null) {
        // game.getDungeonMaster().getDungeon().getProperty(PROPS.SPAWNING_DELAYS);
        // }
        switch (type) {
            case REGULAR:
                amount = RandomWizard.getRandomIntBetween(REGULARS_ROUNDS_TO_FIGHT,
                        REGULARS_ROUNDS_TO_FIGHT_MAX);
                break;
            case ELITE:
                amount = RandomWizard.getRandomIntBetween(ELITE_ROUNDS_TO_FIGHT,
                        ELITE_ROUNDS_TO_FIGHT_MAX);
                break;
            case BOSS:
                amount = RandomWizard.getRandomIntBetween(BOSS_ROUNDS_TO_FIGHT,
                        BOSS_ROUNDS_TO_FIGHT_MAX);
                break;

        }
        amount += waveType.getIntParam(PARAMS.SPAWNING_DELAY_BONUS);
        if (waveType.getIntParam(PARAMS.SPAWNING_DELAY_MOD) > 0) {
            amount = MathMaster.applyMod(amount, waveType.getIntParam(PARAMS.SPAWNING_DELAY_MOD));
        }
        if (getDungeon().isBoss()) {
            amount = MathMaster.applyMod(amount, BOSS_DUNGEON_SPAWN_DELAY_MOD);
        }
        if (amount < 0) {
            amount = 1;
        }
        return MathMaster.applyMod(amount, game.getArenaManager().getArenaOptions().getDifficulty()
                .getRoundsToFightMod());

    } // more on higher levels? battle.getLevel()

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Positioner getPositioner() {
        return positioner;
    }

    public void setPositioner(Positioner positioner) {
        this.positioner = positioner;
    }

    public boolean isEncounter() {
        return encounter;
    }

    public void setEncounter(boolean encounter) {
        this.encounter = encounter;
    }

    public void addDungeonEncounter(Coordinates c, ObjType type) {
        // TODO Auto-generated method stub

    }

}
