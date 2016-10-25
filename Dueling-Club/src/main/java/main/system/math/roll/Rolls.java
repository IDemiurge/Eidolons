package main.system.math.roll;

import main.entity.Ref;
import main.system.auxiliary.ListMaster;

import java.util.List;

public class Rolls {
    private List<Roll> rolls;

    private Boolean or;

    public Rolls(boolean or, Roll... rolls) {
        this.or = or;
        this.rolls = new ListMaster<Roll>().getList(rolls);
    }

    public boolean add(Roll e) {
        return rolls.add(e);
    }

    public boolean roll(Ref ref) {
        boolean result = false;
        for (Roll r : rolls) {
            result |= r.roll(ref);
            if (!result)
                if (!or)
                    return false;
        }
        return result;
    }

}
