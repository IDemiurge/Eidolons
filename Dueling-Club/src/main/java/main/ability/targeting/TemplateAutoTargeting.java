package main.ability.targeting;

import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.system.DC_ConditionMaster;

public class TemplateAutoTargeting extends AutoTargeting {

    private AUTO_TARGETING_TEMPLATES template;

    public TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES template) {
        this((template), null);
    }

    public TemplateAutoTargeting(AUTO_TARGETING_TEMPLATES template,
                                 Condition conditions) {
        super(
                new Conditions(conditions,
                        DC_ConditionMaster
                                .getAutoTargetingTemplateConditions(template)));
        this.setTemplate(template);
    }

    public AUTO_TARGETING_TEMPLATES getTemplate() {
        return template;
    }

    public void setTemplate(AUTO_TARGETING_TEMPLATES template) {
        this.template = template;
    }

}
