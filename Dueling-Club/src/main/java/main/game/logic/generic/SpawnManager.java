package main.game.logic.generic;

import main.ability.UnitTrainingMaster;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.data.XList;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.MicroObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.ai.GroupAI;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DC_ObjInitializer;
import main.game.battlefield.FacingMaster;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.logic.arena.ArenaManager;
import main.game.logic.arena.UnitGroupMaster;
import main.game.logic.arena.Wave;
import main.game.logic.battle.BattleOptions.ARENA_GAME_OPTIONS;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.ai.DungeonCrawler;
import main.game.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.logic.dungeon.building.MapBlock;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.macro.travel.EncounterMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.test.TestMasterContent;
import main.test.PresetMaster;

import java.util.*;

public class SpawnManager {

    public static final Integer MAX_SPACE_PERC_CREEPS = 25; // 1 per cell only
    private static final Integer MAX_SPACE_PERC_PARTY = 0;
    private static boolean playerUnitGroupMode;
    private static boolean enemyUnitGroupMode;
    Map<Dungeon, Map<MapBlock, Map<Coordinates, ObjType>>> specialEncounters = new HashMap<>();
    private ArenaManager arenaManager;
    private DC_Game game;
    private DC_Player enemy;
    private Positioner positioner;
    private String playerPartyData = "";
    private int roundsToWait = 0;
    private LinkedList<ObjType> wavePool;
    private boolean wavesOverlap;
    private Map<Wave, Integer> scheduledWaves;
    private boolean autoSpawnOn;
    private String enemyPartyData;
    private Map<Integer, FACING_DIRECTION> multiplayerFacingMap;

    public SpawnManager(DC_Game game, Positioner positioner) {
        this.game = game;
        this.positioner = positioner;
    }

    public SpawnManager(DC_Game game, ArenaManager arenaManager) {
        this.game = game;
        this.arenaManager = arenaManager;
        this.enemy = arenaManager.getEnemyPlayer();

    }

    public static boolean isPlayerUnitGroupMode() {
        return playerUnitGroupMode;
    }

    public static void setPlayerUnitGroupMode(boolean b) {
        playerUnitGroupMode = b;

    }

    public void init() {
        // this.level = battle.getIntValue(BATTLE_STATS.LEVEL);
        if (!isPlayerUnitGroupMode() // && PresetMaster.getPreset() == null
                ) {
            initPlayerParty();
        } else {
            playerPartyData = getGame().getPlayerParty();
        }
        if (playerPartyData.isEmpty()) {
            playerPartyData = getGame().getData().getPlayerUnitData();
        }

        if (!isEnemyUnitGroupMode() && PresetMaster.getPreset() == null) {
            initEnemyParty();
        } else {
            enemyPartyData = getGame().getEnemyParty();
        }
        if (enemyPartyData.isEmpty()) {
            enemyPartyData = getGame().getData().getPlayer2UnitData();
        }
        // if (!CharacterCreator.isPartyMode())
        // waveCleared(); TODO what for???
        this.roundsToWait = arenaManager.getArenaOptions().getIntValue(
                ARENA_GAME_OPTIONS.TURNS_TO_PREPARE);

    }

    public void addDungeonEncounter(Dungeon c_dungeon, MapBlock block, Coordinates c, ObjType type) {
        Map<MapBlock, Map<Coordinates, ObjType>> map = specialEncounters.get(c_dungeon);
        if (map == null) {
            map = new HashMap<>();
            specialEncounters.put(c_dungeon, map);
        }
        Map<Coordinates, ObjType> encounterMap = map.get(block);
        if (encounterMap == null) {
            encounterMap = new HashMap<>();
            map.put(block, encounterMap);
        }
        encounterMap.put(c, type);
    }

