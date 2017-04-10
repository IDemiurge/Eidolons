package main.ability.targeting;

import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.system.DC_ConditionMaster;
import main.system.math.Formula;

public class TemplateSelectiveTargeting extends SelectiveTargeting {
    private boolean initialized;

    @Override
    public boolean equals(Object obj) {
                if (!(obj instanceof TemplateSelectiveTargeting))
                    return false;
            return super.equals(obj);
    }
    public TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES template) {
        super(null, new Formula("1"));
        this.template = template;

    }

    public TemplateSelectiveTargeting(SELECTIVE_TARGETING_TEMPLATES template, Condition c) {
        super(c, new Formula("1"));
        this.template = template;
    }

    @Override
    public boolean select(Ref ref) {
        if (!initialized) {
            filter.setRef(ref.getCopy());
            initTargeting();
        }

        return super.select(ref);
    }

    @Override
    public Filter getFilter() {
        if (!initialized) {
            initTargeting();
        }
        return super.getFilter();
    }

    @Override
    public Conditions getConditions() {
        if (filter.getConditions() == null) {
            initTargeting();
        }
        return super.getConditions();
    }

    private void initTargeting() {
        Conditions conditions = DC_ConditionMaster
                .getSelectiveTargetingTemplateConditions(template);
        Condition c = filter.getConditions(); // TODO ?
        if (c != null) {
            conditions.add(c);
        }
        filter.setConditions(conditions);
        filter.setTYPE(getTYPEforTemplate(template));
        initialized = true;
    }

}
