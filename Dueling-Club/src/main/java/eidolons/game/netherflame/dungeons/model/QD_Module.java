package eidolons.game.netherflame.dungeons.model;

import eidolons.game.netherflame.dungeons.QD_Enums;
import eidolons.game.netherflame.dungeons.model.assembly.Transform;
import main.system.data.DataUnit;

public class QD_Module  {
    private Transform transform;
    private DataUnit<QD_Enums.ModuleProperty> data;

    public QD_Module(DataUnit<QD_Enums.ModuleProperty> data) {
        this.data = data;
    }

    public QD_Module(QD_Module pick, Transform transform) {
        this(pick.getData());
        this.transform = transform;
    }

    public DataUnit<QD_Enums.ModuleProperty> getData() {
        return data;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
