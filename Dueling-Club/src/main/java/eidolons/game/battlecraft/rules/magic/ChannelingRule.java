package eidolons.game.battlecraft.rules.magic;

import eidolons.ability.DC_CostsFactory;
import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.audio.DC_SoundMaster;
import main.content.mode.STD_MODES;
import main.data.filesys.PathFinder;
import main.elements.costs.Cost;
import main.elements.costs.CostRequirements;
import main.elements.costs.Costs;
import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * original targeting costs cooldown block
 *
 * @author JustMe
 */
public class ChannelingRule {

    private static final String PATH = "effects\\channeling\\";
    private static final PARAMS[] costParamsResolve = {
     PARAMS.AP_COST,
     PARAMS.ESS_COST,
     PARAMS.ENDURANCE_COST,
    };
    private static final PARAMS[] costParamsActivate = {
     PARAMS.AP_COST,
     PARAMS.STA_COST,
     PARAMS.FOC_COST,
    };
    static boolean testMode;

    public static Costs getChannelingActivateCosts(DC_SpellObj action) {
        return getChannelingCosts(action, true);
    }

    public static Costs getChannelingResolveCosts(DC_SpellObj action) {
        return getChannelingCosts(action, false);
    }

    public static Costs getChannelingCosts(DC_SpellObj action, boolean activateOrResolve) {
        List<Cost> list = new ArrayList<>();
        for (PARAMS costParam : activateOrResolve ? costParamsActivate : costParamsResolve) {
            PARAMS payParam = DC_ContentValsManager.getPayParameterForCost(costParam);
            Cost cost = DC_CostsFactory.getCost(action, costParam, payParam);
//            int mod = 100;
//            cost.getPayment().getAmountFormula().applyModifier(mod);
            list.add(cost);
        }
//        Costs costs = getEntity().getCosts();
//        Costs channelingResolveCosts = new Costs(costs.getRequirements(), costs.getCosts());
//        Costs channelingActivateCosts = new Costs(costs.getRequirements(), costs
//         .getCost(PARAMS.C_N_OF_ACTIONS));
//        channelingResolveCosts.removeCost(PARAMS.C_N_OF_ACTIONS);
        CostRequirements reqs = action.getCosts().getRequirements();
        Costs costs = new Costs(reqs, list);
        if (!activateOrResolve)
            costs.removeRequirement(InfoMaster.COOLDOWN_REASON);
        return costs;
    }

    public static void channelingInterrupted(Unit sourceObj) {
        sourceObj.getHandler().clearChannelingData();
    }

    public static void channelingResolves(Unit activeUnit) {
        activeUnit.getHandler().clearChannelingData();
        activeUnit.removeBuff(STD_MODES.CHANNELING.getBuffName());
    }

    public static boolean activateChanneing(DC_SpellObj spell) {

        // ActiveAbility spell_ability = ActivesConstructor
        // .mergeActiveList(spell, TARGETING_MODE.SINGLE);
        spell.getOwnerUnit().getHandler().initChannelingSpellData(spell);


        boolean result = true;
        ModeEffect modeEffect = new ModeEffect(STD_MODES.CHANNELING);
//        String string = STD_MODES.CHANNELING.toString();
//        if (spell instanceof DC_UnitAction) {
//            if (spell.checkProperty(G_PROPS.CUSTOM_PROPS)) {
//                modeEffect.getModPropEffect().setValue(
//                 spell.getProperty(G_PROPS.CUSTOM_PROPS));
//            }
//        }
        Ref REF = spell.getRef().getCopy();
        REF.setTarget(spell.getOwnerUnit().getId());
        // modeEffect.getAddBuffEffect().setEffect(effect)

//        Condition conditions = new Conditions(new RefCondition(
//         KEYS.EVENT_SOURCE, KEYS.SOURCE), new StringComparison(
//         "{SOURCE_MODE}", string, true));

//        CastSpellEffect castEffect = new CastSpellEffect(spell);
//        castEffect.setForceTargeting(true);

//        AddTriggerEffect triggerEffect = new AddTriggerEffect(
//         STANDARD_EVENT_TYPE.UNIT_TURN_STARTED, conditions,
//         new ActiveAbility(null, new Effects(
//          new RemoveBuffEffect(string), castEffect)));
        modeEffect.setReinit(false);
//        modeEffect.getAddBuffEffect().addEffect(triggerEffect);
        modeEffect.getAddBuffEffect().setDuration(2);
        result &= modeEffect.apply(REF);
        return result;

    }

    public static void playChannelingSound(DC_ActiveObj active, boolean female) {
        // TODO try reverse the sounds too, who knows ;)
        String prop = active.getProperty(PROPS.CHANNELING_SOUND);

        if (StringMaster.isEmpty(prop)) {
            prop = generateSoundForSpell(active);
        }
        if (female) {
            prop = "W_" + prop;
        }

        String basePath = PathFinder.getSoundPath() + PATH + prop;
        DC_SoundMaster.playRandomSoundVariant(basePath, true); // TODO find files
        // refactor!
    }

    private static String generateSoundForSpell(DC_ActiveObj spell) {
        // Aspect / Spell_type /

        // if (spell.getAspect()){
        //
        // }
        //
        // if (spell.getSpellType()){
        //
        // }

        return null;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
        ChannelingRule.testMode = testMode;
    }

    public static boolean isPreTargetingNeeded(DC_SpellObj spell) {
        return true;
    }


// Targeting targeting = new FixedTargeting(KEYS.TARGET);
    // spell_ability.setTargeting(targeting);
    // Effect ELSEeffect = new
    // RemoveBuffEffect(modeEffect.getBuffTypeName());
    // Effect IFeffect = new CostEffect(spell);
    // ActiveAbility cost_ability = new ActiveAbility(new FixedTargeting(
    // KEYS.SOURCE), new IfElseEffect(IFeffect, new CostCondition(
    // spell), ELSEeffect));
    // Abilities abilities = new Abilities();
    // abilities.add(spell_ability);
    // abilities.add(cost_ability);
    // public static Costs getChannelingCosts(DC_SpellObj spell) {
    // // TODO
    // List<Cost> list = new ArrayList<>();
    // Cost apCost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING, PARAMS.C_N_OF_ACTIONS);
    // list.add(apCost);
    //
    // if (spell.getIntParam(PARAMS.CHANNELING_ESS_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_ESS_COST, PARAMS.C_ESSENCE);
    // list.add(cost);
    // }
    //
    // if (spell.getIntParam(PARAMS.CHANNELING_FOC_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_FOC_COST, PARAMS.C_FOCUS);
    // list.add(cost);
    // }
    // if (spell.getIntParam(PARAMS.CHANNELING_STA_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_STA_COST, PARAMS.C_STAMINA);
    // list.add(cost);
    // }
    // if (spell.getIntParam(PARAMS.CHANNELING_END_COST) != 0) {
    // Cost cost = DC_CostsFactory
    // .getCost(spell, PARAMS.CHANNELING_END_COST, PARAMS.C_ENDURANCE);
    // list.add(cost);
    // }
    // return new Costs(list);
    // }

}
