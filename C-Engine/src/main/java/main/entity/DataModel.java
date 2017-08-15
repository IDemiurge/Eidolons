package main.entity;

import main.ability.effects.Effect.MOD_PROP_TYPE;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.parameters.MultiParameter;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.VariableManager;
import main.data.xml.XML_Formatter;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.entity.CounterMaster;
import main.system.images.ImageManager;
import main.system.math.Formula;
import main.system.math.FormulaFactory;
import main.system.math.FormulaMaster;
import main.system.math.MathMaster;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JustMe on 1/28/2017.
 */
public abstract class DataModel {
    protected PropMap propMap = new PropMap();
    protected ParamMap paramMap = new ParamMap();
    protected Map<PARAMETER, Map<String, Double>> modifierMaps;
    protected Map<VALUE, Boolean> booleanMap = new HashMap<>();
    protected Ref ref;
    protected ObjType type;
    protected Game game;
    protected Player owner;
    protected Integer id;
    protected boolean constructed = false;
    protected boolean var = false;
    protected boolean dirty = false;
    protected Map<String, String> customParamMap;
    protected OBJ_TYPE TYPE_ENUM;
    protected Map<String, String> customPropMap;
    protected XLinkedMap<VALUE, String> rawValues;
    protected boolean initialized;
    protected boolean constructing;
    protected boolean defaultValuesInitialized;
    protected String name;
    protected Double C;
    protected String originalName;
    protected ImageIcon customIcon;
    protected String modifierKey;
    protected Map<Obj, Map<String, ImageIcon>> customIconCache;
    protected ImageIcon icon;
    protected boolean passivesReady = false;
    protected boolean activesReady = false;
    private HashMap<PROPERTY, Map<String, Boolean>> propCache;
    private Map<PARAMETER, Integer> integerMap;

    public String getToolTip() {
        return getType().getDisplayedName();
    }

    public String getDescription() {
        return getProperty(G_PROPS.DESCRIPTION);
    }

    public String getCustomValue(String value_ref) {
        String value = null;
        if (getCustomParamMap().containsKey(value_ref)) {
            value = getCounter(value_ref).toString();
        } else if (getCustomPropMap().containsKey(value_ref)) {
            value = getCustomProperty(value_ref);
        }

        return value;
    }

    public String getCustomProperty(String value_ref) {
        if (!StringMaster.isEmpty(getProperty(G_PROPS.CUSTOM_PROPS))) {
            // this must be custom container!
            for (String subString : StringMaster.openContainer(getProperty(G_PROPS.CUSTOM_PROPS))) {

            }
        }

        if (getCustomParamMap() == null) {
            return "";
        }
        if (!getCustomParamMap().containsKey(value_ref)) {
            return "";
        }
        LogMaster.log(LogMaster.CORE_DEBUG, value_ref + " - custom prop: "
         + getCustomParamMap().get(value_ref));
        return getCustomPropMap().get(value_ref);

    }

    public Integer getCounter(String value_ref) {
        if (getCustomParamMap() == null) {
            return 0;
        }

        String string = getCustomParamMap().get(value_ref);
        if (string == null) {
            return 0;
        }
        LogMaster.log(LogMaster.CORE_DEBUG, value_ref + " - custom param: "
         + string);
        return new Formula(string).getInt(ref);
    }

    public void setGroup(String group, boolean base) {
        setProperty(G_PROPS.GROUP, group, base);
    }

    public boolean setCounter(String name, int newValue) {
        return setCounter(name, newValue, true);
    }

    public boolean setCounter(String name, int newValue, boolean strict) {
        String realName = new MapMaster<String, String>().getKeyForValue(getCustomParamMap(),
         getCustomParamMap().get(name));
        if (realName == null) {
            if (!name.contains(StringMaster.COUNTER)) {
                return setCounter(name + StringMaster.COUNTER, newValue);
            }
            // if (!strict)
            realName = CounterMaster.findCounter(name, strict);
            if (realName == null) {
                return false;
            }
            // else
            // return false;
        }
        name = realName;

        setDirty(true);
        if (newValue <= 0) {
            removeCounter(name);
        } else {
            getCustomParamMap().put(name, newValue + "");
            LogMaster.log(1, name + " value set: " + newValue);
        }
        return true;
    }

    public void removeCounter(String name) {
        setDirty(true);
        getCustomParamMap().remove(name);
        LogMaster.log(1, name + " Counter Removed from " + this);
    }

    public boolean modifyCounter(String name, int modValue) {
        return modifyCounter(name, modValue, true);
    }

    public boolean modifyCounter(COUNTER counter, int modValue) {
        return modifyCounter(counter.getName(), modValue, true);
    }

    public boolean modifyCounter(String name, int modValue, boolean strict) {
        String realName = new MapMaster<String, String>().getKeyForValue(getCustomParamMap(),
         getCustomParamMap().get(name));
        if (realName == null) {
            realName = CounterMaster.findCounter(name, strict);
        }
        if (realName == null) {
            return false;
        }
        if (realName != null) {
            name = realName;
        }
        // else if (!name.contains(StringMaster.COUNTER)) {
        // modifyCounter(name + StringMaster.COUNTER, modValue);
        // return;
        // }
        // TODO different map, one without CLEAR() on tobase
        if (getCustomParamMap().get(name) != null) {
            Integer value = getCounter(name) + modValue;
            setCounter(name, value);
            LogMaster.log(1, "Counter modified: " + name + value);
        } else {
            setCounter(name, modValue);
            LogMaster.log(1, "New Counter: " + "" + name + modValue);
        }
        game.getLogManager().logCounterModified(this, name, modValue);
        return true;
    }

