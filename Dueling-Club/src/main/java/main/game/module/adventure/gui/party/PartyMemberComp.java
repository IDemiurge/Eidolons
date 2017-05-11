package main.game.module.adventure.gui.party;

import main.client.cc.gui.neo.header.PortraitComp;
import main.entity.obj.unit.Unit;
import main.game.module.adventure.MacroManager;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Panel;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PartyMemberComp extends G_Panel implements MouseListener {

    private Unit hero;
    private TextCompDC nameComp;
    private PortraitComp portrait;
    private HeroSlidePanel slidePanel; // items, full info
    private PartyMembersPanel membersPanel;

    public PartyMemberComp(PartyMembersPanel membersPanel, Unit m) {
        hero = m;
        this.membersPanel = membersPanel;
        init();
    }

    @Override
    public void refresh() {
        slidePanel.refresh();
    }

    private void init() {
        slidePanel = new HeroSlidePanel(hero);
        portrait = new PortraitComp(hero) {
            @Override
            protected void handleMouseClick() {
                // TODO Auto-generated method stub
            }
        };

        // nameComp = new TextComp(null, hero.getName()) {
        // protected int getDefaultFontSize() {
        // return 17;
        // }
        // };
        // add(nameComp, "id n, pos 0 0");
        add(portrait.getComp(), "id portrait, pos 0 n.y2");
        add(slidePanel, "id s, pos portrait.x2 n.y2");
        refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        membersPanel.setSelectedPartyMember(hero);
        MacroManager.setSelectedPartyMember(hero);
        MacroManager.refreshGui();
        SoundMaster.playEffectSound(SOUNDS.WHAT, hero);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
