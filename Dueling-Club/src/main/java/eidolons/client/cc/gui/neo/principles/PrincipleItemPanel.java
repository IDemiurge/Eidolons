package eidolons.client.cc.gui.neo.principles;

import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.swing.generic.components.G_Panel;
import main.system.images.ImageManager;

import javax.swing.*;

public class PrincipleItemPanel extends G_Panel {

    PrincipleItemPagedListPanel list;
    PrincipleTable table;
    PRINCIPLES principle;
    private JLabel principleIcon;

    public PrincipleItemPanel(Unit hero) {
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
