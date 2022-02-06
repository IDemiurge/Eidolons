package main.data.ability;

import main.ability.Abilities;
import main.ability.ActiveAbility;
import main.ability.PassiveAbility;
import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS;
import main.content.values.parameters.Param;
import main.content.values.properties.Prop;
import main.data.dialogue.SpeechData;
import main.data.dialogue.Speeches;
import main.data.xml.XmlNodeMaster;
import main.elements.conditions.Conditions;
import main.system.auxiliary.ClassFinder;
import main.system.auxiliary.Loop;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import org.w3c.dom.Node;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Mapper class makes a map of all public constructors without
 *
 * @ommittedConstructor in elements/... and ability/... folders of C-Engine and Dueling-Club.
 * [known to fail if full path contains Russian letters!]
 */
public class Mapper {

    public static final Class<?>[] CONTAINER_CLASSES = {
            Abilities.class, Effects.class,
            Conditions.class,
            Speeches.class, SpeechData.class,
    };
    public static final String ABILITIES = "Abilities";
    private final static String[] ignoredPaths = {""};
    private final static Class<?>[] IGNORED_CLASSES = {EffectImpl.class, Effect.class};
    private static final Class<?>[] SPECIAL_CLASSES =
            {Abilities.class, PassiveAbility.class,
                    ActiveAbility.class, Param.class, Prop.class,
//      Speeches.class, SpeechData.class, DataString.class,

            };
    private static final String TEXT_NODE = "#text";
    private static final String ARG_LIST_SEPARATOR = ": ";
    static Map<String, Map<List<Argument>, AE_Item>> caches = new HashMap<>();


    private static final Map<Argument, List<AE_Item>> map = new HashMap<>();
    private static final Map<String, AE_Item> itemMap = new HashMap<>();
    private static final Map<ARGS, AE_Item> primitiveItems = new HashMap<>();
    private static List<Argument> args;
    private static boolean initialized;

    public static AE_Item getItem(String itemName, Class<?>[] parameterTypes) {
        return getItem(itemName, getArgList(parameterTypes));
    }

    public static AE_Item getItem(String itemName, List<Argument> argList) {
        Map<List<Argument>, AE_Item> itemCache = caches.get(itemName);
        AE_Item item = itemCache == null ? null : itemCache.get(argList);
        if (item != null)
            return item;
        item = itemMap.get(itemName);
        if (item == null) {
            LogMaster.log(1, "*** No item for " + itemName);
            return null;
        }
        if (item.getArgList().equals(argList)) {
            return item;
        } else {
            if (item.isContainer()) {
                return item;
            }

//subMap = subMaps.get(itemName);
            for (String key : itemMap.keySet()) {
                if (key.contains(ARG_LIST_SEPARATOR)) {

                    String substring = key.substring(0, key.indexOf(ARG_LIST_SEPARATOR));
                    if (StringMaster.compareByChar(substring, itemName, true)) {
                        item = itemMap.get(key);
                        if (itemMap.get(key).getArgList().equals(argList)) {
                            {
                                caches.put(itemName, itemCache);
                                if (itemCache == null)
                                    itemCache = new HashMap<>();
                                itemCache.put(argList, item);
                                return itemMap.get(key);
                            }
                        }
                    }
                }

            }
        }

        return item;

    }

    public static AE_Item getItem(String itemName) {
        try {
            return itemMap.get(itemName);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return null;
        }
    }

    public static AE_Item getItem(Node e) {
        if (!e.hasChildNodes()) {
            return getItem(e.getNodeName());
        }
        if (e.getFirstChild().getNodeName().equals(TEXT_NODE)) {
            return getItem(e.getNodeName());
        }

        return getItem(e.getNodeName(), getArgs(XmlNodeMaster.getNodeList(e)));
    }

    private static List<Argument> getArgList(Class<?>[] parameterTypes) {
        List<Argument> list = new ArrayList<>();
        for (Class<?> type : parameterTypes) {
            list.add(translateToArg(type));
        }
        return list;
    }

