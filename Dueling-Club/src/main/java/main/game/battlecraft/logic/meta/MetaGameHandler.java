package main.game.battlecraft.logic.meta;

/**
 * Created by JustMe on 5/10/2017.
 */
public class MetaGameHandler<E extends MetaGame>  {
    protected MetaGameMaster master;

    public MetaGameHandler(MetaGameMaster master) {
        this.master = master;
    }

    public PartyManager<E> getPartyManager() {
        return master.getPartyManager();
    }
}
