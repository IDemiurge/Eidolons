package eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill;

import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HeroTree;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.HqTreeTab;

/**
 * Created by JustMe on 5/7/2018.
 */
public class SkillTreeTab extends HqTreeTab {
    private final SkillSelectionMenu selectionMenu;

    public SkillTreeTab() {
        super();
        addActor(selectionMenu = new SkillSelectionMenu());

        }

    @Override
    protected void update(float delta) {
        selectionMenu.setUserObject(getUserObject());
    }

    @Override
    protected HeroTree createTree() {
        return new SkillTree();
    }
}