    private static List<Argument> getArgs(List<Node> list2) {
        List<Argument> list = new ArrayList<>();
        for (Node node : list2) {
            list.add(translateToArg(node.getNodeName()));
        }
        return list;
    }

    private static void constructENUM_AE_Items(List<Argument> args) {
        for (Argument arg : args) {
            if (arg.isENUM()) {
                initEnumItem(arg);
            }
        }
        // TODO add CONTENT_CONSTS!!!

    }

    private static void compileContentConsts() {
        for (Class<?> CLASS : CONTENT_CONSTS.class.getDeclaredClasses()) {
            if (CLASS.isEnum()) {
                for (Object o : CLASS.getEnumConstants()) {
                    // TODO TODO TODO
                }
            }
        }

    }

    private static void initEnumItem(Argument arg) { // TODO
        AE_Item item = new AE_Item(arg.name(), arg, null, arg.getCoreClass(), false);
        itemMap.put(arg.name(), item);
        map.get(arg).add(item);
    }

    private static void compilePrimitives() {

        initPrimitiveItem(ARGS.BOOLEAN);
        initPrimitiveItem(ARGS.STRING);
        initPrimitiveItem(ARGS.FORMULA);
        initPrimitiveItem(ARGS.INTEGER);

    }

    private static void initPrimitiveItem(ARGS arg) {
        AE_Item item = new AE_Item(arg.name(), arg, null, arg.getCoreClass(), false);
        itemMap.put(arg.name(), item);
        primitiveItems.put(arg, item);
        // map.get(arg).add(item);
    }

    private static void compileEnumArgs() {
        Class<?>[] classes = CONTENT_CONSTS.class.getDeclaredClasses();
        for (Class<?> c : classes) { // this does not cover *everything*
            // though...
            Argument a = new ArgumentImpl(c, c.getSimpleName()); // format?
            List<AE_Item> list = new ArrayList<>();
            if (!map.containsKey(a)) {
                map.put(a, list);
            }
        }
    }

    private static void initEnumItem(ARGS arg) {
        AE_Item item = new AE_Item(arg.name(), arg, null, arg.getCoreClass(), false);
        itemMap.put(arg.name(), item);
        primitiveItems.put(arg, item);
        // map.get(arg).add(item);
    }

    public static void compileArgMap(List<Argument> args1, List<String> classFolders1)
            throws ClassNotFoundException, SecurityException, IOException {
        main.system.auxiliary.log.LogMaster.log(1, "Mapper: compile Arg Map for " + classFolders1);
        args = args1;
        for (Argument arg : args) {
            List<AE_Item> list = new ArrayList<>();
            map.put(arg, list);
        }
        compilePrimitives();
        compileEnumArgs();
        // compileContentConsts();

        ClassFinder.setIgnoredPaths(ignoredPaths);
        for (String packageName : classFolders1) {
            for (Class<?> CLASS : ClassFinder.getClasses(packageName)) {
                if (CLASS == null) {
                    LogMaster.log(1, "null class in " + packageName + "!");

                }
                if (!constructAE_Item(CLASS)) {
                    //Core Review
                    // LogMaster.system.log(1, CLASS + " in " + "" + packageName
                    //         + " failed to construct!");
                }
            }
        }
        for (Class<?> CLASS : SPECIAL_CLASSES) {

            constructAE_Item(CLASS);
        }

        constructENUM_AE_Items(args);
        if (CoreEngine.isArcaneVault())
            sortLists();
        LogMaster.log(LogMaster.CORE_DEBUG, "ARG MAP: \n" + map);
        LogMaster.log(LogMaster.CORE_DEBUG, "ITEM MAP: \n" + itemMap);
        initialized = true;
    }

    private static void sortLists() {
        for (List<AE_Item> list : map.values()) {
//            LogMaster.system.log(0, list + "");
            Collections.sort(list);
//            LogMaster.system.log(0, list + "");
        }
    }

    private static boolean checkClass(Class<?> CLASS) {
        return !Arrays.asList(IGNORED_CLASSES).contains(CLASS);
    }

