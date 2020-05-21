package eidolons.game.netherflame.main;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.tutorial.TutorialManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.game.netherflame.main.death.HeroChain;
import eidolons.game.netherflame.main.hero.ChainParty;
import eidolons.game.netherflame.main.soul.EidolonLord;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.system.DC_Formulas;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.threading.WaitMaster;

import java.util.Collections;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class NF_PartyManager extends ScenarioPartyManager {

    private static final String LORD_TYPE = "Anphis Var Keserim";
    private static final int LORD_LEVEL = 3;
    private HeroChain chain;
    private int deaths = 0;

    public HeroChain getHeroChain() {
        return chain;
    }

    public NF_PartyManager(MetaGameMaster master) {
        super(master);
    }

    private ObjType getPartyType() {
        //TODO
        return DataManager.getType("Solo", DC_TYPE.PARTY);
    }

    @Override
    public Party initPlayerParty() {
        //??? if (getMaster().getMetaGame().getMissionIndex() > 0) {
        //     party = Eidolons.getParty();
        //     if (party != null)           return party;
        //don't re-init party
        // }

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
        chain = new HeroChain(party,                 1);
        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitsData(party));

        ObjType objType = DataManager.getType(LORD_TYPE, DC_TYPE.LORD);
        if (objType == null) {
            objType = generateLordType();
        }
        new EidolonLord(objType).setChain(chain);

        //        party.setProperty(PROPS.PARTY_MISSION,
        //                getMetaGame().getMission().getName(), true);
        return party;

    }

    private ObjType generateLordType() {
        ObjType type = new ObjType(LORD_TYPE, DC_TYPE.LORD);
        int soulforce = DC_Formulas.getSoulforceForLordLevel(LORD_LEVEL);
        type.setParam(PARAMS.SOULFORCE, soulforce);
        type.setParam(PARAMS.BASE_SOULFORCE, soulforce / 4);
        return type;
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
        for (Unit unit : getGame().getUnits()) {
            unit.getAI().combatEnded(); //TODO don't do that
        }

        log(1, "respawning as " + newHero + "; old hero: " + Eidolons.getMainHero().getInfo());
        removeOldHero();
        selectedHero = newHero;
        ObjType type = DataManager.getType(selectedHero, DC_TYPE.CHARS);
        Coordinates c = getRespawnCoordinates(type);
        //        Unit hero = (Unit) getGame().createUnit(type, c.x, c.y, getGame().getPlayer(true));// HeroCreator.initHero(selectedHero);
        //        party.getMembers().clear();
        Unit hero = null;
        for (Unit member : party.getMembers()) {
            if (member.getName().equalsIgnoreCase(newHero)) {
                hero = member;
                hero.setCoordinates(c);
                //                hero.fullReset(getGame());
                break;
            }
        }
        heroSelected(hero);

        log(1, "respawned as " + newHero + "; new hero: " + Eidolons.getMainHero().getInfo());
    }

    @Override
    public void heroSelected(Unit hero) {
        //is this the after-respawn?
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, hero);
        party.setLeader(hero);
        hero.setMainHero(true);
        hero.setPale(false);
        party.setProperty(PROPS.PARTY_MAIN_HERO, hero.getName());
        Eidolons.setSelectedMainHero(hero.getName());
        Eidolons.setMainHero(hero);
        getGame().getObjMaster().objAdded(hero);
        getGame().getManager().reset();
        getGame().getBattleFieldManager().resetWallMap();
        GuiEventManager.trigger(GuiEventType.ACTIVE_UNIT_SELECTED, hero);
        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, hero);
        GuiEventManager.trigger(GuiEventType.UNIT_VISIBLE_ON, hero);
    }

    private Coordinates getRespawnCoordinates(ObjType type) {
        //TODO shrine
        return getGame().getDungeonMaster().getFloorWrapper().getMainEntrance().getCoordinates();
    }


    private Party createParty(ObjType type, String selectedHero) {
        return new ChainParty(type, selectedHero);
    }

    @Override
    protected String chooseHero(List<String> members) {
        if (EidolonsGame.SELECT_HERO)
            return super.chooseHero(members);
        if (EidolonsGame.PUZZLES) {
            return TutorialManager.NEXT_HERO;
        }
        if (EidolonsGame.FOOTAGE) {
            if (MainLauncher.HERO_INDEX != null) {
                return members.get(MainLauncher.HERO_INDEX);
            }
            return new RandomWizard<String>().getRandomListItem(members);
        }
        if (!EidolonsGame.TUTORIAL_MISSION) {
            Collections.shuffle(members);
        }
        if (isWaitForGdx())
            WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
        return super.chooseHero(members);
    }

    public String chooseNextHero() {
        //        if (!CoreEngine.isIDE())
        if (EidolonsGame.FOOTAGE) {
            return party.getRandomMember().getName();
        }
        if (EidolonsGame.TUTORIAL_PATH || (EidolonsGame.TUTORIAL_MISSION && deaths == 0)) {
            return TutorialManager.nextHero();
        }
        GuiEventManager.trigger(GuiEventType.SHOW_SELECTION_PANEL,
                getChain().getTypes());
        return (String) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.HERO_SELECTION);
    }

    protected boolean isWaitForGdx() {
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
