package elements.exec.condition;

import elements.exec.EntityRef;
import elements.exec.targeting.TargetingTemplates;

import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 * <p>
 * self().alive(). etc
 */
public class ConditionBuilder {
    private Conditions conditions = new Conditions();
    Map args;

    public ConditionBuilder(Map args) {
        this.args = args;
    }

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
    public static Condition build( Map args, TargetingTemplates.ConditionTemplate... conditionTmlt) {
        ConditionBuilder builder = new ConditionBuilder(args);
        for (TargetingTemplates.ConditionTemplate template : conditionTmlt) {
              prebuild(false, template, args, builder);
        }
        return builder.build();
    }
    public static Condition build(TargetingTemplates.ConditionTemplate conditionTmlt, Map args) {
        return prebuild(conditionTmlt, args).build();
    }

    public static ConditionBuilder prebuild(TargetingTemplates.ConditionTemplate conditionTmlt, Map args) {
        return prebuild(false, conditionTmlt, args);
    }

    public static ConditionBuilder prebuild(boolean targeting, TargetingTemplates.ConditionTemplate conditionTmlt, Map args) {
        return prebuild(targeting, conditionTmlt,  args, new ConditionBuilder(args));
    }
    public static ConditionBuilder prebuild(boolean targeting, TargetingTemplates.ConditionTemplate conditionTmlt, Map args, ConditionBuilder builder) {
        //can condition be represented as chain? Kind of... need an  endOr() then

        return switch (conditionTmlt) {
            case POS_CHECK -> builder.pos();
            case TARGET -> builder.target();
            case SELF -> builder.self();
            case SELF_IDENTITY_CHECK -> builder.identity("source");
            case IDENTITY_CHECK -> builder.identity(args);
            case SELF_VALUE_CHECK ->
                    builder.value(args).self(); //"targeted condition?"
            //can we add data later?
            case UNTIL_ATTACK_OR_FALL -> // return builder.not().or().isAttack().lastAction().status().wounded();
                //this is actually more like a trigger-remove?!
                //we can kinda fake it via adding some status to units that have attacked... or via last-action-check!
                    builder.not().isAttack().lastAction();
        };

    }

    ///////////////// region BASE METHODS
    public Condition build() {
        append(args);
        return conditions;
    }

    public ConditionBuilder append(ConditionContext context) {
        conditions.getLast().setContext(context);
        //func use!
        // conditions.getLast().setContext(new ConditionContext(ref -> ref.getTarget()));
        return this;
    }

    public ConditionBuilder append(Map args) {
        Condition last = conditions.getLast();
        for (String arg : last.getArgs()) {
            last.getData().set(arg, args.get(arg));
        }
        return this;
    }

    public ConditionBuilder append(Condition condition, Map args) {
        return append(condition).append(args);
    }

    public ConditionBuilder append(Condition condition) {
        if (condition instanceof ConditionImpl) {
            conditions.add(condition);
        } else {
            conditions.add(new ConditionImpl() {
                @Override
                protected boolean checkThis(EntityRef ref) {
                    return condition.check(ref);
                }
            });
        }
        return this;
    }

    public ConditionBuilder not() {
        Condition last = conditions.getLast();
        if (last == null) {
            conditions.setNot(true);
        } else {
            conditions.getList().set(conditions.getList().size() - 1, new NotCondition(last));
        }
        return this;
    }

    public ConditionBuilder or() {
        conditions.setOr(true);
        return this;
    }

    //endregion
    ///////////////// region CONTEXT
    public ConditionBuilder self() {
        append(new ConditionContext("source"));
        return this;
    }
    public ConditionBuilder target() {
        append(new ConditionContext("target"));
        return this;
    }

    public ConditionBuilder lastAction() {
        // append(new ConditionContext(ref -> ref.getSource().getActionSet().getLastAction()));
        append(new AdvancedContext(ref -> ref.setAction(ref.getSource().getActionSet().getLastAction())));
        return this;
    }

    //endregion
    ///////////////// region CONDITION
    private ConditionBuilder pos() {
        return append(new PositionCondition());
    }

    public ConditionBuilder isAttack() {
        append(ref ->
                ref.getAction().isAttack()
        );
        return this;
    }

    public ConditionBuilder value(Map args) {
        if (args.containsKey("value"))
            return comparison(args.get("value")).append(args);
        return this;
    }

    public ConditionBuilder identity(Map args) {
        return identity(args.get("key").toString());
    }
    public ConditionBuilder identity(String key) {
        append(new IdentityCondition(key.toString()));
        return this;
    }
    public ConditionBuilder comparison(Object value) {
        if (value.getClass() == Integer.class) {
            conditions.add(new IntCondition());
            // conditions.add(new StrCondition());
            // conditions.add(new BoolCondition());
        }
        return this;
    }

    //endregion


}
