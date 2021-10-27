package main.level_editor.gui.panels.palette.tree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import libgdx.StyleHolder;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.assets.texture.TextureCache;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import libgdx.gui.editor.components.TreeX;
import main.system.graphics.FontMaster;

import java.util.List;

public class PaletteTree extends TreeX<PaletteNode> {
    private final TablePanelX table;
    private final DC_TYPE TYPE;
    private String lastSelected;
    private String lastSelectedName;
    private String lastSelectedParent;

    //we need a way to collapse double nodes

    public PaletteTree(DC_TYPE type, TablePanelX table) {
        this.table = table;
        this.TYPE = type;
        PaletteTreeBuilder builder = new PaletteTreeBuilder();
        builder.setShowLeaf(false);
        setUserObject(builder.buildPaletteTreeModel(type));
//        addActor(new SmartButton(ButtonStyled.STD_BUTTON.UNDO,
//                () -> collapseAll()));

//        addActor(new SmartButton(ButtonStyled.STD_BUTTON.UNDO, () -> collapseAll()));
    }

    public void reselect() {
       if (!forceSelectByName(lastSelected )){
           forceSelectByName(lastSelectedParent);
       }

    }

    private boolean forceSelectByName(String lastSelected) {
        for (Node node : nodes) {
            try {
                if (node.toString().equalsIgnoreCase(lastSelected)) {
                    click(1, null, node, (PaletteNode) node.getObject());
                    node.expandTo();
                    return true;
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void rightClick(PaletteNode node) {

    }

    @Override
    protected void doubleClick(PaletteNode node) {

    }

    @Override
    protected void selected(PaletteNode node, Node n) {
        lastSelected = node.getData().toString();
        lastSelectedName = node.toString();
        if (n.getParent() != null) {
             lastSelectedParent = n.getParent().toString();
        }
        List<ObjType> types = LevelEditor.getCurrent().
                getManager().getPaletteHandler().
                getTypesForTreeNode(TYPE, node.getData());
        table.setUserObject(types);
    }

    @Override
    protected Actor createNodeComp(PaletteNode node) {
        Label.LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 20);
        TextureRegion texture = null;
        String name = node.getData().toString();
        String val = null;

        if (node.getData() instanceof ObjType) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 15);
            ObjType ty = (ObjType) node.getData();
            name = node.getData().toString();
            texture = TextureCache.getOrCreateR(ty.getImagePath());
            //emblem? auto-gen 32x32?
        }
        ValueContainer actor = new ValueContainer(style, texture, name, val);

        if (texture != null) {
            actor.getImageContainer().getActor().setScale(0.25f);
        }
        return actor;
    }

    public String getLastSelected() {
        return lastSelected;
    }

    public String getLastSelectedName() {
        return lastSelectedName;
    }
}
