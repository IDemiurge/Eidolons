package eidolons.ability.effects.oneshot.activation;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.companion.Order;
import eidolons.game.battlecraft.ai.advanced.companion.OrderFactory;
import eidolons.game.battlecraft.ai.advanced.companion.OrderMaster;
import main.ability.effects.OneshotEffect;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;

//Untested
public class OrderEffect extends DC_Effect implements OneshotEffect {


    private final Boolean cancel;
    private ORDER_PRIORITY_MODS strictPriority;
    private Boolean customTarget;

    public OrderEffect(Boolean customTarget, ORDER_PRIORITY_MODS strictPriority) {
        this.strictPriority = strictPriority;
        this.customTarget = customTarget;
        cancel = false;
    }

    public OrderEffect(Boolean customTarget, String strictPriority) {
        this(customTarget, new EnumMaster<ORDER_PRIORITY_MODS>().retrieveEnumConst(ORDER_PRIORITY_MODS.class, strictPriority));
    }

    public OrderEffect(Boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean applyThis() {
        boolean partyTargeting = getTarget() == getSource();
        Order order = cancel ? null : OrderFactory.getOrder(partyTargeting, ref.getActive(),
         strictPriority);
        if (partyTargeting) {
            getSource().getAI().getGroup().getMembers().forEach(hero ->
             trySetOrder(hero, order, partyTargeting));
        } else {
            Unit unit = (Unit) ref.getTargetObj();
            trySetOrder(unit, order, partyTargeting);
        }
        if (customTarget) {
//            OrderMaster.selectCustomTarget(order, active, getSource());
        }
        return true;
    }

    private void trySetOrder(Unit hero, Order order, boolean partyTargeting) {
        //TODO cancel with chance? :)
        if (cancel) {
//            hero.getAI().getCurrentOrder().getOrderGiver()
        }
        int chance = cancel ? 100 : OrderMaster.getSuccessChance(partyTargeting, order, hero, getSource(), ref.getActive());
        if (!RandomWizard.chance(chance)) {
            getGame().getLogManager().logOrderFailed(order, hero);
        } else {
            hero.getAI().setCurrentOrder(order);
        }
    }

}
