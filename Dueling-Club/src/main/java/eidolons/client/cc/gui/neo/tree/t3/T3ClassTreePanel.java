package eidolons.client.cc.gui.neo.tree.t3;

import eidolons.client.cc.HC_Master;
import eidolons.client.cc.gui.neo.tree.HT_Node;
import eidolons.client.cc.gui.neo.tree.view.ClassTreeView;
import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;

import java.awt.event.MouseEvent;

public class T3ClassTreePanel extends ClassTreeView {
    private Boolean left;

    public T3ClassTreePanel(Boolean left, Object arg, Unit hero) {
        super(arg, hero);
        this.left = left;
    }

    public void refresh() {
        super.refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO special click-controls ???
        super.mouseClicked(e);
    }

    protected void select(HT_Node node, ObjType type) {
        HC_Master.getT3View(getHero(), false).selected(left, type);
        super.select(node, type);
        // HC_Master.getT3View(getHero(), false).selected(left, type);
        // CharacterCreator.typeSelected(type);
        // DC_SoundMaster.playStandardSound(STD_SOUNDS.SLOT);
        // HC_Master.setSelectedTreeNode(node);
        // tree.refresh();
        // tree.getPanel().repaint();

    }
}
