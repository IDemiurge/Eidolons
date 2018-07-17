package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

public abstract class PartyManager<E extends MetaGame> extends MetaGameHandler<E> {

    public static final java.lang.String NEW_HERO_PARTY = "Your Party";
    protected static String selectedHero;
    protected Party party;
    protected boolean chooseOneHero;
    protected boolean randomOneHero;
    protected int partyLevel;

    public PartyManager(MetaGameMaster master) {
        super(master);
    }

    public static void setSelectedHero(String selectedHero) {
        PartyManager.selectedHero = selectedHero;
    }

    public abstract Party initPlayerParty();

    public Party getParty() {
        return party;
    }


    protected String chooseHero(List<String> members) {
        GuiEventManager.trigger(
         GuiEventType.SHOW_SELECTION_PANEL, DataManager.toTypeList(members, DC_TYPE.CHARS));

        selectedHero = (String) WaitMaster.
         waitForInput(WAIT_OPERATIONS.SELECTION);
        main.system.auxiliary.log.LogMaster.log(1, "+++++++++selectedHero = " + selectedHero);
        return selectedHero;
    }

    public void gameStarted() {
        Unit hero = findMainHero();
        //will find 1st if name==null
        mainHeroSelected(party, hero);

        getMetaGame().setRestarted(false);
    }

    protected abstract Unit findMainHero();

    public void preStart() {

    }

    protected void mainHeroSelected(Party party, Unit hero) {
        party.getMembers().forEach(member -> {
            try {
                SkillMaster.initMasteryRanks(member);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            //TODO refactor
            member.setMainHero(false);
        });
        try {
            hero.getOwner().setHeroObj(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        hero.setMainHero(true);
        party.setProperty(PROPS.PARTY_MAIN_HERO, hero.getName());
        Eidolons.setSelectedMainHero(hero.getName());
        Eidolons.setMainHero(hero);
        party.addMember(hero);
    }


    public boolean isChooseOneHero() {
        return chooseOneHero;
    }

    public void setChooseOneHero(boolean chooseOneHero) {
        this.chooseOneHero = chooseOneHero;
    }

    public boolean isRandomOneHero() {
        return randomOneHero;
    }

    public void setRandomOneHero(boolean randomOneHero) {
        this.randomOneHero = randomOneHero;
    }

    public String checkLeveledHeroVersionNeeded(String heroName) {
        return heroName;
    }

    public int getPartyLevel() {
        return partyLevel;
    }

    public void setPartyLevel(int partyLevel) {
        this.partyLevel = partyLevel;
    }
}
