package main.content.enums.macro;

import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.log.LogMaster;

import java.util.Arrays;

public class C_MACRO_OBJ_TYPE implements OBJ_TYPE {
    public static final C_MACRO_OBJ_TYPE TOWN_PLACES = new C_MACRO_OBJ_TYPE(MACRO_OBJ_TYPES.SHOP
            // ,MACRO_OBJ_TYPES.LIBRARY,
            // MACRO_OBJ_TYPES.TOWN_HALL,
            // MACRO_OBJ_TYPES.TAVERN
    );
    public static final OBJ_TYPE MAP_OBJ = new C_MACRO_OBJ_TYPE(MACRO_OBJ_TYPES.PLACE,
            MACRO_OBJ_TYPES.TOWN, MACRO_OBJ_TYPES.ROUTE);
    private MACRO_OBJ_TYPES[] TYPES;

    public C_MACRO_OBJ_TYPE(MACRO_OBJ_TYPES... types) {
        this.TYPES = types;
    }

    public static C_MACRO_OBJ_TYPE getType(String s) {
        if (s == null) {
            return null;
        }
        C_MACRO_OBJ_TYPES type = null;
        try {
            type = C_MACRO_OBJ_TYPES.valueOf(s.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
        }
        if (type == null) {
            LogMaster.log(0, "OBJ_TYPE not found: " + s);
            // throw new RuntimeException();
        }
        return type.getType();
    }

    @Override
    public String toString() {
        return "combo type: " + TYPES.toString();
    }

    public MACRO_OBJ_TYPES[] getTypes() {
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
        return false;
    }

    public enum C_MACRO_OBJ_TYPES {
        TOWN_PLACES(C_MACRO_OBJ_TYPE.TOWN_PLACES),;
        private C_MACRO_OBJ_TYPE type;

        C_MACRO_OBJ_TYPES(C_MACRO_OBJ_TYPE type) {
            this.setType(type);
        }

        public C_MACRO_OBJ_TYPE getType() {
            return type;
        }

        public void setType(C_MACRO_OBJ_TYPE type) {
            this.type = type;
        }
    }
}
