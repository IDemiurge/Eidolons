package main.game.battlecraft.logic.dungeon.arena;

import main.ability.UnitTrainingMaster;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.DC_Player;
import main.game.battlecraft.logic.battle.arena.ArenaBattleMaster;
import main.game.battlecraft.logic.battle.arena.Wave;
import main.game.battlecraft.logic.dungeon.DungeonMaster;
import main.game.battlecraft.logic.dungeon.Positioner;
import main.game.battlecraft.logic.dungeon.Spawner;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.swing.generic.components.editors.lists.ListChooser;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/7/2017.
 */
public class ArenaSpawner extends Spawner<ArenaDungeon> {
    private int roundsToWait = 0;
    private LinkedList<ObjType> wavePool;
    private boolean wavesOverlap;
    private Map<Wave, Integer> scheduledWaves;
    private boolean autoSpawnOn;

    public ArenaSpawner(DungeonMaster master) {
        super(master);
    }

    public void clear() {
        if (getScheduledWaves() != null) {
            getScheduledWaves().clear();
        }

    }

    @Override
    public ArenaBattleMaster getBattleMaster() {
        return (ArenaBattleMaster) super.getBattleMaster();
    }

    private void spawnWave(Wave wave) {
        spawnWave(wave, false);
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
//    TODO     initGroup(wave);
    }

    public void spawnWave(String typeName, DC_Player player, Coordinates coordinate) {
        ObjType waveType = DataManager.getType(typeName, DC_TYPE.ENCOUNTERS);
        Wave wave = new Wave(coordinate, waveType, game, new Ref(), player);
        spawnWave(null, wave, false);
//        initGroup(wave);
    }

    private void spawnWave(List<ObjAtCoordinate> unitMap, Wave wave, boolean prespawnMode) {
        game.getLogManager().log("New encounter: " + wave.getName());
//        if (game.getParty() != null) {
//            Unit randomMember = game.getParty().getRandomMember();
//            if (!randomMember.isDead()) {
//                SoundMaster.playEffectSound(SOUNDS.THREAT, randomMember);
//            }
//        } else {
//            // active unit?
//        }

        if (unitMap == null) {
            getPositioner().setMaxSpacePercentageTaken(MAX_SPACE_PERC_CREEPS);
            wave.initUnitMap();
            unitMap = wave.getUnitMap();
        }
        try {
            getBattleMaster(). getWaveAssembler().resetPositions(wave);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ObjAtCoordinate oac : unitMap) {
            Coordinates c = oac.getCoordinates();
            FACING_DIRECTION facing = getFacingAdjuster().getFacingForEnemy(c);
            boolean invalid = false;
            if (c == null) {
                invalid = true;
            } else if (c.isInvalid()) {
                invalid = true;
            } else if (game.getBattleField().getGrid().isCoordinateObstructed(c)) {
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
            if (getBattleMaster().getMetaMaster().getPartyManager().getParty() != null) {
//                SoundMaster.playEffectSound(SOUNDS.TAUNT, game.getParty().getLeader());
            }
        }
//        roundsToWait =  getOptions().getIntValue(
//         ARENA_GAME_OPTIONS.TURNS_BETWEEN_WAVES);
        roundsToWait++;
    }


    public void newWave(Wave wave) {
        spawnWave(wave);
        if (wavesOverlap) {
            waveCleared();
        }
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

    public Map<Wave, Integer> getScheduledWaves() {
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


}
