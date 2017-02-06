package main.content;

import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;

public class C_OBJ_TYPE implements OBJ_TYPE {

    public static final C_OBJ_TYPE ALL = new C_OBJ_TYPE(OBJ_TYPES.values());
    public static final C_OBJ_TYPE ITEMS = new C_OBJ_TYPE(OBJ_TYPES.WEAPONS, OBJ_TYPES.ARMOR,
            OBJ_TYPES.JEWELRY, OBJ_TYPES.ITEMS);
    public static final C_OBJ_TYPE UNITS_CHARS = new C_OBJ_TYPE(OBJ_TYPES.UNITS, OBJ_TYPES.CHARS);

    public static final C_OBJ_TYPE HERO_SKILLS = new C_OBJ_TYPE(OBJ_TYPES.SKILLS, OBJ_TYPES.CLASSES);
    public static final C_OBJ_TYPE XP_ITEMS = new C_OBJ_TYPE(OBJ_TYPES.SKILLS, OBJ_TYPES.ACTIONS,
            OBJ_TYPES.SPELLS);
    public static final C_OBJ_TYPE BF = new C_OBJ_TYPE(OBJ_TYPES.UNITS, OBJ_TYPES.BF_OBJ,
            OBJ_TYPES.CHARS, OBJ_TYPES.TERRAIN);
    public static final C_OBJ_TYPE BF_OBJ = new C_OBJ_TYPE(OBJ_TYPES.UNITS, OBJ_TYPES.BF_OBJ,
            OBJ_TYPES.CHARS);
    public static final C_OBJ_TYPE QUICK_ITEMS = new C_OBJ_TYPE(OBJ_TYPES.ITEMS, OBJ_TYPES.WEAPONS);
    public static final C_OBJ_TYPE SLOT_ITEMS = new C_OBJ_TYPE(OBJ_TYPES.ARMOR, OBJ_TYPES.WEAPONS);
    public static final C_OBJ_TYPE ACTIVE = new C_OBJ_TYPE(OBJ_TYPES.ACTIONS, OBJ_TYPES.SPELLS);

    public static final C_OBJ_TYPE FEATS = new C_OBJ_TYPE(OBJ_TYPES.SKILLS, OBJ_TYPES.CLASSES);

    public static final C_OBJ_TYPE LIGHT_EMITTERS = new C_OBJ_TYPE(OBJ_TYPES.UNITS,
            OBJ_TYPES.BF_OBJ, OBJ_TYPES.CHARS, OBJ_TYPES.TERRAIN, OBJ_TYPES.ARMOR,
            OBJ_TYPES.WEAPONS);

    private OBJ_TYPES[] TYPES;

    public C_OBJ_TYPE(OBJ_TYPES... TYPES) {
        this.TYPES = TYPES;
    }

    @Override
    public String toString() {
        return "combo type: " + StringMaster.toStringForm(TYPES.toString());
    }

    public OBJ_TYPES[] getTypes() {
        return TYPES;
    }

    @Override
    public PARAMETER getParam() {
        return TYPES[0].getParam();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof C_OBJ_TYPE) {
            return super.equals(obj);
        }
        return Arrays.asList(getTypes()).contains(obj);
    }

    @Override
    public String getName() {
        return TYPES.toString();
    }

    @Override
    public PROPERTY getGroupingKey() {
        return TYPES[0].getGroupingKey();
    }

    @Override
    public PROPERTY getSubGroupingKey() {
        return TYPES[0].getSubGroupingKey();
    }

    @Override
    public int getCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setImage(String image) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isHidden() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHidden(boolean hidden) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isTreeEditType() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public PROPERTY getUpgradeRequirementProp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHeroTreeType() {
        // TODO Auto-generated method stub
        return false;
    }

}
