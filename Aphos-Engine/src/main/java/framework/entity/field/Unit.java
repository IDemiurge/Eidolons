package framework.entity.field;

import elements.content.enums.FieldConsts;
import elements.stats.UnitParam;
import framework.field.FieldPos;

import java.util.Map;

/**
 * Created by Alexander on 8/20/2023
 * Should we have a FieldEntity ? For obstacles? Maybe... something else? Omens? Hm
 * Is there a good way to use Aggregation?
 */
public class Unit extends FieldEntity {
    protected final Boolean ally;

    // ActionSet actionSet; //HeroActionSet?
    // PassiveSet passiveSet;
    // OmenStack omens;
    // CountersSet counters; //we can intercept get() for calc of formulas and so keep the valueMap free of these!

    public Unit(Map<String, Object> valueMap, Boolean ally) {
        this(valueMap, ally, new FieldPos(ally ?  FieldConsts.Cell.Reserve_ally : FieldConsts.Cell.Reserve_enemy));
        //create w/o pos to deploy later? Or with default 'reserve pos'? To make it non-null!
    }

    public Unit(Map<String, Object> valueMap, Boolean ally, FieldPos pos) {
        super(valueMap, pos);
        this.ally = ally;
        initCurrentValues();
    }

    UnitParam[] curVals = {
            UnitParam.Moves_Max,
            UnitParam.AP_Max,
            UnitParam.Sanity_Max,
            UnitParam.Faith_Max,
            UnitParam.Hp_Max,
            UnitParam.Soul_Max,
    };

    private void initCurrentValues() {
        for (UnitParam val : curVals) {
            Integer value = getInt(val);
            String cur = val.getName().split("_")[0];
            data.setCur(cur, value);
        }
    }
}
