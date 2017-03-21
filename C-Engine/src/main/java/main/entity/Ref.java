package main.entity;

import main.ability.effects.Effect;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref.KEYS;
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
import main.system.net.data.DataUnit;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Stores all the relevant ID's. Used to find proper Entities with getObj(KEYS key).
 * <p>
 * Ref object is passed on activate(Ref ref) from the source entity to Active entity.
 * To activate on a given object, set ref’s {target} key, otherwise Active's Targeting will select()
 */
public class Ref extends DataUnit<KEYS> implements Cloneable, Serializable {

//    Map<KEYS,String> map;
    /*
    TARGET_WEAPON example
    ref replacement is there exactly to avoid putting the whole thing into map!

     */

    protected static final long serialVersionUID = 1L; //why was it necessary?
    protected static final String MULTI_TARGET = KEYS.TARGET.name() + "#";
    public Game game;
    public Event event;
    public boolean base;
    protected GroupImpl group;
    protected String str;
    protected Player player;
    protected Effect effect;
    protected boolean quiet;
    protected boolean debug;
    protected boolean triggered;


    private ActiveObj animationActive;
    private boolean animationDisabled;
    private Entity infoEntity;

    public Ref() {
        this.game = Game.game;
    }

    public Ref(Integer summonerId) {
        setValue(KEYS.SUMMONER, summonerId + "");
    }

    public Ref(String string) {
        super(string);
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


    public static Ref getCopy(Ref ref) {
        if (ref == null) {
            return new Ref(Game.game);
        }
        return (Ref) ref.clone();
    }

    public static Ref getSelfTargetingRefNew(Obj obj) {
        Ref REF = new Ref(obj.getGame(), obj.getRef().getSource());
        REF.setTarget(obj.getId());
        return REF;
    }

    public static boolean isKey(String key) {
        return (new SearchMaster<KEYS>().find(key, Arrays.asList(KEYS.values()), true) != null);
    }

    public static Ref getSelfTargetingRefCopy(Obj obj) {
        Ref REF = obj.getRef().getCopy();
        REF.setTarget(obj.getId());
        return REF;
    }

    public String getData() {
        String result = super.getData();
        // if (GroupImpl != null)
        // result += GroupImpl.toString();
        return result;

    }

    public String toString() {
        if (game == null || values == null) {
            return "invalid ref!";
        }
        String result = "REF values: ";
        for (String key : values.keySet()) {
            String value;
            Integer id;
            try {
                value = values.get(key);
                id = Integer.valueOf(value);
            } catch (Exception e) {
                continue;
            }
            if (id != null) {
                Obj obj = game.getObjectById(id);
                if (obj != null) {
                    result += key + " = " + obj + ";";
                    continue;
                }
                result += key + " = " + value;
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

    @Override
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
        for (REPLACING_KEYS key : REPLACING_KEYS.values()) {
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
            setValue(KEYS.AMOUNT, null);
        } else {
            setValue(KEYS.AMOUNT, String.valueOf(amount));
        }
    }

    public Integer getId(String key) {
        return getInteger(key);
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
        return getId(KEYS.SOURCE.name());

    }

    public void setSource(Integer id) {
        setID(KEYS.SOURCE.name(), id);
    }

    public Integer getTarget() {
        return getId(KEYS.TARGET.name());

    }

    //

    public void setTarget(Integer this_target) {
        setID(KEYS.TARGET.name(), this_target);

    }


    public void setID(String key, Integer id) {

        setStr(formatKeyString(key));
        Ref ref = checkForRefReplacement();

        if (id == null) {
//            ((ref == null) ? this : ref).setValue(getStr(), null);
            ((ref == null) ? this : ref).setValue(key, null);
        } else {
            ((ref == null) ? this : ref).setValue(getStr(), id.toString());
        }
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
        setID(KEYS.BASIS.name(), basis);
    }

    public Integer getThis() {
        return getId(KEYS.THIS);
    }

    public Obj getThisObj() {
        return game.getObjectById(getThis());
    }

    public Obj getSourceObj() {
        return game.getObjectById(getSource());
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

    public Obj getObj(String string) {
        try {
            return game.getObjectById(getId(string));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Obj getLastRemovedObj(KEYS string) {
        try {
            return game.getObjectById(StringMaster.getInteger(getRemovedValues()
             .get(string.toString())));
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
        return getObj(KEYS.TARGET.name());
    }

    public void setID(KEYS key, Integer id) {
        if (key == null) {
            return;
        }
        setID(key.name(), id);
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Integer getId(KEYS key) {
        return getId(key.name());
    }

    public Obj getObj(KEYS key) {
        return getObj(key.name());
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

    public Entity getEntity(String str) {
        if (getObj(str) != null) {
            return getObj(str);
        }
        return getType((str));
    }

    public Entity getEntity(KEYS key) {
        return getEntity((key.toString()));
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

    public enum REPLACING_KEYS {
        BUFF, TARGET, SOURCE, MATCH, BASIS, EVENT, SUMMONER, ACTIVE, SPELL, WEAPON, ARMOR,
    }

}
