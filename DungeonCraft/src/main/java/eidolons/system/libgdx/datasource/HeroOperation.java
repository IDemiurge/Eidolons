package eidolons.system.libgdx.datasource;

import main.content.enums.entity.HeroEnums;

/**
 * Created by Alexander on 2/1/2022
 */
public class HeroOperation {
    HeroEnums.HERO_OPERATION operation;
    Object[] arg;

    public HeroOperation(HeroEnums.HERO_OPERATION operation, Object... arg) {
        this.operation = operation;
        this.arg = arg;
    }

    public HeroEnums.HERO_OPERATION getOperation() {
        return operation;
    }

    public Object[] getArg() {
        return arg;
    }
}
