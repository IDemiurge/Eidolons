package eidolons.entity.active.spaces;

import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import static main.content.enums.entity.NewRpgEnums.*;

/*
    format:
    <ActiveSpacesProp>
    name=X; | name=X2; ...
     */
public class FeatSpaceData extends DataUnit<FEAT_SPACE_VALUE> {
    public FeatSpaceData(String string) {
        super(string);
    }

    @Override
    protected String getSeparator() {
        return "::";
    }

    public static String getInstanceSeparator() {
        return "|";
    }

    @Override
    protected String getPairSeparator() {
        return "=";
    }

    public String getName() {
        return getValue(FEAT_SPACE_VALUE.name);
    }

    public void setActive(int index, String name) {
        setValue("slot_" + (1 + index), name);
    }

    public String getActive(int index) {
        return getValue("slot_" + (1 + index));
    }

    public String getActives() {
        return getValue(FEAT_SPACE_VALUE.feats);
    }

    public void set(int index, String active) {
        setValue("slot_" + index, active);
    }

    public FEAT_SPACE_TYPE getType() {
        return new EnumMaster<FEAT_SPACE_TYPE>().retrieveEnumConst(FEAT_SPACE_TYPE.class, getValue(FEAT_SPACE_VALUE.type));
    }

    public FEAT_SPACE_MODE getMode() {
        return FEAT_SPACE_MODE.normal;
    }

    public int indexOf(String name) {
        for (int i = 0; i < FeatSpaceInitializer.MAX_SLOTS; i++) {
            if (getActive(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

}
