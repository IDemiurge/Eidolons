package main.client.cc.gui.neo.tree.t3;

import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tree.HT_Node;
import main.client.cc.gui.neo.tree.view.SkillTreeView;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.launch.CoreEngine;

import java.awt.event.MouseEvent;

public class T3SkillTreePanel extends SkillTreeView {
    private Boolean left;

    public T3SkillTreePanel(Boolean left, Object arg, Unit hero) {
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
        if (CoreEngine.isArcaneVault()) {
            HC_Master.getT3View(getHero(), true).selected(left, type);
        }
        super.select(node, type);
        // DC_SoundMaster.playStandardSound(STD_SOUNDS.SLOT);
        // HC_Master.setSelectedTreeNode(node);
        // tree.refresh();
        // tree.getPanel().repaint();
    }
}
