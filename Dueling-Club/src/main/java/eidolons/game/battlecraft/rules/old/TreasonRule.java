package eidolons.game.battlecraft.rules.old;

import eidolons.ability.conditions.StatusCheckCondition;
import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import main.ability.effects.common.OwnershipChangeEffect;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.entity.Ref;
import main.game.core.game.GenericGame;
import main.game.logic.event.EventType;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.entity.ConditionMaster;

public class TreasonRule extends DC_RuleImpl {

    public static final String TREASON = "-150+(100-10*({SOURCE_SPIRIT}))";

    private static final Condition CONDITION = new NumericCondition(TREASON,
     "{TARGET_C_MORALE}");
    private static final String MORALE = PARAMS.C_ESSENCE.name();

    private static final Condition CONDITION0 = new NotCondition(
     new StatusCheckCondition(Ref.KEYS.TARGET.name(), UnitEnums.STATUS.CHARMED));

    private Conditions retain_conditions;

    public TreasonRule(GenericGame game) {
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
        String buffTypeName = "Treason";
        effects = new AddBuffEffect(retain_conditions, buffTypeName, effect);

    }

}
