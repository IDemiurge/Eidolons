package eidolons.game.battlecraft.rules.round;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer;
import eidolons.game.core.game.DC_Game;
import main.content.values.parameters.PARAMETER;

public class MoraleRule extends RetainRestoreRule {

    public MoraleRule(DC_Game game) {
        super(game);
    }

    @Override
    protected PARAMETER getBaseParameter() {
        return PARAMS.MORALE;
    }

    @Override
    public boolean check(Unit unit) {
        return !ParamAnalyzer.isMoraleIgnore(unit);
    }


}
