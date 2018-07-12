package eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HeroTree;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HqTreeTab;

/**
 * Created by JustMe on 5/7/2018.
 */
public class ClassTreeTab extends HqTreeTab {
    private final ClassSelectionMenu selectionMenu;
    private final PerkSelectionMenu perkSelectionMenu;

    public ClassTreeTab() {
        this(false);
    }

    public ClassTreeTab(boolean altBackground) {
        super(altBackground);
        addActor(selectionMenu = new ClassSelectionMenu());
        addActor(perkSelectionMenu = new PerkSelectionMenu());

    }

    @Override
    protected void update(float delta) {
        selectionMenu.setUserObject(getUserObject());
        perkSelectionMenu.setUserObject(getUserObject());
    }

    @Override
    protected HeroTree createTree() {
        return new ClassTree(altBackground);
    }
}
