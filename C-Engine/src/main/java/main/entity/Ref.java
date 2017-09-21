package main.entity;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.data.ConcurrentMap;
import main.data.XLinkedMap;
import main.entity.group.GroupImpl;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.SearchMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores all the relevant ID's. Used to find proper Entities with getObj(KEYS key).
 * <portrait>
 * Ref object is passed on activate(Ref ref) from the source entity to Active entity.
 * To activate on a given object, set ref’s {target} key, otherwise Active's Targeting will select()
 */
public class Ref implements Cloneable, Serializable {

    public final static KEYS[] REPLACING_KEYS = {
     KEYS.BUFF, KEYS.TARGET, KEYS.SOURCE, KEYS.MATCH, KEYS.BASIS, KEYS.EVENT, KEYS.
     SUMMONER, KEYS.ACTIVE, KEYS.SPELL, KEYS.WEAPON, KEYS.ARMOR,

    };
    protected static final long serialVersionUID = 1L; //why was it necessary?
    protected static final String MULTI_TARGET = KEYS.TARGET.name() + "#";
    /*
    TARGET_WEAPON example
    ref replacement is there exactly to avoid putting the whole thing into map!

     */
    public Game game;
    public Event event;
    public boolean base;
    protected Map<KEYS, String> values = new XLinkedMap<>();
    protected Map<KEYS, String> removedValues;
    //OPTIMIZATION
    protected Map<String, Obj> objCache = new HashMap<>();
    protected Obj source;
    protected GroupImpl group;
    protected String str;
    protected Player player;
    protected Effect effect;
    protected boolean quiet;
    protected boolean debug;
    protected boolean triggered;
    protected ActiveObj animationActive;
    protected boolean animationDisabled;
    protected Entity infoEntity;

    public Ref() {
        this.game = Game.game;
    }


    public Ref(Integer summonerId) {
        setValue(KEYS.SUMMONER, summonerId + "");
    }


    public Ref(Game game, Integer source) {
        this.game = game;
        setSource(source);
    }

    public Ref(Game game) {
        this.game = game;
    }

    public Ref(Entity entity) {
        this(entity.getGame(), entity.getId());
    }


    public Ref(Entity entity, Entity target) {
        this(entity.getGame(),
         entity.getId());
        setTarget(target.getId());
    }

    public static Ref getCopy(Ref ref) {
        if (ref == null) {
            return new Ref(Game.game);
        }
        return (Ref) ref.clone();
    }

    public static boolean isKey(String key) {
        return (new SearchMaster<KEYS>().find(key, Arrays.asList(KEYS.values()), true) != null);
    }

    public static Ref getSelfTargetingRefCopy(Obj obj) {
        Ref REF = obj.getRef().getCopy();
        REF.setTarget(obj.getId());
        return REF;
    }

    public String getValue(KEYS key) {
        return values.get(key);
    }

    public String getValue(String name) {
        return getValue(getKey(name)
        );
    }

    public void setValue(KEYS name, String value) {
        getValues().put(name, value);
        if (value == null) {
            removeValue(name);
        }
        if (objCache != null)
        objCache.remove(name.name());
    }

    public void removeValue(KEYS name) {
        values.remove(name);
        getRemovedValues().put(name, values.get(name));
    }

    public Map<KEYS, String> getRemovedValues() {
        if (removedValues == null) {
            removedValues = new ConcurrentMap<>();
        }
        return removedValues;
    }

    public Obj getObj(KEYS key) {
        return getObj(key.name());
    }