    public String find(String p) {
        return getValue(game.getValueHelper().getValue(p));
    }

    public String getParam(String p) {
        return getParam(ContentManager.getPARAM(p));
    }

    public String getParam(PARAMETER param) {
        return getDoubleParam(param, false);
    }

    public String getParamRounded(PARAMETER param, boolean base) {
        // Number n = new Formula(getDoubleParam(param, base)).evaluate();
        // if (n instanceof Integer)
        // return n + "";
        // if (n instanceof Double)
        // return Math.round((Double) n) + "";
        // if (n instanceof Float)
        // return Math.round((Float) n) + "";
        if (base) {
            return type.getIntParam(param) + "";
        }
        return getIntParam(param) + "";

    }

    public Double getParamDouble(PARAMETER param) {
        String doubleParam = getDoubleParam(param, false);
        if (doubleParam.isEmpty()) {
            return 0.0;
        }
        return StringMaster.getDouble(getDoubleParam(param, false));
    }

    public Double getParamDouble(PARAMETER param, boolean base) {
        return StringMaster.getDouble(getDoubleParam(param, base));
    }

    public String getDoubleParam(PARAMETER param) {
        return getDoubleParam(param, false);
    }

    public String getDoubleParam(PARAMETER param, boolean base) {
        if (base) {
            return getType().getDoubleParam(param, false);
        }
        String string = paramMap.get(param);
        int index = string.indexOf('.');
        if (index != -1) {
            if (StringMaster.isNumber(string, false)) {
                if (StringMaster.isInteger(string)) {
                    return string.substring(0, index);
                } else {
                    Double val = StringMaster.getDouble(string);
                    if (C == null) {
                        C = Math.pow(10, MathMaster.NUMBERS_AFTER_PERIOD);
                    }
                    int i = (int) (val * C);
                    val = i / C;
                    string = val.toString();
                }
            }
        }
        string = string.trim();
        return string;

    }

    public Integer getIntParam(String param) {
        PARAMETER p = ContentManager.getPARAM(param);
        if (p == null) {
            return 0;
        }
        return getIntParam(p);
    }

    public String getStrParam(PARAMETER param) {
        return String.valueOf(getIntParam(param));
    }

    public Integer getIntParam(PARAMETER param) {
        return getIntParam(param, false);
    }

    public Integer getIntParam(PARAMETER param, boolean base) {

        Integer result;
        // getIntegerMap(base).get(param);
        // if (result != null)
        // return result;
        String string;
        if (base) {
            string = getType().getParam(param);
        } else {
            string = paramMap.get(param);
        }
        if (string == null) {
            return 0;
        }
        if (string.equals("")) {
            return 0;
        }
        result = FormulaMaster.getInt(string, ref);
        getIntegerMap(base).put(param, result);
        return result; // return new Formula(string).getInt(ref);
        // try {
        // return Integer.valueOf(string);
        // } catch (NumberFormatException e) {
        // try {
        //
        // } catch (Exception e1) {
        // return -1;
        // }
        // }
    }

    public Map<PARAMETER, Integer> getIntegerMap(boolean base) {
        if (base) {
            return type.getIntegerMap(false);
        }
        if (integerMap == null) {
            integerMap = new HashMap<>();
        }
        return integerMap;
    }

    public ParamMap getParamMap() {
        return paramMap;
    }

    public void setParamMap(ParamMap paramMap) {
        this.paramMap = paramMap;
    }

    public void getBoolean(VALUE prop, Boolean b) {
        booleanMap.put(prop, b);
    }

    public Boolean getBoolean(String prop) {
        return booleanMap.get(prop);
    }

    public String getProperty(String prop) {
        PROPERTY property = ContentManager.getPROP(prop);
        if (property == null) {
            return null;
        } else {
            return getProperty(property);
        }
    }

    public String getProp(String prop) {
        return getProperty(ContentManager.getPROP(prop));
    }

    public String getGroup() {
        return getProperty(G_PROPS.GROUP);
    }

    public String getProperty(PROPERTY prop) {
        if (prop != null) {
            return propMap.get(prop);
        }
        return "";
    }

    public boolean checkValue(VALUE v) {
        if (v instanceof PARAMETER) {
            return checkParam((PARAMETER) v);
        } else {
            PROPERTY p = ((PROPERTY) v);

            return checkProperty(p);
        }
    }

    public boolean checkValue(VALUE v, String value) {

        if (v instanceof PARAMETER) {
            return checkParam((PARAMETER) v, value);
        } else {
            PROPERTY p = ((PROPERTY) v);

            return checkProperty(p, value);
        }
    }

    public boolean checkParam(PARAMETER param) {
        return checkParam(param, "1");
    }

    public boolean checkParameter(PARAMETER param, int value) {
        int val1 = getIntParam(param);
        return (val1 >= value);
    }

