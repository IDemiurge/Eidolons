package logic.calculation.damage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 8/25/2023
 */
public class MultiDamage {
    List<Damage> damageList = new ArrayList<>() ;

    public MultiDamage() {
    }

    public MultiDamage(List<Damage> damageList) {
        this.damageList = damageList;
    }

    public void
    add(Damage dmg){
        damageList.add(dmg);
    }

    public List<Damage> getDamageList() {
        return damageList;
    }
}
