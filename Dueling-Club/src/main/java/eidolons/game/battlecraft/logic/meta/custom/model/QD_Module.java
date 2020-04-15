package eidolons.game.battlecraft.logic.meta.custom.model;

import eidolons.game.battlecraft.logic.meta.custom.QD_Enums;
import eidolons.game.battlecraft.logic.meta.custom.model.assembly.TransformData;
import main.system.data.DataUnit;

public class QD_Module  {
    private  TransformData transformData;
    private DataUnit<QD_Enums.ModuleProperty> data;

    public QD_Module(DataUnit<QD_Enums.ModuleProperty> data) {
        this.data = data;
    }

    public QD_Module(QD_Module pick, TransformData transformData) {
        this(pick.getData());
        this.transformData = transformData;
    }

    public DataUnit<QD_Enums.ModuleProperty> getData() {
        return data;
    }

    public TransformData getTransformData() {
        return transformData;
    }

    public void setTransformData(TransformData transformData) {
        this.transformData = transformData;
    }
}
