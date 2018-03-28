package main.game.battlecraft.rules.combat.damage;

import java.util.ArrayList;
import java.util.List;

public class MultiDamage extends Damage {
    List<Damage> additionalDamage;

    public MultiDamage() {

    }


    public List<Damage> getAdditionalDamage() {
        if (additionalDamage == null) {
            additionalDamage = new ArrayList<>();
        }
        return additionalDamage;
    }

    public void setAdditionalDamage(List<Damage> additionalDamage) {
        this.additionalDamage = additionalDamage;
    }
}
