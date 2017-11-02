package main.data.ability.construct;

import main.ability.AbilityType;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;
import main.system.math.Property;
import main.system.text.TextParser;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VariableManager {

    public static final Class<?> STRING_VAR_CLASS = String.class;

    public static final char VAR_CHAR = '(';
    public static final String PROP_VAR_CLASS = VARIABLE_TYPES.PROP_VALUE.toString();
    public static final String PARAM_VAR_CLASS = VARIABLE_TYPES.PARAM.toString();
    public static final String NUMBER_VAR_CLASS = VARIABLE_TYPES.NUMBER.toString();
    public static final String BOOLEAN_VAR_CLASS = VARIABLE_TYPES.BOOLEAN.toString();
    public static final String PROP_VALUE_VAR_CLASS = VARIABLE_TYPES.PROP.toString();

    public static final String VARIABLE = StringMaster.VAR_STRING;
    private static boolean variableInputRequesting = false;
    private static List<ObjType> varCache = new LinkedList<>();
    private static String prevValue;

    public static AbilityType getVarType(String abilTypeName, boolean passive, Ref ref) {
        if (passive) {
            abilTypeName = TextParser.parse(abilTypeName, ref, TextParser.ABILITY_PARSING_CODE,
                    TextParser.VARIABLE_PARSING_CODE);
        } else {
            abilTypeName = TextParser.parse(abilTypeName, ref, TextParser.ACTIVE_PARSING_CODE,
                    TextParser.VARIABLE_PARSING_CODE, TextParser.ABILITY_PARSING_CODE);
        }
        return getVarType(abilTypeName);
    }

    public static AbilityType getVarType(String abilTypeName) {

        String vars = getVarPart(abilTypeName);
        abilTypeName = abilTypeName.replace(vars, "");
        AbilityType type = (AbilityType) DataManager.getType(abilTypeName, DC_TYPE.ABILS);
        if (type == null) {
            return null;
        }
        if (vars == "") {
            return type;
        }
        AbilityType newType = new AbilityType(type);
        type.initType();
        setAbilityVariables(newType, vars);
        // type.setVariables(varList);
        return newType;
    }

    private static void setAbilityVariables(AbilityType newType, String vars) {
        // TODO {1}
        List<String> varList = getVarList(vars);
        String xml = newType.getProperty(G_PROPS.ABILITIES);
        XML_Converter.getStringFromXML(newType.getDoc());
        String varProp = "";
        String lastVar = null;
        for (String var : varList) {
            var = var.replace(StringMaster.COMMA_CODE, StringMaster.getVarSeparator());

            lastVar = var;
            varProp += var + StringMaster.CONTAINER_SEPARATOR;
            if (!xml.contains(StringMaster.VAR_STRING)) {
                // parsedVars +=var + StringMaster.CONTAINER_SEPARATOR; TODO
                /*
                 * if I want {1} to serve in multiple places for an Ability, I
				 * will need another Property This property will 1) work in
				 * parallel to Variables? 2) wait, we already are setting
				 * Variables to the *parsed*, the question is how does
				 * TextParser use this? Perhaps I need to run another
				 * replaceCycle on XML here - one that will *parse* references!
				 */
            } else {
                xml = StringMaster.replaceFirst(xml, StringMaster.VAR_STRING, var);
            }
        }

        xml = StringMaster.replace(true, xml, StringMaster.VAR_STRING, lastVar);
        newType.setProperty(G_PROPS.VARIABLES, varProp);
        if (TextParser.checkHasVarRefs(xml)) {
            xml = TextParser.parseXmlVarRefs(newType, xml);
            if (!TextParser.checkHasVarRefs(xml)) {
                newType.setProperty(G_PROPS.ABILITIES, xml);
            }
        }

        Node doc = XML_Converter.getDoc(xml);
        if (!doc.getNodeName().contains("Abil")) {
            doc = doc.getFirstChild();
        }
        newType.setDoc(doc);
        newType.setProperty(G_PROPS.ABILITIES, xml);

    }

    public static List<String> getVarList(String vars) {
        vars = getVarPart(vars);
        vars = StringMaster.cropParenthesises(vars);
        List<String> varList = new ArrayList<>();

        for (String var : vars.split(StringMaster.getVarSeparator())) {
            varList.add(var);
        }
        return varList;
    }

    public static String getVar(String typeName) {
        return StringMaster.cropParenthesises(getVarPart(typeName));
    }

    public static String getVarPart(String typeName) {
        int index = typeName.indexOf(VAR_CHAR);
        if (index == -1) {
            return "";
        }
        return typeName.substring(index);

    }

    public static String getVarPartLast(String typeName) {
        int index = typeName.lastIndexOf(VAR_CHAR);
        if (index == -1) {
            return "";
        }
        return typeName.substring(index);
    }

    public static String getVarsFromXML(String xml, ObjType type) {
        // if (!variableInputRequesting)
        // return null;
        String result = "";
        int index = xml.indexOf(StringMaster.VAR_STRING);
        int i = 1;
        boolean manualVars = variableInputRequesting;
        if (!manualVars) {
            if (isVariablesSet(type)) {
                return getTypeVariables(type);
            }
        }
        while (index != -1) {
            xml = xml.substring(0, index) + xml.substring(index + StringMaster.VAR_STRING.length());
            String input = null;
            if (manualVars) {
                input =

                        JOptionPane.showInputDialog(type.getName()
                                + " "
                                + xml.substring(index + StringMaster.VAR_STRING.length() - 1, xml.indexOf(
                                ">", index + StringMaster.VAR_STRING.length())) + " variable at #"

                                +

                                i);

            }
            if (input == null) {
                manualVars = false;
                input = "VAR" + i;

            }
            i++;
            index = xml.indexOf(StringMaster.VAR_STRING);
            result += input + StringMaster.getSeparator();

        }
        if (manualVars) {
            varCache.add(type);
        }
        return result;
    }

    private static String getTypeVariables(ObjType type) {
        return type.getProperty(G_PROPS.VARIABLES);
    }

    public static void setAbilityVars(ObjType type, String newXml) {
        type.setProperty(G_PROPS.VARIABLES, VariableManager.getVarsFromXML(newXml, type));

    }

    public static boolean isVariablesSet(ObjType type) {
        return (!StringMaster.isEmpty(getTypeVariables(type)) || varCache.contains(type));
    }

    public static boolean isVariableInputRequesting() {
        return variableInputRequesting;
    }

    public static void setVariableInputRequesting(boolean b) {
        VariableManager.variableInputRequesting = b;

    }

    public static String removeVarPart(String value) {
        return value.replace(getVarPart(value), "");
    }

    public static Object getAutoVarValue(String varName,
                                         Entity entity, String stringArg) {
        AUTOVAR VAR = getAutoVarType(varName);
        return VAR.evaluate(entity, stringArg);

    }

    private static AUTOVAR getAutoVarType(String string) {
        return new EnumMaster<AUTOVAR>().retrieveEnumConst(AUTOVAR.class, string);
    }

    public static String promptVariablesInput(VarHolder holder) {
        String result = null; // TODO
        return result;
    }

    private static String promptStringInput(String var) {
        return JOptionPane.showInputDialog("Set " + " value for variable (" + var + " )");
    }

    public static synchronized String promptInputForVariables(String variables,
                                                              List<Object> varTypes) {
        String varPart = "(";
        int i = 0;
        for (String var : StringMaster.open(variables)) {
            String value;
            if (varTypes == null) {
                value = promptStringInput(var);
            } else {
                VARIABLE_TYPES varType = getVarType(varTypes.get(i));
                value = promptVarTypeInput(var, varType, varTypes.get(i));
                if (value == null) {
                    return null;
                }
                value = value.replace(StringMaster.getContainerSeparator(), StringMaster
                        .getVarSeparator());
                prevValue = value;
            }

            if (value == null) {
                return null;
            }
            varPart += value + ",";
            i++;
        }
        varPart = varPart.substring(0, varPart.length() - 1);
        varPart += ")";
        return varPart;
    }

    private static String promptVarTypeInput(String var, VARIABLE_TYPES varType, Object value) {
        switch (varType) {
            case BOOLEAN:
                return promptBoolInput(var);
            case ENUM:
                return promptEnumInput(value, var);
            case NUMBER:
                return promptStringInput(var + "(number)"); // TODO
            case PARAM:
                return promptParamInput(var);
            case PROP:
                return promptPropInput(var);
            case STRING:
                return promptStringInput(var);
            case TYPE:
                return ListChooser.chooseType((OBJ_TYPE) value);
            case PROP_VALUE: {
                String result = promptEnumInput(getPreviousValue(), var);
                if (result == null) {
                    return promptStringInput(var);
                }
                return result;
            }
            default:
                break;
        }
        return null;
    }

    private static String promptBoolInput(String var) {
        if (JOptionPane.showConfirmDialog(null, var + " - Y/N?", "input boolean",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            return Boolean.TRUE.toString();
        }
        return Boolean.FALSE.toString();
    }

    private static Object getPreviousValue() {
        return prevValue;
    }

    private static String promptPropInput(String var) {
        return ListChooser.chooseEnum(ContentManager.getPropEnumClasses());
    }

    private static String promptParamInput(String var) {
        return ListChooser.chooseEnum(ContentManager.getParamEnumClasses());
    }

    private static String promptEnumInput(Object value, String var) {
        if (value instanceof String) {
            value = EnumMaster.getEnumClass(value.toString());
            if (value == null) {
                return null;
            }
        }
        return ListChooser.chooseEnum((Class<?>) value);
    }

    private static VARIABLE_TYPES getVarType(Object object) {
        if (object instanceof OBJ_TYPE) {
            return VARIABLE_TYPES.TYPE;
        }
        if (object instanceof String) {
            if (StringMaster.compare((String) object, PROP_VALUE_VAR_CLASS, true)) {
                return VARIABLE_TYPES.PROP_VALUE;
            }
            if (StringMaster.compare((String) object, NUMBER_VAR_CLASS, true)) {
                return VARIABLE_TYPES.NUMBER;
            }
            if (StringMaster.compare((String) object, PROP_VAR_CLASS, true)) {
                return VARIABLE_TYPES.PROP;
            }
            if (StringMaster.compare((String) object, PARAM_VAR_CLASS, true)) {
                return VARIABLE_TYPES.PARAM;
            }
        }
        if (object instanceof Class<?>) {
            if (((Class<?>) object).isEnum()) {
                return VARIABLE_TYPES.ENUM;
            }
        }

        return VARIABLE_TYPES.STRING;
    }

    public static String getVarText(String text, Object[] variables) {
        return getVarText(text, false, true, variables);
    }

    public static String getVarText(String text, boolean startAtIndexOne, boolean removeEmptyVars,
                                    Object[] variables) {
        String result = text;
        int i = startAtIndexOne ? 1 : 0;
        for (Object var : variables) {
            result = result.replace(getVarIndex(i), var.toString());
            i++;
        }
        while (i < 10 && result.contains(StringMaster.FORMULA_REF_OPEN_CHAR)) {
            result = result.replace(getVarIndex(i), "").trim();
            i++;
        }
        return result;
    }

    public static String getVarIndex(int i) {
        return "{" + i + "}";
    }

    public static Object[] getVariables(String string) {
        return getVarList(string).toArray();

    }

    public static boolean checkVar(String subString) {
        return getVarPart(subString).length() > 2;
    }

    public static String getVarString(String string, String arg) {
        return string.replaceFirst(VARIABLE, arg);
    }

    public static String getVars(String varString) {
        return StringMaster.cropParenthesises(getVarPart(varString));

    }

    public enum AUTOVAR {
        // at construction time or runtime?
        // ++ random attribute
        // ++ corresponding mastery
        //

        SUMMON_ENERGY_COST {
            public Object evaluate(Entity obj, String s) {
                return obj.getGame().getMathManager().evaluateSummonEnergyCost(obj, s);
            }
        },

        CLAIM_COUNTERS_OUTPUT {
            public Object evaluate(Entity obj, String s) {
                return obj.getGame().getMathManager().evaluateClaimOutput((Obj) obj, s);
            }
        },
        CLAIM_COUNTERS_THRESHOLD {
            public Object evaluate(Entity obj, String s) {
                return obj.getGame().getMathManager().evaluateClaimThreshold((Obj) obj, s);
            }
        },
        MEDITATION {
            public Object evaluate(Entity obj, String s) {
                return obj.getGame().getMathManager().evaluateMeditation((Obj) obj);
            }
        },
        CONCENTRATION {
            public Object evaluate(Entity obj, String s) {
                return obj.getGame().getMathManager().evaluateConcentration((Obj) obj);
            }
        },
        REST {
            public Object evaluate(Entity obj, String s) {
                return obj.getGame().getMathManager().evaluateRest((Obj) obj);
            }
        },
        MASTERY {
            public Object evaluate(Entity obj, String s) {
                Entity entity = obj.getRef().getInfoEntity();
                if (entity == null) {
                    entity = obj.getRef().getEntity(KEYS.SKILL);
                    if (entity == null||obj.getGame().isSimulation()) {
                        entity = obj.getRef().getEntity(KEYS.ACTIVE);
                        if (entity == null) {
                            entity = obj.getRef().getEntity(KEYS.INFO);
                        }
                    }
                }
                PARAMETER param;
                if (entity==null )
                    return 0;
                if (entity.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS) {
                    param = ContentManager.findMasteryScore(entity.getProperty(G_PROPS.MASTERY));
                } else {

                    param = ContentManager
                            .findMasteryScore(entity.getProperty(G_PROPS.SPELL_GROUP));
                    if (param == null) {
                        param = ContentManager
                                .findMasteryScore(entity.getProperty(G_PROPS.MASTERY));
                    }
                }
                return obj.getRef().getSourceObj().getIntParam(param);
            }
        },
        RANDOM_ATTRIBUTE {
            public Object evaluate(Entity obj, String s) {
                int index = RandomWizard.getRandomListIndex(ContentManager.getAttributes());
                return ContentManager.getAttributes().get(index).getName();
            }
        },
        RANDOM_PARAMETER {
            public Object evaluate(Entity obj, String s) {
                if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.CHARS) {
                    return ContentManager.getRandomCharParameter().getName();
                } else {
                    return ContentManager.getRandomUnitParameter().getName();
                }

            }
        },
        RANDOM_UNIT_PARAMETER {
            public Object evaluate(Entity obj, String s) {
                return obj.getIntParam(ContentManager.getRandomUnitParameter());

            }
        },
        RANDOM_CHAR_PARAMETER {
            public Object evaluate(Entity obj, String s) {
                return obj.getIntParam(ContentManager.getRandomCharParameter());

            }
        },
        DAMAGE_TYPE,
        TOP_DEAD_UNIT_INT {
            public Object evaluate(Entity obj, String s) {
                Ref ref = obj.getRef();
                Ref REF = ref.getGame().getGraveyardManager().getTopDeadUnit(
                        ref.getSourceObj().getCoordinates()).getRef();
                return new Formula(s).getInt(REF);
            }
        },
        TOP_DEAD_UNIT_PROP {
            public Object evaluate(Entity obj, String s) {
                Ref ref = obj.getRef();
                Ref REF = ref.getGame().getGraveyardManager().getTopDeadUnit(
                        ref.getSourceObj().getCoordinates()).getRef();
                return new Property("SOURCE", s).getStr(REF);
            }
        },;

        AUTOVAR() {

        }

        public Object evaluate(Entity entity, String f) {
            return null;

        }
    }

    public enum VARIABLE_TYPES {
        BOOLEAN, NUMBER, STRING, ENUM, PROP, PARAM, TYPE, PROP_VALUE
    }
}
