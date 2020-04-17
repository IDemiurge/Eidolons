package eidolons.game.netherflame.dungeons.model;

import eidolons.game.netherflame.dungeons.QD_Enums;
import main.system.data.DataUnit;

import java.util.Map;

public class QD_Floor {

    private DataUnit<QD_Enums.FloorProperty> data;

    public QD_Floor(DataUnit<QD_Enums.FloorProperty> data, Map<Integer, QD_Module> modules) {
        this.data = data;
    }

}
