package eidolons.libgdx.gui.panels.headquarters.tabs.tree;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 5/6/2018.
 * apart from the tree itself? what else?
 *
 * "View Full" button?
 *
 */
public abstract class HqTreeTab extends HqElement{
    private final Image tabsBg;
    protected boolean altBackground;
    protected HeroTree tree;

    public HqTreeTab(boolean altBackground) {
        this.altBackground = altBackground;
        addActor(tabsBg = new Image(TextureCache.getOrCreate(Images.COLUMNS)));
        addActor(tree = createTree());
        setSize(530, 890);
//        tree.setPosition( 40-tree.getWidth()/2, -tree.getHeight()/2);
        setFixedSize(true);
        setSize(GDX.size(HqMaster.TAB_WIDTH) ,
         GDX.size(HqMaster.TAB_HEIGHT) );
        tabsBg.setPosition(
                GdxMaster.centerWidth(tabsBg), GdxMaster.top(tabsBg));

    }

    @Override
    public void act(float delta) {
//        tree.setPosition(  0 ,  0);
        super.act(delta);
        tabsBg.setPosition(
                GdxMaster.centerWidth(tabsBg)+14, GdxMaster.top(tabsBg));
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    protected abstract HeroTree createTree();
}
