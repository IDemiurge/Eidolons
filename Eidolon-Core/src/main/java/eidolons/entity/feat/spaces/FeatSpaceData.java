package eidolons.entity.feat.spaces;

import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import static main.content.enums.entity.NewRpgEnums.*;

/*
    format:
    <ActiveSpacesProp>
    name=X; | name=X2; ...
     */
public class FeatSpaceData extends DataUnit<FeatSpaceValue> {
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
        return getValue(FeatSpaceValue.name);
    }

    public void setFeat(int index, String name) {
        setValue("slot_" + (1 + index), name);
    }

    public String getFeat(int index) {
        return getValue("slot_" + (1 + index));
    }

    public String getFeats() {
        return getValue(FeatSpaceValue.feats);
    }

    public void set(int index, String active) {
        setValue("slot_" + index, active);
    }

    public FeatSpaceType getType() {
        return new EnumMaster<FeatSpaceType>().retrieveEnumConst(FeatSpaceType.class, getValue(FeatSpaceValue.type));
    }

    public FeatSpaceMode getMode() {
        return FeatSpaceMode.normal;
    }

    public int indexOf(String name) {
        for (int i = 0; i < FeatSpaceInitializer.MAX_SLOTS; i++) {
            if (getFeat(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

}
