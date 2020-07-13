package main.content;

import com.badlogic.gdx.utils.ObjectMap;
import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.G_ValueInitializer;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.Param;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.xml.XML_Reader;
import main.entity.Entity;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.SearchMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.*;

//?

/**
 * loads content into accessible data structures For "enum browsing"
 *
 * @author JustMe
 */

public class ContentValsManager {

    public static final int INFINITE_VALUE = 999;
    public static final String NEW_EMPTY_VALUE = "-";
    public static final String DEFAULT_EMPTY_VALUE = "";
    public static final String OLD_EMPTY_VALUE = "[...]";
    public static final String RETAINMENT = "_RETAINMENT";
    public static final String RESTORATION = "_RESTORATION";
    private static final String MASTERY = "_MASTERY";
    private static final boolean LOWER_CASE_CACHED = true;
    private static final ObjectMap<String, List<String>> valueNamesMap = new ObjectMap<>();

    private static final ObjectMap<String, List<String>> valueNamesMapAV = new ObjectMap<>();

    private static List<PROPERTY> props;
    private static List<PARAMETER> params;
    private static List<String> sprops;
    private static List<String> sparams;
    private static final ObjectMap<String, List<String>> spropListsMap = new ObjectMap<>(
            400, 0.75f);
    private static final ObjectMap<String, List<String>> sparamListsMap = new ObjectMap<>(
            400, 0.75f);

    private static final ObjectMap<String, List<String>> spropListsMapAV = new ObjectMap<>();
    private static final ObjectMap<String, List<String>> sparamListsMapAV = new ObjectMap<>();

    private static final ObjectMap<String, List<PROPERTY>> propListsMap = new ObjectMap<>(
            400, 0.75f);
    private static final ObjectMap<String, List<PARAMETER>> paramListsMap = new ObjectMap<>(400, 0.75f);
    private static final ObjectMap<String, List<VALUE>> valueListsMap = new ObjectMap<>();
    private static final ObjectMap<String, List<VALUE>> valueListsMapAV = new ObjectMap<>();

    private static final ObjectMap<String, List<PROPERTY>> propListsMapAV = new ObjectMap<>();
    private static final ObjectMap<String, List<PARAMETER>> paramListsMapAV = new ObjectMap<>();

    private static final List<PARAMETER> attributes = new ArrayList<>();
    private static final List<PARAMETER> masteries = new ArrayList<>();
    private static final List<PARAMETER> masteryScores = new ArrayList<>();

    private static ObjectMap<String, List<VALUE>> AV_IgnoredValues;
    private static ArrayList<PARAMETER> perLevelParams = new ArrayList<>();
    private static Class<?>[] propEnumClasses;
    private static Class<?>[] paramEnumClasses;
    private static List<PARAMETER> finalAttributes;
    private static ObjectMap<String, PARAMETER> paramCache;// = new  ObjectMap<>();
    private static ObjectMap<String, PROPERTY> propCache;// = new  ObjectMap<>();
    private static List<VALUE> values;
    private static Set<VALUE> excludedValueSet;
    private static ContentValsManager instance;
    private static final  Map<PARAMETER, PARAMETER> currentCache = new HashMap<>();
    private static final  Map<PARAMETER, PARAMETER> regenCache = new HashMap<>();
    private static final  Map<PARAMETER, PARAMETER> percCache = new HashMap<>();

    public ContentValsManager() {
        instance = this;
    }

    static {
        G_ValueInitializer.init();
    }

    public static void init(ArrayList<PROPERTY> propz, ArrayList<PARAMETER> paramz) {
// Core Fix - Optimization fix - init objectMaps here with size!
        props = propz;
        params = paramz;
        sparams = new ArrayList<>(params.size());
        sprops = new ArrayList<>(props.size());

        propCache = new ObjectMap<>(props.size() * 3 / 2);
        for (PROPERTY p : props) {
            String name = p.getName();
            sprops.add(name);

            if (LOWER_CASE_CACHED)
                name = name.toLowerCase();
            propCache.put(name, p);


        }
        paramCache = new ObjectMap<>(params.size() * 3 / 2);
        for (PARAMETER p : params) {
            String name = p.getName();
            sparams.add(name);
            if (LOWER_CASE_CACHED)
                name = name.toLowerCase();
            paramCache.put(name, p);

            if (p.isAttribute()) {
                getAttributes().add(p);
            }
            if (p.isMastery()) {
                if (p instanceof Param) {
                    getMasteryScores().add(p);
                } else {
                    getMasteries().add(p);
                }
            }
            if (checkParamPerLevel(p)) {
                getPerLevelParams().add(p);
            }
        }

        // Collections.sort(props, new DefaultComparator<PROPERTY>());
        // Collections.sort(params, new DefaultComparator<PARAMETER>());

    }

