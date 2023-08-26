package framework.entity.sub;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 8/26/2023
 */
public class PassiveSet {
    List<UnitPassive> list=    new ArrayList<>() ;

    public void init(){
        for (UnitPassive passive : list) {
            passive.apply(); //some passives may have a condition that disables them, e.g. BURNS
        }
    }
}
