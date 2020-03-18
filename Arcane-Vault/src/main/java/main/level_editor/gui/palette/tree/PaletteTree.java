package main.level_editor.gui.palette.tree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.system.graphics.FontMaster;

public class PaletteTree extends TreeX<PaletteNode> {

    //we need a way to collapse double nodes

    public PaletteTree(DC_TYPE type) {
        setUserObject(new PaletteTreeBuilder().buildPaletteTreeModel(type));
        addActor(new SmartButton(ButtonStyled.STD_BUTTON.UNDO,
                () -> collapseAll()));

//        addActor(new SmartButton(ButtonStyled.STD_BUTTON.UNDO, () -> collapseAll()));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected Node createNodeComp(PaletteNode node) {
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

        if (texture!=null ){
            actor.getImageContainer().getActor().setScale(0.25f);
        }

        boolean hidden;
        return new Node(actor);
    }


}
