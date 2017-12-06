package main.game.battlecraft.logic.meta.scenario;

import main.client.cc.logic.party.PartyObj;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.universal.UnitData;
import main.game.battlecraft.logic.meta.universal.PartyManager;
import main.game.core.Eidolons;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;
import main.system.text.NameMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioPartyManager extends PartyManager<ScenarioMeta> {

    private static String selectedHero;

    public ScenarioPartyManager(ScenarioMetaMaster master) {
        super(master);
    }

    @Override
    public void preStart() {
//  TODO       is this the right time to set it?

    }

    public static void setSelectedHero(String selectedHero) {
        ScenarioPartyManager.selectedHero = selectedHero;
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
        Unit hero = getGame().getMaster().getUnitByName(
         selectedHero, true, null, null, getGame().getPlayer(true),
         null);
        //will find 1st if name==null
        mainHeroSelected(party, hero);
    }

    @Override
    public PartyObj initPlayerParty() {
        if (!getMaster().getMetaGame().isPartyRespawn())
        {
            party = Eidolons.getParty();
            return party;
        }
        //preset
        //choice
        //already as Unit?
        ObjType type = getMetaGame().getScenario().getPartyType();
        randomOneHero = OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.RANDOM_HERO);
        chooseOneHero = !randomOneHero;
        if (type == null) {
            String string = getMetaGame().getScenario().getProperty(PROPS.SCENARIO_PARTY);
            type = new ObjType("dummy", DC_TYPE.PARTY);
            type.setProperty(PROPS.MEMBERS, string);

        } else type = new ObjType(type);
        if (getGame().getMetaMaster().getPartyManager().isRandomOneHero() ||
         getGame().getMetaMaster().getPartyManager().isChooseOneHero()) {
            List<String> members = StringMaster.openContainer(type.getProperty(PROPS.MEMBERS));
            if (getGame().getMetaMaster().getPartyManager().isRandomOneHero()
             || members.size() == 1) {
                String hero = new RandomWizard<String>().getRandomListItem(
                 members);
                type.setProperty(PROPS.MEMBERS, hero);
            } else {
                String hero =selectedHero; //for restart
                if (hero == null )
                    hero = chooseHero(members);
                if (hero == null) {
                    return null; //aborted
                }
                type.setProperty(PROPS.MEMBERS, hero);
            }
        }
        party = new PartyObj(type);

//        if (party.getNextMission().isEmpty()) {
//            String missions = StringMaster.joinList(getMetaGame().getScenario().getAvailableMissions());
//            party.setProperty(PROPS.PARTY_MISSIONS_NEXT,
//             missions
//             StringMaster.openContainer(getMetaGame().getScenario().
//              getProperty(PROPS.SCENARIO_MISSIONS)).get(0)
//             , true);
//        }

        getGame().getState().addObject(party);
        getGame().getDataKeeper().addUnitData(new UnitData(party));

        party.setProperty(PROPS.PARTY_MISSION,
         StringMaster.openContainer(getMetaGame().getScenario().
          getProperty(PROPS.SCENARIO_MISSIONS)).get(0), true);
        return party;

    }

    private String chooseHero(List<String> members) {
        if (isSwingOn()) {
            return ListChooser.chooseType(
             members, DC_TYPE.CHARS);
        }
        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        GuiEventManager.trigger(
         GuiEventType.SHOW_SELECTION_PANEL, DataManager.toTypeList(members, DC_TYPE.CHARS));
        selectedHero = (String) WaitMaster.
         waitForInput(WAIT_OPERATIONS.SELECTION);
        main.system.auxiliary.log.LogMaster.log(1, "+++++++++selectedHero = " + selectedHero);
        return selectedHero;
    }

    private boolean isSwingOn() {
        return false;
    }

}

