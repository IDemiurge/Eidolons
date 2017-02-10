package main.gui.components.tree;

import main.gui.builders.TreeViewBuilder;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TreeMouseListener implements MouseListener {
    private TreeViewBuilder view;

    public TreeMouseListener(TreeViewBuilder view) {
        this.view = view;
    }

    // looking for AV_TreeSelectionListener?
    @Override
    public void mouseClicked(MouseEvent event) {
        // if (event.isAltDown()) {
        // view.moveNode(ArcaneVault.getMainBuilder().getSelectedNode(),
        // SwingUtilities
        // .isRightMouseButton(event));
        // }

//		ArcaneVault.getMainBuilder().getSelectedNode().
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        Boolean down = null;
        if (arg0.isAltDown()) {
            down = true;
        }
        if (arg0.isControlDown()) {
            down = false;
        }
//		if (down != null)
//			view.moveNode(ArcaneVault.getMainBuilder().getSelectedNode(), down);
    }

}
