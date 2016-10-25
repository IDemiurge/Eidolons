package main.client.cc.gui.neo.principles;

import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.entity.obj.DC_HeroObj;
import main.swing.generic.components.G_Panel;
import main.system.images.ImageManager;

import javax.swing.*;

public class PrincipleItemPanel extends G_Panel {

    PrincipleItemPagedListPanel list;
    PrincipleTable table;
    PRINCIPLES principle;
    private JLabel principleIcon;

    public PrincipleItemPanel(DC_HeroObj hero) {
        super(VISUALS.PRINCIPLE_PANEL_FRAME);
        table = new PrincipleTable(hero, false);
        list = new PrincipleItemPagedListPanel(hero, table);
        // TODO default view (no selection) - ???
        principleIcon = new JLabel(new ImageIcon(ImageManager.getPrincipleImage(principle)));
    }

    public void init() {
        add(principleIcon, "id icon, pos 40 15");
        add(list, "id list, pos 40 icon.y2");
        list.refresh();
        add(table, "id table,pos list.x2 15");
    }

    @Override
    public void refresh() {
        principleIcon.setIcon(new ImageIcon(ImageManager.getPrincipleImage(principle)));
        table.refresh();
        list.refresh();

    }

    public PRINCIPLES getPrinciple() {
        return principle;
    }

    public void setPrinciple(PRINCIPLES principle) {
        this.principle = principle;
        table.setSelectedPrinciple(principle);
        refresh();
    }
}
