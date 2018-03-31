package eidolons.entity.handlers.active.spell;

import eidolons.ability.DC_CostsFactory;
import eidolons.ability.conditions.StatusCheckCondition;
import main.content.enums.entity.UnitEnums;
import main.elements.conditions.NotCondition;
import main.elements.conditions.Requirement;
import main.elements.costs.Costs;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import main.entity.handlers.EntityMaster;
import eidolons.entity.handlers.active.ActiveInitializer;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import main.system.auxiliary.secondary.InfoMaster;

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
        costs = DC_CostsFactory.getCostsForSpell(getEntity(),
//             isSpell()
         true);
        costs.getRequirements().add(
         new Requirement(new NotCondition(
          new StatusCheckCondition(UnitEnums.STATUS.SILENCED)),
          InfoMaster.SILENCE));
        costs.setActive(getEntity());
        getEntity().getActivator().setCanActivate(costs.canBePaid(getRef()));
        getEntity().setCosts(costs);
    }
}