package main.content;

import main.content.enums.entity.SkillEnums.MASTERY;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.Param;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ConcurrentMap;
import main.data.xml.XML_Reader;
import main.entity.Entity;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.SearchMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.*;

//?

/**
 * loads content into accessible data structures For "enum browsing"
 *
 * @author JustMe
 */

public class ContentManager {

    public static final int INFINITE_VALUE = 999;
    public static final String NEW_EMPTY_VALUE = "-";
    public static final String DEFAULT_EMPTY_VALUE = "";
    public static final String OLD_EMPTY_VALUE = "[...]";
    private static final String MASTERY = "_MASTERY";
    private static final boolean LOWER_CASE_CACHED = true;


    private static Map<String, List<String>> valueNamesMap = new ConcurrentMap<>();

    private static Map<String, List<String>> valueNamesMapAV = new ConcurrentMap<>();

    private static List<PROPERTY> props;
    private static List<PARAMETER> params;
    private static List<String> sprops;
    private static List<String> sparams;
    private static Map<String, List<String>> spropListsMap = new ConcurrentMap<>(
            400, 0.75f);
    private static Map<String, List<String>> sparamListsMap = new ConcurrentMap<>(
            400, 0.75f);

    private static Map<String, List<String>> spropListsMapAV = new ConcurrentMap<>();
    private static Map<String, List<String>> sparamListsMapAV = new ConcurrentMap<>();

    private static Map<String, List<PROPERTY>> propListsMap = new ConcurrentMap<>(
            400, 0.75f);
    private static Map<String, List<PARAMETER>> paramListsMap = new ConcurrentMap<>(400, 0.75f);
    private static Map<String, List<VALUE>> valueListsMap = new ConcurrentMap<>();
    private static Map<String, List<VALUE>> valueListsMapAV = new ConcurrentMap<>();

    private static Map<String, List<PROPERTY>> propListsMapAV = new ConcurrentMap<>();
    private static Map<String, List<PARAMETER>> paramListsMapAV = new ConcurrentMap<>();

    private static List<VALUE> lowPriorityValues = new ArrayList<>();

    private static List<String> highPriorityValues = new ArrayList<>();
    private static List<VALUE> superLowPriorityValues = new ArrayList<>();
    private static List<PARAMETER> attributes = new ArrayList<>();
    private static List<PARAMETER> masteries = new ArrayList<>();
    private static List<PARAMETER> masteryScores = new ArrayList<>();

    private static Map<String, List<VALUE>> AV_IgnoredValues;
    private static List<PARAMETER> unitParameters;
    private static ArrayList<PARAMETER> filteredUnitParameters;
    private static ArrayList<PARAMETER> charParameters;
    private static ArrayList<PARAMETER> filteredCharParameters;
    private static ArrayList<PARAMETER> perLevelParams = new ArrayList<>();
    private static Class<?>[] propEnumClasses;
    private static Class<?>[] paramEnumClasses;
    private static List<PARAMETER> finalAttributes;
    private static Map<String, PARAMETER> paramCache;// = new ConcurrentMap<>();
    private static Map<String, PROPERTY> propCache;// = new ConcurrentMap<>();
    private static List<VALUE> values;
    private static Set<VALUE> excludedValueSet;
    private static TypeMaster typeMaster;
    private static ContentManager instance;
    private Map<String, VALUE> commons;
    private Map<String, Map<String, VALUE>> maps;

    public ContentManager() {
        instance = this;
    }

