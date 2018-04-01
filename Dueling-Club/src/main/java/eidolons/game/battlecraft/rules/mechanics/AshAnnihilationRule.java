package eidolons.game.battlecraft.rules.mechanics;

import eidolons.ability.effects.oneshot.mechanic.ModifyCounterEffect;
import eidolons.ability.targeting.TemplateAutoTargeting;
import eidolons.game.battlecraft.rules.DC_RuleImpl;
import main.ability.effects.Effect.MOD;
import main.ability.effects.continuous.CustomTargetEffect;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.NumericCondition;
import main.elements.conditions.PropCondition;
import main.elements.conditions.standard.ClassificationCondition;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.game.core.game.MicroGame;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

/**
 * Created by JustMe on 4/22/2017.
 */
public class AshAnnihilationRule extends DC_RuleImpl {
    public AshAnnihilationRule(MicroGame game) {
        super(game);
    }

    @Override
    public void initEffects() {
        effects =
         new CustomTargetEffect(new TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES.CELL),
          new ModifyCounterEffect(COUNTER.Ash, MOD.MODIFY_BY_CONST, "{target_weight}/50"));

    }

    @Override
    public void initConditions() {
        conditions = new Conditions(new NumericCondition("{match_blaze_counters}", "0"));
        conditions.add(new NotCondition(new ClassificationCondition(CLASSIFICATIONS.ELEMENTAL)));
        conditions.add(new NotCondition(new PropCondition(G_PROPS.STANDARD_PASSIVES, STANDARD_PASSIVES.IMMATERIAL.getName())));
    }

    @Override
    public void initEventType() {
        event_type = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;
    }
    /*


     */
}
