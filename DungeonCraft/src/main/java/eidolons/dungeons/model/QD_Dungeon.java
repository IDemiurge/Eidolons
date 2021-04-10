package eidolons.game.netherflame.dungeons.model;

import eidolons.game.netherflame.dungeons.QD_Enums;
import main.system.data.DataUnit;

import java.util.Map;

public class QD_Dungeon {
    Map<Integer, QD_Floor> floors;
    DataUnit<QD_Enums.DungeonProperty> dungeonData;

    public QD_Dungeon(Map<Integer, QD_Floor> floors, DataUnit<QD_Enums.DungeonProperty> dungeonData) {
        this.floors = floors;
        this.dungeonData = dungeonData;
    }

    public Map<Integer, QD_Floor> getFloors() {
        return floors;
    }

    public DataUnit<QD_Enums.DungeonProperty> getDungeonData() {
        return dungeonData;
    }
}
