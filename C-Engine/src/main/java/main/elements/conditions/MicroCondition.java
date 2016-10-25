package main.elements.conditions;

import main.entity.Ref;
import main.game.MicroGame;

public abstract class MicroCondition extends ConditionImpl {
    protected MicroGame game;

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        this.game = (MicroGame) this.ref.getGame();
    }

}
