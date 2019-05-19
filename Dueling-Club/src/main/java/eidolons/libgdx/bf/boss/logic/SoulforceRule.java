package eidolons.libgdx.bf.boss.logic;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;

/**
 * Shadow action costs?
 *
 * Soul Ash
 *
 *
 *
 */
public class SoulforceRule {


    //exchange for life


    public int applySoulforce(Unit hero, HeroChain chain) {
        int amount=0;
        chain.getParty().setParam(PARAMS.SOULFORCE, amount);

        return 0;
    }
        public int getSoulforceCostToRaise(Unit hero) {

        return 0;
    }
        public int getSoulforceCost(DC_ActiveObj action) {
        //displayed how?

        //perhaps it is Shadow's Essence?

        return 0;
    }
    public int getSoulforceFromKill(Unit killed) {
        return 0;
    }

}
