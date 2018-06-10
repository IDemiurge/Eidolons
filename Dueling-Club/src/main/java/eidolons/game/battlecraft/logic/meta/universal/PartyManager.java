package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.TargetRunnable;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static main.system.GuiEventType.ADD_FLOATING_TEXT;
import static main.system.GuiEventType.SELECT_MULTI_OBJECTS;

public abstract class PartyManager<E extends MetaGame> extends MetaGameHandler<E> {

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

    public void gameStarted() {
        String name = getParty().getProperty(PROPS.PARTY_MAIN_HERO);
        if (Eidolons.getSelectedMainHero() != null)
            name = Eidolons.getSelectedMainHero();
        if (name.isEmpty())
            name = chooseMainHero();
        if (name.isEmpty())
            if (getMaster().getEntity() != null) {
                name = getMaster().getEntity().getProperty(PROPS.PARTY_MAIN_HERO);
            }

        Ref ref = new Ref(getParty().getLeader());
        Unit hero = getGame().getMaster().getUnitByName(name, ref, true, null, null);
        //will find 1st if name==null
        mainHeroSelected(party, hero);
    }

    protected String chooseMainHero() {
        if (party.getMembers().size() == 1) {
            return party.getLeader().getName();
        }
        //        if (chooseOneHero)
        //            return ListChooser.chooseObj(party.getMembers(), SELECTION_MODE.SINGLE);
        if (!WaitMaster.isComplete(WAIT_OPERATIONS.GUI_READY)) {
            Object result = WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY, 4000);
            if (result == null)
                return ListChooser.chooseObj(party.getMembers(), SELECTION_MODE.SINGLE);
        }
        Set<Obj> selectingSet = new HashSet<>(party.getMembers());
        Pair<Set<Obj>, TargetRunnable> p = new ImmutablePair<>(selectingSet, (t) -> {
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_BF_OBJ, t);
        });

        party.getMembers().forEach(hero -> {
            GuiEventManager.trigger(ADD_FLOATING_TEXT,
             FloatingTextMaster.getInstance().getFloatingText
              (hero, TEXT_CASES.BATTLE_COMMENT, hero.getName()));
        });
        GuiEventManager.trigger(SELECT_MULTI_OBJECTS, p);
        Unit unit = (Unit) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECT_BF_OBJ, 15000);
        if (unit == null) {
            //SWING!
            String hero = ListChooser.chooseObj(party.getMembers(), SELECTION_MODE.SINGLE);
            return hero;
        }
        return unit.getName();
    }

    protected void mainHeroSelected(Party party, Unit hero) {
        party.getMembers().forEach(member -> {
            //            if (chooseOneHero) {
            //                if (member != hero)
            //                    member.kill(member, false, true);
            //            } else
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

    public void preStart() {

    }

    protected String chooseHero(List<String> members) {
        GuiEventManager.trigger(
         GuiEventType.SHOW_SELECTION_PANEL, DataManager.toTypeList(members, DC_TYPE.CHARS));

        selectedHero = (String) WaitMaster.
         waitForInput(WAIT_OPERATIONS.SELECTION);
        main.system.auxiliary.log.LogMaster.log(1, "+++++++++selectedHero = " + selectedHero);
        return selectedHero;
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
