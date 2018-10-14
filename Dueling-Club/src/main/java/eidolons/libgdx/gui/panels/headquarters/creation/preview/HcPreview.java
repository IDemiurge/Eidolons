package eidolons.libgdx.gui.panels.headquarters.creation.preview;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.libgdx.gui.panels.headquarters.tabs.HqTabs;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes.ClassTreeTab;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill.SkillTreeTab;

/**
 * Created by JustMe on 7/2/2018.
 */
public class HcPreview extends HqTabs {

    public HcPreview() {
        setSize(300, 600);
    }


    protected Cell createContentsCell() {
        return super.createContentsCell().top();
    }

    @Override
    protected void initTabs() {

        addTab(new HcGeneralTab(), "General");
        addTab(new ClassTreeTab(), HQ_TAB.Class.name());
        addTab(new SkillTreeTab(), HQ_TAB.Skills.name());
        //        addTab(new HqSpellTab(), HQ_TAB.Spells.name());
        resetCheckedTab();
    }
}
