package eidolons.entity.active.spaces;

import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import static main.content.enums.entity.NewRpgEnums.*;

/*
    format:
    <ActiveSpacesProp>
    name=X; | name=X2; ...
     */
public class ActiveSpaceData extends DataUnit<ACTIVE_SPACE_VALUE> {
    public ActiveSpaceData(String string) {
        super(string);
    }

    @Override
    protected String getSeparator() {
        return ";";
    }

    public static String getInstanceSeparator() {
        return "|";
    }

    @Override
    protected String getPairSeparator() {
        return "=";
    }

    public String getName() {
        return getValue(ACTIVE_SPACE_VALUE.name);
    }

    public String getActive(int index) {
        return getValue("slot_" + index);
    }

    public String getActives() {
        return getValue(ACTIVE_SPACE_VALUE.actives);
    }

    public void set(int index, String active) {
        setValue("slot_" + index, active);
    }

    public ACTIVE_SPACE_TYPE getType() {
        return new EnumMaster<ACTIVE_SPACE_TYPE>().retrieveEnumConst(ACTIVE_SPACE_TYPE.class, getValue(ACTIVE_SPACE_VALUE.type));
    }

    public ACTIVE_SPACE_MODE getMode() {
        return ACTIVE_SPACE_MODE.normal;
    }

    public int indexOf(String name) {
        for (int i = 0; i < ActiveSpaceInitializer.MAX_SLOTS; i++) {
            if (getActive(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

}
