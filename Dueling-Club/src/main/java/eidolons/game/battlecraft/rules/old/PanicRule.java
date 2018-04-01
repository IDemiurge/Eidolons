package eidolons.game.battlecraft.rules.old;

import eidolons.ability.conditions.StatusCheckCondition;
import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.oneshot.status.ImmobilizeEffect;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.entity.Ref;
import main.game.core.game.MicroGame;
import main.game.logic.event.Event;
import main.game.logic.event.EventType;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.entity.ConditionMaster;

public class PanicRule extends DC_RuleImpl {

    private static final String PANIC = "-100+(100-10*{TARGET_SPIRIT})"; // TODO
    // bad
    // formula!
    private static final Condition CONDITION = new NumericCondition(PANIC,
     "{TARGET_C_MORALE}");
    private static final Condition CONDITION2 = new NumericCondition(
     "{TARGET_C_MORALE}", TreasonRule.TREASON);
    private static final String MORALE = PARAMS.C_MORALE.name();
    private static final Condition CONDITION0 = new NotCondition(
     new StatusCheckCondition(Ref.KEYS.TARGET.name(), UnitEnums.STATUS.IMMOBILE));

    private String buffTypeName = "Panic";
    private Conditions retain_conditions;

    // put a conditional continuous effect

    public PanicRule(MicroGame game) {
        super(game);

    }

    @Override
    public boolean check(Event event) {
        if (event_type.equals(event.getType())) {
            super.check(event);
        }
        return super.check(event);
    }

    @Override
    public void initEventType() {
        event_type = new EventType(CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED,
         MORALE);

    }

    @Override
    public void initEffects() {
        ImmobilizeEffect effect = new ImmobilizeEffect();
        effects = new AddBuffEffect(retain_conditions, buffTypeName, effect);

    }

    @Override
    public void initConditions() {
        conditions = new Conditions();
        conditions.add(CONDITION0);
        conditions.add(CONDITION);
        conditions.add(CONDITION2);
        conditions.add(ConditionMaster
         .getMoraleAffectedCondition(Ref.KEYS.TARGET));

        retain_conditions = new Conditions();
        retain_conditions.add(CONDITION);
        retain_conditions.add(CONDITION2);
        retain_conditions.add(ConditionMaster
         .getMoraleAffectedCondition(Ref.KEYS.TARGET));

    }
}
