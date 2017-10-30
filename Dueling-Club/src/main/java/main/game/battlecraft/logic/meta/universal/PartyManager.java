package main.game.battlecraft.logic.meta.universal;

import main.client.cc.logic.party.PartyObj;
import main.content.PROPS;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.core.Eidolons;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.bf.TargetRunnable;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.GuiEventManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

import static main.system.GuiEventType.ADD_FLOATING_TEXT;
import static main.system.GuiEventType.SELECT_MULTI_OBJECTS;

public abstract class PartyManager<E extends MetaGame> extends MetaGameHandler<E> {

    protected PartyObj party;
    protected boolean chooseOneHero;
    private int partyLevel;

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
        if (Eidolons.getSelectedMainHero() != null)
            name = Eidolons.getSelectedMainHero();
        if (name.isEmpty())
            if (getMaster().getEntity() != null) {
                //TODO set main hero if created
                name = getMaster().getEntity().getProperty(PROPS.PARTY_MAIN_HERO);
            }
//            if (true){
        name = chooseMainHero();
        if (name == null)
            name = "Harlen Rolwain";

        Ref ref = new Ref(getParty().getLeader());
        Unit hero = getGame().getMaster().getUnitByName(name, ref, true, null, null);
        //will find 1st if name==null
        mainHeroSelected(party, hero);
    }

    private String chooseMainHero() {
        if (party.getMembers().size() == 1) {
            return party.getLeader().getName();
        }
        if (chooseOneHero)
            return ListChooser.chooseObj(party.getMembers(), SELECTION_MODE.SINGLE);
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
//            List<JButton> pics = party.getMembers().stream().map(hero ->
//             new JButton(ImageManager.getIcon(hero.getImagePath().replace(" 128", "")))).collect(Collectors.toList());
//            int i = DialogMaster.optionChoice(pics.toArray(), "Choose a hero to control...");
//            if (i==-1)
//                i=0;
            String hero =
//             party.getMembers().get(i).getName();
             ListChooser.chooseObj(party.getMembers(), SELECTION_MODE.SINGLE);
            return hero;
        }
        return unit.getName();
    }

    private void mainHeroSelected(PartyObj party, Unit hero) {
        party.getMembers().forEach(member -> {
            if (chooseOneHero)
                member.kill(member, false, true);
            else
                member.setMainHero(false);
        });
        hero.getOwner().setHeroObj(hero);
        hero.setMainHero(true);
        party.setProperty(PROPS.PARTY_MAIN_HERO, hero.getName());
        Eidolons.setSelectedMainHero(hero.getName());
    }

    public void preStart() {

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
