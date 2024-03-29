package eidolons.game.battlecraft.rules.combat.mechanics;

import eidolons.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import main.ability.effects.Effect.MOD;
import main.content.DC_TYPE;
import main.content.enums.entity.EffectEnums;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.ObjTypeComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.core.game.GenericGame;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;

/**
 * Add bleeding counters for every 1% below 25%. Lose 1% max endurance per turn
 * for each counter
 *
 * @author JustMe
 */

public class BleedingRule extends DC_RuleImpl {
    private static final Integer THRESHOLD = 50;
    private static final Integer MODIFIER = 20;

    public BleedingRule(GenericGame game) {
        super(game);
    }

    @Override
    public void apply(Ref ref) {
        super.apply(ref);
    }

    @Override
    public boolean check(Event event) {
        if (!getEventType().equals(event.getType())) {
            return false;
        }
        return super.check(event);
    }

    @Override
    public void initEffects() {
        // LIMIT BY MAX
        effects = new ModifyCounterEffect(EffectEnums.COUNTER.Bleeding.getName(),
                MOD.MODIFY_BY_CONST,

                "{SOURCE_BLEEDING_MOD}/100*" +
                        StringMaster.wrapInParenthesis(
                                // TODO formula?
                                THRESHOLD + "-" + "({TARGET_C_TOUGHNESS}*100/"
                                        + "{TARGET_TOUGHNESS})*" + MODIFIER + "/100")

        );
    }

    @Override
    public void initConditions() {
        // DAMAGE TYPE CHECK? event_damage_type?
        conditions = new Conditions(ConditionMaster.getAliveCondition(KEYS.TARGET), ConditionMaster
                .getLivingCondition("target"), new NotCondition(
                new ObjTypeComparison(DC_TYPE.BF_OBJ, "target")),
                new NumericCondition(
                        "{TARGET_TOUGHNESS}*" + THRESHOLD + "/100"
                        , "{TARGET_C_TOUGHNESS}"));

    }

    @Override
    public void initEventType() {
        this.event_type = STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE;
    }

}
