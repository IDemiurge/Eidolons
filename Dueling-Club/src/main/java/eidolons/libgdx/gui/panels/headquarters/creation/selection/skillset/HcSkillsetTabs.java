package eidolons.libgdx.gui.panels.headquarters.creation.selection.skillset;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.tabs.HqTabs;
import eidolons.libgdx.gui.panels.headquarters.tabs.spell.HqSpellTab;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes.ClassTreeTab;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill.SkillTreeTab;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcSkillsetTabs extends HqTabs {

    @Override
    protected void initTabs() {
        addTab(new HcStatsPanel(), "Stats");
        addTab(new SkillTreeTab(true), HQ_TAB.Skills.name());
        addTab(new ClassTreeTab(true), HQ_TAB.Class.name());
        addTab(new HqSpellTab(), HQ_TAB.Spells.name());
        resetCheckedTab();
    }

    @Override
    public void tabSelected(String tabName) {
        super.tabSelected(tabName);
        setUserObject(new HqHeroDataSource(HeroCreationMaster.getModel()));
    }
    protected Cell createContentsCell() {
        return    super.createContentsCell().top();
    }
}
