package main.entity.tools.active.spell;

import main.ability.DC_CostsFactory;
import main.ability.conditions.StatusCheckCondition;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.NotCondition;
import main.elements.conditions.Requirement;
import main.elements.costs.Costs;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.tools.EntityMaster;
import main.entity.tools.active.ActiveInitializer;
import main.rules.magic.ChannelingRule;
import main.system.auxiliary.secondary.InfoMaster;

import java.util.LinkedList;

/**
 * Created by JustMe on 2/26/2017.
 */
public class SpellInitializer extends ActiveInitializer {


    public SpellInitializer(DC_ActiveObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }
    public DC_SpellObj getEntity() {
        return (DC_SpellObj) super.getEntity();
    }

    public void initChannelingCosts() {
        Costs channelingResolveCosts = ChannelingRule.getChannelingResolveCosts(getEntity());
        Costs channelingActivateCosts = ChannelingRule.getChannelingActivateCosts(getEntity());

        getEntity().setChannelingActivateCosts(channelingActivateCosts);
        getEntity().setChannelingResolveCosts(channelingResolveCosts);
    }
    @Override
    public void initCosts() {
        Costs costs;
        if (game.isDebugMode() && getMaster().getGame().getTestMaster().isActionFree(getName())) {
            costs = new Costs(new LinkedList<>());
        } else {
            costs = DC_CostsFactory.getCostsForSpell(getEntity(),
//             isSpell()
                    true);
            costs.getRequirements().add(
                    new Requirement(new NotCondition(
                            new StatusCheckCondition(UnitEnums.STATUS.SILENCED)),
                            InfoMaster.SILENCE));
        }
        costs.setActive(getEntity());
        getEntity().getActivator().setCanActivate(costs.canBePaid(getRef()));
        getEntity().setCosts(costs);
    }
}
