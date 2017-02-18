package main.ability.conditions;

import main.content.enums.entity.AbilityEnums.TARGETING_MODIFIERS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.MicroCondition;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.system.DC_ConditionMaster;

public class TemplateCondition extends MicroCondition {
    private Conditions conditions;
    private SELECTIVE_TARGETING_TEMPLATES selectiveTemplate;
    private AUTO_TARGETING_TEMPLATES autoTemplate;
    private TARGETING_MODIFIERS modeTemplate;

    public TemplateCondition(TARGETING_MODIFIERS template) {
        modeTemplate = template;

    }

    public TemplateCondition(SELECTIVE_TARGETING_TEMPLATES template) {
        selectiveTemplate = template;
    }

    public TemplateCondition(AUTO_TARGETING_TEMPLATES template) {
        autoTemplate = template;
    }

    @Override
    public boolean check() {
        if (conditions == null) {
            initConditions();
        }
        return conditions.check(ref);
    }

    private void initConditions() {
        conditions = new Conditions();
        if (modeTemplate != null) {
            Condition modConditions = DC_ConditionMaster.getTargetingModConditions(modeTemplate);
            if (!conditions.contains(modConditions)) {
                conditions.add(modConditions);
            }
        }
        if (selectiveTemplate != null) {
            conditions.add(DC_ConditionMaster
                    .getSelectiveTargetingTemplateConditions(selectiveTemplate));
        }
        if (autoTemplate != null) {
            conditions.add(DC_ConditionMaster.getAutoTargetingTemplateConditions(autoTemplate));
        }
    }
}
