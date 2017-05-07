package main.game.module.adventure.gui.party;

import main.game.module.adventure.MacroGame;
import main.swing.generic.components.G_Panel;

public class PartyTabPanel extends G_Panel {

    PartyHeader header;
    PartyMembersPanel membersPanel;
    // perhaps other views as well?
    PartyInfoPanel infoPanel; // party's gold, provisions, spirit, org, speed,
    boolean info;

    public PartyTabPanel() {
        header = new PartyHeader(MacroGame.getGame().getPlayerParty());
        infoPanel = new PartyInfoPanel(MacroGame.getGame().getPlayerParty());
        membersPanel = new PartyMembersPanel();
        add(header, "id header, pos 0 0");
        add(membersPanel.getPanel(), "id membersPanel, pos 0 header.y2");
        refresh();
    }

    public void refresh() {
        // removeAll();
        header.refresh();
        membersPanel.refresh();
        // if (info) {
        // add(infoPanel, "id infoPanel, pos 0 header.y2");
        // infoPanel.refresh();
        // } else {
        // }
        // revalidate();
    }

    public PartyHeader getHeader() {
        return header;
    }

    public PartyMembersPanel getMembersPanel() {
        return membersPanel;
    }

    public PartyInfoPanel getInfoPanel() {
        return infoPanel;
    }

    public boolean isInfo() {
        return info;
    }

}