    private static boolean checkParamPerLevel(PARAMETER p) {
        return p.toString().contains(StringMaster.PER_LEVEL);
    }

    public static PARAMETER getCurrentParam(PARAMETER param) {
        PARAMETER cParam = currentCache.get(param);
        if (cParam != null) {
            return cParam;
        }
        if (param.name().startsWith(StringMaster.CURRENT))
            return param;
        cParam = getPARAM(StringMaster.CURRENT + param.getName(), true);
        currentCache.put(param, cParam);
        return cParam;
    }


    public static PARAMETER getBaseParameterFromCurrent(PARAMETER param) {
        PARAMETER baseParam = new MapMaster<PARAMETER, PARAMETER>().
                getKeyForValue(currentCache, param);
        if (baseParam != null) {
            return baseParam;
        }
        if (!param.name().startsWith(StringMaster.CURRENT)) {
            return param;
        }
        baseParam = getPARAM(param.getFullName().replace(StringMaster.CURRENT, ""), true);
        if (baseParam != null) {
            currentCache.put(baseParam, param);
        }
        return baseParam;
    }
    //  TODO refactor?
    //  public static PARAMETER getParameterVersion(PARAMETER param, String versionId) {
    //        ObjectMap<PARAMETER, PARAMETER> cache = getCache(versionId);
    //    }

    public static PARAMETER getPercentageParam(PARAMETER param) {
        if (param.isDynamic() && param.name().startsWith(StringMaster.CURRENT)) {
            param = getBaseParameterFromCurrent(param);
        }
        PARAMETER percParam = percCache.get(param);
        if (percParam != null) {
            return percParam;
        }
        if (param.name().startsWith(StringMaster.CURRENT))
            return param;
        percParam = getPARAM(param.getName() + StringMaster.PERCENTAGE);
        percCache.put(param, percParam);
        return percParam;
    }

    public static PARAMETER getReqParam(PARAMETER p) {
        return getPARAM(p.getName() + StringMaster.REQUIREMENT, true);
    }

    public static PARAMETER getFinalAttrFromBase(PARAMETER param) {
        return ContentValsManager.getPARAM(param.name().replace(StringMaster.BASE, ""));
    }

    public static PARAMETER getMasteryScore(PARAMETER mastery) {
        PARAMETER param = getPARAM(mastery.getName() + (StringMaster.SCORE));
        return param;
    }

    public static PARAMETER getMasteryFromScore(PARAMETER mastery) {
        PARAMETER param = getPARAM(mastery.getName()
                .replace(StringMaster.SCORE, ""));
        return param;
    }


    public static PARAMETER getRegenParam(PARAMETER param) {
        PARAMETER regenParam = regenCache.get(param);
        if (regenParam != null) {
            return regenParam;
        }
        regenParam = getPARAM(param.getName() + StringMaster.REGEN, true);
        regenCache.put(param, regenParam);
        return regenParam;
    }

    public static PARAMETER getPARAM(String valueName) {
        if (StringMaster.isEmpty(valueName)) {
            return null;
        }
        if (LOWER_CASE_CACHED)
            valueName = valueName.toLowerCase();
        PARAMETER param = paramCache.get(valueName);

        if (param != null) {
            return param;
        }
        param = getPARAM(valueName, true);

        if (param == null) {
            param = getPARAM(valueName, false);
        }

        if (param == null) {
            LogMaster.log(LogMaster.CORE_DEBUG, "PARAM NOT FOUND: "
                    + valueName + "!");
            param = G_PARAMS.EMPTY_PARAMETER;
        } else
            paramCache.put(valueName, param);

        if (param == G_PARAMS.EMPTY_PARAMETER) {
            return null;
        }
        return param;
    }

