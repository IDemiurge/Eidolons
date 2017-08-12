package main.game.battlecraft.logic.meta.universal;

import main.client.cc.logic.party.PartyObj;
import main.content.PROPS;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;

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
//            if (true){
        name = ListChooser.chooseObj(party.getMembers(), SELECTION_MODE.SINGLE);
        if (name==null )
        name = "Harlen Rolwain";

        Ref ref = new Ref(getParty().getLeader());
        Unit hero = getGame().getMaster().getUnitByName(name, ref, true, null, null);
        //will find 1st if name==null
        mainHeroSelected(party, hero);
    }

    private void mainHeroSelected(PartyObj party, Unit hero) {
        party.getMembers().forEach(member->{
            member.setMainHero(false);
        });
        hero.getOwner().setHeroObj(hero);
        hero.setMainHero(true);
        party.setProperty(PROPS.PARTY_MAIN_HERO, hero.getName());

    }

    public void preStart() {

    }

    public String checkLeveledHeroVersionNeeded(String heroName) {
        return heroName;
    }
}
