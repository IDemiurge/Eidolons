package main.game.battlecraft.logic.meta.universal;

import main.client.cc.logic.party.PartyObj;
import main.content.PROPS;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.DC_Player;

public abstract class PartyManager<E extends MetaGame> extends MetaGameHandler<E> {

    protected PartyObj party;

    public PartyManager(MetaGameMaster master) {
        super(master);
    }

    public abstract PartyObj initPlayerParty();

    public PartyObj getParty() {
        return party;
    }

    public void gameStarted() {
        DC_Player player = getMaster().getBattleMaster().getPlayerManager().getPlayer(true);
        String name = getParty().getProperty(PROPS.PARTY_MAIN_HERO);
        if (name.isEmpty())
            if (getMaster().getEntity() != null) {
                //TODO set main hero if created
                name = getMaster().getEntity().getProperty(PROPS.PARTY_MAIN_HERO);
            }
        name = "Harlen Rolwain";
        Ref ref = new Ref(getParty().getLeader());
        Unit hero = getGame().getMaster().getUnitByName(name, ref, true, null, null);
        //will find 1st if name==null
        player.setHeroObj(hero);
        getParty().setProperty(PROPS.PARTY_MAIN_HERO, name);
    }

    public void preStart() {

    }
}