    public static PARAMETER getPARAM(String valueName, boolean strict) {
        for (PARAMETER p : params) {
            if (StringMaster.compareByChar(valueName, p.toString(), true)) {
                return p;
            }
        }
        for (PARAMETER p : params) {
            if (StringMaster.compareByChar(valueName, p.getDisplayedName(), true)) {
                return p;
            }
        }
        if (strict)
            return null;
        for (PARAMETER p : params) {
            if (StringMaster.compareByChar(valueName, p.toString(), false)) {
                return p;
            }
        }
        for (PARAMETER p : params) {
            if (StringMaster.compareByChar(valueName, p.getDisplayedName(), false)) {
                return p;
            }
        }
        //TODO this created bugs, but why?..
        //        if (!strict) {
        //            for (PARAMETER p : params) {
        //                if (StringMaster.compare(valueName, p.toString(), strict)) {
        //                    return p;
        //                }
        //            }
        //        }
        return null;
    }

    public static VALUE find(String valueName, Boolean param) {

        VALUE value = null;
        if (param == null) {
            value = findPARAM(valueName);
            VALUE value2 = findPROP(valueName);
            return (VALUE) SearchMaster.findClosest(valueName, value, value2);
        }
        valueName = valueName.replace("_", " ");
        valueName = valueName.replace(" ", "");
        for (VALUE v : param ? params : props) {
            if (StringMaster.compareByChar(valueName, v.toString().replace(" ", ""), false)) {
                value = v;
            }
            break;
        }
        if (value == null) {
            for (VALUE p : param ? params : props) {
                if (StringMaster.compare(valueName, p.toString().replace(" ", ""), true)) {
                    value = p;
                    break;
                }
            }
        }
        List<VALUE> valuesFound = new ArrayList<>();
        if (value == null) {
            for (VALUE p : param ? params : props) {
                if (StringMaster.compare(valueName, p.toString().replace(" ", ""), false)) {
                    if (excludedValueSet != null) {
                        if (excludedValueSet.contains(p)) {
                            continue;
                        }
                    }
                    valuesFound.add(p);
                    break;
                }
            }
        }
        int weight = Integer.MIN_VALUE;
        for (VALUE p : valuesFound) {
            int weight_ = StringMaster.compareSimilar(p.toString().replace(" ", ""), valueName);
            if (weight_ >= weight) {
                weight = weight_;
                value = p;
            }
        }
        if (LOWER_CASE_CACHED)
            valueName = valueName.toLowerCase();
        if (param) {
            paramCache.put(valueName, (PARAMETER) value);
        } else {

            propCache.put(valueName, (PROPERTY) value);
        }
        return value;

    }

    public static PARAMETER findPARAM(String valueName) {
        return (PARAMETER) find(valueName, true);
    }

    public static PROPERTY findPROP(String valueName) {
        return (PROPERTY) find(valueName, false);
    }

    private static PROPERTY getPROP(String valueName, boolean strict) {
        for (PROPERTY p : props) {
            if (StringMaster.compareByChar(valueName, p.toString(), strict)) {
                return p;
            }
        }
        if (!strict) {
            for (PROPERTY p : props) {
                if (StringMaster.compare(valueName, p.toString(), false)) {
                    return p;
                }
            }
        }
        return null;
    }

    public static PROPERTY getPROP(String valueName) {
        PROPERTY property = propCache.get(valueName);

        if (property != null) {
            return property;
        }
        property = getPROP(valueName, true);
        if (property == null) {
            property = getPROP(valueName, false);
        }

        if (property == null) {
            LogMaster.log(LogMaster.CORE_DEBUG, "PROPERTY NOT FOUND: "
                    + valueName + "!");
        }

        propCache.put(valueName, property);
        return property;
    }

    public static VALUE findValue(String valueName) {
        return find(valueName, null);
    }

    public static VALUE getValue(String valueName) {
        return getValue(valueName, false);
    }

