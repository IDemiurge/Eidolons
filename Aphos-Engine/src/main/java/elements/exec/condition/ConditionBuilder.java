package elements.exec.condition;

import elements.exec.targeting.TargetingTemplates;
import framework.data.Datum;
import framework.data.TypeData;

import java.util.BitSet;
import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 *
 * self().alive(). etc
 */
public class ConditionBuilder {
    Conditions conditions= new Conditions();
    //like with() - set data ?
    /*
    I have a sense of some even more general and concise system:
    what if we make something that 'exposes' certain values via syntax?
    examples of use:
    melee

    it would be better to keep things a bit more atomic - e.g. any OR should be constructed
    how would that go with yaml exec structs?

    condition:
        context:
        value:

     */
    public static Condition build(TargetingTemplates.ConditionTemplate conditionTmlt, Map args) {
        //can condition be represented as chain? Kind of... need an  endOr() then
        ConditionBuilder builder = new ConditionBuilder();
        switch (conditionTmlt) {
            case SELF_CHECK -> {
                //"targeted condition?"
                return builder.value(args).self().build();
                //can we add data later?
            }

            // case UNTIL_ATTACK_OR_FALL -> {
            //     return builder.not().or().isAttack().lastAction().status().wounded();
            //     //this is actually more like a trigger-remove?!
            //     //we can kinda fake it via adding some status to units that have attacked... or via last-action-check!
            // }
        }
        return null;
    }



    ///////////////// region BASE METHODS
    public Condition build(){
        return conditions;
    }

    private  ConditionBuilder append(ConditionContext context) {
        conditions.getLast().setContext(context);
        //func use!
        // conditions.getLast().setContext(new ConditionContext(ref -> ref.getTarget()));
        return this;
    }
    private  ConditionBuilder append(Map args) {
        Condition last = conditions.getLast();
        for (String arg : last.getArgs()) {
            last.getData().set(arg, args.get(arg));
        }
        return this;
    }
    private  ConditionBuilder append(Condition condition) {
        conditions.add(condition);
        return this;
    }
    //endregion
    ///////////////// region COMMON CHAIN METHODS
    private ConditionBuilder not() {
        // conditions.setNot(true); //next condition will be NOT?
        return this;
    }
    private ConditionBuilder or() {
        conditions.setOr(true);
        return this;
    }
    private ConditionBuilder self() {
        append(new ConditionContext("source"));
        return this;
    }
    private  ConditionBuilder value(Map args) {
        return comparison(args.get("value")).append(args);
    }

    private ConditionBuilder comparison(Object value) {
        if (value.getClass() == Integer.class) {
            conditions.add(new IntCondition());
            // conditions.add(new StrCondition());
            // conditions.add(new BoolCondition());
        }
        return this;
    }
    //endregion


}
