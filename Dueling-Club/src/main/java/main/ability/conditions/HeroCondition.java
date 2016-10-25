package main.ability.conditions;

import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.elements.conditions.StringComparison;

public class HeroCondition extends ConditionImpl {

    private final String hero = OBJ_TYPES.CHARS.getName();
    private StringComparison c;

    public HeroCondition(String obj_ref) {
        this.c = new StringComparison(hero, "{" + obj_ref + "_"
                + G_PROPS.TYPE.name() + "}", false);
    }

    public HeroCondition() {
        this("MATCH");
    }

    @Override
    public boolean check() {
        return c.check(ref);
    }

}
