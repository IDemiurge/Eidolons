package main.ability.effects.oneshot.rpg;

import main.ability.ItemMaster;
import main.ability.conditions.special.CanActCondition;
import main.ability.effects.DC_Effect;
import main.content.CONTENT_CONSTS.ROLL_TYPES;
import main.content.PARAMS;
import main.elements.conditions.*;
import main.elements.targeting.SelectiveTargeting;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.DC_HeroObj;
import main.system.ConditionMaster;
import main.system.DC_ConditionMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;
import main.system.math.roll.RollMaster;

public class TossItemEffect extends DC_Effect {

    private Condition conditions;

    public TossItemEffect() {

    }

	/*
     * size? from hand?
	 * 
	 * interrupt?
	 */

    @Override
    public boolean applyThis() {
        DC_HeroObj source = (DC_HeroObj) ref.getSourceObj();
        Ref REF = ref.getCopy();
        conditions = new OrConditions(
                DC_ConditionMaster
                        .getSelectiveTargetingTemplateConditions(SELECTIVE_TARGETING_TEMPLATES.MY_ITEM),
                DC_ConditionMaster
                        .getSelectiveTargetingTemplateConditions(SELECTIVE_TARGETING_TEMPLATES.MY_WEAPON));
        if (!new SelectiveTargeting(conditions).select(REF)) {
            ref.getActive().setCancelled(true);
            return false;
        }
        DC_HeroItemObj item = (DC_HeroItemObj) REF.getTargetObj();
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

        DC_HeroObj unit = (DC_HeroObj) REF.getTargetObj();

        boolean result = roll(source, unit, item);
        if (item instanceof DC_QuickItemObj) {
            DC_QuickItemObj quickItem = (DC_QuickItemObj) item;
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
        unit.modifyParameter(PARAMS.C_N_OF_ACTIONS, -1);
        return true;
    }

    private boolean roll(DC_HeroObj source, DC_HeroObj unit, DC_HeroItemObj item) {
        String fail = "5*1.5*sqrt"
                + StringMaster.wrapInParenthesis(""
                + (1 + PositionMaster.getDistance(unit, source)));
        // account for range
        if (item instanceof DC_QuickItemObj) {
            DC_QuickItemObj quickItemObj = (DC_QuickItemObj) item;
        }
        Ref REF = ref.getCopy();
        REF.setTarget(source.getId());

        boolean result = RollMaster.roll(ROLL_TYPES.ACCURACY, "-", fail, ref, "@, missing the "
                + item.getName() + " toss", item.getName() + " toss");
        fail = "5";
        if (!result) {
            fail += "*2";
        }
        REF.setTarget(unit.getId());
        result = !RollMaster.roll(ROLL_TYPES.REFLEX, "-", fail, ref, "@, dropping the tossed "
                + item.getName(), item.getName() + " toss");

        return result;
    }

    private void dropped(DC_HeroItemObj item, DC_HeroObj unit) {
        // if potion - break; otherwise drop on the cell
        // ItemMaster.isWeapon(item);
        if (ItemMaster.isBreakable(item)) {
            item.broken(); // remove from game?
            if (item instanceof DC_QuickItemObj) {
                DC_QuickItemObj quickItemObj = (DC_QuickItemObj) item;
                if (quickItemObj.isConcoction()) {
                    quickItemObj.activate(Ref.getSelfTargetingRefCopy(unit)); // setForceTarget(true)
                }
            }
        } else {
            unit.getGame().getDroppedItemManager().itemFalls(unit.getCoordinates(), item);
        }
    }

}
