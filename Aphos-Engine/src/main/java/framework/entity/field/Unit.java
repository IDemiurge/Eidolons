package framework.entity.field;

import elements.content.enums.FieldConsts;
import elements.stats.UnitParam;
import elements.stats.generic.StatConsts;
import framework.entity.sub.ActionSet;
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
    // protected PassiveSet passiveSet;
    // protected OmenStack omens;
    // protected CountersSet counters; //we can intercept get() for calc of formulas and so keep the valueMap free of these!

    public Unit(Map<String, Object> valueMap, Boolean ally) {
        this(valueMap, ally, new FieldPos(ally ?  FieldConsts.Cell.Reserve_ally : FieldConsts.Cell.Reserve_enemy));
        //create w/o pos to deploy later? Or with default 'reserve pos'? To make it non-null!
    }

    public Unit(Map<String, Object> valueMap, Boolean ally, FieldPos pos) {
        super(valueMap, pos);
        this.ally = ally;
        initCurrentValues();
        actionSet =combat.init. ActionInitializer.initActionSet(this);
    }

    public ActionSet getActionSet() {
        return actionSet;
    }

    private void initCurrentValues() {
        for (UnitParam val : StatConsts.unitCurrentVals) {
            Integer value = getInt(val);
            String cur = val.getName().split("_")[0];
            data.setCur(cur, value);
        }
    }
}
