package framework.entity;

import elements.stats.Property;
import elements.stats.Stat;
import main.system.auxiliary.NumberUtils;

import java.util.Map;

/**
 * Created by Alexander on 6/10/2023
 * <p>
 * Q: toBase() required or not? Suppose there is an AURA that boosts some stats; and may be disabled or just move
 * somewhere >> I'd say we should rather define this on a case-by-case basis via bonus_x params or such
 */
public abstract class Entity {
    protected String name;
    protected int id;
    protected EntityData data;
    // protected Map<String, Object> valueMap; // from yaml, xml, enum
    // protected Map<String, Integer> intMap = new LinkedStringMap<>();
    // protected Map<String, String> stringMap = new LinkedStringMap<>();
    // protected Map<String, Boolean> boolMap = new LinkedStringMap<>();


    // protected Map<String, Object> baseValueMap;
    //MAYBE a map per Integer/Boolean/String?
    //Container properties are still a must - but more atomization would be nice

    //maybe this DATA thingy can be used like ObjType before? Clone units with it?

    public Entity(Map<String, Object> valueMap) {
        data = new EntityData(valueMap);
        this.name = data.get(Property.Name).toString();
        //TODO
        // id = combat.Battle.current.getIdManager().nextId();
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void toBase() {
        data.toBase();
        //check visibility?
    }

    //////////////// SETTERS ///////////////////
    public void setValue(Stat key, Object val) {
        data.set(key, val);
    }

    public void setValue(String key, Object val) {
        data.set(key, val);
    }

    public void addIntValue(Stat key, int i) {
        setValue(key, getInt(key) + i);
    }

    //////////////// GETTERS ///////////////////
    public int getInt(Object identifier) {
        Object o = data.get(identifier.toString());
        if (o == null)
            return 0;
        return NumberUtils.getIntParse(o.toString());
    }

    public <T> T getEnum(String name, Class<T> className) {
        return data.getEnum(name, className);
    }

    public String getString(String name) {
        return data.getS(name);
    }

    public Boolean getBoolean(String name) {
        return data.getB(name);
    }

    public String getString(Stat key) {
        return data.getS(key);
    }

    public Boolean getBoolean(Stat key) {
        return data.getB(key);
    }

    ////////////////// SHORTCUTS ////////////////////

    public String getName() {
        return name;
    }

    public String getImagePath() {
        Object o = data.get(Property.Image);
        if (o == null) {
            throw new RuntimeException(this + " has no image!");
        }
        return o.toString();
    }


    public boolean isDead() {
        return data.getB("dead");
    }
}
