package eidolons.libgdx.gui.panels.headquarters.tabs.tree;

import eidolons.libgdx.GDX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;

/**
 * Created by JustMe on 5/6/2018.
 * apart from the tree itself? what else?
 *
 * "View Full" button?
 *
 */
public abstract class HqTreeTab extends HqElement{
   protected HeroTree tree;

    public HqTreeTab() {
        add(tree = createTree()) ;
        setFixedSize(true);
        setSize(GDX.size(HqMaster.TAB_WIDTH) ,
         GDX.size(HqMaster.TAB_HEIGHT) );

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    protected abstract HeroTree createTree();
}
