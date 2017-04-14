package main.ability.effects.oneshot.activation;

import main.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.entity.obj.unit.Unit;
import main.game.ai.advanced.companion.Order;
import main.game.ai.advanced.companion.OrderFactory;
import main.game.ai.advanced.companion.OrderMaster;
import main.system.auxiliary.RandomWizard;
import main.test.Untested;

@Untested
public class OrderEffect extends DC_Effect implements OneshotEffect {

    boolean partyTargeting;

    public OrderEffect(boolean partyTargeting) {
        this.partyTargeting = partyTargeting;
    }

    @Override
    public boolean applyThis() {
        Order order = OrderFactory.getOrder(partyTargeting, ref.getActive());

        if (partyTargeting) {
            getSource().getAI().getGroup().getMembers().forEach(hero -> trySetOrder(hero,order));
        }
        else {
            Unit unit = (Unit) ref.getTargetObj();
              trySetOrder(unit , order);
        }

        return true;
    }

    private void trySetOrder(Unit hero, Order order) {
        int chance = OrderMaster.getSuccessChance(partyTargeting, order, hero, getSource(), ref.getActive());
        if (RandomWizard.chance(chance)) {
            getGame().getLogManager().logOrderFailed(  order, hero);
        }
        else {
            hero.getAI().setCurrentOrder(order);
        }
    }

}
