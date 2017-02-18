package main.elements;

import main.entity.Ref;
import main.entity.Referred;
import main.game.core.game.Game;

public class ReferredElement implements Referred {

    protected Ref ref;
    protected Game game;

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = Ref.getCopy(ref);
        this.game = this.ref.getGame();

    }

}
