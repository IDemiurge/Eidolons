package eidolons.ability.effects.oneshot.rule;

import eidolons.content.PARAMS;
import eidolons.entity.item.HeroItem;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;

public class DurabilityReductionEffect extends MicroEffect implements OneshotEffect {

    private Boolean attacker;
    private Integer dmg_amount;
    private int durabilityLost;
    private Integer hardness;
    private Integer hardness2;

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
        if (armorItem != null) {
            armor = armorItem.getIntParam(PARAMS.ARMOR);
        }
        if (attacker == null) {// spell
            HeroItem item = (HeroItem) ref.getTargetObj();
            // mod = ref.getActive().getIntParam(param, base)
            durabilityLost = item.reduceDurabilityForDamage(dmg_amount, armor, 100, false);
            return true;
        }

        HeroItem item = (HeroItem) ((attacker) ? weapon : armorItem);
        if (item == null) {
            return false;
        }
//        if (attacker) { TODO wtf?
//            if (ref.getObj(KEYS.SPELL) != null) {
//                return false;
//            }
//        }

        // ref.getObj(KEYS.TARGET).getIntParam(PARAMS.ARMOR);

        int mod;
        mod = ((attacker) ? weapon : armorItem).getIntParam(PARAMS.DURABILITY_DAMAGE_MOD);
        if (hardness == null )
         hardness = ((!attacker) ? weapon : armorItem).getIntParam(PARAMS.HARDNESS);
        if (hardness2 == null )
            hardness2 = ((attacker) ? weapon : armorItem).getIntParam(PARAMS.HARDNESS);

        mod += hardness * 100 / hardness2;
        if (mod > 0) {
            durabilityLost = item.reduceDurabilityForDamage(amount, armor, mod, false);
        }

        // preCheck broken item TODO

        return true;
    }

    public int getDurabilityLost() {
        return durabilityLost;
    }


    public void setHardness(int hardness) {
        this.hardness = hardness;
    }

    public int getHardness() {
        return hardness;
    }

    public void setHardness2(int hardness2) {
        this.hardness2 = hardness2;
    }

    public int getHardness2() {
        return hardness2;
    }
}