    public static void init(ArrayList<PROPERTY> propz, ArrayList<PARAMETER> paramz) {

        props = propz;
        params = paramz;
        sparams = new ArrayList<>(params.size());
        sprops = new ArrayList<>(props.size());

        propCache = new HashMap<>(props.size()*3/2);
        for (PROPERTY p : props) {
            String name = p.getName();
            sprops.add(name);

            if (LOWER_CASE_CACHED)
                name = name.toLowerCase();
            propCache.put(name, p);


        }
        paramCache = new HashMap<>(params.size()*3/2);
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

    public static PARAMETER getCurrentParam(PARAMETER p) {
        if (p.name().startsWith(StringMaster.CURRENT)) {
            return p;
        }
        return getPARAM(StringMaster.CURRENT + p.getName(), true);
    }

    public static PARAMETER getBaseParameterFromCurrent(PARAMETER param) {
        if (!param.name().startsWith(StringMaster.CURRENT)) {
            return param;
        }
        return getPARAM(param.getFullName().replace(StringMaster.CURRENT, ""), true);
    }

    public static PARAMETER getReqParam(PARAMETER p) {
        return getPARAM(p.getName() + StringMaster.REQUIREMENT, true);
    }

    public static PARAMETER getFinalAttrFromBase(PARAMETER param) {
        return ContentManager.getPARAM(param.name().replace(StringMaster.BASE, ""));
    }

    public static PARAMETER getMasteryScore(PARAMETER mastery) {
        PARAMETER param = getPARAM(mastery.getName() + (StringMaster.SCORE));
        return param;
    }


    public static PARAMETER getRegenParam(PARAMETER p) {
        return getPARAM(p.getName() + StringMaster.REGEN, true);
    }

    public static PARAMETER getRandomUnitParameter() {
        if (unitParameters == null || filteredUnitParameters == null) {
            initUnitParams();
        }
        int i = RandomWizard.getRandomIntBetween(0, unitParameters.size());
        return filteredUnitParameters.get(i);
    }

    public static PARAMETER getRandomCharParameter() {
        if (charParameters == null || filteredCharParameters == null) {
            initCharParams();
        }
        int i = RandomWizard.getRandomIntBetween(0, charParameters.size());
        return filteredCharParameters.get(i);
    }

    private static void initCharParams() {
        charParameters = new ArrayList<>(getParamList().size());
        for (PARAMETER p : getParamList()) {
            if (p.getEntityType().equals(DC_TYPE.CHARS.getName())) {
                charParameters.add(p);
                continue;
            }
            if (Arrays.asList(p.getEntityTypes()).contains((DC_TYPE.CHARS.getName()))) {
                charParameters.add(p);
            }
        }

        filteredCharParameters = new ArrayList<>(charParameters.size());
        for (PARAMETER p : charParameters) {
            if (!p.isDynamic()) {
                if (!p.isLowPriority()) {
                    filteredCharParameters.add(p);
                }
            }
        }
    }

    private static void initUnitParams() {
        unitParameters = new ArrayList<>(getParamList().size());
        for (PARAMETER p : getParamList()) {
            if (p.getEntityType().equals(DC_TYPE.UNITS.getName())) {
                unitParameters.add(p);
                continue;
            }
            if (Arrays.asList(p.getEntityTypes()).contains((DC_TYPE.UNITS.getName()))) {
                unitParameters.add(p);
            }
        }

        filteredUnitParameters = new ArrayList<>(unitParameters.size());
        for (PARAMETER p : unitParameters) {
            if (!p.isDynamic()) {
                if (!p.isLowPriority()) {
                    filteredUnitParameters.add(p);
                }
            }
        }
    }

    public static PARAMETER getPercentageParam(PARAMETER p) {
        if (p.isDynamic() && p.name().startsWith(StringMaster.CURRENT)) {
            p = getBaseParameterFromCurrent(p);
        }
        return getPARAM(p.getName() + StringMaster.PERCENTAGE);
    }

    public static PARAMETER getPARAM(String valueName) {
        if (StringMaster.isEmpty(valueName)) {
            return null;
        }
        if (LOWER_CASE_CACHED)
            valueName = valueName.toLowerCase();
        PARAMETER param = paramCache.get(valueName);
        if (param == G_PARAMS.EMPTY_PARAMETER) {
            return null;
        }

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
        }

        paramCache.put(valueName, param);
        if (param == G_PARAMS.EMPTY_PARAMETER) {
            return null;
        }
        return param;
    }

    public static PARAMETER getPARAM(String valueName, boolean strict) {
        for (PARAMETER p : params) {
            if (StringMaster.compareByChar(valueName, p.toString(), strict)) {
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
        if (value == null) {
            for (VALUE v : param ? params : props) {
                if (StringMaster.compareByChar(valueName, v.toString().replace(" ", ""), false)) {
                    value = v;
                }
                break;
            }
        }
        if (value == null) {
            for (VALUE p : param ? params : props) {
                if (StringMaster.compare(valueName, p.toString().replace(" ", ""), true)) {
                    value = p;
                    break;
                }
            }
        }
        List<VALUE> valuesFound = new LinkedList<>();
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
                if (StringMaster.compare(valueName, p.toString(), strict)) {
                    return p;
                }
            }
        }
        return null;
    }

    public static PROPERTY getPROP(String valueName) {
        PROPERTY property = propCache.get(valueName);

        if (property != null) {
            if (property != G_PROPS.EMPTY_VALUE) {
                return property;
            }
        }
        property = getPROP(valueName, true);
        if (property == null) {
            property = getPROP(valueName, false);
        }

        if (property == null)

        {
            LogMaster.log(LogMaster.CORE_DEBUG, "PROPERTY NOT FOUND: "
                    + valueName + "!");
        }

        if (property == null) {
            property = G_PROPS.EMPTY_VALUE;
        }
        propCache.put(valueName, property);
        if (property == G_PROPS.EMPTY_VALUE) {
            return null;
        }
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

        v = getPROP(valueName, true );
        if (v == null) {
            v = getPARAM(valueName, true );
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
            if (excludedValueSet.contains(v)) {
                return true;
            }
        }
        return false;
    }

    public static List<PARAMETER> getParamList() {
        return params;
    }

