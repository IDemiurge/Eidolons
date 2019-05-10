package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitData;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioPartyManager extends PartyManager<ScenarioMeta> {


    public ScenarioPartyManager(MetaGameMaster master) {
        super(master);
    }

    @Override
    public void preStart() {
        //  TODO       is this the right time to set it?

    }

    protected boolean isWaitForGdx() {
        return true;
    }

    @Override
    public int getPartyLevel() {
        return getMetaGame().getMissionIndex();
    }

    @Override
    public String checkLeveledHeroVersionNeeded(String heroName) {

        int i = getMetaGame().getMissionIndex();
        //        getMetaDataManager().getMissionName()
        while (i > 0) {
            heroName = NameMaster.appendVersionToName(heroName, i + 1);
            if (DataManager.isTypeName(heroName, DC_TYPE.CHARS))
                break;
            i--;
        }

        return super.checkLeveledHeroVersionNeeded(heroName);
    }

    @Override
    public ScenarioMetaDataManager getMetaDataManager() {
        return (ScenarioMetaDataManager) super.getMetaDataManager();
    }

    @Override
    public void gameStarted() {
        Unit hero = findMainHero();
        //will find 1st if name==null
        mainHeroSelected(party, hero);

        getMetaGame().setRestarted(false);


    }

    protected Unit findMainHero() {
        return getParty().getLeader();
        //        Unit hero = Eidolons.getMainHero();
        //        if (hero == null || getMetaGame().isRestarted()) {
        //            hero = getGame().getMaster().getUnitByName(
        //             PartyManager.selectedHero, true, null, null, getGame().getPlayer(true),
        //             null);
        //            if (hero == null) {
        //                List<Unit> list = getGame().getUnits().stream().
        //                 filter(unit -> unit.isPlayerCharacter()).collect(Collectors.toList());
        //                hero = list.get(0);
        //            }
        //        }
        //        return hero;
    }


    @Override
    public Party initPlayerParty() {
        if (!getMaster().getMetaGame().isPartyRespawn()) {
            party = Eidolons.getParty();
            if (party != null)
                return party;
        }


        ObjType type = new ObjType(getMetaGame().getScenario().getPartyType());
        randomOneHero = OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.RANDOM_HERO);
        chooseOneHero = !randomOneHero;

        if (isCreateNewHero()) {
            type =  new ObjType
             (PartyManager.NEW_HERO_PARTY, DC_TYPE.PARTY);
            getGame().initType(type);
            Unit hero = HeroCreationMaster.newHero();
            party = new Party(type);
            party.addMember(hero);
        } else if (isRandomOneHero() ||
         isChooseOneHero()) {
            List<String> members = ContainerUtils.openContainer(type.getProperty(PROPS.MEMBERS));
            if (isRandomOneHero()
             || members.size() == 1) {
                String hero = new RandomWizard<String>().getRandomListItem(
                 members);
                type.setProperty(PROPS.MEMBERS, hero);
            } else {
                String hero = selectedHero; //for restart
                if (hero == null)
                    hero = chooseHero(members);
                if (hero == null) {
                    return null; //aborted
                }
                type.setProperty(PROPS.MEMBERS, hero);
            }
        }
        if (party == null) {
            party = new Party(type);
        }

        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));

        party.setProperty(PROPS.PARTY_MISSION,
         ContainerUtils.openContainer(getMetaGame().getScenario().
          getProperty(PROPS.SCENARIO_MISSIONS)).get(0), true);
        return party;

    }

    @Override
    protected String chooseHero(List<String> members) {
        if (isWaitForGdx())
            WaitMaster.waitForInput(WAIT_OPERATIONS.DUNGEON_SCREEN_PRELOADED);
        return super.chooseHero(members);
    }

    private boolean isCreateNewHero() {
        return HeroCreationMaster.NEW_ON_LAUNCH;
    }

}
