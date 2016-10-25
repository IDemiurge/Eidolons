package main.elements.conditions;

import main.data.ability.OmittedConstructor;
import main.entity.Ref;

import java.util.Arrays;

public class OrConditions extends Conditions {

    public OrConditions(Condition c1, Condition c2) {
        super(c1, c2);
    }

    public OrConditions(Condition c1, Condition c2, Condition c3, Condition c4) {
        super(c1, c2, c3, c4);
    }

    public OrConditions(OrConditions c) {
        addAll(c);
    }

    public OrConditions(Condition... c) {
        super(Arrays.asList(c));
    }

    @OmittedConstructor
    public OrConditions() {
        super();
    }

    // public OrConditions(Condition c1, Condition c2){
    // this.c1=
    // }
    @Override
    public boolean check(Ref ref) {
        setRef(ref);

        return checkAny();

    }

    protected boolean checkConditionUnwrap(Condition c) {
        return c instanceof OrConditions;
    }

    @Override
    public boolean checkAny() {
        isTrue = false;
        for (int i = 0; i < this.size(); i++) {
            isTrue |= this.get(i).check(ref);
            // if ((isTrue & negative))
            if (isTrue)
                if (isFastFailOnCheck())
                    break;
                else
                    this.setLastCheckedCondition(get(i));

        }

        return isTrue;
    }
}