    public void spawnDungeonCreeps(Dungeon dungeon) {

        // special units (preset)
        // groups - in rooms/spec places; behavior - per preference

        // for open-air instances - pick areas around entrances or treasures or
        // other objects or just random but zone-based!

        // for (b block : dungeon.getBlocks()) {
        // }
        // dungeon.getMap().getBlock(blockName);

		/*
         * Assign block per creep group? So a dungeon has a repertoire and map template...
		 * then we calculate total power...
		 * First, spawn the 'must have' groups, around entrances and treasures
		 */
        if (dungeon.isSublevel()) {

        } else {
            // different alg?
        }
        // PartyManager.getParty().getTotalPower();
        // int power = DungeonMaster.getDungeonPowerTotal(dungeon);
        // int maxGroups = dungeon.getIntParam(PARAMS.MAX_GROUPS);

        int power = 0;

        int preferredPower = dungeon.getLevel()
                // + PartyManager.getParty().getPower()
                + getGame().getArenaManager().getBattleLevel();
        int min = preferredPower * 2 / 3;
        int max = preferredPower * 3 / 2;

        for (MapBlock block : dungeon.getPlan().getBlocks()) {
            Wave group;

            if (specialEncounters.get(dungeon) != null) {
                Map<Coordinates, ObjType> specEncounters = specialEncounters.get(dungeon)
                        .get(block);
                for (Coordinates c : specEncounters.keySet()) {
                    ObjType waveType = specEncounters.get(c);

                    if (waveType.getGroup().equalsIgnoreCase("Substitute")) {
                        waveType = EncounterMaster.getSubstituteEncounterType(waveType, dungeon,
                                preferredPower);
                    }

                    group = new Wave(waveType, game, new Ref(), game.getPlayer(false));
                    group.setCoordinates(c);
                    spawnWave(group, true);
                    initGroup(group);
                    power += group.getPower();

                }

            } else { // TODO POWER PER BLOCK!
                if (!autoSpawnOn) {
                    continue;
                }
                // if (power < preferredPower)
                // preferredPower = power;
                // if (power < preferredPower / 3)
                // break;
                if (!checkSpawnBlock(block)) {
                    continue;
                }
                // sort blocks! by spawn priority...
                // can be more than 1 group, right? maybe merge?
                group = getCreepGroupForBlock(preferredPower, dungeon, block, min, max);
                group.setPreferredPower(preferredPower);

                spawnWave(group, true);
                initGroup(group);
                // power -= group.getPower();
                power += group.getPower();
            }
        }

        if (power > min) {
            // spawn wandering creeps - apart from groups? in max distance from
            // them?
        }
    }

    private void initGroup(Wave group) {
        GroupAI groupAi = new GroupAI(group);
        groupAi.setLeader(group.getParty().getLeader());
        groupAi.setWanderDirection(FacingMaster.getRandomFacing().getDirection());
        group.setAi(groupAi);
        if (getGame().getGameMode() == GAME_MODES.DUNGEON_CRAWL) {
            XList<MapBlock> permittedBlocks = new XList<>();
            permittedBlocks.addAllUnique(group.getBlock().getConnectedBlocks().keySet());
            int wanderBlockDistance = 1;
            for (int i = 0; i < wanderBlockDistance; i++) {
                for (MapBlock b : group.getBlock().getConnectedBlocks().keySet()) {
                    permittedBlocks.addAllUnique(b.getConnectedBlocks().keySet());
                }
            }
            groupAi.setPermittedBlocks(permittedBlocks);
        }
    }

    private boolean checkSpawnBlock(MapBlock block) {
        if (DungeonCrawler.isAiTestOn()) {
            return block.getId() < 2;
        }
        return block.getRoomType() == ROOM_TYPE.GUARD_ROOM
                || block.getRoomType() == ROOM_TYPE.COMMON_ROOM
                || block.getRoomType() == ROOM_TYPE.THRONE_ROOM
                || block.getRoomType() == ROOM_TYPE.EXIT_ROOM
                || block.getRoomType() == ROOM_TYPE.DEATH_ROOM;
    }

