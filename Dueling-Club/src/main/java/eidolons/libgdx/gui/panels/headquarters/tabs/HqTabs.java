package eidolons.libgdx.gui.panels.headquarters.tabs;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.panels.TabbedPanel;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.tabs.inv.HqInvTab;
import eidolons.libgdx.gui.panels.headquarters.tabs.spell.HqSpellTab;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.classes.ClassTreeTab;
import eidolons.libgdx.gui.panels.headquarters.tabs.tree.skill.SkillTreeTab;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqTabs extends TabbedPanel<HqElement> implements HqActor{

    public HqTabs() {
        addTab(new SkillTreeTab() , HQ_TAB.Skills.name());
        addTab(new ClassTreeTab() , HQ_TAB.Class.name());
        addTab(new HqInvTab() , HQ_TAB.Items.name());
        addTab(new HqSpellTab() , HQ_TAB.Spells.name());
        setFixedSize(true);
        setSize(460, 832);
        resetCheckedTab();
//        addTab( , HQ_TAB.Inventory.name());
    }

    @Override
    public void setUserObject(Object userObject) {
        setSize(460, 832);
        super.setUserObject(userObject);
        tabsToNamesMap.values().forEach(tab-> tab.setUserObject(userObject));
    }

    @Override
    protected TextButtonStyle getTabStyle() {
        return StyleHolder.getHqTabStyle();
    }

    @Override
    public HqHeroDataSource getUserObject() {
        setSize(460, 832);
        return (HqHeroDataSource) super.getUserObject();
    }

    public enum HQ_TAB{
        Items,
        Spells,
        Skills,
        Class,
        //PRINCIPLES
    }
}
