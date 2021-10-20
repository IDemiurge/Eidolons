package eidolons.game.battlecraft.rules.counter.active;

import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;

public class WrathRule extends ActiveCRule{
    @Override
    protected PARAMETER getRetainmentParam() {
        return null;
    }

    @Override
    public UnitEnums.COUNTER getCounter() {
        return null;
    }

    @Override
    public String getBuffName() {
        return null;
    }

    @Override
    protected Effect getEffect() {
        return new Effects(
                new ModifyValueEffect(PARAMS.STR_DMG_MODIFIER, Effect.MOD.MODIFY_BY_CONST, ""),
                new ModifyValueEffect(PARAMS.AGI_DMG_MODIFIER, Effect.MOD.MODIFY_BY_CONST, ""),
                new ModifyValueEffect(PARAMS.SP_DMG_MODIFIER, Effect.MOD.MODIFY_BY_CONST, "")
        );
    }

    @Override
    public UnitEnums.STATUS getStatus() {
        return null;
    }
}
