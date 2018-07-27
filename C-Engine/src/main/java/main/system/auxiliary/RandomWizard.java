package main.system.auxiliary;

import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums.ROLL_TYPES;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.WeightMap;
import main.system.math.MathMaster;

import java.util.*;

public class RandomWizard<E> {
    public static final long seed = System.nanoTime();
    static Random randomGenerator = new Random(seed);
    private static boolean averaged;
    private LinkedHashMap<Integer, E> invertedMap;

    public static boolean isWeightMap(String property) {
        for (String string : ContainerUtils.open(property)) {
            try {
                if (StringMaster.getWeight(string) > 1) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static Map<ObjType, Integer> constructWeightMap(String property, OBJ_TYPE TYPE) {
        return new RandomWizard<ObjType>().constructWeightMap(property, ObjType.class, TYPE);
    }

    public static ObjType getObjTypeByWeight(String property, OBJ_TYPE TYPE) {
        return new RandomWizard<ObjType>().getObjectByWeight(constructWeightMap(property, TYPE));
    }

    public static int getRandomIntBetween(int i, int j, boolean inclusive) {
        return getRandomIntBetween(i, j, inclusive, randomGenerator);
    }

    public static int getRandomIntBetween(int i, int j, boolean inclusive, Random randomGenerator) {
        if (i > j && i > 0 && j > 0) {
            int buffer = i;
            i = j;
            j = buffer;
        }
        int n = j - i;
        if (n == 0) {
            return 0;
        }
        boolean negative = false;
        if (n < 0) {
            n = Math.abs(n);
            negative = true;
        }
        if (inclusive) {
            n++;
        }
        int k = (averaged) ? i + MathMaster.round(n / 2) : i + randomGenerator.nextInt(n);
        // main.system.auxiliary.LogMaster.log(1, "*** NEW RANDOM: " + k + "[" +
        // i + " - " + j + "], "
        // + randomGenerator);
        if (negative) {
            return -k;
        }
        return k;
    }

    public static boolean isAveraged() {
        return averaged;
    }

    public static void setAveraged(boolean averaged) {
        RandomWizard.averaged = averaged;
    }

    public static int getRandomIntBetween(int i, int j) {
        return getRandomIntBetween(i, j, randomGenerator);
    }

    // INCLUSIVE
    public static int getRandomIntBetween(int i, int j, Random randomGenerator) {
        return getRandomIntBetween(i, j, false, randomGenerator);
    }

    public static boolean chance(int i) {
        return chance(i, randomGenerator);
    }

    public static boolean chance(int i, Random random) {
        if (i >= 100) {
            return true;
        }
        if (i <= 0) {
            return false;
        }
        int res = getRandomIntBetween(0, 100, random);
        LogMaster.log(0, i + " chance hits " + res);
        return res < i;
    }

    public static int getRandomListIndex(Collection list) {
        boolean bool = averaged;
        averaged = false;
        int index = getRandomListIndex(list, randomGenerator);
        averaged = bool;
        return index;
    }

    public static int getRandomListIndex(Collection list, Random random) {
        if (list == null) {
            return -1;
        }
        if (list.isEmpty()) {
            return -1;
        }
        return getRandomIntBetween(0, list.size(), random);
    }

    public static int getRandomInt(int j) {
        if (j == 0) {
            return 0;
        }
        if (j < 0) {
            return -getRandomIntBetween(0, -j);
        }

        return getRandomIntBetween(0, j);
    }

    public static boolean random() {
        return random(randomGenerator);
    }

    public static boolean random(Random random) {
        return random.nextBoolean();
    }

    public static Integer rollDice(Integer dieNumber, Integer dieSize, List<Integer> dieList) {
        return initDice(dieNumber, dieSize, dieList, false);

    }

    public static Integer initAverageDice(Integer dieNumber, Integer dieSize, List<Integer> dieList) {
        return initDice(dieNumber, dieSize, dieList, true);
    }

    public static Integer initDice(Integer dieNumber, Integer dieSize, List<Integer> dieList,
                                   boolean average) {
        int max = Math.max(1, dieSize);
        int min = dieNumber;
        int result = average ? (min + max) / 2 : getRandomIntBetween(min, max, true);
        initDice(dieList, result, dieNumber, dieSize);
        return result;
    }

    private static void initDice(List<Integer> dieList, int result, Integer dieNumber,
                                 Integer dieSize) {
        for (int n = 0; n < dieNumber; n++) {
            int die = Math.min(result, getRandomIntBetween(1, dieSize, true));
            result -= die;
            dieList.add(die);
        }

    }

    public static boolean roll(Integer greater, Integer than) {

        return getRandomInt(greater) > getRandomInt(than);
    }

    public static boolean roll(ROLL_TYPES roll_type, int greater, int than, Ref ref) {

        int randomInt = getRandomInt(greater);
        int randomInt2 = getRandomInt(than);
        ref.getGame().getLogManager().logStdRoll(ref, greater, randomInt, than, randomInt2,
         roll_type);
        return randomInt > randomInt2;
    }

    public static String getWeightStringItem(String string, String value) {
        return string + StringMaster.wrapInParenthesis(value.toString()) + ";";
    }

    public static ObjType getRandomType(OBJ_TYPE TYPE) {
        return getRandomType(TYPE, null, false);
    }

    public static ObjType getRandomType(OBJ_TYPE TYPE, String group, boolean subgroup) {
        List<ObjType> types;
        if (subgroup) {
            types = DataManager.getTypesSubGroup(TYPE, group);
        } else {
            types = DataManager.getTypesGroup(TYPE, group);
        }
        if (types == null) {
            types = DataManager.getTypes(TYPE);
        }

        return types.get(getRandomListIndex(types));

    }

    public static Random getRandomGenerator() {
        return randomGenerator;
    }

    public static void setRandomGenerator(Random randomGenerator) {
        RandomWizard.randomGenerator = randomGenerator;
    }

    public static Object getRandomListObject(List list) {
        return list.get(getRandomListIndex(list));
    }

    public static float getRandomFloat() {
        return getRandomFloatBetween(0, 1f);
    }

    public static float getRandomFloatBetween(float alphaMin, float alphaMax) {
        return new Float(getRandomIntBetween((int) (alphaMin * 100),
         (int) (alphaMax * 100))) / 100;
    }

    public E getObjectByWeight(String string, Class<? extends E> CLASS) {
        return getObjectByWeight(constructWeightMap(string, CLASS));
    }

    public E getRandomListItem(List<E> list) {
        return list.get(getRandomListIndex(list));
    }

    public E getObjectByWeight(Map<E, Integer> map) {
        Integer total_weight = 0;
        for (E group : map.keySet()) {
            total_weight += map.get(group);
        }
        E chosenObject = null;
        int random = RandomWizard.getRandomInt(total_weight);
        int index = 0;
        for (E group : map.keySet()) {
            index += map.get(group);
            if (random < index) {
                chosenObject = group;
                break;
            }
        }
        return chosenObject;
    }

    public Map<E, Integer> constructWeightMap(String property, Class<? extends E> CLASS) {
        return constructWeightMap(property, CLASS, null);
    }

    public LinkedHashMap<String, E> constructStringWeightMapInversed(String property,
                                                                     Class<? extends E> CLASS) {
        LinkedHashMap<String, E> map = new LinkedHashMap<>();
        for (String string : ContainerUtils.open(property)) {
            String value = "";
            try {
                value = StringMaster.cropParenthesises(VariableManager.getVarPartLast(string));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            E object = new EnumMaster<E>().retrieveEnumConst(CLASS, value);
            string = (string).replace(StringMaster.wrapInParenthesis(value), "");
            if (object != null) {
                map.put(string, object);
            }
        }

        return map;
    }

    public Map<E, String> constructStringWeightMap(String property, Class<? extends E> CLASS) {
        LinkedHashMap<E, String> map = new LinkedHashMap<>();
        for (String string : ContainerUtils.open(property)) {

            String value = "";
            try {
                value = VariableManager.removeVarPart(string);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            E object = new EnumMaster<E>().retrieveEnumConst(CLASS, value);
            string = StringMaster.cropParenthesises(VariableManager.getVarPartLast(string));
            if (object != null) {
                map.put(object, string);
            }
        }

        return map;
    }

    public WeightMap<E> constructWeightMap(String property, Class<? extends E> CLASS, OBJ_TYPE TYPE) {
        return constructWeightMap(property, CLASS, TYPE, false);
    }

    public WeightMap<E> constructWeightMap(String property, Class<? extends E> CLASS,
                                           OBJ_TYPE TYPE, boolean inverse) {
        WeightMap<E> map = new WeightMap<>();
        if (inverse) {
            invertedMap = new LinkedHashMap<>();
        }
        for (String string : ContainerUtils.open(property)) {
            Integer value = 0;
            try {
                value = StringMaster.getWeight(string, inverse);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (value <= 0) {
                value = 1;
            }
            string = StringMaster.getWeightItem(string, inverse);
            E object;

            if (CLASS != ObjType.class) {
                object = new EnumMaster<E>().retrieveEnumConst(CLASS, string);
            } else {
                object = (E) DataManager.getType(string, TYPE);
                if (object == null) {
                    object = (E) DataManager.findType(string, TYPE);
                }
            }
            // if (object != null) //EMPTY option allowed!
            map.put(object, value);
            if (inverse) {
                invertedMap.put(value, object);
            }
        }

        return map;
    }

    public LinkedHashMap<Integer, E> getInvertedMap() {
        return invertedMap;
    }

    public E getRandomEnumConst(Class<E> CLASS) {
        return CLASS.getEnumConstants()[getRandomInt(CLASS.getEnumConstants().length)];
    }

    public E getRandomArrayItem(E[] exits) {
        int i = getRandomInt(exits.length);
        return exits[i];
    }
}
