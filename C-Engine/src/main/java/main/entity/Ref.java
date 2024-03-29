package main.entity;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.data.ConcurrentMap;
import main.data.XLinkedMap;
import main.entity.group.GroupImpl;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.system.ExceptionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Stores all the relevant ID's. Used to find proper Entities with getObj(KEYS key).
 * <portrait>
 * Ref object is passed on activate(Ref ref) from the source entity to Active entity. To activate on a given object, set
 * ref’s {target} key, otherwise Active's Targeting will select()
 */
public class Ref implements Cloneable, Serializable {
    public final static KEYS[] REPLACING_KEYS = {
            KEYS.BUFF, KEYS.TARGET, KEYS.SOURCE, KEYS.MATCH, KEYS.BASIS, KEYS.EVENT, KEYS.
            SUMMONER, KEYS.ACTIVE, KEYS.SPELL, KEYS.WEAPON, KEYS.ARMOR,
    };
    protected static final long serialVersionUID = 1L; //why was it necessary? => deep clone...
    protected static final String MULTI_TARGET = KEYS.TARGET.name() + "#";
    private static final KEYS[] KEYS_VALUES = KEYS.values();
    public Game game; //reference to the game object
    public Event event; //reference to the event associated with this ref branch/stack
    public boolean base; // affects calculations - all entity params will be taken from base type
    /*
      TARGET_WEAPON example
    ref replacement is there exactly to avoid putting the whole thing into map!
     */
    protected String str; //buffer field for cases when we're replacing keyword, e.g. *event_source* => event's ref's *source*
    protected IActiveObj animationActive; //reference to the game object
    protected Entity infoEntity; //reference to the game object
    protected Map<KEYS, String> values = new XLinkedMap<>(); //main value map
    protected Map<KEYS, String> removedValues; //map for values that were removeValue()-ed
    protected Map<String, Obj> objCache = new HashMap<>(); //cache for performance
    protected Obj source; //utility reference to the source object
    protected GroupImpl group; //reference to the group object (so far don't need >1)
    protected Player player; //reference to the player who owns the original source object of this branch
    protected Effect effect; //reference to the effect object that originated this branch
    protected boolean quiet; // things activate with quiet ref won't throw events
    protected boolean debug; // some things may work differently when ref debug is on
    protected boolean triggered; // signifies that this branch/stack comes from a trigger
    protected boolean animationDisabled; // outdated phase anim flag...
    private boolean clone;
    private boolean original;
    private Set<Coordinates> area;
    private Object arg;

    public Ref() {
        this.game = Game.game;
    }


    public Ref(Integer amount) {
        this();
        setAmount(amount);
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
        for (KEYS keys : KEYS_VALUES) {
            if (keys.name().equalsIgnoreCase(key)) {
                return true;
            }
        }
        //     way too heavy
        //     return (new SearchMaster<KEYS>().find(key, Arrays.asList(KEYS.values()), true) != null);
        return false;
    }

    public static Ref getSelfTargetingRefCopy(Obj obj) {
        Ref REF = obj.getRef().getCopy();
        REF.setTarget(obj.getId());
        return REF;
    }

    public static Ref getBasisRefCopy(Obj obj) {
        Ref REF = obj.getRef().getCopy();
        REF.setBasis(obj.getId());
        return REF;
    }

    public String getValue(KEYS key) {
        if (key == null) {
            return null;
        }
        return values.get(key);
    }

    public String getValue(String name) {
        return getValue(getKey(name)
        );
    }

    public void setValue(KEYS name, String value) {
        if (value == null) {
            removeValue(name);
        } else {
            getValues().put(name, value);
        }
        if (objCache != null)
            objCache.remove(name.name().toLowerCase());
    }

