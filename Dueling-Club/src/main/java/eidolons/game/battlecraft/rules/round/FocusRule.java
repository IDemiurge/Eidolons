package eidolons.game.battlecraft.rules.round;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;

public class FocusRule extends RetainRestoreRule {

    public FocusRule(DC_Game game) {
        super(game);
    }

    public static boolean isFatigueOn() {
        return true;
    }

    @Override
    protected PARAMETER getBaseParameter() {
        return PARAMS.FOCUS;
    }

    public boolean check(Unit unit) {
        return !ParamAnalyzer.isFocusIgnore(unit);
    }

    @Override
    public PARAMETER getMaxParam() {
        return PARAMS.STARTING_FOCUS;
    }

    @Override
    protected PARAMETER getFatigueParam() {
        return PARAMS.FOCUS_FATIGUE;
    }

    public void onReset(Unit unit){
        int amountGained;
        /*
        for every 5 points of focus you gain in combat above your Base Focus, you will receive

        Perhaps actions that 'cost' focus should apply focus fatigue!!!
         */

    }

    @Override
    protected void paramLost(int amount, Unit unit) {
        super.paramLost(amount, unit);
//        unit.addParam(PARAMS.FOCUS_FATIGUE, amount);
//        if (amount > 0)
//            if (unit.isPlayerCharacter()) {
//                game.getLogManager().log(unit + "'s focus fatigue is now " +
//                        unit.getIntParam(PARAMS.FOCUS_FATIGUE));
//            }
//        unit.addOrProlongBuff("Focus Fatigue", )
        // do it via new parameter! FOCUS_FATIGUE => used perhaps in the same rule here?
    }

    @Override
    protected void paramGained(int amount, Unit unit) {
        super.paramGained(amount, unit);
        unit.addParam(PARAMS.FOCUS_FATIGUE, -amount/2);
        if (amount > 0)
            if (unit.isPlayerCharacter()) {
                game.getLogManager().log(unit + "'s focus fatigue is now " +
                        unit.getIntParam(PARAMS.FOCUS_FATIGUE));
            }
    }
}
