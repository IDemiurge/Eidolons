package main.game.logic.combat.damage;

import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiDamage extends Damage{
    List<Damage> additionalDamage;

    public MultiDamage(DAMAGE_TYPE damage_type, Ref ref, int amount, List<Damage> additionalDamage) {
        super(damage_type, ref, amount);
        this.additionalDamage = additionalDamage;
    }

    public MultiDamage(DAMAGE_TYPE damage_type, Ref ref, int amount, Damage... additionalDamage) {
        super(damage_type, ref, amount);
        this.additionalDamage =new LinkedList<>( Arrays.asList(     additionalDamage));
    }

    public List<Damage> getAdditionalDamage() {
        return additionalDamage;
    }

}
