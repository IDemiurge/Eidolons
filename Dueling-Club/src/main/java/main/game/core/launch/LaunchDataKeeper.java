package main.game.core.launch;

import main.game.battlecraft.logic.dungeon.DungeonData;
import main.game.battlecraft.logic.dungeon.DungeonInitializer;
import main.game.battlecraft.logic.dungeon.Spawner;
import main.game.battlecraft.logic.dungeon.UnitData;
import main.system.data.PlayerData;
import main.test.Preset;
import main.test.Preset.PRESET_DATA;

/**
 * Created by JustMe on 5/10/2017.
 */
public class LaunchDataKeeper {
    /*
    'new Game()' in testLauncher isn't very refined
    how to feed the preset/hardcode data?
    package into these:
    PlayerData
    UnitData
    DungeonData

    dataKeeper can store them until they are needed

    for MISSION

    for ARENA
     */
    PlayerData[] playerData;
     UnitData[] unitData;
    DungeonData dungeonData;

    public LaunchDataKeeper(String hardcodedPlayerData,
                            String hardcodedEnemyData,
                            String hardcodedDungeonData) {
        unitData = new UnitData[2];
        //suppose it's just a list of units? coordinates will be figured out later then
        unitData[0] = Spawner.generateData(hardcodedPlayerData);
        unitData[1] = Spawner.generateData(hardcodedEnemyData);
        dungeonData = DungeonInitializer.generateDungeonData(hardcodedDungeonData);
    }

    public LaunchDataKeeper(Preset preset) {
        this(preset.getValue(PRESET_DATA.PLAYER_UNITS),
        preset.getValue(PRESET_DATA.ENEMIES),

         preset.getValue(PRESET_DATA.FIRST_DUNGEON));
        PresetLauncher.initPresetData(dungeonData, preset);
    }

    public LaunchDataKeeper(PlayerData[] playerData, UnitData[] unitData, DungeonData dungeonData) {
        this.playerData = playerData;
        this.unitData = unitData;
        this.dungeonData = dungeonData;
    }

    public PlayerData[] getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData[] playerData) {
        this.playerData = playerData;
    }

    public UnitData[] getUnitData() {
        return unitData;
    }

    public void setUnitData(UnitData[] unitData) {
        this.unitData = unitData;
    }

    public DungeonData getDungeonData() {
        return dungeonData;
    }

    public void setDungeonData(DungeonData dungeonData) {
        this.dungeonData = dungeonData;
    }
}
