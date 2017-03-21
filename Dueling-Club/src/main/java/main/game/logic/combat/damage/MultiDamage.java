package main.game.logic.combat.damage;

import java.util.List;

public class MultiDamage extends Damage{
    List<Damage> additionalDamage;

    public List<Damage> getAdditionalDamage() {
        return additionalDamage;
    }

    public void setAdditionalDamage(List<Damage> additionalDamage) {
        this.additionalDamage = additionalDamage;
    }
}
