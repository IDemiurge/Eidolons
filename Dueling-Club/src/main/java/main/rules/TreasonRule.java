package main.rules;

import main.ability.conditions.StatusCheckCondition;
import main.ability.effects.AddBuffEffect;
import main.ability.effects.oneshot.common.OwnershipChangeEffect;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.entity.Ref;
import main.game.MicroGame;
import main.game.event.EventType;
import main.game.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.ConditionMaster;

public class TreasonRule extends DC_RuleImpl {

    public static final String TREASON = "-150+(100-10*({SOURCE_SPIRIT}))";

    private static final Condition CONDITION = new NumericCondition(TREASON,
            "{TARGET_C_MORALE}");
    private static final String MORALE = PARAMS.C_MORALE.name();

    private static final Condition CONDITION0 = new NotCondition(
            new StatusCheckCondition(Ref.KEYS.TARGET.name(), STATUS.CHARMED));
    private String buffTypeName = "Treason";

    private Conditions retain_conditions;

    public TreasonRule(MicroGame game) {
        super(game);

    }

    @Override
    public void initConditions() {
        conditions = new Conditions();
        conditions.add(CONDITION0);
        conditions.add(CONDITION);
        conditions.add(ConditionMaster
                .getMoraleAffectedCondition(Ref.KEYS.TARGET));

        retain_conditions = new Conditions();
        retain_conditions.add(CONDITION);
        retain_conditions.add(ConditionMaster
                .getMoraleAffectedCondition(Ref.KEYS.TARGET));

    }

    @Override
    public void initEventType() {
        event_type = new EventType(CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED,
                MORALE);

    }

    @Override
    public void initEffects() {
        OwnershipChangeEffect effect = new OwnershipChangeEffect(true);
        effects = new AddBuffEffect(retain_conditions, buffTypeName, effect);

    }

}
