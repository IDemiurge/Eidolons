package main.ability.effects.common;

import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.system.math.Formula;

public class FearsomeEffect extends ModifyValueEffect { // mod val

    public final static String template = "-(max(0, amount-{target_spirit}/6))";

    private boolean friendlyFire;

    public FearsomeEffect(String amount) {
        super(PARAMS.C_MORALE, MOD.MODIFY_BY_CONST, (template.replace(
                "amount", amount)));

    }

    @Override
    public boolean applyThis() {
        super.applyThis();
        Formula buffer = new Formula(formula.toString());
        formula = formula.getAppendedByFactor(-0.5);
        try {
            for (DC_HeroObj unit : ((DC_Game) game).getUnits()) {
                if (!friendlyFire) {
                    if (unit.getOwner().equals(ref.getSourceObj().getOwner())) {
                        continue;
                    }
                }
                // if (!UnitAnalyzer.isLiving(unit))
                // continue;

                if (!unit.checkInSightForUnit((DC_HeroObj) ref.getSourceObj())) {
                    continue;
                }

                ref.setTarget(unit.getId());
                super.applyThis();
            }
        } catch (Exception e) {

        } finally {
            formula = buffer;
        }

        // fearsome *rule* to determine when to apply? or perhaps in AE

        // formula based on damage dealt?

        // half for all who see

        return true;
    }

}
