package eidolons.game.module.nethergate.apholon.eidola;

import main.entity.type.ObjType;

/**
 * Created by JustMe on 12/7/2018.
 */
public class RealEidolon {

    ObjType unitType;
//    EIDOLON_STATE state;

    public RealEidolon(ObjType unitType) {
        this.unitType = unitType;
    }

    public ObjType getUnitType() {
        return unitType;
    }
}
