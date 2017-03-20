package main.game.logic.combat.attack;

import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.enums.entity.ItemEnums;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.rules.RuleMaster;
import main.rules.RuleMaster.RULE;
import main.system.DC_Formulas;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

/**
 * Created by JustMe on 3/16/2017.
 */
public class ParryRule {

    DC_Game game;

    public ParryRule(DC_Game game) {
        this.game = game;
    }

    protected boolean tryParry(Attack attack) {
        // TODO could return and Integer for dmg reduction or -1 if all
        // absorbed!
        if (!RuleMaster.isRuleTestOn(RULE.PARRYING)) {
            if (!canParry(attack)) {
                return false;
            }
        }

        int attackValue = DefenseVsAttackRule.getAttackValue(attack);
        int defenseValue = DefenseVsAttackRule.getDefenseValue(attack);

        float chance = DefenseVsAttackRule.getProportionBasedChance(attackValue, defenseValue, false);
        chance += attack.getAttacked().getIntParam(PARAMS.PARRY_CHANCE);
        chance += -attack.getAttacker().getIntParam(PARAMS.PARRY_PENETRATION);
        Integer chanceRounded = Math.round(chance);

        game.getLogManager().newLogEntryNode(ENTRY_TYPE.PARRY, attack.getAttacked().getName(),
         attack.getAction().getName(), attack.getAttacker().getName(),
         chanceRounded.toString());
        if (!RandomWizard.chance(chanceRounded)) {
            game.getLogManager().log(attack.getAttacked().getName() + " fails to parry " + attack.getAction().getName()
             + " from " + attack.getAttacker().getNameIfKnown()
             + StringMaster.wrapInParenthesis(chanceRounded + "%"));
            game.getLogManager().doneLogEntryNode();
            if (!RuleMaster.isRuleTestOn(RULE.PARRYING)) {
                return false;
            }
        }
        Unit attacked = attack.getAttacked();
        Unit attacker = attack.getAttacker();
        boolean dual = false;
        if (attacked.checkDualWielding()) {
            dual = true;
        }
        int damage =
         attack.getPrecalculatedDamageAmount(); //raw damage
//         attack.getDamage();
        game.getLogManager().log(attack.getAttacked().getName() + " parries " + attack.getAction().getName() + " from "
         + attack.getAttacker().getNameIfKnown()
         + StringMaster.wrapInParenthesis(chanceRounded + "%") + ", deflecting " + damage
         + " " + attack.getDamageType() + " damage");
        int mod = DC_Formulas.DEFAULT_PARRY_DURABILITY_DAMAGE_MOD;
        if (dual) {
            mod /= 2;
        }
        AnimPhase animPhase = new AnimPhase(PHASE_TYPE.PARRY, chanceRounded);
        int durabilityLost = attacked.getWeapon(false).reduceDurabilityForDamage(damage, damage,
         mod, false);
        animPhase.addArg(durabilityLost);
        if (dual) {
            durabilityLost += attacked.getWeapon(true).reduceDurabilityForDamage(damage, damage,
             mod, false);
            animPhase.addArg(durabilityLost);
        }

        // if (BROKEN)
        // return false
        mod = DC_Formulas.DEFAULT_PARRY_DURABILITY_DAMAGE_MOD;
        durabilityLost = durabilityLost * mod / 100;
        attacker.getActiveWeapon(attack.isOffhand()).reduceDurability(durabilityLost);
        animPhase.addArg(durabilityLost);

        attack.getAnimation().addPhase(animPhase);

        // game.getLogManager().doneLogEntryNode(); ???
        return true;

    }
    // precalculateRawDamageForDisplay
    private boolean canParry(Attack attack) {
        // if (!RuleMaster.isParryOn())return false;
        if (attack.getAttacked().getIntParam(PARAMS.PARRY_CHANCE) <= 0) {
            return false;
        }
        if (attack.isSneak()) {
            return false;
        }
        if (attack.isCritical()) {
            return false;
        }
        if (attack.isRanged()) {
            return false;
        }
        if (attack.getWeapon().getWeaponType() == ItemEnums.WEAPON_TYPE.NATURAL) {
            return false;
        }
        if (attack.getWeapon().getWeaponType() == ItemEnums.WEAPON_TYPE.BLUNT) {
            return false;
        }
        // if (attack.getWeapon().getWeaponSize() == WEAPON_SIZE.TINY)
        // {
        // TODO
        DC_WeaponObj parryWeapon = attack.getAttacked().getActiveWeapon(false);
        if (Math.abs(DC_ContentManager.compareSize(parryWeapon.getWeaponSize(), attack.getWeapon()
         .getWeaponSize())) > 2) {
            if (attack.getAttacked().checkDualWielding()) {

            } else {
                return false;
            }
        }
        // }
        return true;
    }


}
