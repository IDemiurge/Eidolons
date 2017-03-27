package main.game.logic.combat.damage;

import java.util.LinkedList;
import java.util.List;

public class MultiDamage extends Damage {
    List<Damage> additionalDamage;

    public MultiDamage() {

    }



    public List<Damage> getAdditionalDamage() {
        if (additionalDamage == null)
            additionalDamage = new LinkedList<>();
        return additionalDamage;
    }

    public void setAdditionalDamage(List<Damage> additionalDamage) {
        this.additionalDamage = additionalDamage;
    }
}