    public boolean checkParam(PARAMETER param, String value) {
        if (value.isEmpty()) {
            return true;
        }
        int val2;
        try {
            val2 = StringMaster.getInteger(value); // Integer.valueOf(value);
        } catch (Exception e) {
            try {
                val2 = new Formula(value).getInt(ref);
            } catch (Exception e2) {
                e2.printStackTrace();
                return true;
            }
        }
        return checkParameter(param, val2);
    }

    public boolean checkProperty(PROPERTY p, String value) {
        return checkProperty(p, value, false);

    }

    public Map<PROPERTY, Map<String, Boolean>> getPropCache(boolean base) {
        return base ? type.getPropCache() : getPropCache();
    }

    public boolean checkProperty(PROPERTY p, String value, boolean base) {
        Map<String, Boolean> boolCache = getPropCache().get(p);
        if (boolCache == null) {
            boolCache = new HashMap<>();
            getPropCache().put(p, boolCache);
        }
        Boolean result = boolCache.get(value);
        if (result != null) {
            return result;
        }
        if (base) {
            result = type.checkProperty(p, value, false);
        } else if (p.isContainer()) {
            result = checkContainerProp(p, value);
        } else {
            result = checkSingleProp(p, value);
        }
        boolCache.put(value, result);
        return result;
    }

    public boolean checkSingleProp(String PROP, String value) {
        return checkSingleProp(ContentManager.getPROP(PROP), value);
    }

    public boolean checkSingleProp(PROPERTY PROP, String value) {
        if (StringMaster.isEmpty(getProperty(PROP))) {
            return false;
        }
        String property = getProperty(PROP);
        property = StringMaster.formatComparedProperty(property);
        value = StringMaster.formatComparedProperty(value);
        return StringMaster.compare(property, value, true);
    }

    public boolean checkContainerProp(PROPERTY PROP, String value) {
        return checkContainerProp(PROP, value, false);
    }

    public boolean checkContainerProp(PROPERTY PROP, String value, boolean any) {
        boolean result = false;
        List<String> list = ListMaster.toStringList(value);
        if (any) {
            list = StringMaster.openContainer(value);
        }

        for (String sub : list) {
            for (String item : StringMaster.openContainer(getProperty(PROP))) {
                String variable = VariableManager.getVar(item);
                if (StringMaster.isInteger(variable)) {
                    item = VariableManager.removeVarPart(item);
                }
                if (StringMaster.compareByChar(sub, item, false)) {
                    result = true;
                    break;
                }
            }
        }
        return result;

    }

    public boolean checkSubGroup(String string) {
        return checkSingleProp(getSubGroupingKey(), string);
    }

    public boolean checkProperty(PROPERTY p) {
        return !StringMaster.isEmpty(getProperty(p));
    }

    public boolean checkGroup(String string) {
        return checkSingleProp(G_PROPS.GROUP, string);
    }

    public String getProperty(PROPERTY prop, boolean base) {
        if (base) {
            return getType().getProperty(prop);
        }
        return propMap.get(prop);

    }

    public PropMap getPropMap() {
        return propMap;
    }

    public void setPropMap(PropMap propMap) {
        this.propMap = propMap;
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        if (game instanceof MicroGame) {
            ref.setGame(game);
        }
        ref.setPlayer(owner);
        // this.ref = ref;
        this.ref = ref.getCopy(); // what does it change?
        if (isSetThis()) {
            this.ref.setID(KEYS.THIS, getId());
        }
    }

    public ObjType getType() {
        return type;
    }

    public void setType(ObjType type) {
        this.type = type;

    }

    public String getValue(String valName) {
        return getValue(ContentManager.getValue(valName));
    }

    public String getValue(VALUE valName) {
        return getValue(valName, false);
    }

    public String getValue(VALUE val, boolean base) {
        String value = "";
        if (val instanceof MultiParameter) {
            MultiParameter multiParameter = (MultiParameter) val;
            for (PARAMETER p : multiParameter.getParameters()) {
                value += getParamRounded(p, base) + multiParameter.getSeparator();
                // % sign?
            }
            value = StringMaster.cropLast(value, multiParameter.getSeparator().length());
        } else if (val instanceof PARAMETER) {
            value = getDoubleParam((PARAMETER) val, base);
        } else if (val instanceof PROPERTY) {

            value = getProperty((PROPERTY) val);
        }
        if (value == null) {
            LogMaster.log(LogMaster.VALUE_DEBUG, "Value not found: "
             + val.getName());
        }
        return value;
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly,
                                   String modifierKey) {
        return modifyParameter(param, amount + "", minMax, quietly, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly) {
        return modifyParameter(param, amount + "", minMax, quietly, null);
    }

    public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax,
                                   boolean quietly) {
        return modifyParameter(param, amountString, minMax, quietly, null);
    }

