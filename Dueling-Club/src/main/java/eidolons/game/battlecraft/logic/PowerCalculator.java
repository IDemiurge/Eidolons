package eidolons.game.battlecraft.logic;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.obj.unit.Unit;

import java.util.List;

/**
 * Created by JustMe on 11/17/2018.
 */
public class PowerCalculator {
    Unit unit;
    int currentPower;

    public PowerCalculator(Unit unit) {
        this.unit = unit;
    }

    public int eval(Unit unit) {
        currentPower = 0;
        //flexible constants please, so we can adjust again and again on real units
        //        sync with ai analyzer!
        unit.reset();
        currentPower += calcRawPower(unit);
        //unit.getAttrs();
        //unit.getMasteries();
        //unit.getPassiveAbils();
        currentPower += calcSlotItemsPower(unit,
         unit.getSlotItems());
        unit.getQuickItems();



        calcSpellsPower(unit.getSpells(),
         getFactor(5, 100, unit.getIntParam(PARAMS.SPELLPOWER)));
        return currentPower;
    }

    private int calcRawPower(Unit unit) {
        int danger;
        int durability;
        int longevity;
        int tricks;
        //        String powerFormula =
        // "sqrt(sqrt(danger)*sqrt(durability)*sqrt(longevity)*sqrt(tricks))
        //    + min/max + sum ";
        return 0;
    }

    private int calcSlotItemsPower(Unit unit, List<DC_HeroSlotItem> slotItems) {
        //perhaps via balance parameters after all?
        return 0;
    }

    private void calcSpellsPower(List<DC_SpellObj> spells, int spFactor) {
        int power = 0;
        for (DC_SpellObj spell : spells) {
            power += getPower(spell, spFactor);
        }
    }

    private int getPower(DC_SpellObj spell, int spFactor) {
        return 0;
    }

    private int getFactor(int min, int max, Integer value) {
        //exponential from value/min ,
        //max??
        return 0;
    }


}
