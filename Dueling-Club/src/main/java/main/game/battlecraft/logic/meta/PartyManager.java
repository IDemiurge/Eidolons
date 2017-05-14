package main.game.battlecraft.logic.meta;

import main.client.cc.logic.party.PartyObj;

public abstract class PartyManager<E extends MetaGame> extends  MetaGameHandler<E> {

    public PartyManager(MetaGameMaster master) {
        super(master);
    }

    public  abstract PartyObj initPlayerParty();
}
