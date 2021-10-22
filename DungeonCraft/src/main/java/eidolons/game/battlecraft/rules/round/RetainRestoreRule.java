package eidolons.game.battlecraft.rules.round;

import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 4/9/2018.
 */
public abstract class RetainRestoreRule extends RoundRule {
    private PARAMETER currentParam;
    private PARAMETER restoreParam;
    private PARAMETER retainParam;

    public RetainRestoreRule(DC_Game game) {
        super(game);
    }

    @Override
    public void apply(Unit unit, float delta) {
        int diff = unit.getIntParam(getCurrentParam())
         - unit.getIntParam(getMaxParam()) ;

        if (diff == 0) {
            return;
        }
        boolean restore = diff < 0;
        int mod;
        if (restore) {
            mod = 25
             + unit.getIntParam(getRestoreParam());
        } else {
            mod = 25
             - unit.getIntParam(getRetainParam());
        }
        mod = Math.min(100, mod);
        int amount = Math.abs(diff);
        if (mod > 0) {
            amount = Math.round(amount * mod / 100 * delta);
        } else {
            return;
        }

        if(getFatigueParam()!=null ){
            amount -= amount*unit.getIntParam(getFatigueParam())/
                    (restore? 100: -100);
        }
        if (restore) {
            unit.modifyParameter(getCurrentParam(), amount, unit
             .getIntParam(getMaxParam()));
            paramGained(amount, unit);
        } else {
            unit.modifyParameter(getCurrentParam(), -amount);
            paramLost(amount, unit);
        }

    }

    protected PARAMETER getFatigueParam() {
        return null;
    }

    protected void paramGained(int amount, Unit unit) {
    }

    protected void paramLost(int amount, Unit unit) {
    }

    protected abstract PARAMETER getBaseParameter();

    public PARAMETER getMaxParam() {
        return getBaseParameter();
    }
    public PARAMETER getCurrentParam() {
        if (currentParam == null) {
            currentParam = ContentValsManager.getCurrentParam(getBaseParameter());
        }
        return currentParam;
    }

    public PARAMETER getRestoreParam() {
        if (restoreParam == null) {
            restoreParam =
             ContentValsManager.getPARAM(getBaseParameter().getName()
              + ContentValsManager.RESTORATION);
        }
        return restoreParam;
    }

    public PARAMETER getRetainParam() {
        if (retainParam == null) {
            retainParam =ContentValsManager.getPARAM(getBaseParameter().getName()
             + ContentValsManager.RETAINMENT);
        }
        return retainParam;
    }
}
