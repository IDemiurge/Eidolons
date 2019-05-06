package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.meta.igg.death.ChainHero;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.igg.hero.ChainParty;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.threading.WaitMaster;

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

//    @Override
//    public Party initPlayerParty() {
//        if (party != null)
//            return party;
//        party = new Party();
//        return party;
//    }

    private ObjType getPartyType() {
        return DataManager.getType("Chained", DC_TYPE.PARTY);
    }
    @Override
    public Party initPlayerParty() {
        if ( getMaster().getMetaGame().getMissionIndex()>0) {
            party = Eidolons.getParty();
            //choose again?
            if (party != null)
                return party;
        }

        ObjType type = new ObjType(getPartyType());
        List<String> members = ContainerUtils.openContainer(type.getProperty(PROPS.MEMBERS));
                String hero = selectedHero; //for restart
                if (hero == null)
                    hero = chooseHero(members);
                if (hero == null) {
                    return null; //aborted
                }

        if (party == null) {
            party = createParty(type, selectedHero);
            party.toBase();
        }
        chain = new HeroChain(party, //getMetaGame().getActIndex()+
                1);

        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));

//        party.setProperty(PROPS.PARTY_MISSION,
//                getMetaGame().getMission().getName(), true);
        return party;

    }

    private void removeOldHero() {
        Eidolons.getMainHero().removeFromGame();
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, Eidolons.getMainHero());
    }

    @Override
    public ChainParty getParty() {
        return (ChainParty) super.getParty();
    }

    public void respawn(String newHero) {
//        avatar = chain.findHero(newHero);
        main.system.auxiliary.log.LogMaster.log(1,"respawn as " +
                newHero +
                "; old hero: " +Eidolons.getMainHero().getInfo());
        removeOldHero();
        selectedHero = newHero;
        ObjType type = DataManager.getType(selectedHero, DC_TYPE.CHARS);
        Coordinates c = getGame().getDungeonMaster().getDungeonLevel().getEntranceCoordinates();
        Unit hero = (Unit) getGame().createUnit(type, c.x, c.y, getGame().getPlayer(true));// HeroCreator.initHero(selectedHero);
        party.getMembers().clear();
        party.setLeader(hero);
//        hero.toBase();
//        hero.afterEffects();

        hero.setMainHero(true);
        party.setProperty(PROPS.PARTY_MAIN_HERO, hero.getName());
        Eidolons.setSelectedMainHero(hero.getName());
        Eidolons.setMainHero(hero);

//        mainHeroSelected(party, hero );
//        hero.setOriginalOwner(getGame().getPlayer(true));
//        hero.setCoordinates(getGame().getDungeonMaster().getDungeonLevel().getEntranceCoordinates()
//        , true);

//        hero.getVisionController().log(hero);
//        hero.getVisionController().logFor(hero);

        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, hero);
        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, hero);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, hero);


        main.system.auxiliary.log.LogMaster.log(1,"respawned as " +
                newHero +
                "; new hero: " +Eidolons.getMainHero().getInfo());
    }


    private Party createParty(ObjType type, String selectedHero) {
        return new ChainParty(type, selectedHero);
    }

    @Override
    protected String chooseHero(List<String> members) {
        if (isWaitForGdx())
            WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
        return super.chooseHero(members);
    }

    private boolean isWaitForGdx() {
        return true; //f
    }


    @Override
    protected Unit findMainHero() {

        return getParty().getLeader();
    }

    public HeroChain getChain() {
        return chain;
    }

}
