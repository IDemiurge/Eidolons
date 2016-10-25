package main.ability.effects.standard;

import main.ability.effects.oneshot.MicroEffect;
import main.content.PARAMS;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroItemObj;
import main.entity.obj.Obj;

public class DurabilityReductionEffect extends MicroEffect {

    private Boolean attacker;
    private Integer dmg_amount;
    private int durabilityLost;
    private boolean simulation;

    public DurabilityReductionEffect(Boolean attacker) {
        this.attacker = attacker;
    }

    public DurabilityReductionEffect(Boolean attacker, Integer dmg_amount) {
        this.attacker = attacker;
        this.dmg_amount = dmg_amount;
    }

    @Override
    public boolean applyThis() {
        Obj weapon = ref.getObj(KEYS.WEAPON);
        Obj armorItem = ref.getTargetObj().getRef().getObj(KEYS.ARMOR);
        int amount = (dmg_amount == null) ? ref.getAmount() : dmg_amount;
        int armor = 0;
        durabilityLost = 0;
        if (armorItem != null)
            armor = armorItem.getIntParam(PARAMS.ARMOR);
        if (attacker == null) {// spell
            DC_HeroItemObj item = (DC_HeroItemObj) ref.getTargetObj();
            // mod = ref.getActive().getIntParam(param, base)
            durabilityLost = item.reduceDurabilityForDamage(dmg_amount, armor, 100, simulation

            );
            return true;
        }

        DC_HeroItemObj item = (DC_HeroItemObj) ((attacker) ? weapon : armorItem);
        if (item == null)
            return false;
        if (attacker) {
            if (ref.getObj(KEYS.SPELL) != null)
                return false;
        }

        // ref.getObj(KEYS.TARGET).getIntParam(PARAMS.ARMOR);

        int mod = 0;
        mod = ((attacker) ? weapon : armorItem).getIntParam(PARAMS.DURABILITY_DAMAGE_MOD);
        int hardness = ((!attacker) ? weapon : armorItem).getIntParam(PARAMS.HARDNESS);
        int hardness2 = ((attacker) ? weapon : armorItem).getIntParam(PARAMS.HARDNESS);

        mod += hardness * 100 / hardness2;
        if (mod > 0)
            durabilityLost = item.reduceDurabilityForDamage(amount, armor, mod, simulation);

        // check broken item TODO

        return true;
    }

    public int getDurabilityLost() {
        return durabilityLost;
    }

    public void setSimulation(boolean simulation) {
        this.simulation = simulation;

    }

}