    public static VALUE getValue(String valueName, boolean extensiveSearch) {
        if (StringMaster.isEmpty(valueName)) {
            return null;
        }
        if (LOWER_CASE_CACHED)
            valueName = valueName.toLowerCase();
        VALUE v = propCache.get(valueName);
        if (v != null) {
            if (!checkExcluded(v)) {
                return v;
            }
        }

        v = paramCache.get(valueName);
        if (v != null) {
            if (!checkExcluded(v)) {
                return v;
            }
        }

        v = getPROP(valueName, true);
        if (v == null) {
            v = getPARAM(valueName, true);
        }
        if (v == null) {
            if (extensiveSearch) {
                v = getPROP(valueName, false);
                if (v == null) {
                    v = getPARAM(valueName, false);
                }

                PROPERTY prop = findPROP(valueName);
                PARAMETER param = findPARAM(valueName);
                if (prop != null && param != null) {
                    v = StringMaster.compareSimilar(prop.toString(), valueName) >= StringMaster
                            .compareSimilar(param.toString(), valueName) ? prop : param;
                } else if (prop != null) {
                    v = prop;
                } else {
                    v = param;
                }
                // //compare length difference?
                // //cache key to value
            }
            if (v == null) {
                LogMaster.log(LogMaster.CORE_DEBUG, "VALUE NOT FOUND: "
                        + valueName);
            }

        }
        return v;
    }

    private static boolean checkExcluded(VALUE v) {
        if (excludedValueSet != null) {
            return excludedValueSet.contains(v);
        }
        return false;
    }

    public static List<PARAMETER> getParamList() {
        return params;
    }

    public static List<VALUE> getValueList() {
        if (values == null) {
            values = new ArrayList<>();
            values.addAll(getPropList());
            values.addAll(getParamList());
        }
        return values;
    }

    public static List<PROPERTY> getPropList() {
        return props;
    }

    public static boolean isProperty(String name) {
        if (sprops.contains(name)) {
            return true;
        }
        return sprops.contains(StringMaster.format(name));
    }

    public static boolean isPropertyExtendedSearch(String name) {
        return ListMaster.contains(sprops, name, false);
    }

    public static boolean isParameterExtendedSearch(String name) {
        return ListMaster.contains(sparams, name, false);
    }

    public static boolean isParameter(String name) {
        if (sparams.contains(name)) {
            return true;
        }
        return sparams.contains(StringMaster.format(name));
    }

    public static List<PARAMETER> getHeroStatsTabValueList() {
        List<PARAMETER> list = new ArrayList<>();
        for (PARAMETER p : getParamsForType(DC_TYPE.CHARS.getName(), false)) {
            if (!(p.isAttribute() || p.isMastery())) {
                list.add(p);
            }
        }
        return list;
    }

    public static List<PARAMETER> getParamsForType(String entity, boolean dynamic) {
        List<PARAMETER> paramList = (dynamic) ? paramListsMap.get(entity) : paramListsMapAV
                .get(entity);
        if (paramList != null) {
            return paramList;
        }
        paramList = new ArrayList<>();
        for (PARAMETER param : params) {
            if (!dynamic) {
                if (param.isDynamic() && !param.isWriteToType()) {
                    continue;
                }
            }
            if (isValueForOBJ_TYPE(entity, param)) {
                paramList.add(param);
            }
        }
        if (!dynamic) {
            paramListsMapAV.put(entity, paramList);
        } else {
            paramListsMap.put(entity, paramList);
        }
        return paramList;
    }

    public static List<String> getParamNames(String entity, boolean dynamic) {
        List<String> paramList = (dynamic) ? sparamListsMap.get(entity) : sparamListsMapAV
                .get(entity);
        if (paramList != null) {
            return paramList;
        }
        paramList = new ArrayList<>();
        for (PARAMETER param : getParamsForType(entity, dynamic)) {

            paramList.add(param.getName());
        }
        if (!dynamic) {
            sparamListsMapAV.put(entity, paramList);
        } else {
            sparamListsMap.put(entity, paramList);
        }
        return paramList;
    }

    public static List<VALUE> getValuesForType(String TYPE, boolean dynamic) {
        List<VALUE> propList = (dynamic) ? valueListsMap.get(TYPE) : valueListsMapAV.get(TYPE);
        if (propList != null) {
            return propList;
        }
        propList = new ArrayList<>();

        propList.addAll(getPropsForType(TYPE, dynamic));
        propList.addAll(getParamsForType(TYPE, dynamic));
        if (!dynamic) {
            valueListsMap.put(TYPE, propList);
        } else {
            valueListsMapAV.put(TYPE, propList);
        }
        return propList;
    }

