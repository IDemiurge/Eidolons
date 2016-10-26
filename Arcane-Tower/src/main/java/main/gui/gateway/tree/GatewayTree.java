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
	// for (ArcaneEntity sub : branch) {
	//
	// String pos = "wrap";
	// panel.add(new GatewayTreeNode(sub));
	// }
	// }
	// }
	//
	// private List<ArcaneEntity> getTopEntities(List<ArcaneEntity> data) {
	// List<ArcaneEntity> list = new LinkedList<>();
	// for (ArcaneEntity sub : data) {
	// if (sub.getOBJ_TYPE_ENUM() == TOP_TYPE)
	// list.add(sub);
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
	// panel.add(new GatewayTreeNode(sub).getClass(), "pos " +
	// parent +
	// relativeX + "+("
	// + xOffset + ")" + " " + parent + relativeY + "+(" + yOffset +
	// ")");

	// OR just use flowx with wrap?
	// OR use subPanel
}
