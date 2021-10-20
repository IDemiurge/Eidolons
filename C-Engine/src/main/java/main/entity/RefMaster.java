package main.entity;

/**
 * Created by JustMe on 2/17/2017.
 */
public class RefMaster {

//    public static Ref getSelfTargetingRefNew(Obj obj) {
//        Ref REF = new Ref(obj.getGame(), obj.getRef().getSource());
//        REF.setTarget(obj.getId());
//        return REF;
//    }
//    public Object clone() {
//        Ref ref = new Ref();
//        ref.cloneMaps(this);
//        ref.setPlayer(player);
//        ref.setEvent(event);
//        ref.setGroup(group);
//        ref.setBase(base);
//        ref.setGame(game);
//        ref.setEffect(effect);
//        ref.setValue(value);
//        ref.setPeriodic(periodic);
//        ref.setTriggered(triggered);
//        ref.setDebug(debug);
//        ref.setAnimationActive(animationActive);
//        // ref.setAmount(getAmount());
//        // if (refClones.size() % 100 == 5)
//        // main.system.auxiliary.LogMaster.src.main.system.log(1, " " + refClones.size());
//        // refClones.add(ref);
//        return ref;
//    }
//
//    protected void cloneMaps(Ref ref) {
//        // no deep copy required here
//        values = new HashMap<>(ref.getValues());
//    }
//
//
//
//    protected String formatKeyString(String key) {
//        // return key;
//        return key.toUpperCase();
//        // [OPTIMIZED]
//    }
//    protected Ref checkForRefReplacement() {
//        String s = getStr();
//        if (s.startsWith("{")) {
//            s = s.replaceFirst("{", "");
//        }
//        if (StringMaster.compareByChar(StringMaster.getSegment(0, s, "_"), "EVENT", true)) {
//            // setStr(getStr().replace(EVENT_PREFIX, "")); [OPTIMIZED]
//            setStr(StringMaster.cropFirstSegment(getStr(), "_").replace("}", ""));
//            return getEvent().getRef();
//        }
//
//        // if (StringMaster.compare(str, MATCH_PREFIX)) {
//        // if (getStr().contains(MATCH_PREFIX)) {
//        // setStr(getStr().replace(MATCH_PREFIX, ""));
//        // return game.getObjectById(getMatch()).getRef();
//        // }
//        for (REPLACING_KEYS key : REPLACING_KEYS.values()) {
//            String prefix = key.name() + StringMaster.FORMULA_REF_SEPARATOR;
//            if (getStr().contains(prefix)) {
//                setStr(getStr().replace(prefix, ""));
//                try {
//                    return game.getObjectById(Integer.valueOf(getValue(key.name()))).getRef();
//                } catch (Exception e) {
//                    LogMaster.src.main.system.log(1, prefix + " + " + getStr());
//                    main.system.ExceptionMaster.printStackTrace(e);
//                }
//            }
//        }
//        return null;
//    }
}
