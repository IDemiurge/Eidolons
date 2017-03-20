package main.game.logic.combat.damage;

import main.ability.effects.oneshot.DealDamageEffect;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;

import java.util.List;

/**
 * Created by JustMe on 3/20/2017.
 */
public class DamageFactory {

  public static Damage  getDamageFromEffect(DealDamageEffect effect, int amount){
          Damage damageObject;
          List<Damage> list =
           DamageCalculator.getBonusDamageList(effect.getRef(),effect. isMagical()
            ? DAMAGE_CASE.SPELL : DAMAGE_CASE.ACTION);
          if (!list.isEmpty())
              damageObject = new MultiDamage(effect.getDamageType(), effect.getRef(), amount, list);
          else damageObject = new Damage(effect.getDamageType(),
           effect.getRef(), amount);

          return damageObject;
      }

    public static Damage getDamageForAttack(DAMAGE_TYPE dmg_type, Ref ref, Integer final_amount) {
        List<Damage> list =
         DamageCalculator.getBonusDamageList(ref, DAMAGE_CASE.ATTACK);
        Damage damageObj = new MultiDamage(dmg_type, ref, final_amount,list);

        return damageObj;
    }
}
