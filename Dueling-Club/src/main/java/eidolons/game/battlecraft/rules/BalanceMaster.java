package eidolons.game.battlecraft.rules;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.damage.DamageCalculator;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.enums.GenericEnums.DAMAGE_TYPE;

import java.util.Map;

/**
 * Created by JustMe on 11/4/2018.
 */
public class BalanceMaster {

    //of course some things I will have to remember to add by hand to Power
    public enum DERIVED_STAT{
        THREAT,
        //how much dps can the unit deal?
        HARDINESS,
        //how much dmg goes thru on average from every 100 dmg dealt?

        LONGEVITY,
        //how long will the unit survive if taking [dmg] each round?
        //+restorations
        //+undying
        MOBILITY,
        //how quickly can the unit move from a to b?
        TRICKS,
        //all debuff and custom ai spell priority
        //stealth and sneak mods
        //
        SUPPORT,


        ;
        
        String formula;
    }

    Unit dummy;
    public void comparePartyMembers(Party party) {
//        dummy = new Unit(dummyType);
        Map<Unit, Map<DERIVED_STAT, Float>> results;

    }
        public float calcDerivedStat(DERIVED_STAT stat, Unit entity) {
        switch (stat) {
            case THREAT:
//                return calcThreat(entity);
            case HARDINESS:
                return calcHardiness(entity);
            case LONGEVITY:
                return calcLongevity(entity);
//            case MOBILITY:
//                return calcThread(entity);
//            case TRICKS:
//                return calcThread(entity);
        }
            return 0;
        }

    private float calcThreat(Unit entity, boolean current) {
        float val = 0;
//        DC_ActiveObj best = entity.getActives().stream().filter(activeObj ->
//         ActionManager.isWeaponAttack(activeObj))
//         .sorted(SortMaster.getObjSorterByExpression(a-> FutureBuilder.getDamage(a, dummy)))
//         .findFirst().getVar();
//        Double initiative = entity.getParamDouble(PARAMS.N_OF_ACTIONS);
//        val =FutureBuilder.getDamage(best, dummy) / best.getIntParam(PARAMS.AP_COST) * initiative
//         / AtbController.SECONDS_IN_ROUND / AtbController.ATB_READINESS_PER_AP;

        return val;
    }
    private float calcHardiness(Unit entity) {
        float val = 0;
        //how much from 100 dmg is blocked, on average?
        int sum=0;
        for (DAMAGE_TYPE damage_type : DAMAGE_TYPE.values()) {
            int result = DamageCalculator.precalculateDamage(100, damage_type, entity, dummy, dummy.getWeapon(false),
             dummy.getPreferredAttackAction());
            sum+=result;
        }
        val= sum / DAMAGE_TYPE.values().length;
        return val;
    }

    private float calcLongevity(Unit entity) {
        //how long will the unit survive if taking [dmg] each round?

        float physical = 0;
//        DamageDealer.dealDamage(damage, entity);

//        DC_AttackMaster.

        float val = 0;
        return val;
    }

}
