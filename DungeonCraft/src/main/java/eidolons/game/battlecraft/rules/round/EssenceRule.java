package eidolons.game.battlecraft.rules.round;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;

import static eidolons.content.PARAMS.C_ESSENCE;
import static eidolons.content.PARAMS.ESSENCE;

public class EssenceRule extends RetainRestoreRule {

    public EssenceRule(DC_Game game) {
        super(game);
    }

    @Override
    protected PARAMETER getBaseParameter() {
        return  C_ESSENCE;
    }

    @Override
    public PARAMETER getMaxParam() {
        return  ESSENCE;
    }

    @Override
    public boolean check(Unit unit) {
        return !ParamAnalyzer.isMoraleIgnore(unit);
    }


}
