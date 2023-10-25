package framework.entity.field;

import combat.init.ActionInitializer;
import elements.content.enums.FieldConsts;
import elements.exec.EntityRef;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import elements.stats.generic.StatConsts;
import framework.entity.sub.*;
import framework.field.FieldPos;

import java.util.Map;

/**
 * Created by Alexander on 8/20/2023 Should we have a FieldEntity ? For obstacles? Maybe... something else? Omens? Hm Is
 * there a good way to use Aggregation?
 */
public class Unit extends FieldEntity {

    private final int faction;
    protected ActionSet actionSet; //HeroActionSet?
    protected PassiveSet passiveSet;
    protected OmenStack omens;

    public Unit(Map<String, Object> valueMap, int faction) {
        this(valueMap, faction  , new FieldPos(faction  > 0  ? FieldConsts.Cell.Reserve_ally : FieldConsts.Cell.Reserve_enemy));
        //create w/o pos to deploy later? Or with default 'reserve pos'? To make it non-null!
    }

    public Unit(Map<String, Object> valueMap, int faction, FieldPos pos) {
        super(valueMap, pos);
        this.faction = faction;
        initCurrentValues();
        actionSet = ActionInitializer.initActionSet(this);
        initPerks();
    }

    private void initPerks() {

    }

    private void initCurrentValues() {
        for (UnitParam cur : StatConsts.unitCurrentVals) {
            Integer value = getInt(cur);
            data.setCur(cur.getName(), value);
            String max = cur.getName() + "_max";
            data.set(max, value);
        }
    }


    //////////////////region GETTERS ////////////////////
    public ActionSet getActionSet() {
        return actionSet;
    }

    //endregion
    //////////////////region SHORTCUTS ////////////////////
    public int getAtkOrSp(UnitAction action, EntityRef ref) {
        Boolean min_base_max = null;//        omen.current.get();
        if (action.isSpell()) {
            //sp coef and all that?!
        }
        return getInt(StatConsts.getAtkVal(min_base_max));
    }

    public int getDefOrRes(UnitAction action, EntityRef ref) {
        Boolean min_base_max = null;//        omen.current.get();
        return getInt(action.isSpell()
                ? StatConsts.getResVal(min_base_max)
                : StatConsts.getDefVal(min_base_max));
    }

    public Object get(UnitProp stat) {
        return super.get(stat);
    }

    public Boolean isTrue(UnitProp key) {
        return data.isTrue(key);
    }

    public int getInt(String key) {
        Integer value = counters.get(key);
        if (value==null)
            return data.getInt(key);
        return value;
    }
    public int getInt(UnitParam stat) {
        return data.getInt(stat);
    }

    public String getS(UnitProp stat) {
        return getS(stat.getName());
    }

    public boolean isAlly() {
        return faction > 0;
    }

    public int initiative() {
        return getInt(UnitParam.Initiative);
    }

    public void setProp(UnitProp prop, Object value) {
        setValue(prop, value);
    }
    public void setParam(UnitParam param, Object value) {
        setValue(param, value);
    }

    //endregion
}
