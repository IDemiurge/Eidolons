package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.death.ChainHero;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.DC_TYPE;
import main.data.DataManager;

import java.util.List;

public class IGG_PartyManager extends PartyManager<IGG_Meta> {


    private HeroChain chain;
    private ChainHero avatar;

    public HeroChain getHeroChain() {
        return chain;
    }
    public IGG_PartyManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    protected String chooseHero(List<String> members) {
        return super.chooseHero(members);
    }

    @Override
    public Party initPlayerParty() {
        if (party!=null )
            return party;
         party=new Party(DataManager.getType("Chained", DC_TYPE.PARTY));

        chain = new HeroChain(party);
        return party;
    }
    public void respawn(String newHero) {
        avatar = chain.findHero(newHero);
//        getMaster().getBattleMaster().getSpawner().spawn()
   avatar.getUnit().fullReset(getGame());
   avatar.getUnit().setCoordinates(getMaster().getDungeonMaster().getDungeonLevel().getEntranceCoordinates());
    }
    @Override
    protected Unit findMainHero() {

        return getParty().getLeader();
    }

    public HeroChain getChain() {
        return chain;
    }

}
