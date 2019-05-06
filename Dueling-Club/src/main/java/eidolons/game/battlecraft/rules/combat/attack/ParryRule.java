package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.system.DC_Formulas;
import main.content.enums.entity.ItemEnums;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
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
        if (!RuleKeeper.isRuleTestOn(RULE.PARRYING)) {
            if (!canParry(attack)) {
                return false;
            }
        }

        int attackValue = DefenseVsAttackRule.getAttackValue(attack);
        int defenseValue = DefenseVsAttackRule.getDefenseValue(attack);

        float chance = DefenseVsAttackRule.getProportionBasedChance(attackValue, defenseValue, false);
        chance += attack.getAttackedUnit().getIntParam(PARAMS.PARRY_CHANCE);
        chance += -attack.getAttacker().getIntParam(PARAMS.PARRY_PENETRATION);
        Integer chanceRounded = Math.round(chance);

//        if (!simulation)
        if (attack.getAction().getGame().getCombatMaster().isChancesOff()) {
            if (chanceRounded < 50)
                chanceRounded = 0;
            else chanceRounded = 100;
        }

        game.getLogManager().newLogEntryNode(ENTRY_TYPE.PARRY, attack.getAttackedUnit().getName(),
         attack.getAction().getName(), attack.getAttacker().getName(),
         chanceRounded.toString());
        if (!RandomWizard.chance(chanceRounded)) {
            game.getLogManager().log(attack.getAttackedUnit().getName() + " fails to parry " + attack.getAction().getName()
             + " from " + attack.getAttacker().getNameIfKnown()
             + StringMaster.wrapInParenthesis(chanceRounded + "%"));
            game.getLogManager().doneLogEntryNode();
            if (!RuleKeeper.isRuleTestOn(RULE.PARRYING)) {
                return false;
            }
        }
        Unit attacked = (Unit) attack.getAttackedUnit();
        Unit attacker = attack.getAttacker();
        boolean dual = false;
        if (attacked.checkDualWielding()) {
            dual = true;
        }
        int damage =
         attack.getPrecalculatedDamageAmount(); //raw damage
//         attack.getDamage();

        Ref ref =  (attacked.getRef()).getCopy();
        ref.setAmount(damage);

        game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.ATTACK_PARRIED, ref));

        FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.ATTACK_PARRIED,
                "Counter Attack!", attacked);

        game.getLogManager().log(attack.getAttackedUnit().getName() + " parries " + attack.getAction().getName() + " from "
         + attack.getAttacker().getNameIfKnown()
         + StringMaster.wrapInParenthesis(chanceRounded + "%") + ", deflecting " + damage
         + " " + attack.getDamageType() + " damage");
        int mod = DC_Formulas.DEFAULT_PARRY_DURABILITY_DAMAGE_MOD;
        if (dual) {
            mod /= 2;
        }
        int durabilityLost = attacked.getWeapon(false).reduceDurabilityForDamage(damage, damage,
         mod, false);
        if (dual) {
            durabilityLost += attacked.getWeapon(true).reduceDurabilityForDamage(damage, damage,
             mod, false);
        }

        // if (BROKEN)
        // return false
        mod = DC_Formulas.DEFAULT_PARRY_DURABILITY_DAMAGE_MOD;
        durabilityLost = durabilityLost * mod / 100;
        attacker.getActiveWeapon(attack.isOffhand()).reduceDurability(durabilityLost);

        if (CoreEngine.isPhaseAnimsOn()) {
            //TODO
        }

        // game.getLogManager().doneLogEntryNode(); ???
        return true;

    }

    // precalculateRawDamageForDisplay
    private boolean canParry(Attack attack) {
        // if (!RuleMaster.isParryOn())return false;
        Unit attackedUnit = (Unit) attack.getAttackedUnit();
        if (attackedUnit == null)
            return false;
        if (attackedUnit.getIntParam(PARAMS.PARRY_CHANCE) <= 0) {
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
        DC_WeaponObj parryWeapon = attackedUnit.getActiveWeapon(false);
        if (Math.abs(DC_ContentValsManager.compareSize(parryWeapon.getWeaponSize(), attack.getWeapon()
         .getWeaponSize())) > 2) {
            if (attackedUnit.checkDualWielding()) {

            } else {
                return false;
            }
        }
        // }
        return true;
    }


}
