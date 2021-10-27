package libgdx.gui.dungeon.panels.headquarters.tabs;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import libgdx.StyleHolder;
import libgdx.gui.dungeon.panels.TabbedPanel;
import libgdx.gui.dungeon.panels.headquarters.HqActor;
import libgdx.gui.dungeon.panels.headquarters.HqElement;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.dungeon.panels.headquarters.tabs.inv.HqInvTab;
import libgdx.gui.dungeon.panels.headquarters.tabs.spell.HqSpellTab;
import libgdx.gui.dungeon.panels.headquarters.tabs.tree.classes.ClassTreeTab;
import libgdx.gui.dungeon.panels.headquarters.tabs.tree.skill.SkillTreeTab;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqTabs extends TabbedPanel<HqElement> implements HqActor {

    public static final float WIDTH =460 ;
    public static final float HEIGHT =832 ;

    public HqTabs() {
        initTabs();
        setFixedSize(true);
        setSize(WIDTH, HEIGHT);
    }

    protected void initTabs() {
        addTab(new SkillTreeTab(), HQ_TAB.Skills.name());
        addTab(new ClassTreeTab(), HQ_TAB.Class.name());
        addTab(new HqInvTab(), HQ_TAB.Items.name());
        addTab(new HqSpellTab(), HQ_TAB.Spells.name());
//        resetCheckedTab();

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

    @Override
    public void setUserObject(Object userObject) {
                setSize(460, 832);
        if (getUserObject() == null) {
            super.setUserObject(userObject);
            tabsToNamesMap.values().forEach(tab -> tab.setUserObject(userObject));
            tabSelected(HQ_TAB.Class.name());
            return;
        }
        super.setUserObject(userObject);
        tabsToNamesMap.values().forEach(tab -> tab.setUserObject(userObject));
    }

    @Override
    public void tabSelected(String tabName) {
        super.tabSelected(tabName);
    }

    public enum HQ_TAB {
        Items,
        Spells,
        Skills,
        Class,
        //PRINCIPLES
    }
}
