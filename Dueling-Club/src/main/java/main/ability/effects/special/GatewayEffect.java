package main.ability.effects.special;

import main.ability.effects.common.SummonEffect;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.conditions.StringComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.net.WaitingThread;
import main.system.net.socket.ServerConnector.NetCode;

import java.util.List;

public class GatewayEffect extends SummonEffect {
    private static final NetCode code = HOST_CLIENT_CODES.CUSTOM_PICK;
    private PROPERTY prop;
    private String filter;
    private List<ObjType> typeList;
    private StringComparison condition;
    private boolean random;
    private String unitType;

    public GatewayEffect(PROPERTY prop, String filter, Boolean random) {
        super(null);
        this.prop = prop;
        this.filter = filter;
        this.random = random;
        this.setCondition(new StringComparison(StringMaster.getValueRef(KEYS.MATCH, prop), filter,
                true));

    }

    @Override
    public void setRef(Ref REF) {
        super.setRef(REF);
        this.ref = REF;
    }

    @Override
    public boolean applyThis() {

        typeName = getUnitType();
        if (typeName == null) {
            return false;
        }
        boolean result = super.applyThis();
        unitType = null;
        return result;
    }

    @Override
    public String getTypeName() {
        // if (typeName == null)
        typeName = getUnitType();
        return typeName;
    }

    public String getUnitType() {
        if (unitType != null) {
            return unitType;
        }
        Obj obj = ref.getSourceObj().getOwner().getHeroObj();
        typeList = null;
        if (prop == null) {
            typeList = DataManager.toTypeList(((Unit) obj).getDeity().getUnitPool(),
                    DC_TYPE.UNITS);
        } else {
            typeList = new Filter<ObjType>(ref, getCondition()).filter(DataManager
                    .getTypes(C_OBJ_TYPE.UNITS_CHARS));
        }

        if (typeList.isEmpty()) {
            return null;
        }

        random = ref.getObj(KEYS.ACTIVE).checkBool(GenericEnums.STD_BOOLS.RANDOM);
        if (!obj.getOwner().isAi()) {
            random = true;
        }

        if (random) {
            unitType = typeList.get(RandomWizard.getRandomListIndex(typeList)).getName();
        } else {
            if (!getGame().isOffline()) {
                if (!obj.isMine()) {
                    unitType = WaitingThread.waitOrGetInput(code);
                    return unitType;
                }
            }

            unitType = ListChooser.chooseType(DataManager.toStringList(typeList),
                    C_OBJ_TYPE.BF_OBJ);
            if (!getGame().isOffline()) {
                if (obj.isMine()) {
                    getGame().getConnection().send(code, unitType);
                }
            }
        }
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public StringComparison getCondition() {
        return condition;
    }

    public void setCondition(StringComparison condition) {
        this.condition = condition;
    }
}// new Thread(new Runnable() {
// @Override
// public void run() {
// new ChoiceEffect(typeList).apply(ref);
// }
// }).start();
// Object input =
// WaitMaster.waitForInput(WAIT_OPERATIONS.CUSTOM_SELECT);
// if (input == null)
// return false;
// String unitType = input.toString();
// List<String> unitPool
// Condition conditions = new NumericCondition("{SOURCE_C_ENERGY}",
// "[AV(SUMMON_ENERGY_COST,MATCH)]", false);
// typeList =
// new Filter<ObjType>(ref, conditions).filter(DataManager
// .convertToTypeList(unitPool, OBJ_TYPES.UNITS));
// ListMaster.filterTypeList(unitPool, PROPS.ASPECT, aspect);