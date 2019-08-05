package eidolons.game.battlecraft.logic.meta.igg;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.meta.igg.death.ChainHero;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.igg.hero.ChainParty;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.SoulforceMaster;
import eidolons.game.battlecraft.logic.meta.tutorial.TutorialManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.party.Party;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.Collections;
import java.util.List;

public class IGG_PartyManager extends PartyManager<IGG_Meta> {

    private static final String LORD_TYPE = "Anphis Ar Keserim";
    private HeroChain chain;
    private ChainHero avatar;
    private int deaths=0;

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
        if (getMaster().getMetaGame().getMissionIndex() > 0) {
            party = Eidolons.getParty();
            //choose again?
            if (party != null)
                return party;
        }

        ObjType type = new ObjType(getPartyType());
        List<String> members = ContainerUtils.openContainer(type.getProperty(PROPS.MEMBERS));
        if (selectedHero == null)
            selectedHero = chooseHero(members);
        String hero = selectedHero; //for restart

        if (hero == null) {
            return null; //aborted
        }

        if (party == null) {
            party = createParty(type, selectedHero);
            Eidolons.setParty(party);
            party.toBase();
        }
        chain = new HeroChain(party, //getMetaGame().getActIndex()+
                1);
        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));

        new EidolonLord(DataManager.getType(LORD_TYPE, DC_TYPE.LORD)).setChain(chain);

//        party.setProperty(PROPS.PARTY_MISSION,
//                getMetaGame().getMission().getName(), true);
        return party;

    }

    private void removeOldHero() {
        Eidolons.getMainHero().removeFromGame();
    }

    @Override
    public ChainParty getParty() {
        return (ChainParty) super.getParty();
    }

    public void respawn(String newHero) {
//        avatar = chain.findHero(newHero);
        deaths++;
        // TODO igg demo hack

        for (Unit unit : getGame().getUnits()) {
            unit.getAI().combatEnded();

        }

        main.system.auxiliary.log.LogMaster.log(1, "respawn as " +
                newHero +
                "; old hero: " + Eidolons.getMainHero().getInfo());
        removeOldHero();
        selectedHero = newHero;
        ObjType type = DataManager.getType(selectedHero, DC_TYPE.CHARS);
        Coordinates c = getRespawnCoordinates(type);
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

        getGame().getManager().reset();
        //TODO what matters is to reset ObjArray
//        hero.getVisionController().reset();
//        hero.getGame().getVisionMaster().refresh();

//        hero.getVisionController().logFor(hero);
        getGame().getBattleFieldManager().resetWallMap();
//        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, hero);
        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, hero);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, hero);


        main.system.auxiliary.log.LogMaster.log(1, "respawned as " +
                newHero +
                "; new hero: " + Eidolons.getMainHero().getInfo());
    }

    private Coordinates getRespawnCoordinates(ObjType type) {
        if (EidolonsGame.BRIDGE){
            return getGame().getDungeonMaster().getDungeonLevel().getEntranceCoordinates();
//            return SoulforceMaster.getLastRespPoint();
        }
        if (EidolonsGame.BOSS_FIGHT || EidolonsGame.TUTORIAL_MISSION) {
            return Positioner.adjustCoordinate(type,
                    getParty().getLastHero().getCoordinates(), getParty().getLastHero().getFacing().flip());
        }
        return getGame().getDungeonMaster().getDungeonLevel().getEntranceCoordinates();
    }


    private Party createParty(ObjType type, String selectedHero) {
        return new ChainParty(type, selectedHero);
    }

    @Override
    protected String chooseHero(List<String> members) {
        if (!EidolonsGame.TUTORIAL_MISSION) {
            Collections.shuffle(members);
        }
        if (CoreEngine.isLiteLaunch() || getMetaGame().getMissionIndex() == 0) {
            return TutorialManager.NEXT_HERO;
        }
//        if (getMetaGame().getMissionIndex()>0) {
//            return selectedHero;
//        }
        if (isWaitForGdx())
            WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
        return super.chooseHero(members);
    }

    public String chooseNextHero() {
//        if (!CoreEngine.isIDE())
        if ( EidolonsGame.TUTORIAL_PATH || (EidolonsGame.TUTORIAL_MISSION&& deaths ==0)) {
            return TutorialManager.nextHero();
        }
        GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
                getMetaGame().getMaster().getPartyManager().getChain().getTypes());
        return (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.HERO_SELECTION);
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
