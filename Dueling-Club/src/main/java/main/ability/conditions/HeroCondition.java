package main.ability.conditions;

import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.elements.conditions.StringComparison;
import main.entity.Ref;

public class HeroCondition extends ConditionImpl {

    private final String hero = DC_TYPE.CHARS.getName();
    private StringComparison c;

    public HeroCondition(String obj_ref) {
        this.c = new StringComparison(hero, "{" + obj_ref + "_"
                + G_PROPS.TYPE.name() + "}", false);
    }

    public HeroCondition() {
        this("MATCH");
    }

    @Override
    public boolean check(Ref ref) {
        return c.preCheck(ref);
    }

}