    public Obj getObj(String string) {
        if (objCache != null) {
            Obj obj = objCache.get(string.toLowerCase());
            if (obj==null )
                obj =   game.getObjectById(getId(string));
            objCache.put(string.toLowerCase(), obj);
            return obj;
        }
//        try {
        return game.getObjectById(getId(string));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public Integer getId(String key) {
        return getInteger(key);
    }

    public void setID(String key, Integer id) {

        setStr(formatKeyString(key));
        Ref ref = checkForRefReplacement();

        if (id == null) {
            ((ref == null) ? this : ref).setValue(getKey(getStr()), null);
        } else {
            ((ref == null) ? this : ref).setValue(getKey(getStr()), id.toString());
        }
    }

    protected KEYS getKey(String name) {
        try {
            return KEYS.valueOf(name.toUpperCase());
        } catch (Exception e) {

        }
//   COULD FIND WRONG STUFF!
//     return new EnumMaster<KEYS>().retrieveEnumConst(KEYS.class, name);
        return null;
    }

    public Map<KEYS, String> getValues() {
        return values;
    }

    public String toString() {
        if (game == null || values == null) {
            return "invalid ref!";
        }
        String result = "REF values: \n";
        for (KEYS key : values.keySet()) {
            String value;
            Integer id = null;
            try {
                value = values.get(key);
                if (value != null)
                    id = Integer.valueOf(value);
            } catch (Exception e) {
                continue;
            }
            if (id != null) {
                Obj obj = game.getObjectById(id);
                if (obj != null) {
                    result += key + " = " + obj;
                } else {
                    result += key + " = " + value;
                }
                result = result + ";" + "\n";
            }
        }

        return result;

    }

    public Entity getInfoEntity() {
        return infoEntity;
    }

    public void setInfoEntity(Entity entity) {
        infoEntity = entity;

    }

    public Object clone() {
        Ref ref = new Ref();
        ref.cloneMaps(this);
        ref.setPlayer(player);
        ref.setEvent(event);
        ref.setGroup(group);
        ref.setBase(base);
        ref.setGame(game);
        ref.setEffect(effect);
        ref.setTriggered(triggered);
        ref.setDebug(debug);
        ref.setAnimationActive(animationActive);
        // ref.setAmount(getAmount());
        // if (refClones.size() % 100 == 5)
        // main.system.auxiliary.LogMaster.log(1, " " + refClones.size());
        // refClones.add(ref);
        return ref;
    }

    protected void cloneMaps(Ref ref) {
        // no deep copy required here
        values = new HashMap<>(ref.getValues());
    }

    protected String formatKeyString(String key) {
        // return key;
        return key.toUpperCase();
        // [OPTIMIZED]
    }

    protected Ref checkForRefReplacement() {
        String s = getStr();
        if (s.startsWith("{")) {
            s = StringMaster.replaceFirst(s, "{", "");
        }
        if (StringMaster.compareByChar(StringMaster.getSegment(0, s, "_"), "EVENT", true)) {
            // setStr(getStr().replace(EVENT_PREFIX, "")); [OPTIMIZED]
            setStr(StringMaster.cropFirstSegment(getStr(), "_").replace("}", ""));
            return getEvent().getRef();
        }

        // if (StringMaster.compare(str, MATCH_PREFIX)) {
        // if (getStr().contains(MATCH_PREFIX)) {
        // setStr(getStr().replace(MATCH_PREFIX, ""));
        // return game.getObjectById(getMatch()).getRef();
        // }
        for (KEYS key : REPLACING_KEYS) {
            String prefix = key.name() + StringMaster.FORMULA_REF_SEPARATOR;
            if (getStr().contains(prefix)) {
                setStr(getStr().replace(prefix, ""));
                try {
                    return game.getObjectById(Integer.valueOf(getValue(key.name()))).getRef();
                } catch (Exception e) {
                    LogMaster.log(1, prefix + " + " + getStr());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setAmount(String amount) {
        setValue(KEYS.AMOUNT, amount);
    }

    public Integer getAmount() {
        return getInteger(KEYS.AMOUNT.name());
    }

    public void setAmount(Integer amount) {
        if (amount == null) {
            setID(KEYS.AMOUNT, null); //TODO ?
        } else {
            setValue(KEYS.AMOUNT, String.valueOf(amount));
        }
    }

    public Integer getInteger(String key) {
        setStr(formatKeyString(key));
        // if (!getStr().equals(formatKeyString(key)))
        // throw new RuntimeException();
        Ref ref = checkForRefReplacement();

        String value = ((ref == null) ? this : ref).getValue(getStr());
        if (StringMaster.isInteger(value)) {
            return Integer.valueOf(value.replace(".0", ""));
        }

        if (StringMaster.contains(getStr(), MULTI_TARGET)) {
            if (group != null) {
                int i = Integer.valueOf(getStr().replace(MULTI_TARGET, ""));

                LogMaster.log(LogMaster.CORE_DEBUG_1, "multi targeting effect, selecting target #"
                 + i + group);

                if (i > 0) {
                    i--;
                }
                return group.getObjectIds().get(i);

            }

        }
        if (key.equalsIgnoreCase("spell")) {
            return getId(KEYS.ACTIVE);
        }
        return null;
    }

    public Integer getSource() {
        return getId(KEYS.SOURCE);

    }

    public void setSource(Integer id) {
        setID(KEYS.SOURCE, id);
    }

    //

    public Integer getTarget() {
        return getId(KEYS.TARGET);

    }

    public void setTarget(Integer this_target) {
        setID(KEYS.TARGET, this_target);

    }

    public Ref getCopy() {
        return (Ref) clone();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game2) {
        this.game = game2;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        // Err.info("set " + event + " for " + toString());
        this.event = event;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public Integer getMatch() {
        return getId(KEYS.MATCH);
    }

    public void setMatch(Integer match) {
        setValue(KEYS.MATCH, match + "");
    }

    public GroupImpl getGroup() {
        return group;
    }

    public void setGroup(GroupImpl GroupImpl) {
        this.group = GroupImpl;
    }

    public Integer getBasis() {
        return getId(KEYS.BASIS);
    }

    public void setBasis(Integer basis) {
        setID(KEYS.BASIS, basis);
    }

    public Integer getThis() {
        return getId(KEYS.THIS);
    }

    public Obj getThisObj() {
        return game.getObjectById(getThis());
    }

    public Obj getSourceObj() {
        if (source == null)
            source = game.getObjectById(getSource());
        return source;
    }

    public Player getPlayer() {
        if (player == null) {
            return getSourceObj().getOwner();
        }
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Obj getLastRemovedObj(KEYS key) {
        try {
            return game.getObjectById(StringMaster.getInteger(getRemovedValues()
             .get(key)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ObjType getType(String string) {
        try {
            return game.getTypeById(getId(string));
        } catch (Exception e) {
            return null;
        }
    }

    public Obj getTargetObj() {
        return getObj(KEYS.TARGET);
    }

    public void setObj(KEYS key, Obj obj) {
        if (obj == null) {
            setID(key, null);
        } else {
            setID(key, obj.getId());
        }
    }

    public void setID(KEYS key, Integer id) {
        if (key == null) {
            return;
        }
        setValue(key, String.valueOf(id));
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Integer getId(KEYS key) {
        return getInteger(key.name());
    }

    public boolean checkInterrupted() {

        return false;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Obj getMatchObj() {
        return getObj(KEYS.MATCH);
    }

    public Entity getEntity(KEYS key) {
        if (getObj(key) != null) {
            return getObj(key);
        }
        return getType((key.name()));
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public ActiveObj getActive() {
        Obj obj = getObj(KEYS.ACTIVE);
        if (obj == null) {
            obj = getObj(KEYS.SPELL);
        }
        if (obj instanceof ActiveObj) // TODO QUICK ITEM INTERFACE
        {
            try {
                return (ActiveObj) obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean b) {
        triggered = b;
    }

    public DAMAGE_TYPE getDamageType() {
        if (getValue(KEYS.DAMAGE_TYPE) == null) {
            return null;
        }
        return new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class,
         getValue(KEYS.DAMAGE_TYPE));
    }

    public boolean isAnimationDisabled() {
        return animationDisabled;
    }

    public void setAnimationDisabled(boolean b) {
        animationDisabled = b;
    }

    public ActiveObj getAnimationActive() {
        if (animationActive == null) {

            return getActive(); // TODO ?? ?

        }
        return animationActive;
    }

    public void setAnimationActive(ActiveObj animationActive) {
        this.animationActive = animationActive;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Ref getTargetingRef(Obj target) {
        Ref ref = (Ref) clone();
        ref.setTarget(target.getId());
        return ref;
    }

    //

    public String getInfoString() {
        return "Ref: target =" + getTargetObj() + "\n ; group =" + getGroup();
    }

    public enum KEYS {
        THIS,

        TARGET,
        SOURCE,
        MATCH,
        BASIS,
        ABILITY,
        EVENT,
        TRIGGER,
        SUMMONER,
        ACTIVE,
        SPELL,
        WEAPON,
        ARMOR,
        OFFHAND,
        SLOT_ITEM,
        EVENT_SOURCE,
        EVENT_AMOUNT,
        EVENT_TARGET,
        EVENT_MATCH,
        EVENT_ABILITY,
        // ++ EFFECT
        BUFF,
        SUMMONED,
        TARGET2,
        PAYEE,
        CUSTOM_TARGET,
        ITEM,
        SKILL,
        MATCH_SOURCE,
        PARTY,
        INFO,
        AMMO,
        RANGED,
        KILLER,

        //non-id
        AMOUNT,
        FORMULA,
        DAMAGE_MODS,
        DAMAGE_TYPE,
        STRING,
        DAMAGE_TOTAL,
        DAMAGE_AMOUNT,
        DAMAGE_DEALT,

        IMAGE,
        OBJECTIVE,
    }

}
