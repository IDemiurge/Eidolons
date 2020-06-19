package eidolons.game.battlecraft.rules.round;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;

import static eidolons.content.PARAMS.ESSENCE;
import static eidolons.content.PARAMS.STARTING_ESSENCE;

public class EssenceRule extends RetainRestoreRule {

    public EssenceRule(DC_Game game) {
        super(game);
    }

    @Override
    protected PARAMETER getBaseParameter() {
        return  ESSENCE;
    }

    @Override
    public PARAMETER getMaxParam() {
        return STARTING_ESSENCE;
    }

    @Override
    public boolean check(Unit unit) {
        return !ParamAnalyzer.isMoraleIgnore(unit);
    }


}
