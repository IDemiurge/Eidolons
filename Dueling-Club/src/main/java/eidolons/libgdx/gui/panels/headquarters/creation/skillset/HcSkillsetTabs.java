package eidolons.libgdx.gui.panels.headquarters.creation.skillset;

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
        addTab(new SkillTreeTab(), HQ_TAB.Skills.name());
        addTab(new ClassTreeTab(), HQ_TAB.Class.name());
        addTab(new HqSpellTab(), HQ_TAB.Spells.name());
        resetCheckedTab();
    }
}
