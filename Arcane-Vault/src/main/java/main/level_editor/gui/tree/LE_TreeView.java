package main.level_editor.gui.tree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.level_editor.backend.struct.boss.BossDungeon;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.backend.struct.module.LE_Module;
import main.level_editor.backend.struct.module.ObjNode;
import main.level_editor.gui.palette.tree.TreeX;
import main.level_editor.gui.tree.data.LE_DataNode;
import main.system.graphics.FontMaster;

public class LE_TreeView extends TreeX<LE_DataNode> {

    @Override
    protected Node createNodeComp(LE_DataNode node) {

        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 16);
        TextureRegion texture = null;
        String name = node.getData().toString();
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
            texture = TextureCache.getOrCreateR(Images.CHEST_OPEN);
        }
        if (node.getData() instanceof LE_Module) {
            LE_Module module = ((LE_Module) node.getData());
            name = "--- " + module.toString();
            texture = TextureCache.getOrCreateR(Images.ITEM_BACKGROUND_STONE);
        }
        if (node.getData() instanceof LevelZone) {
            LevelZone zone = ((LevelZone) node.getData());
            name = "-- " +"Zone " + zone.toString();
//            val = zone.getSubParts().size();
        }
        if (node.getData() instanceof LevelBlock) {
            LevelBlock block = ((LevelBlock) node.getData());
            name = "- " + (block.isTemplate() ? "Template " : "Custom ") + block.getRoomType() + " [" +
                    block.getSquare() +
                    "]";
        }
        if (node.getData() instanceof ObjNode) { // or id?
            ObjNode dcObj = ((ObjNode) node.getData());
            texture = TextureCache.getOrCreateR(dcObj.getObj().getImagePath());

        }
//        if (node.getData() instanceof String) {
//other stuff...
//        }


        Actor actor = new ValueContainer(style, texture, name, val);
        boolean hidden;

        return new Node(actor);
    }
}
