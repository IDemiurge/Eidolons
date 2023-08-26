package framework.entity.field;

import combat.init.ActionInitializer;
import elements.content.enums.FieldConsts;
import elements.exec.EntityRef;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import elements.stats.generic.StatConsts;
import framework.entity.sub.ActionSet;
import framework.entity.sub.OmenStack;
import framework.entity.sub.PassiveSet;
import framework.entity.sub.UnitAction;
import framework.field.FieldPos;

import java.util.Map;

/**
 * Created by Alexander on 8/20/2023
 * Should we have a FieldEntity ? For obstacles? Maybe... something else? Omens? Hm
 * Is there a good way to use Aggregation?
 */
public class Unit extends FieldEntity {
    protected final Boolean ally;

    protected ActionSet actionSet; //HeroActionSet?
    protected PassiveSet passiveSet;
    protected OmenStack omens;
    // protected CountersSet counters; //we can intercept get() for calc of formulas and so keep the valueMap free of these!

    public Unit(Map<String, Object> valueMap, Boolean ally) {
        this(valueMap, ally, new FieldPos(ally ?  FieldConsts.Cell.Reserve_ally : FieldConsts.Cell.Reserve_enemy));
        //create w/o pos to deploy later? Or with default 'reserve pos'? To make it non-null!
    }

    public Unit(Map<String, Object> valueMap, Boolean ally, FieldPos pos) {
        super(valueMap, pos);
        this.ally = ally;
        initCurrentValues();
        actionSet = ActionInitializer.initActionSet(this);
    }

    private void initCurrentValues() {
        for (UnitParam val : StatConsts.unitCurrentVals) {
            Integer value = getInt(val);
            String cur = val.getName().split("_")[0];
            data.setCur(cur, value);
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
        if (action.isSpell()){
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
    public Object get(UnitParam stat) {
        return super.get(stat);
    }
    public Object get(UnitProp stat) {
        return super.get(stat);
    }
    public Boolean isTrue(UnitProp key) {
        return data.isTrue(key);
    }
    public int getInt(UnitParam stat) {
        return data.getInt(stat);
    }
    public String getS(UnitProp stat) {
        return getS(stat.getName());
    }

    //endregion
}
