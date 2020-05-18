package eidolons.game.battlecraft.rules.counter;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.ActionRule;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.math.roll.RollMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effect.MOD;
import main.ability.effects.Effects;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.system.MetaEnums;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.system.math.MathMaster;

/*
 * similar to knockdown - unit will spend action points to reduce ensnare counters, 
 * 1 each time their turn comes up in the queue. 
 * Implementation? 
 * Oneshot effect upon applying, then Conditional status if there are still any counters! 
 * To be used by Spiders of course :) 
 *  
 * So this can be implemented with a trigger... perhaps I need another EnsnaredRule ;) 
 * 
 */

//TODO a "mental" version would be great too - willpower rolls and spellpower cutaways 

public class EnsnaredRule extends DC_CounterRule implements ActionRule {

    private static final String AGI_REDUCTION_PER_COUNTER = "(-1)";
    private static final String DEX_REDUCTION_PER_COUNTER = "(-1)";
    private static final Integer CUT_AWAY_MOD = 25;

    public EnsnaredRule(DC_Game game) {
        super(game);
    }

    public static void applied(Unit unit) {

        // TODO Auto-generated method stub

    }

    @Override
    protected Effect getSpecialRoundEffects() {
        // reduce ap? how to getOrCreate the number? min(n_of_counters, max ap)
        return super.getSpecialRoundEffects();
    }

    @Override
    public String getCounterName() {
        return COUNTER.Ensnared.getName();
    }

    @Override
    public COUNTER getCounter() {
        return COUNTER.Ensnared;
    }

    @Override
    public int getCounterNumberReductionPerTurn(BattleFieldObject unit) {
        // unit spends all of his AP to reduce the counter count?
        // this can be dynamic i suppose
        return 0;
    }

    @Override
    public STATUS getStatus() {
        if (checkEnsnared()) {
            return UnitEnums.STATUS.ENSNARED;
        }
        return null;
    }

    private boolean checkEnsnared() {
        if (object.checkClassification(UnitEnums.CLASSIFICATIONS.SMALL)) {
            return getNumberOfCounters(object) > 10;
        }
        if (object.checkClassification(UnitEnums.CLASSIFICATIONS.HUGE)) {
            return getNumberOfCounters(object) > 50;
        }
        return getNumberOfCounters(object) > 20;
    }

    @Override
    public String getBuffName() {
        return MetaEnums.STD_BUFF_NAMES.Entangled.getName();
    }

    @Override
    protected Effect getEffect() {
        // TODO reduce defense, make immobile, possibly block counters

        // the question is - what do "small numbers of ensnare counters" do???
        // ideally, they would force to spend AP while reducing stuff in the
        // meanwhile

        // so this must act as *action rule*... I can add it to such!

        return new Effects(new ModifyValueEffect(PARAMS.AGILITY,
         MOD.MODIFY_BY_PERCENT, getNumberOfCounters(object)
         + " * " + AGI_REDUCTION_PER_COUNTER),
         // TODO square root maybe? so that there is some chance at least for big
         // values and some harm from small ones!
         new ModifyValueEffect(PARAMS.DEXTERITY,
          MOD.MODIFY_BY_PERCENT,
          getNumberOfCounters(object) + " * "
           + DEX_REDUCTION_PER_COUNTER));
    }

    protected Integer getEffectLayer() {
        return Effect.BASE_LAYER;
    }

    @Override
    public void actionComplete(ActiveObj activeObj) {

    }

    @Override
    public boolean unitBecomesActive(Unit unit) {
        if (getNumberOfCounters(unit) <= 0) {
            return true;
        }
        Ref ref = new Ref(unit);
        ref.setTarget(unit.getId());
        if (!RollMaster.roll(GenericEnums.ROLL_TYPES.BODY_STRENGTH, "-",
         getNumberOfCounters(unit) + "*1.5", ref, " and breaks free!",
         "Entanglement")) {
            unit.setCounter(getCounterName(), 0);
            unit.modifyParameter(PARAMS.C_ATB, -10); //TODO review
            return false;
        }

        if (RollMaster.roll(GenericEnums.ROLL_TYPES.QUICK_WIT, "-",
         getNumberOfCounters(unit) + "/2.5", ref,
         "@, unable to figure out how to cut free...", "Entanglement")) {
            unit.modifyParameter(PARAMS.C_ATB, -10);
            return false;
        }

        if (checkCutAway(false)) {
            return false;
        }
        if (checkCutAway(true)) {
            return false;
        }

        // TODO ally help PLS

        return false;
    }

    private boolean checkCutAway(boolean offhand) {
        int amount = 0;
        if (object instanceof Unit) {
            Unit unit = ((Unit) object);
        DC_WeaponObj weapon = unit.getWeapon(offhand);
        if (weapon == null) {
            weapon = unit.getNaturalWeapon(offhand);
        }

        DC_UnitAction attack_action = unit.getAction(offhand ? "Offhand Attack"
         : "Attack");
        if (!attack_action.canBeActivated(unit.getRef(), true)) // unit.getRef(),
        // true
        {
            return false; // TODO log " has no strength left to cut the bonds"
        }

        // Integer sta_cost = attack_action.getIntParam(PARAMS.STA_COST);
        // if (unit.getIntParam(PARAMS.C_STAMINA)<
        // sta_cost)
        // {
        // }
        if (weapon.getDamageType() != GenericEnums.DAMAGE_TYPE.BLUDGEONING) {
            amount += unit.calculateDamage(offhand);
        }

        amount = MathMaster.applyMod(amount, object
         .getIntParam(offhand ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK));
        amount = MathMaster.applyMod(amount, CUT_AWAY_MOD);
        amount = Math.min(getNumberOfCounters(object), amount);
            unit.modifyCounter(getCounterName(), -amount);

        attack_action.payCosts();

        // unit.modifyParameter(PARAMS.C_STAMINA, -sta_cost);
        // unit.modifyParameter(PARAMS.C_N_OF_ACTIONS, -1);
        String string = ((offhand) ? "...Then " : object.getName())
         + " cuts away "
         + ((offhand) ? " another " + amount : amount
         + " Ensnare counters") + " with " + weapon.getName();

        if (getNumberOfCounters(object) <= 0) {
            string += " and breaks free!";
            game.getLogManager().log(string);
            return true;
        } else {
            game.getLogManager().log(string);
        }
        }
        return false;
    }

}
