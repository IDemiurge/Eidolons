package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HeroTree;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HqTreeTab;

/**
 * Created by JustMe on 5/7/2018.
 */
public class ClassTreeTab extends HqTreeTab {
    @Override
    protected void update(float delta) {

    }

    @Override
    protected HeroTree createTree() {
        return new ClassTree();
    }
}
