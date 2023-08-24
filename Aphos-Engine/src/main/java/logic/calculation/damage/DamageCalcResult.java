package logic.calculation.damage;

import elements.exec.EntityRef;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static elements.content.enums.types.CombatTypes.*;

/**
 * Created by Alexander on 8/21/2023
 */
public class DamageCalcResult {
    private final EntityRef ref;
    private List<Pair<DamageType, Integer>> damageDealt;

    public DamageCalcResult(EntityRef ref) {
        this.ref = ref;
    }

    public void add(DamageType type, int amount){

    }

    public EntityRef getRef() {
        return ref;
    }

    public List<Pair<DamageType, Integer>> getDamageDealt() {
        return damageDealt;
    }
}
