package framework.entity;

import elements.stats.UnitParam;
import elements.stats.UnitProp;
import elements.stats.generic.Property;
import elements.stats.generic.Stat;
import system.consts.MathConsts;

import java.util.Map;

import static combat.sub.BattleManager.combat;

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
        this.name = data.getS(Property.Name).toString();
        //TODO
        id = combat().getEntities().addEntity(this);

    }

    @Override
    public String toString() {
        return name + " id-" + id;
    }

    public int getId() {
        return id;
    }

    public void toBase() {
        data.toBase();
        //check visibility?
    }

    ////////////////region  SETTERS ///////////////////
    public void setValue(Stat key, Object val) {
        data.set(key, val);
    }

    public void setValue(String key, Object val) {
        data.set(key, val);
    }

    public void setCur(String key, int val) {
        data.setCur(key, val);
    }

    public void addCurValue(Stat key, int i) {
        data.addCurValue(key, i);
    }

    //endregion
    ////////////////region GETTERS ///////////////////
    public int getInt(Stat stat) {
        return data.getInt(stat);
    }
    public int getInt(String key) {
        return data.getInt(key);
    }

    public <T> T getEnum(String name, Class<T> className) {
        return data.getEnum(name, className);
    }

    public Object get(String name) {
        return data.get(name);
    }
    public Object get(Stat stat) {
        return data.get(stat);
    }

    public String getS(String name) {
        return data.getS(name);
    }
    public String getS(Stat stat) {
        return getS(stat.getName());
    }

    public Boolean isTrue(String name) {
        return data.isTrue(name);
    }

    public Boolean isTrue(Stat key) {
        return data.isTrue(key);
    }

    //endregion
    //////////////////region  SHORTCUTS ////////////////////

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
//endregion

    public boolean isDead() {
        return data.isTrue("dead");
    }

    public void modifyValue(UnitParam valueName, Object value) {
        modifyValue(valueName.getName(), value);
    }
    public void modifyValue(String valueName, Object value) {
        if (value instanceof Integer) {
            data.addIntValue(valueName, (Integer) value);
        } else {
            if (value.toString().contains(MathConsts.MULTIPLY_SYMBOL)){
                data.multiply(valueName, value.toString().replace(MathConsts.MULTIPLY_SYMBOL, ""));
            } else
                //TODO
            // if (value.toString().contains(MathConsts.SET_TO_PERCENT_SYMBOL)){
            //     data.setPercent(valueName, value.toString().replace(MathConsts.SET_TO_PERCENT_SYMBOL, ""));
            // } else
                data.set(valueName, value);
        }
    }
}