    public static List<PROPERTY> getPropsForType(String entity, boolean dynamic) {
        List<PROPERTY> propList = (dynamic) ? propListsMap.get(entity) : propListsMapAV.get(entity);
        if (propList != null) {
            return propList;
        }
        propList = new ArrayList<>();
        for (PROPERTY prop : props) {
            if (!dynamic) {
                if (prop.isDynamic() && !prop.isWriteToType()) {
                    continue;
                }
            }
            if (isValueForOBJ_TYPE(entity, prop)) {
                propList.add(prop);
            }
        }
        if (!dynamic) {
            propListsMapAV.put(entity, propList);
        } else {
            propListsMap.put(entity, propList);
        }
        return propList;
    }

    public static Collection<String> getPropNames(String entity) {
        return getPropNames(entity, false);

    }

    public static List<String> getPropNames(String entity, boolean dynamic) {
        List<String> propList = (dynamic) ? spropListsMap.get(entity) : spropListsMapAV.get(entity);
        if (propList != null) {
            return propList;
        }
        propList = new ArrayList<>();
        for (PROPERTY prop : getPropsForType(entity, dynamic)) {

            propList.add(prop.getName());

        }

        if (!dynamic) {
            spropListsMapAV.put(entity, propList);
        } else {
            spropListsMap.put(entity, propList);
        }
        return propList;
    }

    public static List<String> getArcaneVaultValueNames(String objType) {

        return getFullValueList(objType, true);
    }

    public static List<String> getFullValueList(String objType) {
        return getFullValueList(objType, false);

    }

