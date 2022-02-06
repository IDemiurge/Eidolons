package eidolons.entity.unit.attach.buff;

import eidolons.content.PARAMS;
import eidolons.entity.handlers.DC_ObjMaster;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.BuffMaster;
import main.ability.effects.Effect;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.obj.BuffObj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public class DC_BuffObj extends BuffObj {

    public DC_BuffObj(ObjType type, Player owner, GenericGame game, Ref ref, Effect effect,
                      double duration, Condition retainCondition) {
        super(type, owner, game, ref, effect, duration, retainCondition);

    }

    public DC_BuffObj(ObjType type, Player owner, GenericGame game, Ref ref) {
        this(type, owner, game, ref, null, 0, null);
    }

    public DC_BuffObj(DC_BuffObj buff) {
        this(buff.type, buff.owner,
         buff.getGame(), buff.ref, buff.effect,
         buff.duration, buff.getRetainConditions());

    }

    public DC_BuffObj(String name, BattleFieldObject basis,
                      float duration) {
        this( BuffMaster.getBuffType(name)
               , basis, duration);

    }
    public DC_BuffObj(ObjType type, BattleFieldObject basis, float duration) {
        super(type, basis.getOwner(), basis.getGame(),
         Ref.getBasisRefCopy(basis), null ,
         duration, null);
    }

    @Override
    protected EntityMaster initMaster() {
        return new DC_ObjMaster(this);
    }

    @Override
    public boolean applyEffect() {
        getEffect().resetOriginalFormula();
//        TODO min(x,y) won't work
//        getEffect().multiplyFormula("1+" + StringMaster.getValueRef(KEYS.BUFF, PARAMS.BUFF_STACKS));
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
    public boolean isPhysical() {
        return  !isDispelable();
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
