package eidolons.game.module.adventure.gui.party;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.entity.MacroParty;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.Refreshable;

import java.util.HashMap;
import java.util.Map;

public class PartyMembersPanel implements Refreshable {
    private G_Panel panel;
    private MacroParty party;
    private Unit selectedPartyMember;
    private Map<Unit, PartyMemberComp> memberComps = new HashMap<>();

    public PartyMembersPanel() {
        panel = new G_Panel();
        refresh();
    }

    public void refresh() {
        party = MacroGame.getGame().getPlayerParty();
        panel.removeAll();
        for (Unit m : party.getMembers()) {
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

    public Unit getSelectedPartyMember() {
        return selectedPartyMember;
    }

    public void setSelectedPartyMember(Unit selectedPartyMember) {
        this.selectedPartyMember = selectedPartyMember;
    }
}
