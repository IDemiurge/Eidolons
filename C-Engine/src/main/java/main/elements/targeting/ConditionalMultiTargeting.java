package main.elements.targeting;

import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.system.math.Formula;

import java.util.List;

/**
 * Created by JustMe on 12/26/2016.
 */
public class ConditionalMultiTargeting extends SelectiveTargeting {
    List<Integer> selectedTargets;
    private Formula nOfTargets;
    private boolean noDuplicates = true;
    private boolean ignoreGroupTargeting = true;

    public ConditionalMultiTargeting(
     Condition c) {
        super(c);


    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConditionalMultiTargeting)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public Conditions getConditions() {
        boolean consecutive = true;
        if (consecutive) {
//            conditions.add()
            selectedTargets.get(selectedTargets.size() - 1);

        }
        return super.getConditions();
    }

    @Override
    public boolean select(Ref ref) {

        return super.select(ref);
    }
}
