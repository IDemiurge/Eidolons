package main.level_editor.backend.model;

import main.level_editor.gui.tree.data.LE_DataNode;
import main.level_editor.gui.tree.data.LE_TreeBuilder;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;

public class LE_TreeModel {

    private LE_DataNode rootNode;

    public LE_DataNode getRootNode() {
        return rootNode;
    }

    public LE_TreeModel(Campaign campaign) {
        rootNode = new LE_TreeBuilder(campaign).getRoot();
    }
    public LE_TreeModel(Floor floor) {
        rootNode = new LE_TreeBuilder(floor).getRoot();
    }
}