    public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax,
                                   boolean quietly, String modifierKey) {

        if (amountString == null) {
            return true;
        }
        if (amountString.isEmpty()) {
            return true;
        }

        if (StringMaster.isInteger(amountString)) {
            if (StringMaster.getInteger(amountString) == 0) {
                return true;
            }
        }
        Number amount = new Formula(amountString).evaluate(ref);

        LogMaster.log(LogMaster.VALUE_DEBUG, "modifying " + getName() + "'s "
         + param.getName() + " by " + amount);

        if (!fireParamEvent(param, String.valueOf(amount),
         CONSTRUCTED_EVENT_TYPE.PARAM_BEING_MODIFIED)) {
            return true; // false?
        }

        boolean result = true;
        try {
            String prevValue = getDoubleParam(param, false);

            // } catch (Exception e) {
            // }
            Number newValue;
            if (!prevValue.isEmpty()) {
                try {

                    newValue = FormulaFactory.getFormulaByAppend(prevValue,
                     amount).evaluate(ref);
                } catch (Exception e) {
                    setParam(param, FormulaFactory.getFormulaByAppend(prevValue, amount).toString(),
                     quietly);
                    return true;
                }
            } else {
                newValue = amount;
            }
            // intAmount = prevValue
            if (minMax != null) {
                if (amount.intValue() < 0) {
                    if (StringMaster.getInteger(prevValue) < minMax) {
                        return false;
                    }
                    if (newValue.intValue() < minMax) {
                        newValue = minMax;
                    }
                } else {
                    if (StringMaster.getInteger(prevValue) > minMax) {
                        return false;
                    }
                    if (newValue.intValue() > minMax) {
                        newValue = minMax;
                    }
                }
            }

            setParam(param, newValue.toString(), quietly);

            Map<String, Double> map = getModifierMaps().get(param);
            if (map == null) {
                map = new XLinkedMap<>();
                getModifierMaps().put(param, map);
            }
            if (modifierKey == null) {
                modifierKey = this.modifierKey;
            }
            Double amountByModifier = map.get(modifierKey);
            this.modifierKey = null;
            if (amountByModifier == null) {
                map.put(modifierKey, amount.doubleValue());
            } else {
                map.put(modifierKey, amountByModifier + amount.doubleValue());
            }

            if (newValue.intValue() <= 0) {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<PARAMETER, Map<String, Double>> getModifierMaps() {
        if (modifierMaps == null) {
            modifierMaps = new XLinkedMap<>();
        }
        return modifierMaps;
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, String modifierKey) {
        return modifyParameter(param, amount, minMax, false, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax) {
        return modifyParameter(param, amount, minMax, false);
    }

    public void modifyParameter(PARAMETER param, int amount, boolean base) {
        modifyParameter(param, amount, null, false, null);
    }

    public void modifyParameter(PARAMETER param, int amount, boolean base, String modifierKey) {
        modifyParameter(param, amount, modifierKey);
        if (base) {
            type.modifyParameter(param, amount, modifierKey);
        }

    }

    public boolean modifyParameter(PARAMETER param, int amount, String modifierKey) {
        return modifyParameter(param, amount, null, false, modifierKey);
    }

    public boolean modifyParameter(PARAMETER param, int amount) {
        return modifyParameter(param, amount, null, null);

    }

    public void decrementParam(PARAMETER param) {
        setParam(param, getIntParam(param) - 1);
    }

    public int getContainerCount(PROPERTY p) {
        return StringMaster.openContainer(getProperty(p)).size();

    }

    public void incrementParam(PARAMETER param) {
        setParam(param, getIntParam(param) + 1);
    }

    /**
     * @param perc MathManager.PERCENTAGE
     * @param base if true, will calculate mod value from base value
     */
    public boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base) {
        if (perc == 100) {
            return true;
        }
        if (perc < 1000 && perc > -MathMaster.PERCENTAGE) {
            perc = MathMaster.getFullPercent(perc);
        }
        return modifyParamByPercent(param, perc - MathMaster.PERCENTAGE, base);
    }

    public boolean modifyParamByPercent(PARAMETER[] params, int perc) {
        for (PARAMETER p : params) {
            modifyParamByPercent(p, perc);
        }
        return true;
    }

    public boolean modifyParamByPercent(PARAMETER param, int perc) {
        if (perc < 1000) {
            perc = MathMaster.getFullPercent(perc);
        }
        return modifyParamByPercent(param, perc, false);
    }

    public boolean modifyParamByPercent(PARAMETER param,
                                        int perc, boolean base) {
        if (perc == 0 || getIntParam(param, base) == 0) {
            return false;
        }
        if (perc < 1000 && perc > 0) {
            perc = MathMaster.getFullPercent(perc);
        }
        if (perc > -1000 && perc < 0) {
            perc = MathMaster.getFullPercent(perc);
        }
        // int mod = MathManager.getFractionValue(getIntParam(param, base),
        // perc);
        float mod = MathMaster.getFractionValueFloat(getIntParam(param, base), perc);

        return modifyParameter(param, "" + mod, null, false);

    }

    protected boolean firePropEvent(CONSTRUCTED_EVENT_TYPE EVENT_TYPE, String val) {
        if (!isValueEventsOn()) {
            return true;
        }

        Ref REF = Ref.getCopy(ref);
        REF.setTarget(id);
        return getGame().fireEvent(new Event(EVENT_TYPE, "" + val, REF));
    }

    private boolean isValueEventsOn() {
        if (getGame().isSimulation() || this instanceof ObjType) {
            return false;
        }
        return false;
    }

    public boolean fireParamEvent(PARAMETER param, String amount, CONSTRUCTED_EVENT_TYPE event_type) {
        if (!isValueEventsOn()) {
            return true;
        }
        if (param.isMastery()) {
            return true; // TODO [PERFORMANCE] DEMANDS...
        }

        if (ref == null || !getGame().isStarted() || getGame().isSimulation() || this instanceof ObjType) {
            return true;
        }
        Ref REF = Ref.getCopy(ref);
        REF.setAmount(amount);
        REF.setTarget(id);
        return getGame().fireEvent(new Event(event_type, "" + param, REF));
    }

    public void resetParam(PARAMETER param) {
        setParam(param, getParam(ContentManager.getBaseParameterFromCurrent(param)));
    }

    public void setParam(PARAMETER param, int i, boolean quietly, boolean base) {
        setParam(param, "" + i, quietly);
        if (base) {
            type.setParam(param, "" + i, quietly);
        }
    }

    public void setParam(PARAMETER param, int i, boolean quietly) {
        setParam(param, "" + i, quietly);
    }

    public void setParamDouble(PARAMETER param, double i, boolean quietly) {
        setParam(param, "" + i, quietly);
    }

    public void setParameter(PARAMETER param, int i) {
        setParam(param, i);
    }

    public void setParam(PARAMETER param, int i) {
        setParam(param, i, false);

    }

    public void setParam(String param, int i) {
        setParam(ContentManager.getPARAM(param), i);

    }

    public void setParamMax(PARAMETER p, int i) {
        int amount = getIntParam(p);
        if (i >= amount) {
            return;
        } else {
            setParam(p, i);
        }
    }

    public void setParamMin(PARAMETER p, int i) {
        int amount = getIntParam(p);
        if (i <= amount) {
            return;
        } else {
            setParam(p, i);
        }
    }

    public String getDisplayedName() {
        if (!checkProperty(G_PROPS.DISPLAYED_NAME)) {
            return getName();
        }
        return getProperty(G_PROPS.DISPLAYED_NAME);
    }

    public void modifyParameter(String param, String string) {
        PARAMETER p = ContentManager.getPARAM(param);
        int perc = StringMaster.getInteger(string);
        modifyParameter(p, perc);
    }

    public void modifyParamByPercent(String param, String string) {
        PARAMETER p = ContentManager.getPARAM(param);
        int perc = StringMaster.getInteger(string);
        modifyParamByPercent(p, perc);
    }

    public boolean setParam(PARAMETER param, String value, boolean quiety) {
        if (param == null) {
            return false;
        }
        if (!quiety) {
            if (getGame() == null) {
                return false;
            }
            if (getGame().isStarted()) {
                if (!fireParamEvent(param, value, CONSTRUCTED_EVENT_TYPE.PARAM_BEING_MODIFIED)) {
                    return false;
                }
            }
        }
        // if (isParamSetLogged())
        LogMaster.log(0, "==========> " + getName() + "'s " + param + "  is set to" + value);

        putParameter(param, value);
        getIntegerMap(false).remove(param);
        setDirty(true);


        return true;
    }

    public void resetPercentages() {

    }

    public void resetCurrentValues() {

    }

    protected void resetCurrentValue(PARAMETER base_p) {

        base_p = ContentManager.getBaseParameterFromCurrent(base_p);
        PARAMETER c_p = ContentManager.getCurrentParam(base_p);
        PARAMETER c_perc = ContentManager.getPercentageParam(base_p);
        int percentage = getIntParam(c_perc);
        int base_value = getIntParam(base_p);
        int c_value = MathMaster.getFractionValue(base_value, percentage);
        setParam(c_p, c_value, true);
        LogMaster.log(LogMaster.VALUE_DEBUG, getName() + "'s "
         + base_p.getName() + " current value reset: " + percentage + "% out of "
         + base_value + " = " + c_value);

    }

    protected void resetPercentage(PARAMETER p) {
        p = ContentManager.getBaseParameterFromCurrent(p);
        PARAMETER c_p = ContentManager.getCurrentParam(p);
        PARAMETER c_perc = ContentManager.getPercentageParam(p);
        if (c_perc == null || c_perc == p) {
            return;
        }
        int base_value = getIntParam(p);
        Integer c_value = getIntParam(c_p);
        int percentage = MathMaster.getPercentage(c_value, base_value);
        setParam(c_perc, percentage, true);
        LogMaster.log(LogMaster.VALUE_DEBUG, getName() + "'s " + p.getName()
         + " percentage reset: " + c_value + " out of " + base_value + " = " + percentage);
    }

    public Game getGame() {
        if (game == null) return Game.game;
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean setParam(PARAMETER param, String value) {
        return setParam(param, value, false);

    }

    public void setProperty(PROPERTY name, String value, boolean base) {
        if (base) {
            if (type != null) {
                type.setProperty(name, value);
            }
        }
        setProperty(name, value);
    }

    public void setProperty(String prop, String value) {
        PROPERTY p = ContentManager.getPROP(prop);

        setProperty(p, value);
    }

    public void setProperty(PROPERTY prop, String value) {
        if (prop == G_PROPS.NAME) {
            this.name = XML_Formatter.restoreXmlNodeName(value);
        }
        putProperty(prop, value);
        getPropCache().remove(prop);
        setDirty(true);
    }

    public void modifyProperty(MOD_PROP_TYPE p, PROPERTY prop, String value) {
        switch (p) {
            case ADD:
                addProperty(prop, value);
                break;
            case SET:
                setProperty(prop, value);
                break;
            case REMOVE:
                removeProperty(prop, value);
                break;
        }
    }

    public void removeLastPartFromProperty(PROPERTY prop) {
        String value = getProperty(prop);
        setProperty(prop, value.replace(StringMaster.getLastPart(value, ";") + ";", ""));
    }

    public void removeFromProperty(PROPERTY prop, String value) {
        setProperty(prop, getProperty(prop).replace(value, ""));
    }

    public void appendProperty(PROPERTY prop, String value) {
        setProperty(prop, getProperty(prop) + value);
    }

    public boolean addOrRemoveProperty(PROPERTY prop, String value) {
        if (checkProperty(prop, value)) {
            removeProperty(prop, value);
            return false;
        } else {
            addProperty(prop, value, true);
            return true;
        }
    }

    public boolean addProperty(PROPERTY prop, String value) {
        return addProperty(prop, value, true);
    }

    public boolean addProperty(PROPERTY prop, List<String> values, boolean noDuplicates) {
        for (String value : values) {
            addProperty(prop, value, noDuplicates);
        }
        return true;
    }

    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates) {
        return addProperty(prop, value, noDuplicates, false);
    }

    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates, boolean addInFront) {

        LogMaster.log(LogMaster.VALUE_DEBUG, "adding  " + value + " to "
         + getName() + "'s " + prop.getName());

        if (value.contains(StringMaster.AND_PROPERTY_SEPARATOR)) {
            for (String s : StringMaster.openContainer(value, StringMaster.AND_PROPERTY_SEPARATOR)) {
                addProperty(prop, s, noDuplicates);
            }
            return true;
        }

        if (!firePropEvent(CONSTRUCTED_EVENT_TYPE.PROP_BEING_ADDED, prop.getName())) {
            return false;
        }

        if (checkSingleProp(prop, value)) {
            return false;
        }
        if (noDuplicates) {
            if (checkProperty(prop, value)) {
                return false;
            }
        }
        String prevValue = propMap.get(prop);
        if (!StringMaster.isEmpty(prevValue)) {
            if (!prevValue.endsWith(StringMaster.getContainerSeparator())) {
                prevValue = prevValue + StringMaster.getContainerSeparator();
            }
        }

        value = value + StringMaster.getContainerSeparator();

        if (!StringMaster.isEmpty(prevValue)) {
            if (addInFront) {
                value = value + prevValue;
            } else {
                value = prevValue + value;
            }
        }
        putProperty(prop, value);
        getPropCache().remove(prop);
        setDirty(true);

        firePropEvent(CONSTRUCTED_EVENT_TYPE.PROP_ADDED, prop.getName());
        return true;
    }

    protected void putProperty(PROPERTY prop, String value) {

        if (isTypeLinked()) {
            type.getPropMap().put(prop, value);
        }
        propMap.put(prop, value);
    }

    protected void putParameter(PARAMETER param, String value) {
        if (isTypeLinked()) {
            type.getParamMap().put(param, value);
        }
        paramMap.put(param, value);
    }

    public boolean isTypeLinked() {
        return false;
    }

    public void addProperty(String prop, String value) {
        addProperty(ContentManager.getPROP(prop), value);
    }

    public boolean clearProperty(PROPERTY prop) {
        if (!checkProperty(prop)) {
            return false;
        }
        setProperty(prop, "");
        return true;
    }

    public boolean removeProperty(PROPERTY prop) {
        return removeProperty(prop, "", false);
    }

    public boolean removeProperty(PROPERTY prop, String value) {
        return removeProperty(prop, value, false);
    }

    public boolean removeProperty(PROPERTY prop, String value, boolean all) {

        LogMaster.log(LogMaster.VALUE_DEBUG, "Removing  " + value + " from "
         + getName() + "'s " + prop.getName());
        // if (!firePropEvent(CONSTRUCTED_EVENT_TYPE.PROP_BEING_REMOVED,
        // prop.getName()))
        // return false;

        boolean result;
        if (prop.isContainer()) {
            result = removeMultiProp(prop.getName(), value, all);
        } else {
            result = StringMaster.isEmpty(getProperty(prop));
            setProperty(prop, "");
        }
        getPropCache().remove(prop);
        // firePropEvent(CONSTRUCTED_EVENT_TYPE.PROP_REMOVED, prop.getName());

        return result;
    }

    protected boolean removeMultiProp(String prop, String value, boolean all) {
        boolean result = true;
        String prevValue = propMap.get(prop);
        String strToReplace = value + StringMaster.getContainerSeparator();
        if (!prevValue.contains(strToReplace)) {
            strToReplace = StringMaster.getContainerSeparator() + value;
        } // TODO well-formatted?
        if (!prevValue.contains(strToReplace)) {
            strToReplace = value;
        }
        if (!prevValue.contains(strToReplace)) {
            result = false;
        } else {
            value = StringMaster.replace(all, prevValue, strToReplace, "");

            propMap.put(prop, value);
            setDirty(true);
        }

        return result;

    }

    public String getSubGroupingKey() {
        return getProperty(TYPE_ENUM.getSubGroupingKey());
    }

    public boolean isSetThis() {
        return true;
    }

    public void setValue(VALUE valName, String value) {
        setValue(valName, value, false);
    }

    public void setValue(VALUE valName, String value, boolean base) {
        if (value == null) {
            return;
        }
        if (valName == G_PROPS.NAME) {
            setName(value);
        } else if (valName instanceof PROPERTY) {
            setProperty((PROPERTY) valName, value, base);
        } else if (valName instanceof PARAMETER) {
            if (!base) {
                setParam((PARAMETER) valName, value);
            } else {
                setParam((PARAMETER) valName, StringMaster.getInteger(value), false, base);
            }
        }
        setDirty(true);
    }

    public void setValue(String name, String value) {
        setValue(name, value, false);
    }

    public void setValue(String name, String value, boolean base) {
        if (name == null) {
            return;
        }
        if (value == null) {
            return;
        }
        // if (valueMap==null) initValueMap();
        // valueMap.put(nodeName, nodeValue);
        if (name.equalsIgnoreCase(G_PROPS.NAME.getName())) {
            setName(value);
        } else {
            setValue(ContentManager.getValue(name), value, base);
        }

    }

    @Override
    public String toString() {
        return getName() + " - " + id;
    }

    public void cloneMaps(DataModel type) {
        cloneMapsWithExceptions(type);
    }

    public void mergeValues(Entity type, VALUE... vals) {

        for (VALUE val : vals) {
            if (val instanceof PROPERTY) {
                PROPERTY property = (PROPERTY) val;
                addProperty(property, type.getProperty(property), true);
            } else {
                if (val instanceof PARAMETER) {
                    PARAMETER parameter = (PARAMETER) val;
                    addParam(parameter, type.getParam(parameter), false);
                }
            }
        }
    }

    public void addParam(PARAMETER parameter, String param, boolean base) {
        modifyParameter(parameter, StringMaster.getInteger(param), base);

    }

    public void copyValues(Entity type, List<VALUE> list) {
        for (VALUE val : list) {
            setValue(val, type.getValue(val));
        }
    }

    public void copyValues(Entity type, VALUE... vals) {
        for (VALUE val : vals) {
            setValue(val, type.getValue(val));
        }
    }

    public void cloneMapsWithExceptions(DataModel type, VALUE... exceptions) {
        Map<VALUE, String> map = new HashMap<>();
        for (VALUE exception : exceptions) {
            map.put(exception, getValue(exception));
        }
        this.propMap = clonePropMap(type.getPropMap().getMap());
        this.paramMap = cloneParamMap(type.getParamMap().getMap());
        for (VALUE exception : exceptions) {
            String value = map.get(exception);
            if (exception instanceof PARAMETER) {
                paramMap.put(exception.getName(), value);
            } else if (exception instanceof PROPERTY) {
                propMap.put(exception.getName(), value);
            }
        }
        setDirty(true);
    }

    protected ParamMap cloneParamMap(Map<PARAMETER, String> map) {
        ParamMap clone = new ParamMap();
        Map<PARAMETER, String> innerMap = new HashMap<>();
        innerMap.putAll(map);

        clone.setMap(innerMap);
        return clone;
    }

    protected PropMap clonePropMap(Map<PROPERTY, String> map) {
        PropMap clone = new PropMap();
        Map<PROPERTY, String> innerMap = new ConcurrentHashMap<>();
        innerMap.putAll(map);

        clone.setMap(innerMap);
        return clone;
    }

    public Integer getId() {
        if (id == null) {
            if (game != null) {
                id = getGame().getIdManager().getNewId();
            }
        }
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameIfKnown() {

        return getName();
    }

    public String getName() {
        if (name == null) {
            name = getProperty(G_PROPS.NAME);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setProperty(G_PROPS.NAME, name, true);
        name = StringMaster.formatDisplayedName(name);
        setProperty(G_PROPS.DISPLAYED_NAME, name, true);
    }

    public String getUniqueId() {
        return getProperty(G_PROPS.UNIQUE_ID);
    }

    public boolean isConstructed() {
        return constructed;
    }

    public void setConstructed(boolean b) {
        this.constructed = b;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isPassivesReady() {
        return passivesReady;
    }

    public void setPassivesReady(boolean passivesReady) {
        this.passivesReady = passivesReady;
    }

    public boolean isActivesReady() {
        return activesReady;
    }

    public void setActivesReady(boolean activesReady) {
        this.activesReady = activesReady;
    }

    public boolean checkBool(DYNAMIC_BOOLS bool) {
        String value = getProperty(G_PROPS.DYNAMIC_BOOLS);
        if (StringMaster.isEmpty(value)) {
            return false;
        }
        return StringMaster.compareContainers(value, bool.toString(), false);
    }

    public boolean checkBool(STD_BOOLS bool) {
        String value = getProperty(G_PROPS.STD_BOOLS);
        if (StringMaster.isEmpty(value)) {
            return false;
        }
        return StringMaster.compareContainers(value, bool.toString(), false);
    }

    public boolean checkCustomProp(String name) {
        return !StringMaster.isEmpty(getCustomProperty(name));
    }

    public Map<String, String> getCustomPropMap() {
        if (customPropMap == null) {
            setCustomPropMap(new HashMap<>());
        }
        return customPropMap;
    }

    public void setCustomPropMap(Map<String, String> customPropMap) {
        this.customPropMap = customPropMap;
    }

    public void addCustomProperty(String name, String value) {
        getCustomPropMap().put(name, value);
    }

    public void addCustomParameter(String name, String value) {
        getCustomParamMap().put(name, value);
    }

    public Map<String, String> getCustomParamMap() {
        if (customParamMap == null) {
            setCustomParamMap(new XLinkedMap<>());
        }
        return customParamMap;
    }

    public void setCustomParamMap(Map<String, String> customParamMap) {
        this.customParamMap = customParamMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entity)) {
            return false;
        }
        try {
            return ((Entity) obj).getId().equals(getId());
        } catch (Exception e) {
            return getId() == null;
        }
    }

    public boolean replaceContainerPropItem(PROPERTY prop, String replacing, String replaced) {
        String value = getProperty(prop);
        if (!value.contains(replaced)) {
            return false;
        }
        value = value.replace(replaced, replacing);
        setProperty(prop, value);
        return true;
    }

    public void copyValue(VALUE param, Entity entity) {
        if (param instanceof PARAMETER) {
            setParamDouble((PARAMETER) param, entity.getParamDouble((PARAMETER) param), false);
        } else {
            setProperty((PROPERTY) param, entity.getProperty(param.toString()));
        }
    }

    public void setModifierKey(String modifierKey) {
        this.modifierKey = modifierKey;
    }

    public void removed() {
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getNameOrId() {
        if (getGame().isSimulation()) {
            if (this instanceof ObjType) {
                return getName();
            }
        }
        return getId() + "";
    }

    public String getNameAndId() {
                return getName()+getId()  ;
    }
    public String getRawValue(VALUE value) {
        String string = getRawValues().get(value);
        if (string == null) {
            return getValue(value, true);
        }
        return string;
    }

    public XLinkedMap<VALUE, String> getRawValues() {
        if (rawValues == null) {
            rawValues = new XLinkedMap<>();
        }
        return rawValues;
    }

    public void setRawValues(XLinkedMap<VALUE, String> rawValues) {
        this.rawValues = rawValues;
    }

    public boolean isDefaultValuesInitialized() {
        return defaultValuesInitialized;
    }

    public void setDefaultValuesInitialized(boolean defaultValuesInitialized) {
        this.defaultValuesInitialized = defaultValuesInitialized;
    }

    public void cloned() {

    }

    public int getLevel() {
        return getIntParam("level");
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public ImageIcon getCustomIcon() {
        if (customIcon == null) {
            if (game != null) {
                if (getGame().isSimulation()) {
                    if (ref != null) {
                        Map<String, ImageIcon> cache = ImageManager.getCustomIconCache().get(
                         ref.getSourceObj());
                        if (cache == null) {
                            return null;
                        }
                        return cache.get(getName()); // modified name for
                        // upgrades?
                        // or
                        // displayed only?

                    }
                }
            }
        }

        return customIcon;
    }

    public void setCustomIcon(ImageIcon customIcon) {
        if (getGame().isSimulation()) {
            Map<String, ImageIcon> cache = ImageManager.getCustomIconCache()
             .get(ref.getSourceObj());
            if (cache == null) {
                cache = new HashMap<>();
                ImageManager.getCustomIconCache().put(ref.getSourceObj(), cache);
            }
            cache.put(getName(), customIcon);
        }
        this.customIcon = customIcon;
    }

    public WORKSPACE_GROUP getWorkspaceGroup() {
        return new EnumMaster<WORKSPACE_GROUP>().retrieveEnumConst(WORKSPACE_GROUP.class,
         getProperty(G_PROPS.WORKSPACE_GROUP));
    }

    public void setWorkspaceGroup(WORKSPACE_GROUP value) {
        setProperty(G_PROPS.WORKSPACE_GROUP, value.toString());
    }

    public int getTypeId() {
        return StringMaster.getInteger(getProperty(G_PROPS.ID));
    }

    public List<ObjType> getListFromProperty(OBJ_TYPE TYPE, PROPERTY prop) {
        return DataManager.toTypeList(getProperty(prop), TYPE);
    }

    public void resetPropertyFromList(PROPERTY prop, List<? extends Entity> list) {
        if (ListMaster.isNotEmpty(list)) {
            setProperty(prop, StringMaster.constructContainer(ListMaster.toNameList(list)),
             isTypeLinked());
        } else {
            removeProperty(prop);
            getType().removeProperty(prop);
        }
    }

    public HashMap<PROPERTY, Map<String, Boolean>> getPropCache() {
        if (propCache == null) {
            propCache = new HashMap<>();
        }
        return propCache;
    }

    public int getSumOfParams(PARAMETER... params) {
        Integer sum = 0;
        for (PARAMETER param : params) {
            sum += getIntParam(param);
        }
        return sum;
    }

    public String getImagePath() {
        return getProperty(G_PROPS.IMAGE);
    }


    public int getParamPercentage(PARAMETER parameter) {
        return getIntParam(ContentManager.getPercentageParam(parameter)) / MathMaster.MULTIPLIER;
    }
}
