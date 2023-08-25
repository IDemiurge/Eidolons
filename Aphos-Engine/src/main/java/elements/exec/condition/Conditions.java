package elements.exec.condition;

import elements.exec.EntityRef;
import system.ListMaster;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alexander on 8/25/2023
 */
public class Conditions implements Condition {
    private List<Condition> list = new ArrayList<>();
    private boolean or;

    public Condition getLast() {
        return ListMaster.getLast(list);
    }

    public void setOr(boolean or) {
        this.or = or;
    }


    public void add(Condition condition) {
        list.add(condition);
    }

    @Override
    public boolean check(EntityRef ref) {
        boolean result = !or;
        for (Condition condition : list) {
            boolean check = condition.check(ref);
            if (check) {
                if (or) {
                    return true;
                }
            } else {
                if (!or) {
                    return false;
                }
            }

        }
        return result;
    }
}