    private Wave getCreepGroupForBlock(int preferredPower, Dungeon dungeon, MapBlock block,
                                       int min, int max) {
        // alt? vielleicht fur einige spezielle orte...
        String property = dungeon.getProperty(PROPS.ENCOUNTERS);
        int mod = block.getSpawningPriority();
        if (mod == 0) {
            mod = 100;
        }
        Wave wave;
        List<ObjType> list = DataManager.toTypeList(property, DC_TYPE.ENCOUNTERS);
        Collections.shuffle(list);
        ObjType type = null;
        for (ObjType t : list) {
            type = t;
            if (EncounterMaster.getPower(type, false) < min * mod / 100) {
                continue;
            }
            if (EncounterMaster.getPower(type, false) > max * mod / 100) {
                continue;
            }
            break;
        }
        if (type == null) {
            type = new RandomWizard<ObjType>().getObjectByWeight(property, ObjType.class);
        }
        wave = new Wave(type, game, new Ref(game), game.getPlayer(false));
        wave.setPreferredPower(preferredPower * mod / 100);
        wave.setBlock(block);

        return wave;
    }

    public Wave spawnGroup(ObjType groupType, MapBlock block) {
        // WaveAssembler. //special power limits
        Wave wave = new Wave(groupType, game, new Ref(game), game.getPlayer(false));
        wave.setBlock(block);
        spawnWave(wave, true);
        return wave;

    }

    public void spawnParty(Coordinates origin, Boolean me, ObjType party) {
        // from entrances, from sides, by default, by event, by test - useful!
        // positioner.getCoordinatesForUnitGroup(presetGroupTypes, wave);
        List<String> partyTypes = StringMaster.openContainer(party.getProperty(PROPS.MEMBERS));
        List<Coordinates> c = positioner.getPartyCoordinates(origin, me, partyTypes);
        String partyData = DC_ObjInitializer.getObj_CoordinateString(partyTypes, c);
        spawnParty(me, partyData);
    }

