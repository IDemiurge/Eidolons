package main.ability.effects.oneshot.mechanic;

import main.ability.effects.DC_Effect;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.COUNTER_INTERACTION;
import main.content.enums.entity.UnitEnums.COUNTER_OPERATION;
import main.data.ability.AE_ConstrArgs;
import main.rules.counter.CounterMasterAdvanced;
import main.system.auxiliary.EnumMaster;
import main.system.math.Formula;

/**
 * Created by JustMe on 4/19/2017.
 */
public class CounterOperationEffect extends DC_Effect {
    COUNTER_INTERACTION interactionType;
    COUNTER_OPERATION operation;
    COUNTER counter;
    COUNTER counter2;
    Boolean upOrDownTransform;

    @AE_ConstrArgs(argNames = {"amount", "counter", "operation"})
    public CounterOperationEffect(String amount, String counter, COUNTER_OPERATION operation) {
        this(amount, new EnumMaster<COUNTER>().retrieveEnumConst(COUNTER.class, counter), operation);
    }

    public CounterOperationEffect(String amount, COUNTER counter, COUNTER_OPERATION operation) {
        this.operation = operation;
        this.counter = counter;
        if (amount != null)
            this.formula = new Formula(amount);
    }

    @AE_ConstrArgs(argNames = "all counters state up or down")
    public CounterOperationEffect(String amount, Boolean upOrDownTransform) {
        this.upOrDownTransform = upOrDownTransform;
    }

    public CounterOperationEffect(String amount, COUNTER counter, Boolean upOrDownTransform) {
        this.counter = counter;
        this.upOrDownTransform = upOrDownTransform;
        if (amount != null)
            this.formula = new Formula(amount);
    }

    public CounterOperationEffect(COUNTER_INTERACTION interactionType, COUNTER counter, COUNTER counter2) {
        this(null, interactionType, counter, counter2);

    }

    public CounterOperationEffect(String amount, COUNTER_INTERACTION interactionType, COUNTER counter, COUNTER counter2) {
        this.interactionType = interactionType;
        this.counter = counter;
        this.counter2 = counter2;
        if (amount != null)
            this.formula = new Formula(amount);

    }


    @Override
    public boolean applyThis() {
        Integer amount = getFormula().getInt(getRef());
        if (operation != null) {
            CounterMasterAdvanced.
             operation(counter, operation, amount, getSource(), getTarget());
        } else if (upOrDownTransform != null) {
            CounterMasterAdvanced.
             transform(counter, upOrDownTransform, amount, getTarget());
        } else {
            CounterMasterAdvanced.
             interact(counter, counter2, interactionType, amount, getTarget());
        }
        return true;
    }
}
