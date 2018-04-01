package eidolons.ability.targeting;

import eidolons.system.DC_ConditionMaster;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.system.auxiliary.EnumMaster;

public class TemplateAutoTargeting extends AutoTargeting {

    private AUTO_TARGETING_TEMPLATES template;

    public TemplateAutoTargeting(String template) {
        this((new EnumMaster<AUTO_TARGETING_TEMPLATES>().retrieveEnumConst(AUTO_TARGETING_TEMPLATES.class, template)), null);
    }

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemplateAutoTargeting)) {
            return false;
        }
        return super.equals(obj);
    }
}
