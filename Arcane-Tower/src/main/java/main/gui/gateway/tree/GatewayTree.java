package main.gui.gateway;

import main.logic.AT_OBJ_TYPE;
import main.logic.ArcaneEntity;
import main.logic.Direction;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_ScrolledPanel;

import java.util.List;

public class GatewayTree {
    G_Panel panel;
    List<ArcaneEntity> data;
    private AT_OBJ_TYPE TOP_TYPE;

    public void construct() {

        G_ScrolledPanel<Direction> directions;

    }

    // public void init() {
    // List<ArcaneEntity> tops = getTopEntities(data);
    //
    // for (ArcaneEntity top : tops) {
    // List<ArcaneEntity> branch = top.addChildrenTo(branch);
    // Collections.reverse(branch);
    // maxWidth = w/tops.size();
    // //top to bottom
    // TYPE = null ;
    // for (ArcaneEntity generic : branch) {
    //
    // String pos = "wrap";
    // panel.add(new GatewayTreeNode(generic));
    // }
    // }
    // }
    //
    // private List<ArcaneEntity> getTopEntities(List<ArcaneEntity> data) {
    // List<ArcaneEntity> list = new ArrayList<>();
    // for (ArcaneEntity generic : data) {
    // if (generic.getOBJ_TYPE_ENUM() == TOP_TYPE)
    // list.add(generic);
    // }
    // return list;
    // }

    // if (prevTYPE!=TYPE){
    // //new subpanel/wrap
    // }
    // max width of a branch? total/count

	/*
     * distance between nodes
	 * what will the nodes look like? 
	 * flexible - header+icon+param/prop panel a la WrappedText
	 */
    // String parent;
    // String relativeX;
    // String xOffset;
    // String relativeY;
    // String yOffset;
    // panel.add(new GatewayTreeNode(generic).getClass(), "pos " +
    // parent +
    // relativeX + "+("
    // + xOffset + ")" + " " + parent + relativeY + "+(" + yOffset +
    // ")");

    // OR just use flowx with wrap?
    // OR use subPanel
}
