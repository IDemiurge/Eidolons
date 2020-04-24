package eidolons.game.netherflame.dungeons.model;

import eidolons.game.netherflame.dungeons.QD_Enums;
import main.system.data.DataUnit;

import java.util.Map;

public class QD_Floor {

    private final Map<Integer, QD_Module> modules;
    private DataUnit<QD_Enums.FloorProperty> data;

    public QD_Floor(DataUnit<QD_Enums.FloorProperty> data, Map<Integer, QD_Module> modules) {
        this.data = data;
        this.modules = modules;
    }

    public Map<Integer, QD_Module> getModules() {
        return modules;
    }

    public DataUnit<QD_Enums.FloorProperty> getData() {
        return data;
    }
}
