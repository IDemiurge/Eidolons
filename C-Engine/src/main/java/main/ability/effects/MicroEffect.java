package main.ability.effects;

import main.entity.Ref;
import main.game.core.game.MicroGame;

public abstract class MicroEffect extends EffectImpl {
    protected MicroGame game;

    public MicroEffect() {
        super();
    }

    public boolean isAnimationDisabled() {
        return false;
    }

    public void initLayer() {
        super.initLayer();
    }

    public MicroGame getGame() {
        return game;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        this.game = (MicroGame) ref.getGame();
    }

}
