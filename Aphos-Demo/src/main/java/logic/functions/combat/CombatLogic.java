package logic.functions.combat;

import eidolons.game.core.Core;
import libgdx.GdxMaster;
import logic.content.AUnitEnums;
import logic.entity.Entity;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.LogicController;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;

public class CombatLogic extends LogicController {
    private static final int BASE_HIT_CHANCE = 75;

    public CombatLogic(GameController controller) {
        super(controller);
    }

//    public boolean attackedBy(Unit unit) {
//        return doAttack(unit, hero);
//    }
    public void attack(Unit unit) {
        Core.onThisOrNonGdxThread(()-> attack(hero, unit));
    }
    public void attack(Entity source, Entity target) {
        GuiEventManager.trigger(GuiEventType.DUMMY_ANIM_ATK, target);
        boolean result = doAttack(source, target);
        if (result){
            //hit
            GuiEventManager.trigger(GuiEventType.DUMMY_ANIM_HIT, target);
        } else {
            //die
            GuiEventManager.trigger(GuiEventType.DUMMY_ANIM_DEATH, target);
        }
    }
    private boolean doAttack(Entity source, Entity target) {

        boolean result = true;

        int attack = source.getInt(AUnitEnums.ATTACK);
        int defense = target.getInt(AUnitEnums.DEFENSE);

        int hitChance = BASE_HIT_CHANCE + getHitChanceMod(attack, defense);
        if (!RandomWizard.chance(hitChance)) {
            return true;
        }

        int hp = target.getInt(AUnitEnums.HP);
        int armor = target.getInt(AUnitEnums.ARMOR);
        int damage = source.getInt(AUnitEnums.DAMAGE);

        damage -= armor;
        hp -= damage;

        target.setValue(AUnitEnums.HP, hp);

        if (hp <= 0)
            result = false;

        return result;
    }

    private int getHitChanceMod(int attack, int defense) {
        int diff = attack - defense;
        if (diff == 0)
            return 0;
        int mod = 0;
        if (diff > 0) {
            // 54 vs 28:  diff == 26 => first 10 gives +0.5, then +1, then +1.5 => 5 + 10 + 9
            float modifier = 0.5f;
            for (int i = 0; i < diff / 10 + 1; i++) {
                int n = (diff - i * 10);
                if (n >= 10) n = 10;
                else n = n % 10;

                mod += n * modifier;
                modifier += 0.25f;
            }
        } else {
            float modifier = 2.5f;
            diff = -diff;
            for (int i = 0; i < diff / 10 + 1; i++) {
                int n = (diff - i * 10);
                if (n >= 10) n = 10;
                else n = n % 10;

                mod += n * modifier;
                modifier = modifier * 0.8f;
            }
        }
        return mod;
    }
}
