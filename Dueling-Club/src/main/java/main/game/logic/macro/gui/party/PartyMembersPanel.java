package main.game.logic.macro.gui.party;

import main.entity.obj.unit.DC_HeroObj;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.travel.MacroParty;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.Refreshable;

import java.util.HashMap;
import java.util.Map;

public class PartyMembersPanel implements Refreshable {
    private G_Panel panel;
    private MacroParty party;
    private DC_HeroObj selectedPartyMember;
    private Map<DC_HeroObj, PartyMemberComp> memberComps = new HashMap<>();

    public PartyMembersPanel() {
        panel = new G_Panel();
        refresh();
    }

    public void refresh() {
        party = MacroGame.getGame().getPlayerParty();
        panel.removeAll();
        for (DC_HeroObj m : party.getMembers()) {
            PartyMemberComp memberComp = memberComps.get(m);
            if (memberComp == null) {
                memberComp = new PartyMemberComp(this, m);

                memberComps.put(m, memberComp);
            }
            memberComp.refresh();
            panel.add(memberComp, "wrap");

        }
        // TODO refresh
    }

    public G_Panel getPanel() {
        return panel;
    }

    public void setPanel(G_Panel panel) {
        this.panel = panel;
    }

    public MacroParty getParty() {
        return party;
    }

    public void setParty(MacroParty party) {
        this.party = party;
    }

    public DC_HeroObj getSelectedPartyMember() {
        return selectedPartyMember;
    }

    public void setSelectedPartyMember(DC_HeroObj selectedPartyMember) {
        this.selectedPartyMember = selectedPartyMember;
    }
}
