package eidolons.ability.costs;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.ai.tools.target.AI_SpellMaster;
import eidolons.game.battlecraft.rules.parameters.EssenceRule;
import main.content.enums.entity.SpellEnums;
import main.elements.costs.CostImpl;
import main.elements.costs.Payment;
import main.entity.Ref;
import main.entity.obj.IActiveObj;

/**
 * When you pay essence - it goes into the spell's target? Or leaks kinda?
 *
 * As per spell type:
 * Single - around target
 * Summon - to unit
 * Zone - split on zone cells
 * Global - around source
 *
 * curious fact - it is to be applied ... after..?
 */
public class EssenceCost extends CostImpl {

    public EssenceCost(int value) {
        super(new Payment(PARAMS.C_ESSENCE, value));
    }

    @Override
    public boolean pay(Ref ref) {
        //measure how much was actually paid?
        //does it matter if spell was resisted etc?
        boolean result = super.pay(ref);
        IActiveObj spell = ref.getActive();
        spell.setOnComplete(()-> {

        int paid = getPayment().getLastPaid();
        SpellEnums.SPELL_CATEGORY category = AI_SpellMaster.getSpellCategory(spell);
        switch (category) {
            case SINGLE:
                EssenceRule.split(paid, (BattleFieldObject) spell.getRef().getTargetObj());
                break;
            case SUMMON:
                //TODO NF Rules - Essence
                break;
            case CONJURE:
                break;
            case ZONE:
                break;
            case GLOBAL:
                break;
        }
                }
        );
        return result;
    }
}



















