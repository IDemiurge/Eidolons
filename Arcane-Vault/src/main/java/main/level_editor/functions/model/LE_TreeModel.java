package main.level_editor.functions.model;

import main.level_editor.gui.tree.data.LE_Node;
import main.level_editor.gui.tree.data.LE_TreeBuilder;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;

public class LE_TreeModel {

    private LE_Node rootNode;

    public LE_Node getRootNode() {
        return rootNode;
    }

    public LE_TreeModel(Campaign campaign) {
        rootNode = new LE_TreeBuilder(campaign).getNode();
    }
    public LE_TreeModel(Floor floor) {
        rootNode = new LE_TreeBuilder(floor).getNode();
    }
}
