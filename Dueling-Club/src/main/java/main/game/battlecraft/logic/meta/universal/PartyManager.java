package main.game.battlecraft.logic.meta.universal;

import main.client.cc.logic.party.PartyObj;

public abstract class PartyManager<E extends MetaGame> extends MetaGameHandler<E> {

    protected PartyObj party;
    public PartyManager(MetaGameMaster master) {
        super(master);
    }

    public  abstract PartyObj initPlayerParty();

    public PartyObj getParty() {
        return party;
    }

    public void gameStarted() {
    }
}
