package main.entity.obj.attach;

import main.ability.effects.Effect;
import main.content.PARAMS;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BuffObj;
import main.entity.type.BuffType;
import main.game.core.game.DC_Game;
import main.game.core.game.MicroGame;
import main.game.core.master.BuffMaster;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;

public class DC_BuffObj extends BuffObj {

    public DC_BuffObj(BuffType type, Player owner, MicroGame game, Ref ref, Effect effect,
                      int duration, Condition retainCondition) {
        super(type, owner, game, ref, effect, duration, retainCondition);

    }

    @Override
    public boolean applyEffect() {
        getEffect().resetOriginalFormula();
        getEffect().appendFormula("1+" + StringMaster.getValueRef(KEYS.BUFF, PARAMS.BUFF_STACKS));
        return super.applyEffect();
    }

    public boolean isMaxStacks() {
        return getStacks() >= getIntParam(PARAMS.MAX_BUFF_STACKS);
    }

    public Integer getStacks() {
        return getIntParam(PARAMS.BUFF_STACKS);
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    @Override
    public void invokeClicked() {

        super.invokeClicked();
    }

    @Override
    public boolean isDispelable() {
        return BuffMaster.checkBuffDispelable(this);
    }

	/*
     * You know, it is remarkable how little tenacity for short-term goals I
	 * have compared to my long-term endurance... I really can't see how I could
	 * have been ignoring the simple factor of playability for so long even
	 * given the
	 */
}