    public void removeValue(KEYS name) {
        getRemovedValues().put(name, values.remove(name));
        objCache.remove(name.name().toLowerCase());
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
            if (obj == null)
                obj = game.getObjectById(getId(string));
            objCache.put(string.toLowerCase(), obj);
            return obj;
        }
        //        try {
        return game.getObjectById(getId(string));
        //        } catch (Exception e) {
        //            main.system.ExceptionMaster.printStackTrace(e);
        //            return null;
        //        }
    }

    public Integer getId(String key) {
        return getInteger(key);
    }

    public void setID(KEYS key, Integer id) {
        if (key == null) {
            return;
        }
        setValue(key, String.valueOf(id));
    }

    public void setID(String key, Integer id) {

        setStr(formatKeyString(key));
        Ref ref = checkForRefReplacement(); //TODO EA check

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
        StringBuilder result = new StringBuilder("REF values: \n");
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
                    result.append(key).append(" = ").append(obj.getNameAndCoordinate());
                } else {
                    result.append(key).append(" = ").append(value);
                }
                result.append(";").append("\n");
            }
        }

        return result.toString();

    }

    public Entity getInfoEntity() {
        return infoEntity;
    }

    public void setInfoEntity(Entity entity) {
        infoEntity = entity;

    }

    public Object clone() {
        Ref ref = new Ref();
        ref.setClone(true);
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
        setValue(KEYS.AMOUNT, getValue(KEYS.AMOUNT));
        // if (refClones.size() % 100 == 5)
        // main.system.auxiliary.LogMaster.system.log(1, " " + refClones.size());
        // refClones.add(ref);
        return ref;
    }

    protected void cloneMaps(Ref ref) {
        // no deep copy required here
        values = new ConcurrentHashMap<>(ref.values);
    }

    protected String formatKeyString(String key) {
        // return key;
        return key.toUpperCase();
        // [OPTIMIZED]
    }

    protected Ref checkForRefReplacement() {
        String s = getStr();
        while (s.startsWith("{")) {
            s = s.substring(1);
        }
        String prefix_ = s.split("_")[0];
        if (prefix_.isEmpty() || prefix_.equals(s))
            return null;
        prefix_ = prefix_.toUpperCase();
        if (prefix_.equals("EVENT")) {
            // setStr(getStr().replace(EVENT_PREFIX, "")); [OPTIMIZED]
            String str = StringMaster.cropFirstSegment(getStr(), "_");
            while (str.endsWith("}")) {
                str = str.substring(0, str.length() - 1);
            }
            setStr(str);
            return getEvent().getRef();
        }

        // if (StringMaster.compare(str, MATCH_PREFIX)) {
        // if (getStr().contains(MATCH_PREFIX)) {
        // setStr(getStr().replace(MATCH_PREFIX, ""));
        // return game.getObjectById(getMatch()).getRef();
        // }

        for (KEYS key : REPLACING_KEYS) {
            //            String prefix = key.name() + StringMaster.FORMULA_REF_SEPARATOR;
            if (prefix_.equals(key.name())) {
                setStr(getStr().replace(key.name() + Strings.FORMULA_REF_SEPARATOR, ""));
                return game.getObjectById(Integer.valueOf(getValue(key.name()))).getRef();
            }
        }
        return null;
    }

    public void setAmount(String amount) {
        setValue(KEYS.AMOUNT, amount);
    }

    public Integer getAmount() {
        Integer amount = getInteger(KEYS.AMOUNT.name(), false);
        if (amount == null) {
            return 0;
        }
        return amount;
    }

    public void setAmount(Integer amount) {
        if (amount == null) {
            setID(KEYS.AMOUNT, null); //TODO ?
        } else {
            setValue(KEYS.AMOUNT, String.valueOf(amount));
        }
    }

    public Integer getInteger(String key) {
        return getInteger(key, true);
    }

    public Integer getInteger(String key, boolean checkReplacements) {
        setStr(formatKeyString(key));
        // if (!getStr().equals(formatKeyString(key)))
        // throw new RuntimeException();
        Ref ref = this;
        if (checkReplacements)
            ref = checkForRefReplacement();

        String value = ((ref == null) ? this : ref).getValue(getStr());
        if (NumberUtils.isInteger(value)) {
            if (value.contains("."))
                return Integer.valueOf(value.split(Pattern.quote("."))[0]);
            return Integer.valueOf(value);
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

    public Ref setTarget(Integer this_target) {
        setID(KEYS.TARGET, this_target);
        if (original && !isClone() && getSource() != null) {
            LogMaster.log(1, ">>>> target set for original ref? => \n " + this);
        }
        return this;
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

    public Ref setMatch(Integer match) {
        setValue(KEYS.MATCH, match + "");
        return this;
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
            return game.getObjectById(NumberUtils.getIntParse(getRemovedValues()
                    .get(key)));
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
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


    public Effect getEffect() {
        return effect;
    }

    public Ref setEffect(Effect effect) {
        this.effect = effect;
        return this;
    }

    public Integer getId(KEYS key) {
        return getInteger(key.name(), key.replacement);
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
        Entity entity = getObj(key);
        if (entity != null) {
            return entity;
        }
        return getType((key.name()));
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public IActiveObj getActive() {
        Obj obj = getObj(KEYS.ACTIVE);
        if (obj == null) {
            obj = getObj(KEYS.SPELL);
        }
        if (obj instanceof IActiveObj) // TODO QUICK ITEM INTERFACE
        {
            return (IActiveObj) obj;
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

    public IActiveObj getAnimationActive() {
        if (animationActive == null) {

            return getActive(); // TODO ?? ?

        }
        return animationActive;
    }

    public void setAnimationActive(IActiveObj animationActive) {
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

    public void setClone(boolean clone) {
        this.clone = clone;
    }

    public boolean isClone() {
        return clone;
    }

    public Set<Coordinates> getArea() {
        return area;
    }

    public void setArea(Set<Coordinates> area) {
        this.area = area;
    }

    public void addValue(KEYS keys, int s) {
        setValue(keys, (NumberUtils.getInt(getValue(keys.name())) + s) + "");
    }

    public Object getArg() {
        if (arg == null) {
            return getValue(KEYS.ARG);
        }
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
        setValue(KEYS.ARG, arg.toString());
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
        EVENT_SOURCE(true),
        EVENT_AMOUNT(true),
        EVENT_TARGET(true),
        EVENT_MATCH(true),
        EVENT_ABILITY(true),
        // ++ EFFECT
        BUFF,
        SUMMONED,
        TARGET2,
        PAYEE,
        CUSTOM_TARGET,
        ITEM,
        SKILL,
        MATCH_SOURCE(true),
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
        ARG,
        IMAGE,
        OBJECTIVE, BLOCK,
        //macro
        REGION,
        ROUTE,
        PLACE,
        ENCOUNTER,
        FACTION,
        AREA, ATTACK_WEAPON, RESERVE_WEAPON, RESERVE_OFFHAND_WEAPON, HOSTILITY,
        ;
        public boolean replacement;

        KEYS(boolean replacement) {
            this.replacement = replacement;
        }

        KEYS() {
        }
    }

}
