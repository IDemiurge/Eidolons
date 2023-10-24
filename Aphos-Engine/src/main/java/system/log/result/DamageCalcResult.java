package system.log.result;

import elements.exec.EntityRef;
import logic.calculation.damage.MultiDamage;

/**
 * Created by Alexander on 8/21/2023
 */
public class DamageCalcResult extends LoggableResult {
    private final EntityRef ref;
    private MultiDamage damageToDeal;
    private boolean miss;

    public DamageCalcResult(EntityRef ref) {
        this.ref = ref;
    }

    public EntityRef getRef() {
        return ref;
    }

    public void setMiss(boolean miss) {
        this.miss = miss;
    }

    public boolean isMiss() {
        return miss;
    }

    public void setDamageToDeal(MultiDamage damageToDeal) {
        this.damageToDeal = damageToDeal;
    }

    public MultiDamage getDamageToDeal() {
        return damageToDeal;
    }
}
