package eidolons.game.battlecraft.logic.meta.universal;

/**
 * Created by JustMe on 5/10/2017.
 */
public abstract class MetaGame<E extends MetaGame> {

    protected boolean restarted;
    MetaGameMaster<E> master;

    public MetaGame(MetaGameMaster<E> master) {
        this.master = master;
    }

    public MetaGameMaster<E> getMaster() {
        return master;
    }


    public boolean isRestarted() {
        return restarted;
    }

    public void setRestarted(boolean restarted) {
        this.restarted = restarted;
    }
}
