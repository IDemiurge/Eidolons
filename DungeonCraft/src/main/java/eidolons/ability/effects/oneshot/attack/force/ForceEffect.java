package eidolons.ability.effects.oneshot.attack.force;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import main.ability.effects.OneshotEffect;
import main.data.ability.AE_ConstrArgs;
import main.system.math.Formula;

public class ForceEffect extends DC_Effect implements OneshotEffect {

    private final Boolean attack;

    @AE_ConstrArgs(argNames = {
            "formula", "attack"
    })
    public ForceEffect(String forceFormula, Boolean attack) {

        this.attack = attack;
        this.formula = new Formula(forceFormula);
    }

    @Override
    public boolean applyThis() {
        if (!RuleKeeper.isRuleOn(RuleEnums.RULE.FORCE)) return false;
        int force = getFormula().getInt(ref);
        if (ref.getTargetObj() instanceof BattleFieldObject) {
            //TODO NF Content
            // ForceRule.applyForceEffects(force, (DC_ActiveObj) ref.getActive(), (BattleFieldObject) ref.getTargetObj());
        }

        return true;
    }

}