    public static List<String> getFullValueList(String objType, boolean av) {
        if (objType == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> valueNames = (av) ? getValueNamesMapAV().get(objType) : getValueNamesMap()
                .get(objType);
        if (valueNames != null) {
            return valueNames;
        }
        valueNames = new ArrayList<>();
        appendLast(valueNames, getParamNames(objType, !av));
        appendFirst(valueNames, getPropNames(objType, !av));
        if (av) {
            if (getAV_IgnoredValues() != null) {
                if (getAV_IgnoredValues().get(objType) != null) {
                    for (VALUE v : getAV_IgnoredValues().get(objType)) {
                        valueNames.remove(v.getName());
                    }
                }
            }

            getValueNamesMapAV().put(objType, valueNames);
        } else {
            getValueNamesMap().put(objType, valueNames);
        }
        return valueNames;

    }

    static void appendFirst(List<String> valueNames, List<String> values) {
        append(valueNames, values, false);
    }

    static void append(List<String> valueNames, List<String> values, boolean last) {
        for (int n = 0; n < values.size(); n++) {
            String value = values.get((last) ? n : values.size() - 1 - n);
            int i = valueNames.indexOf(value);
            if (i != -1) {
                valueNames.remove(i);
            }
            valueNames.add((last) ? valueNames.size() : 0, value);

        }
    }

    static void appendLast(List<String> valueNames, List<VALUE> values, String objType) {
        List<String> names = new ArrayList<>();
        for (VALUE val : values) {
            if (isValueForOBJ_TYPE(objType, val)) {
                names.add(val.getName());
            }
        }
        append(valueNames, names, true);
    }

    static void appendLast(List<String> valueNames, List<String> values) {
        append(valueNames, values, true);
    }

    public static String getDefaultEmptyValue() {
        return DEFAULT_EMPTY_VALUE;
    }

    public static ObjectMap<String, List<VALUE>> getAV_IgnoredValues() {
        return AV_IgnoredValues;
    }

    public static void setAV_IgnoredValues(ObjectMap<String, List<VALUE>> aV_IgnoredValues) {
        AV_IgnoredValues = aV_IgnoredValues;
    }

    public static PARAMETER getMastery(String property) {
        String mastery = property + MASTERY;
        return getPARAM(mastery);
    }

    public static PARAMETER findMasteryScore(String property) {
        MASTERY group = new EnumMaster<MASTERY>().retrieveEnumConst(MASTERY.class, property);
        if (group == null) {
            return null;
        }
        return getMasteryScore(group.toString());

    }

    public static PARAMETER findMastery(String property) {
        MASTERY group = new EnumMaster<MASTERY>().retrieveEnumConst(MASTERY.class, property, true);
        if (group == null) {
            return null;
        }
        return getPARAM((group.toString()));

    }

    public static PARAMETER getMasteryScore(String property) {
        String mastery = property + StringMaster.SCORE;
        return getPARAM(mastery);
    }

    public static PARAMETER getSpellMasteryForSpell(Entity type) {
        return findMastery(type.getProperty(G_PROPS.SPELL_GROUP));
    }

    public static OBJ_TYPE getOBJ_TYPE(String typeName) {
        OBJ_TYPE type = null;

        type = DC_TYPE.getType(typeName);
        if (type == null)
            if (XML_Reader.isMacro()) {
                type = MACRO_OBJ_TYPES.getType(typeName);
            }

        if (type == null) {
            if (!Flags.isMapEditor())
                if (CoreEngine.isArcaneVault())
                    return null;
            if (!XML_Reader.isMacro()) {
                type = MACRO_OBJ_TYPES.getType(typeName);
            } else {
                type = DC_TYPE.getType(typeName);
            }
        }
        return type;
    }

    public static int getTypeCode(String typeName) {

        return getOBJ_TYPE(typeName).getCode();
    }

    public static String getTypeImage(String tabName) {
        return getOBJ_TYPE(tabName).getImage();
    }

    public static ObjectMap<String, List<String>> getValueNamesMap() {
        return valueNamesMap;
    }

    public static ObjectMap<String, List<String>> getValueNamesMapAV() {
        return valueNamesMapAV;
    }

    public static PARAMETER getPerLevelValue(String string) {
        return getPARAM(string + StringMaster.PER_LEVEL);
    }

    public static boolean isValueForOBJ_TYPE(String type, VALUE p) {
        OBJ_TYPE TYPE = getOBJ_TYPE(type);
        if (TYPE == null)
            return false;
        if (p.getEntityType() != null)
            if (p.getEntityType().equalsIgnoreCase("all")) {
                if (instance == null) {
                    return true;
                }
                return instance.checkAllApplies(p, TYPE);
            }
        Boolean override = getInstance().getValueForTypeOverride(TYPE, p);
        if (override != null) {
            return override;
        }

        if (p.getEntityTypes() != null) {
            if (Arrays.asList(p.getEntityTypes()).contains((type))) {
                return true;
            }
        }
        if (p.getEntityType() == null) {
            return false;
        }
        if (p.getEntityType().equals(type))
            return true;
        TYPE = TYPE.getParent();
        while (TYPE != null) {
            if (isValueForOBJ_TYPE(TYPE, p)) {
                return true;
            }
            TYPE = TYPE.getParent();
        }
        return false;
    }

    protected Boolean getValueForTypeOverride(OBJ_TYPE type, VALUE p) {
        return null;
    }

    public static List<OBJ_TYPE> getOBJ_TYPEsForValue(VALUE value) {
        List<OBJ_TYPE> list = new ArrayList<>();
        if (value.getEntityTypes() == null) {
            list.add(getOBJ_TYPE(value.getEntityType()));
        } else {
            for (String type : value.getEntityTypes()) {
                list.add(getOBJ_TYPE(type));
            }
        }
        return list;
    }

    public static List<String> getParamNames() {
        return sparams;
    }

    public static List<String> getPropNames() {
        return sprops;
    }

    public static boolean isValueForOBJ_TYPE(OBJ_TYPE type, VALUE p) {
        return isValueForOBJ_TYPE(type.getName(), p);
    }

    public static ArrayList<PARAMETER> getPerLevelParams() {
        return perLevelParams;
    }

    public static void setPerLevelParams(ArrayList<PARAMETER> perLevelParams) {
        ContentValsManager.perLevelParams = perLevelParams;
    }

    public static List<PARAMETER> getAttributes() {
        return attributes;
    }

    public static List<PARAMETER> getFinalAttributes() {
        if (finalAttributes == null) {
            finalAttributes = new ArrayList<>();
            for (PARAMETER attr : attributes) {
                if (StringMaster.compare(attr.getName(), "Base")) {
                    continue;
                }
                finalAttributes.add(attr);
            }
        }
        return finalAttributes;
    }

    public static List<PARAMETER> getMasteryScores() {
        return masteryScores;
    }

    public static List<PARAMETER> getMasteries() {
        return masteries;
    }

    public static PARAMETER getBaseAttribute(PARAMETER param) {
        return getPARAM(StringMaster.BASE + param.getName());
    }

    public static PARAMETER getCostParam(PARAMETER param) {

        return getPARAM(param.getName() + StringMaster.COST);
    }

    public static Class<?>[] getPropEnumClasses() {
        return propEnumClasses;
    }

    public static void setPropEnumClasses(Class<?>[] propEnumClasses) {
        ContentValsManager.propEnumClasses = propEnumClasses;
    }

    public static Class<?>[] getParamEnumClasses() {
        return paramEnumClasses;
    }

    public static void setParamEnumClasses(Class<?>[] paramEnumClasses) {
        ContentValsManager.paramEnumClasses = paramEnumClasses;
    }

    public static void setExcludedValueSet(Set<VALUE> set) {
        excludedValueSet = set;
    }

    public static String getMasteryGroup(PARAMETER mastery, String masteryGroup) {
        String name = mastery.getName().replace(" Mastery", "");
        if (StringMaster.compareByChar(name, "Spellcraft")) {
            masteryGroup = "Sorcery";
        } else if (StringMaster.compareByChar(name, "Elemental")) {
            masteryGroup = "Sorcery";
        } else if (StringMaster.compareByChar(name, "Redemption")) {
            masteryGroup = "Sorcery";
        } else if (StringMaster.compareByChar(name, "Destruction")) {
            masteryGroup = "Sorcery";
        } else if (StringMaster.compareByChar(name, "Sorcery")) {
            masteryGroup = "Sorcery";
        } else if (StringMaster.compareByChar(name, "Celestial")) {
            masteryGroup = "Light";
        } else if (StringMaster.compareByChar(name, "Meditation")) {
            masteryGroup = "Light";
        }

        if (StringMaster.compareByChar(name, "Stealth")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Marksmanship")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Unarmed")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Detection")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Tactics")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Detection")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Item")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Mobility")) {
            masteryGroup = "Stealth";
        } else if (StringMaster.compareByChar(name, "Blunt")) {
            masteryGroup = "Heavy";
        } else if (StringMaster.compareByChar(name, "Two Handed")) {
            masteryGroup = "Heavy";
        } else if (StringMaster.compareByChar(name, "Leadership")) {
            masteryGroup = "Heavy";
        } else if (StringMaster.compareByChar(name, "Discipline")) {
            masteryGroup = "Misc";
        } else if (StringMaster.compareByChar(name, "Divination")) {
            masteryGroup = "Misc";
        } else if (StringMaster.compareByChar(name, "Divination")) {
            masteryGroup = "Misc";
        } else if (StringMaster.compareByChar(name, "Savage")) {
            masteryGroup = "Savage";
        } else if (StringMaster.compareByChar(name, "Warcry")) {
            masteryGroup = "Savage";
        } else {
            switch (masteryGroup) {
                case "Defense":
                    masteryGroup = "Combat";
                    break;
                case "Body Mind":
                    masteryGroup = "Combat";
                    break;
                case "Offense":
                    masteryGroup = "Weapons";
                    break;
            }
        }
        masteryGroup = masteryGroup.replace("Arts", "");
        return masteryGroup;
    }

    public static ContentValsManager getInstance() {
        return instance;
    }

    public static void setInstance(ContentValsManager instance) {
        ContentValsManager.instance = instance;
    }

    public static String getCurrentOutOfTotal(PARAMETER value, Entity obj) {
        return obj.getValue(value) +
                "/" + obj.getValue(getBaseParameterFromCurrent(value));
    }

    public static PARAMETER getDefaultAttribute(PARAMETER sub) {
        return getPARAM(StringMaster.DEFAULT + sub.getName());
    }

    public static VALUE getValueByDisplayedName(String name) {
        for (PARAMETER parameter : getParamList()) {
            if (parameter.getDisplayedName().equalsIgnoreCase(name)) {
                return parameter;
            }
        }
        for (PROPERTY property : getPropList()) {
            if (property.getDisplayedName().equalsIgnoreCase(name)) {
                return property;
            }
        }
        return getValue(name);
    }

    public void init() {

    }

    public boolean checkAllApplies(VALUE p, OBJ_TYPE type) {
        return true;
    }

    public String getFormattedVal(VALUE v, String value) {
        return value;
    }

    public boolean isTextAlwaysShownInListItems(OBJ_TYPE TYPE) {
        return false;
    }

    public enum AV_EDITOR_TYPES {
        TYPE_LIST, ENUM_LIST, TEXT,
        ;
    }

}
