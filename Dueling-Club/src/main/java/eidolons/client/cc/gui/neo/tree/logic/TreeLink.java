package eidolons.client.cc.gui.neo.tree.logic;

import eidolons.client.cc.gui.neo.tree.HC_Tree.LINK_TYPE;
import eidolons.client.cc.gui.neo.tree.HT_Node;
import main.swing.XLine;

public class TreeLink {
    LINK_TYPE type;
    XLine line;
    HT_Node node1;
    HT_Node node2;

    public TreeLink(LINK_TYPE type, XLine line) {
        this.type = type;
        this.line = line;
    }

    public HT_Node getNode1() {
        return node1;
    }

    public void setNode1(HT_Node node1) {
        this.node1 = node1;
    }

    public HT_Node getNode2() {
        return node2;
    }

    public void setNode2(HT_Node node2) {
        this.node2 = node2;
    }

    public LINK_TYPE getType() {
        return type;
    }

    public XLine getLine() {
        return line;
    }

}
