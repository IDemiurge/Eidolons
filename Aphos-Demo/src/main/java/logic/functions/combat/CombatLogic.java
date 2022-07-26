package logic.functions.combat;

import com.badlogic.gdx.Input;
import content.LOG;
import eidolons.game.core.Core;
import libgdx.GdxMaster;
import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.functions.LogicController;
import logic.lane.LanePos;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.auxiliary.RandomWizard;
import main.system.threading.WaitMaster;

import java.util.*;

import static logic.functions.combat.CombatLogic.ATK_TYPE.Power;
import static logic.functions.combat.CombatLogic.ATK_TYPE.Quick;

public class CombatLogic extends LogicController {
    private static final int BASE_HIT_CHANCE = 75;
    private static final int PWR_HIT_CHANCE = 65;
    private static final int QK_HIT_CHANCE = 50;

    public boolean canAttack(Entity attacker, Entity target) {
        //TODO front ?
        if (attacker instanceof Unit) {
            if (!canUnitAttack((Unit) attacker, target)) {
                return false;
            }
        } else {
            if (!attacker.isInFrontLine()) {
                return false;
            }
            int diffX = Math.abs(attacker.getLane() - target.getLane());
            int diffY = Math.abs(attacker.getCell() - target.getCell());
            long diff = Math.round(Math.hypot(diffX, diffY));
            if (attacker.getInt(AUnitEnums.RANGE) < diff)
                return false;
        }
        return true;
    }

    public boolean canUnitAttack(Unit attacker, Entity target) {
        if (attacker.getPos().cell != 0) {
            return false;
        }
        int diff = Math.abs(attacker.getLane() - target.getLane());
        if (attacker.getInt(AUnitEnums.RANGE) < diff)
            return false;
        return true;
    }

    public enum ATK_OUTCOME {
        Lethal, Ineffective, Hit, Miss
    }

    public enum ATK_TYPE {
        Power, Standard, Quick
    }


    public CombatLogic(GameController controller) {
        super(controller);
    }

    public void attack(Unit unit) {
        if (!canAttack(Aphos.hero, unit)) {
            LOG.log("Cannot attack ", unit);
            return;
        }
        ATK_TYPE type = ATK_TYPE.Standard;
        boolean ctrl = GdxMaster.isKeyPressed(Input.Keys.CONTROL_LEFT);
        boolean alt = GdxMaster.isKeyPressed(Input.Keys.ALT_LEFT);
        boolean shift = GdxMaster.isKeyPressed(Input.Keys.SHIFT_LEFT);
        if (shift && !ctrl && !alt)
            type = Quick;
        if (!shift && ctrl && alt)
            type = Power;
        ATK_TYPE finalType = type;
        Core.onThisOrNonGdxThread(() -> getAtbLogic().attackAction(Aphos.hero, finalType));
        Core.onThisOrNonGdxThread(() -> attack(Aphos.hero, unit, finalType));
    }

    public void explode(Unit source) {
        Map<Unit, ATK_OUTCOME> impactedUnits = new LinkedHashMap<>();
        Map<Hero, ATK_OUTCOME> impactedHeroes = new LinkedHashMap<>();
        int dst = source.getInt(AUnitEnums.AOE);
        int damage = source.getInt(AUnitEnums.EXPLODE);
        LanePos pos = source.getPos();

        if (pos.dst(Aphos.hero.getPos()) <= dst) {
            ATK_OUTCOME outcome = explodeImpact(Aphos.hero, damage);
            impactedHeroes.put(Aphos.hero, outcome);
        }
        Aphos.game.getUnits().stream().filter(unit -> unit.getPos().dst(pos) <= dst).forEach(
                unit -> impactedUnits.put(unit, explodeImpact(Aphos.hero, damage))
        );
        boolean coreImpact = source.getPos().cell == 0;
        //checkblock
        if (coreImpact) {
            game.getCoreHandler().explodeDamage(damage);
            //core
        }
        kill(source, source);
        GuiEventManager.triggerWithNamedParams(AphosEvent.DUMMY_ANIM_EXPLODE, "source", source,
                "target_heroes", impactedHeroes, "target_units", impactedUnits);

    }

    private ATK_OUTCOME explodeImpact(Entity target, int damage) {
        int resist = target.getInt(AUnitEnums.RESIST);
        if (resist >=100)
            return ATK_OUTCOME.Ineffective;
        damage = damage - damage*resist/100;

        if (!target.damage(damage))
            return ATK_OUTCOME.Lethal;

        return ATK_OUTCOME.Hit;
    }

    private void kill(Entity target, Entity source) {
        target.killed(source);
        GuiEventManager.triggerWithNamedParams(AphosEvent.DUMMY_ANIM_DEATH, "target", target);
        //TODO ?!

    }

    public void attack(Entity source, Entity target, ATK_TYPE type) {
        ATK_OUTCOME result = doAttack(source, target, type);
        GuiEventManager.triggerWithNamedParams(AphosEvent.DUMMY_ANIM_ATK, "target", target, "outcome", result, "atk_type", type);
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.ATK_ANIMATION_FINISHED);//, 1000, ActionAnims.DUMMY_ANIM_TYPE.atk);
        switch (result) {
            case Lethal:
                kill(target, source);
            case Hit:
            case Miss:
            case Ineffective:
                GuiEventManager.triggerWithNamedParams(AphosEvent.DUMMY_ANIM_HIT, "target", target, "outcome", result);
                break;
        }
    }

    private ATK_OUTCOME doAttack(Entity source, Entity target, ATK_TYPE type) {

        ATK_OUTCOME result = ATK_OUTCOME.Hit;

        int attack = source.getInt(AUnitEnums.ATTACK);
        int defense = target.getInt(AUnitEnums.DEFENSE);

        int hitChance = getHitChance(type) + getHitChanceMod(attack, defense);
        if (!RandomWizard.chance(hitChance)) {
            System.out.printf("Miss! (%s%% to hit)%n", hitChance);
            return ATK_OUTCOME.Miss;
        }

        System.out.printf("Hit! (%s%% to hit)%n", hitChance);
        int hp = target.getInt(AUnitEnums.HP);
        int armor = target.getInt(AUnitEnums.ARMOR);
        int damage = source.getInt(AUnitEnums.DAMAGE);

        if (damage <= armor) {
            result = ATK_OUTCOME.Ineffective;
            System.out.printf("Attack ineffective! (Damage: %s | Armor: %s)%n", damage, armor);
            return result;
        }
        damage -= armor;
        hp -= damage;

        target.setValue(AUnitEnums.HP, hp);

        if (hp <= 0)
            result = ATK_OUTCOME.Lethal;

        System.out.printf("Dealt %s damage! (%s | HP left: %s)%n", damage, result, hp);
        return result;
    }

    private int getHitChance(ATK_TYPE type) {
        switch (type) {
            case Power:
                return PWR_HIT_CHANCE;
            case Standard:
                return BASE_HIT_CHANCE;
            case Quick:
                return QK_HIT_CHANCE;
        }
        return 0;
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


    //    public boolean attackedBy(Unit unit) {
//        return doAttack(unit, hero);
//    }
}
