package eidolons.ability.ignored.oneshot.rule;

import eidolons.ability.conditions.special.CanActCondition;
import eidolons.ability.effects.DC_Effect;
import eidolons.content.PARAMS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.unit.Unit;
import eidolons.entity.mngr.item.ItemMaster;
import eidolons.system.DC_ConditionMaster;
import main.ability.effects.OneshotEffect;
import main.elements.conditions.*;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;
import main.system.math.PositionMaster;

public class TossItemEffect extends DC_Effect implements OneshotEffect {

    public TossItemEffect() {

    }

	/*
     * size? from hand?
	 * 
	 * interrupt?
	 */

    @Override
    public boolean applyThis() {
        Unit source = (Unit) ref.getSourceObj();
        Ref REF = ref.getCopy();
        Condition conditions = new OrConditions(
                DC_ConditionMaster
                        .getSelectiveTargetingTemplateConditions(SELECTIVE_TARGETING_TEMPLATES.MY_ITEM),
                DC_ConditionMaster
                        .getSelectiveTargetingTemplateConditions(SELECTIVE_TARGETING_TEMPLATES.MY_WEAPON));
        if (!new SelectiveTargeting(conditions).select(REF)) {
            ref.getActive().setCancelled(true);
            return false;
        }
        HeroItem item = (HeroItem) REF.getTargetObj();
        conditions = new Conditions(
         // ++ Max distance?
         new DistanceCondition(ref.getActive().getIntParam(PARAMS.RANGE, false) + ""),
         // new NumericCondition("{match_c_n_of_actions}", "1"),
         new CanActCondition(KEYS.MATCH),
         new NotCondition(ConditionMaster.getSelfFilterCondition()),
         DC_ConditionMaster
          .getSelectiveTargetingTemplateConditions(SELECTIVE_TARGETING_TEMPLATES.ANY_ALLY));
        // non-immobile, ++facing?
        if (!new SelectiveTargeting(conditions).select(REF)) {
            ref.getActive().setCancelled(true);
            return false;
        }

        Unit unit = (Unit) REF.getTargetObj();

        boolean result = roll(source, unit, item);
        if (item instanceof QuickItem) {
            QuickItem quickItem = (QuickItem) item;
            source.removeQuickItem(quickItem);
            if (result) {
                unit.addQuickItem(quickItem);
            } else {
                dropped(item, unit);
            }

        } else {
            source.unequip(item, null);
            if (result) {
                unit.addItemToInventory(item); // TODO equip in hand if
            }
// possible? spend AP?
            else {
                dropped(item, unit);
            }

        }
        // ref.getObj(KEYS.ITEM);
        return true;
    }

    private boolean roll(Unit source, Unit unit, HeroItem item) {
        String fail = "5*1.5*sqrt"
         + StringMaster.wrapInParenthesis(""
         + (1 + PositionMaster.getDistance(unit, source)));
        // account for range
        if (item instanceof QuickItem) {
            QuickItem quickItemObj = (QuickItem) item;
        }
        Ref REF = ref.getCopy();
        REF.setTarget(source.getId());
//TODO
        // boolean result = RollMaster.roll(GenericEnums.ROLL_TYPES.ACCURACY, "-", fail, ref, "@, missing the "
        //  + item.getName() + " toss", item.getName() + " toss");
        // fail = "5";
        // if (!result) {
        //     fail += "*2";
        // }
        // REF.setTarget(unit.getId());
        // result = !RollMaster.roll(GenericEnums.ROLL_TYPES.REFLEX, "-", fail, ref, "@, dropping the tossed "
        //  + item.getName(), item.getName() + " toss");
        // return result;
        return false;
    }

    private void dropped(HeroItem item, Unit unit) {
        // if potion - break; otherwise drop on the cell
        // ItemMaster.isWeapon(item);
        if (ItemMaster.isBreakable(item)) {
            item.broken(); // remove from game?
            if (item instanceof QuickItem) {
                QuickItem quickItemObj = (QuickItem) item;
                if (quickItemObj.isConcoction()) {
                    quickItemObj.activate(Ref.getSelfTargetingRefCopy(unit)); // setForceTarget(true)
                }
            }
        } else {
            unit.getGame().getDroppedItemManager().itemFalls(unit.getCoordinates(), item);
        }
    }

}
