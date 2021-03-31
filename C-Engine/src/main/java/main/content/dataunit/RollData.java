package main.content.dataunit;

import main.content.enums.GenericEnums;
import main.system.auxiliary.EnumMaster;

public class RollData extends XmlDataUnit<RollData.ROLL_VALUE> {
    public RollData(String text) {
        super(text);
    }

    public SaveType getSaveType() {
        return new EnumMaster<SaveType>().retrieveEnumConst(SaveType.class, getValue(ROLL_VALUE.saveType));
    }

    public GenericEnums.DieType getDieType() {
        return new EnumMaster<GenericEnums.DieType>().retrieveEnumConst(GenericEnums.DieType.class, getValue(ROLL_VALUE.die));
    }

    public GenericEnums.RollType getRollType() {
        return new EnumMaster<GenericEnums.RollType>().retrieveEnumConst(GenericEnums.RollType.class, getValue(ROLL_VALUE.type));
    }

    public enum SaveType {
        full, half, redirect_any, redirect_source,
    }

    public enum ROLL_VALUE {
        type,
        die,
        saveType,
        s_value,
        t_value,
        s_dice,
        t_dice,
        //hints?
        ;
    }

}

