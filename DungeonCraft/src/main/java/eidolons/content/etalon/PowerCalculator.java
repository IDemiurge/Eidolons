package eidolons.content.etalon;

import eidolons.entity.unit.Unit;

public class PowerCalculator {
/*
for etalon hero entities? Or used on any?
 */

    public int eval(Unit unit) {
        int currentPower = 0;
        //flexible constants please, so we can adjust again and again on real units
        //        sync with ai analyzer!
        unit.reset();
        currentPower += calcRawPower(unit);
        //unit.getAttrs();
        //unit.getMasteries();
        //unit.getPassiveAbils();
        // currentPower += calcSlotItemsPower(unit,
        //         unit.getSlotItems());
        // unit.getQuickItems();
        //
        // calcSpellsPower(unit.getSpells(),
        //         getFactor(5, 100, unit.getIntParam(PARAMS.SPELLPOWER)));
        return currentPower;
    }

    private int calcRawPower(Unit unit) {
        /*
        Total perks and other points

         */

        return 0;
    }


}