    private static boolean constructAE_Item(Class<?> CLASS) {

        if (!checkClass(CLASS)) {
            return false;
        }
        String name = CLASS.getSimpleName();
        // create an item for each constructor
        try {
            for (Constructor<?> constr : CLASS.getConstructors()) {
                if (constr.getAnnotation(OmittedConstructor.class) != null) {
                    continue;
                }
                Argument mappedArg = translateToArg(CLASS);

                boolean container = Arrays.asList(CONTAINER_CLASSES).contains(CLASS)
                        || mappedArg.isContainer();
                if (container) {
                    if (itemMap.containsKey(name)) {
                        continue;
                    }
                }
                List<Argument> argList = new ArrayList<>();

                for (Class<?> argType : constr.getParameterTypes()) {
                    argList.add(translateToArg(argType));
                }

                AE_Item item = new AE_Item(name, mappedArg, argList, CLASS, container, constr);
                Map<List<Argument>, AE_Item> itemCache = caches.get(name);
                if (itemCache == null) {
                    itemCache = new HashMap<>();
                    caches.put(name, itemCache);
                }
                itemCache.put(argList, item);


                if (!itemMap.containsKey(name)) {
                    itemMap.put(name, item);
                } else {
                    // additional_item_list.add(item); TODO
                    itemMap.put(name + ARG_LIST_SEPARATOR + item.getArgList(), item);
                }

                mappedArg = translateToArg(CLASS
                        // .getSuperclass()
                );
                if (!(map.get(mappedArg).contains(name))) {
                    map.get(mappedArg).add(item);
                }

            }
        } catch (Exception e) {
            LogMaster.log(1, CLASS.toString()
                    + " failed to construct into item map");
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return true;
    }


    public static String getArgName(Class key) {
        for (Argument sub : args) {
            if (sub.getCoreClass().equals(key)) {
                return sub.name();
            }
        }
        Argument arg = translateToArg(key);
        if (arg != null)
            return arg.toString();
//        Class<?> core = translateToCoreClass(key.getSimpleName());
//        for (Argument sub : args) {
//            if (sub.getCoreClass().equals(core)) {
//                return sub.name();
//            }
//        }
        return key.getSimpleName();
    }

    public static Class<?> translateToCoreClass(String className) {
        return translateToArg(getMappedClass(className)).getCoreClass();
    }

    public static Argument translateToArg(String className) {
        return translateToArg(getMappedClass(className));
    }

    public static Argument translateToArg(Class<?> CLASS) {
        if (CLASS == null) {
            return ARGS.UNKNOWN;
        }
        for (Argument arg : args) {
            if (arg.getCoreClass() == CLASS) {
                return arg;
            }
        }
        for (Argument arg : args) {
            if (Arrays.asList(CLASS.getInterfaces()).contains(arg.getCoreClass())) {
                return arg;
            }
            Class<?> sc = CLASS.getSuperclass();
            Loop.startLoop(10);
            while (sc != Object.class && sc != null) {
                if (Loop.loopEnded()) {
                    break;
                }
                if (sc.equals(arg.getCoreClass())
                        || Arrays.asList(sc.getInterfaces()).contains(arg.getCoreClass())) {
                    return arg;
                } else {
                    sc = sc.getSuperclass();
                }
            }
        }
        return ARGS.UNKNOWN;
    }

    public static List<AE_Item> getItemList(Argument arg) {
        return map.get(arg);
    }

    public static Class<?> getMappedClass(String className) {

        AE_Item item = itemMap.get(className);
        if (item == null) {
            LogMaster.log(1, className);
            return null;
        }

        Class<?> CLASS = item.getConcreteClass();
        return CLASS;
    }

    public static AE_Item getPrimitiveItem(Argument arg) {
        return primitiveItems.get(arg);
    }

    public static void addEnumConstItem(AE_Item item) {

        itemMap.put(item.getName(), item);
        map.get(item.getArg()).add(item);
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