    public static List<VALUE> getValueList() {
        if (values == null) {
            values = new LinkedList<>();
            for (PROPERTY p : getPropList()) {
                values.add(p);
            }
            for (PARAMETER p : getParamList()) {
                values.add(p);
            }
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
        return sprops.contains(StringMaster.getWellFormattedString(name));
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
        return sparams.contains(StringMaster.getWellFormattedString(name));
    }

    public static List<PARAMETER> getHeroStatsTabValueList() {
        List<PARAMETER> list = new LinkedList<>();
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
        paramList = new LinkedList<>();
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
        paramList = new LinkedList<>();
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
        propList = new LinkedList<>();

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
        propList = new LinkedList<>();
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
        propList = new LinkedList<>();
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
        valueNames = new LinkedList<>();
        appendLast(valueNames, getParamNames(objType, !av));
        // appendLast(valueNames, masteries);
        // appendFirst(valueNames, attributes);
        appendFirst(valueNames, getPropNames(objType, !av));
        // appendFirst(valueNames, highPriorityValues);
        appendLast(valueNames, lowPriorityValues, objType);
        appendLast(valueNames, superLowPriorityValues, objType);
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
        List<String> names = new LinkedList<>();
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

    public static Map<String, List<VALUE>> getAV_IgnoredValues() {
        return AV_IgnoredValues;
    }

    public static void setAV_IgnoredValues(Map<String, List<VALUE>> aV_IgnoredValues) {
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

    public static PARAMETER getSpellMasteryScoreForSpell(Entity type) {
        return getMasteryScore(type.getProperty(G_PROPS.SPELL_GROUP));
    }

    public static OBJ_TYPE getOBJ_TYPE(String typeName) {
        if (typeMaster != null) {
            return typeMaster.getOBJ_TYPE(typeName);
        }

        OBJ_TYPE type = DC_TYPE.getType(typeName);

        if (type == null || XML_Reader.isMacro()) {
            type = MACRO_OBJ_TYPES.getType(typeName);
        }

        return type;
    }

    public static TypeMaster getTypeMaster() {
        return typeMaster;
    }

    public static void setTypeMaster(TypeMaster typeMaster) {
        ContentManager.typeMaster = typeMaster;
    }

    public static int getTypeCode(String typeName) {

        return getOBJ_TYPE(typeName).getCode();
    }

    public static String getTypeImage(String tabName) {
        return getOBJ_TYPE(tabName).getImage();
    }

    public static Map<String, List<String>> getValueNamesMap() {
        return valueNamesMap;
    }

    public static void setValueNamesMap(Map<String, List<String>> valueNamesMap) {
        ContentManager.valueNamesMap = valueNamesMap;
    }

    public static Map<String, List<String>> getValueNamesMapAV() {
        return valueNamesMapAV;
    }

    public static void setValueNamesMapAV(Map<String, List<String>> valueNamesMapAV) {
        ContentManager.valueNamesMapAV = valueNamesMapAV;
    }

    public static PARAMETER getPerLevelValue(String string) {
        return getPARAM(string + StringMaster.PER_LEVEL);
    }

    public static boolean isValueForOBJ_TYPE(String type, VALUE p) {
        if (p.getEntityTypes() != null) {
            if (Arrays.asList(p.getEntityTypes()).contains((type))) {
                return true;
            }
        }
        if (p.getEntityType() == null) {
            return false;
        }
        if (p.getEntityType().equalsIgnoreCase("all")) {
            if (instance == null) {
                return true;
            }
            return instance.checkAllApplies(p, type);
        }

        return p.getEntityType().equals(type);
    }

    public static List<OBJ_TYPE> getOBJ_TYPEsForValue(VALUE value) {
        List<OBJ_TYPE> list = new LinkedList<>();
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
        ContentManager.perLevelParams = perLevelParams;
    }

    public static List<PARAMETER> getAttributes() {
        return attributes;
    }

    public static List<PARAMETER> getFinalAttributes() {
        if (finalAttributes == null) {
            finalAttributes = new LinkedList<>();
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
        ContentManager.propEnumClasses = propEnumClasses;
    }

    public static Class<?>[] getParamEnumClasses() {
        return paramEnumClasses;
    }

    public static void setParamEnumClasses(Class<?>[] paramEnumClasses) {
        ContentManager.paramEnumClasses = paramEnumClasses;
    }

    public static void setExcludedValueSet(Set<VALUE> set) {
        excludedValueSet = set;
    }

    public static boolean isBase(PARAMETER param) {
        return param.name().startsWith(StringMaster.BASE);
    }

    public static String getMasteryGroup(PARAMETER mastery) {
        return getMasteryGroup(mastery, "");
    }

    public static String getFormattedValue(VALUE v, String value) {
        return instance.getFormattedVal(v, value);
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

    public static ContentManager getInstance() {
        return instance;
    }

    public static void setInstance(ContentManager instance) {
        ContentManager.instance = instance;
    }

    public static String getCurrentOutOfTotal(PARAMETER value, Entity obj) {
        return obj.getValue(value) +
                "/" + obj.getValue(getBaseParameterFromCurrent(value));
    }

    public void init() {

    }

    public boolean checkAllApplies(VALUE p, String type) {
        return true;
    }

    public String getFormattedVal(VALUE v, String value) {
        return value;
    }

    public boolean isTextAlwaysShownInListItems(OBJ_TYPE TYPE) {
        return false;
    }

    public enum AV_EDITOR_TYPES {
        TYPE_LIST, ENUM_LIST, TEXT,;
    }

}
