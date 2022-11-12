package eidolons.ability.ignored.special.meta;

import eidolons.ability.effects.DC_Effect;

public class EndGameEffect extends DC_Effect {

    public EndGameEffect(boolean outcome) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean applyThis() {
//        getGame().getBattleManager().victory();
        return false;
    }

}
