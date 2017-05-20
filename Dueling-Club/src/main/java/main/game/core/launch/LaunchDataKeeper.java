package main.game.core.launch;

import main.game.battlecraft.logic.dungeon.universal.DungeonData;
import main.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import main.game.battlecraft.logic.dungeon.universal.Spawner;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.core.game.DC_Game;
import main.system.data.PlayerData;
import main.test.Preset;
import main.test.Preset.PRESET_DATA;
import main.system.util.Refactor;

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

    public LaunchDataKeeper() {

    }

    public LaunchDataKeeper(DC_Game game, String hardcodedPlayerData,
                            String hardcodedEnemyData,
                            String hardcodedDungeonData) {
        unitData = new UnitData[2];
        //suppose it's just a list of units? coordinates will be figured out later then
        unitData[0] = Spawner.generateData(hardcodedPlayerData,
//         game.getPlayer(true)
         null, null, null);
        unitData[1] = Spawner.generateData(hardcodedEnemyData,
//         game.getPlayer(false)
         null, null, null);
        dungeonData = DungeonInitializer.generateDungeonData(hardcodedDungeonData);
    }

    public LaunchDataKeeper(DC_Game game, Preset preset) {
        this(game, preset.getValue(PRESET_DATA.PLAYER_UNITS),
                preset.getValue(PRESET_DATA.ENEMIES),
         preset.getValue(PRESET_DATA.FIRST_DUNGEON));

        PresetLauncher.initPresetData(dungeonData, preset);
    }

    public LaunchDataKeeper(PlayerData[] playerData, UnitData[] unitData, DungeonData dungeonData) {
        this.playerData = playerData;
        this.unitData = unitData;
        this.dungeonData = dungeonData;
    }
@Refactor
    public void addUnitData(UnitData data) {
        int i = 0;
        if (unitData != null) i = unitData.length;
        unitData = new UnitData[i + 1];
        unitData[i] = data;
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
        if (dungeonData==null )
            dungeonData = new DungeonData();
        return dungeonData;
    }

    public void setDungeonData(DungeonData dungeonData) {
        this.dungeonData = dungeonData;
    }

}
