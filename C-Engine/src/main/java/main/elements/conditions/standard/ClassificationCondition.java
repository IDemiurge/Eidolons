package main.elements.conditions.standard;

import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.elements.conditions.StringComparison;
import main.elements.conditions.StringContainersComparison;
import main.entity.Ref;

public class ClassificationCondition extends ConditionImpl {

    private final static String comparison = "{MATCH_"
     + G_PROPS.CLASSIFICATIONS + "}";
    private StringComparison c;

    public ClassificationCondition(String classification, String comparison) {
        this.c = new StringContainersComparison(true, "{" + comparison + "_"
         + G_PROPS.CLASSIFICATIONS + "}", classification, false);
    }

    public ClassificationCondition(String classification) {
        this(classification, comparison);
    }

    public ClassificationCondition(CLASSIFICATIONS classification) {
        this(classification.name());
    }

    @Override
    public boolean check(Ref ref) {
        return c.preCheck(ref);
    }

    @Override
    public String toString() {
        return c.toString();
    }
}
