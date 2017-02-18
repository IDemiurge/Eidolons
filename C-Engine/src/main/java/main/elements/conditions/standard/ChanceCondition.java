package main.elements.conditions.standard;

import main.content.enums.GenericEnums.ROLL_TYPES;
import main.elements.conditions.ConditionImpl;
import main.system.auxiliary.RandomWizard;
import main.system.math.Formula;

public class ChanceCondition extends ConditionImpl {
    private Formula greater;
    private Formula than;
    private Formula percentage;
    private ROLL_TYPES roll_type;

    public ChanceCondition(ROLL_TYPES roll_type, Formula greater, Formula than) {
        this(greater, than);
        this.roll_type = roll_type;
    }

    public ChanceCondition(Formula greater, Formula than) {
        this.greater = greater;
        this.than = than;
    }

    public ChanceCondition(Formula percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

    @Override
    public boolean check() {
        boolean result;
        if (percentage != null) {
            result = RandomWizard.chance(percentage.getInt(ref));
        } else {
            int n1 = greater.getInt(ref);
            int n2 = than.getInt(ref);

            String string = "";
            ref.getGame().getLogManager().log(string);
            if (roll_type != null) {
                result = RandomWizard.roll(roll_type, n1, n2, ref);
            } else {
                result = RandomWizard.roll(n1, n2); // logging?
            }

            // int result1 = RandomWizard.getRandomIntBetween(0, n1);
            // int result2 = RandomWizard.getRandomIntBetween(0, n2);
            // result = result1 >= result2;
        }
        return result;
    }
}