    public void spawnParties() {
        try {
            spawnParty(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            spawnParty(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spawnParty(boolean me) {
        spawnParty(me, null);
    }

    public void spawnUnitsAt(List<Unit> units, Coordinates coordinates) {
        List<String> partyTypes = StringMaster.toNameList(units);
        List<Coordinates> coordinateList = game.getArenaManager().getSpawnManager()
                .initPartyCoordinates(partyTypes, null);
        int index = 0;
        for (Unit m : units) {
            m.setCoordinates(coordinateList.get(index));
            index++;
        }
    }

    public void spawnPartyAt(PartyObj party, Coordinates coordinates) {
        spawnUnitsAt(party.getMembers(), coordinates);
    }

    public boolean isUnitGroupMode(boolean me) {
        return me ? isPlayerUnitGroupMode() : isEnemyUnitGroupMode();
    }

    public void spawnParty(boolean me, String partyData) {
        boolean custom = false;
        if (partyData == null) {
            partyData = me ? playerPartyData : enemyPartyData;
        } else {
            custom = true;
        }
        if (StringMaster.isEmpty(partyData)) {
            return;
        }
        if ((isUnitGroupMode(me)) || partyData.contains(".xml")) {
            spawnUnitGroup(me, partyData);
            return;
        }

        DC_Player player = game.getPlayer(me);
        if (BooleanMaster.isTrue(me)) {
            if (game.getParty() != null) {
                SoundMaster.playEffectSound(SOUNDS.READY, game.getParty().getLeader());
                spawnPlayerParty(game.getParty(), partyData);
                return;
            }

        }
        if (!partyData.contains(DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR)) {
            partyData = StringMaster.convertVarStringToObjCoordinates(partyData);
        }
        List<MicroObj> list = DC_ObjInitializer.processUnitDataString(player, partyData, game);
        if (!ListMaster.isNotEmpty(list)) {
            return;
        }
        for (MicroObj unit : list) {
            FACING_DIRECTION facing;
            if (!game.isOffline()) {
                // TODO not always vertical!
                facing = FacingMaster.getFacingFromDirection(getPositioner().getClosestEdgeY(
                        unit.getCoordinates()).getDirection().flip());
            } else
//             TODO    if (game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
            {
                facing = FacingMaster.getPresetFacing(me);
            }
//            } else if (me) {
//                facing = getPositioner().getPartyMemberFacing(unit.getCoordinates());
//            } else {
//                facing = getPositioner().getFacingForEnemy(unit.getCoordinates());
//            }
            ((BattleFieldObject) unit).setFacing(facing);
        }
        DC_ObjInitializer.initializePartyPositions(partyData, list);
        if (!custom) {
            player.setHeroObj(list.get(0));
            try {
                player.setEmblem( // TODO ???
                        ((Unit) list.get(0)).getEmblem().getImage());
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

    }

    private void spawnUnitGroup(boolean me, String partyData) {
        String data = UnitGroupMaster.readGroupFile(partyData);
        boolean mirror = me;
        if (UnitGroupMaster.isFactionMode()) {
            if (UnitGroupMaster.factionLeaderRequired) {
                data += UnitGroupMaster.getHeroData(me);
            }
            mirror = !mirror;
        }
        UnitGroupMaster.setMirror(mirror);

        int width = UnitGroupMaster.maxX;
        int height = UnitGroupMaster.maxY;

        Coordinates offset_coordinate;
        Coordinates spawnCoordinates;
        int offsetX = -width / 2;
        int offsetY = -height / 2;
        if (!UnitGroupMaster.isFactionMode()) {
            UnitGroupMaster.setCurrentGroupHeight(MathMaster.getMaxY(data));
            UnitGroupMaster.setCurrentGroupWidth(MathMaster.getMaxX(data));
            width = 1 + UnitGroupMaster.getCurrentGroupWidth();
            height = 2 * UnitGroupMaster.getCurrentGroupHeight();
            offsetX = -width / 2;
            offsetY = -height / 2;
        } else {
            if (UnitGroupMaster.isMirror()) {
                offsetY -= 1;
            }

        }
        spawnCoordinates = (me) ? game.getDungeon().getPlayerSpawnCoordinates() : game.getDungeon()
                .getEnemySpawningCoordinates();
        offset_coordinate = spawnCoordinates.getOffsetByX(offsetX).getOffsetByY(offsetY);
        DC_ObjInitializer.processObjData(game.getPlayer(me), data, offset_coordinate);

        // if (me) // TODO consider FLIPPING!!!
        // if (isPlayerUnitGroupMode()) {
        // spawnCoordinates =(me) ?
        // game.getDungeon().getPlayerSpawnCoordinates():game.getDungeon().getEnemySpawningCoordinates();
        //
        // offset_coordinate = spawnCoordinates.getOffsetByX(offsetX)
        // .getOffsetByY(offsetY);
        // DC_ObjInitializer.processObjData(game.getPlayer(me), data,
        // offset_coordinate);
        // }
        // if (!me)
        // if (isEnemyUnitGroupMode()) {
        // spawnCoordinates =
        // game.getDungeon().getEnemySpawningCoordinates();
        // offset_coordinate =
        // spawnCoordinates.getOffsetByX(offsetX).getOffsetByY(
        // offsetY );
        //
        // DC_ObjInitializer.processObjData(game.getPlayer(me), data,
        // offset_coordinate);
        // }
        LogMaster.logToFile("spawnCoordinates=" + spawnCoordinates + " ;offset_coordinate="
                + offset_coordinate + ";height=" + height + "; width=" + width);
        LogMaster.log(1, "spawnCoordinates=" + spawnCoordinates
                + " ;offset_coordinate=" + offset_coordinate + ";height=" + height + "; width="
                + width);
    }

    public boolean isEnemyUnitGroupMode() {
        return enemyUnitGroupMode;
    }

    public static void setEnemyUnitGroupMode(boolean b) {
        enemyUnitGroupMode = b;

    }

    private void spawnPlayerParty(PartyObj party, String partyData) {

        DC_ObjInitializer.initializePartyPositions(partyData, party.getMembers());
        int i = 0;
        getPositioner().setMaxSpacePercentageTaken(MAX_SPACE_PERC_PARTY);
        Boolean last = null;
        for (Unit hero : party.getMembers()) {
            // on initializePartyPositions() !
            // if (!game.getRules().getStackingRule().canBeMovedOnto(hero,
            // hero.getCoordinates())) {
            // // TODO tactics?
            // Coordinates c =
            // Positioner.adjustCoordinate(hero.getCoordinates(), FacingMaster
            // .getRandomFacing());
            // hero.setCoordinates(c);
            // }

            if (party.getPartyCoordinates() == null) {
                hero.setFacing(getPositioner().getPartyMemberFacing(hero.getCoordinates()));
            }

            hero.setOriginalOwner(game.getPlayer(true));
            // the player's party will be "in game" all the while, from
            // simulation?
            // game.placeUnit(hero);
            // game.getState().addObject(hero);
            i++;
            if (i == party.getMembers().size()) {
                last = true;
            }
            if (game.isDebugMode()) {
                TestMasterContent.addTestItems(hero.getType(), last);
            }
            last = false;
            if (!Launcher.isRunning()) {
                continue;
            }
            hero.setSpells(null);
            hero.initSpells(false);
            hero.fullReset(game);

        }
        game.getPlayer(true).setEmblem(party.getLeader().getEmblem().getImage());
    }

    public List<Coordinates> initPartyCoordinates(List<String> partyTypes, Boolean mine_enemy_third) {
        String partyData = "";

        if (positioner == null) {
            setPositioner(new Positioner(this));
        } else {
            positioner.setSpawner(this);
        }
        getPositioner().setMaxSpacePercentageTaken(MAX_SPACE_PERC_PARTY);
        List<Coordinates> coordinates = null;

        if (PartyManager.getParty() != null) {
            if (MapMaster.isNotEmpty(PartyManager.getParty().getPartyCoordinates())) {
                coordinates = new LinkedList<>(PartyManager.getParty().getPartyCoordinates()
                        .values());
                partyTypes = ListMaster.toNameList(PartyManager.getParty().getPartyCoordinates()
                        .keySet());
            }

        }
        if (coordinates == null) {
            coordinates = positioner.getPartyCoordinates(null, BooleanMaster
                    .isTrue(mine_enemy_third), partyTypes);
        }

        int i = 0;

        for (String subString : partyTypes) {
            Coordinates c = coordinates.get(i);
            if (c == null) {
                LogMaster.log(1, subString + " coordinate BLAST!!!");
            }
            i++;
            subString = c + DC_ObjInitializer.COORDINATES_OBJ_SEPARATOR + subString;
            partyData += subString + DC_ObjInitializer.OBJ_SEPARATOR;
        }
        if (mine_enemy_third != null) {
            if (mine_enemy_third) {
                playerPartyData = partyData;
            } else {
                enemyPartyData = partyData;
            }
        }

        return coordinates;
    }

    private void initPlayerParty() {
        initPartyCoordinates(StringMaster.openContainer(game.getPlayerParty()), true);
    }

    private void initEnemyParty() {
        initPartyCoordinates(StringMaster.openContainer(game.getEnemyParty()), false);
    }

    private void spawnWave(Wave wave) {
        spawnWave(wave, false);
    }

    public void spawnGroup(String unitNames, List<Coordinates> coordinates, DC_Player neutral) {
        // TODO wave w/o adjustment?
        // new Wave(player)
    }

    private void spawnCustomWave(String unitData, boolean coordinatesSet, DC_Player player) {
        Wave wave = new Wave(player);
        // DC_ObjInitializer.getCoordinatesFromObjListString(item)
        // wave.setUnitMap(unitMap);

        wave.setProperty(PROPS.UNIT_TYPES, unitData);
        spawnWave(wave);
    }

    private void spawnWave(Wave wave, boolean prespawnMode) {
        spawnWave(wave.getUnitMap(), wave, prespawnMode);
        initGroup(wave);
    }

    public void spawnWave(String typeName, DC_Player player, Coordinates coordinate) {
        ObjType waveType = DataManager.getType(typeName, DC_TYPE.ENCOUNTERS);
        Wave wave = new Wave(coordinate, waveType, game, new Ref(), player);
        spawnWave(null, wave, false);
        initGroup(wave);
    }

    private void spawnWave(List<ObjAtCoordinate> unitMap, Wave wave, boolean prespawnMode) {
        game.getLogManager().log("New encounter: " + wave.getName());
        if (game.getParty() != null) {
            Unit randomMember = game.getParty().getRandomMember();
            if (!randomMember.isDead()) {
                SoundMaster.playEffectSound(SOUNDS.THREAT, randomMember);
            }
        } else {
            // active unit?
        }
        if (positioner == null) {
            setPositioner(new Positioner(this));
        }
        if (unitMap == null) {
            positioner.setMaxSpacePercentageTaken(MAX_SPACE_PERC_CREEPS);
            wave.initUnitMap();
            unitMap = wave.getUnitMap();
        }
        try {
            arenaManager.getWaveAssembler().resetPositions(wave);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ObjAtCoordinate oac : unitMap) {
            Coordinates c = oac.getCoordinates();
            FACING_DIRECTION facing = getPositioner().getFacingForEnemy(c);
            boolean invalid = false;
            if (c == null) {
                invalid = true;
            } else
            if (c.isInvalid()) {
                invalid = true;
            } else
            if (game.getBattleField().getGrid().isCoordinateObstructed(c)) {
                invalid = true;
            }
            if (invalid) {
                c = Positioner.adjustCoordinate(c, facing);
            }
            ObjType type = oac.getType();
            Unit unit = (Unit) game.createUnit(type, c, wave.getOwner());
            UnitTrainingMaster.train(unit);

            unit.setFacing(facing);
            wave.addUnit(unit);
            game.fireEvent(
                    new Event(STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING, Ref.getSelfTargetingRefCopy(unit)));
        }
        if (!PartyManager.checkMergeParty(wave)) {
            try {
                PartyManager.addCreepParty(wave);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void waveCleared() {
        if (game.isStarted()) {
            game.getLogManager().log(
                    "*** Enemies cleared! Encounters left: " + getScheduledWaves().toString());
            if (game.getParty() != null) {
                SoundMaster.playEffectSound(SOUNDS.TAUNT, game.getParty().getLeader());
            }
        }
        roundsToWait = arenaManager.getArenaOptions().getIntValue(
                ARENA_GAME_OPTIONS.TURNS_BETWEEN_WAVES);
        roundsToWait++;
    }

    public void newWave(Wave wave) {
        spawnWave(wave);
        if (wavesOverlap) {
            waveCleared();
        }
    }

    public void newWave() {
        newWave(enemy);
    }

    public void newWave(DC_Player player) {
        String type = ListChooser.chooseType(DC_TYPE.ENCOUNTERS);
        if (type == null) {
            return;
        }
        newWave(new Wave(DataManager.getType(type, DC_TYPE.ENCOUNTERS), game, new Ref(game),
                player));
    }

    public void newRound() {
        Map<Wave, Integer> waves = getScheduledWaves();
        if (!waves.isEmpty()) {
            for (Wave wave : waves.keySet()) {
                if (scheduledWaves.get(wave) <= game.getState().getRound()) {
                    try {
                        spawnWave(wave);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        scheduledWaves.remove(wave);
                    }
                }
            }
        }

    }

    public FACING_DIRECTION getMultiplayerFacingForUnit(MicroObj unit) {
        return getMultiplayerFacingMap().get(unit.getId());
    }

    public Map<Integer, FACING_DIRECTION> getMultiplayerFacingMap() {
        if (multiplayerFacingMap == null) {
            multiplayerFacingMap = game.getConnector().getFacingMap();
        }
        return multiplayerFacingMap;
    }

    public void setMultiplayerFacingMap(Map<Integer, FACING_DIRECTION> multiplayerFacingMap) {
        this.multiplayerFacingMap = multiplayerFacingMap;
    }

    public synchronized DC_Game getGame() {
        return game;
    }

    public synchronized void setGame(DC_Game game) {
        this.game = game;
    }

    public Positioner getPositioner() {
        return positioner;
    }

    public void setPositioner(Positioner positioner) {
        this.positioner = positioner;
    }

    public Map<Wave, Integer> getScheduledWaves() {
        if (scheduledWaves == null) {
            arenaManager.getBattleConstructor().construct();
        }
        return scheduledWaves;
    }

    public void setScheduledWaves(Map<Wave, Integer> scheduledWaves) {
        this.scheduledWaves = scheduledWaves;
    }

    public int getRoundsToWait() {
        return roundsToWait;
    }

    public void setRoundsToWait(int roundsToWait) {
        this.roundsToWait = roundsToWait;
    }

    public void clear() {
        if (getScheduledWaves() != null) {
            getScheduledWaves().clear();
        }

    }

}
