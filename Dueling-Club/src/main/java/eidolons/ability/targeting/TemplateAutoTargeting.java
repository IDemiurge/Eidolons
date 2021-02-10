package eidolons.ability.targeting;

import eidolons.system.DC_ConditionMaster;
import main.content.DC_TYPE;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.AutoTargeting;
import main.entity.Ref;
import main.entity.obj.Obj;
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

    @Override
    public boolean select(Ref ref) {
        return super.select(ref);
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

    @Override
    public Filter<Obj> getFilter() {
        Filter<Obj> filter = super.getFilter();
        switch (template) {
            case PARTY:
                filter.setTYPE(DC_TYPE.PARTY);
                break;
                //TODO
        }
        return filter;
    }
}
