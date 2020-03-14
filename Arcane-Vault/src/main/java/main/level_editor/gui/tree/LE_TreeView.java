package main.level_editor.gui.tree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisTree;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import main.level_editor.backend.model.LE_TreeModel;
import main.level_editor.gui.tree.data.LE_DataNode;
import main.level_editor.struct.boss.BossDungeon;
import main.level_editor.struct.campaign.Campaign;
import main.level_editor.struct.level.Floor;
import main.level_editor.struct.module.LE_Module;
import main.level_editor.struct.module.ObjNode;
import main.system.graphics.FontMaster;

import java.util.Set;

public class LE_TreeView extends VisTree {

    private Node root;

    @Override
    public void add(Node node) {
        super.add(node);
    }

    @Override
    public void setUserObject(Object userObject) {
        LE_TreeModel treeModel = (LE_TreeModel) userObject;
        LE_DataNode rootNode = treeModel.getRootNode();
        add(root = createRootNode(rootNode));
        recursiveAdd(root, rootNode);
        super.setUserObject(userObject);
        debugAll();
    }

    private Node createRootNode(LE_DataNode rootNode) {
        //just name
        return (createNodeComp(rootNode));
    }

    private void recursiveAdd(Node root, LE_DataNode node) {
        root.add(root = createNodeComp(node));

        Set<? extends LE_DataNode> set = node.getChildren();
        for (LE_DataNode child : set) {
            if (child.isLeaf()) {
                 root.add(createNodeComp(node));
            } else
                recursiveAdd(root, child);
        }
    }

    private Node createNodeComp(LE_DataNode node) {
        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
        TextureRegion texture = null;
        String name = null;
        String val = null;
        if (node.getData() instanceof Campaign) {
            Campaign campaign = ((Campaign) node.getData());

            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
            texture = TextureCache.getOrCreateR(campaign.getImagePath());
        }
        if (node.getData() instanceof BossDungeon) {
            BossDungeon dungeon = ((BossDungeon) node.getData());
        }
        if (node.getData() instanceof Floor) {
            Floor floor = ((Floor) node.getData());
        }
        if (node.getData() instanceof LE_Module) {
            LE_Module module = ((LE_Module) node.getData());
        }
        if (node.getData() instanceof LevelZone) {
            LevelZone zone = ((LevelZone) node.getData());
        }
        if (node.getData() instanceof LevelBlock) {
            LevelBlock block = ((LevelBlock) node.getData());
        }
        if (node.getData() instanceof ObjNode) { // or id?
            DC_Obj dcObj = ((DC_Obj) node.getData());
        }
//        if (node.getData() instanceof String) {
//other stuff...
//        }


        Actor actor = new ValueContainer(style, texture, name, val);
        boolean hidden;

        return new Node(actor);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
