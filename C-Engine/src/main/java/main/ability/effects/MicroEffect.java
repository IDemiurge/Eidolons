package main.ability.effects;

import main.entity.Ref;
import main.game.core.game.GenericGame;

public abstract class MicroEffect extends EffectImpl {
    protected GenericGame game;

    public MicroEffect() {
        super();
    }

    public boolean isAnimationDisabled() {
        return false;
    }

    public void initLayer() {
        super.initLayer();
    }

    public GenericGame getGame() {
        return game;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        this.game = (GenericGame) ref.getGame();
    }

}
