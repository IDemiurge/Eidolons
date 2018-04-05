package eidolons.client.cc.gui.neo.slide;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.gui.neo.points.HC_PointView;
import eidolons.client.cc.gui.neo.tabs.HC_TabPanel;
import eidolons.client.cc.gui.neo.tabs.TabChangeListener;
import eidolons.client.cc.gui.views.HeroView;
import eidolons.content.PARAMS;
import eidolons.content.ValuePages;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.math.DC_MathManager;
import main.content.values.parameters.PARAMETER;

import java.util.Arrays;
import java.util.List;

public class MasterySlidePanel extends HeroView {

    private static final String COMBAT = "Combat";
    private static final String MAGIC = "Magic";
    private static final String MISC = "Misc";
    private static final String UNLOCKED = "Mastered";
    private static int UNLOCKED_INDEX = 3;
    HC_TabPanel tabs;
    private List<PARAMETER> combatList;
    private List<PARAMETER> magicList;
    private List<PARAMETER> miscList;
    private List<PARAMETER> unlockedList;
    private HC_PointView[] views;

    public MasterySlidePanel(final Unit hero) {
        super(hero);
        initLists();
        views = new HC_PointView[]{
         // new HC_PointView(allList, hero, true),
         new HC_PointView(combatList, hero),
         new HC_PointView(magicList, hero),
         new HC_PointView(miscList, hero),
         new HC_PointView(unlockedList, hero)

        };
        tabs = new HC_TabPanel();
        // tabs.addTab(ALL, views[0]);
        tabs.addTab(COMBAT, views[0]);
        tabs.addTab(MAGIC, views[1]);
        tabs.addTab(MISC, views[2]);
        UNLOCKED_INDEX = 3;
        tabs.addTab(UNLOCKED, views[UNLOCKED_INDEX]);
        add(tabs, "pos 0 0");
        if (unlockedList.size() > 0) {
            tabs.select(UNLOCKED_INDEX);
        } else {
            tabs.select(0);
        }
        tabs.setChangeListener(new TabChangeListener() {
            public void tabSelected(int index) {

                if (index == UNLOCKED_INDEX) {
                    resetUnlocked();
                }

                if (tabs.getIndex() == UNLOCKED_INDEX) {
                    resetUnlocked();
                }

                refresh();
            }

            @Override
            public void tabSelected(String name) {

            }
        });
    }

    public HC_PointView getCurrentView() {
        int index = tabs.getIndex();
        return (HC_PointView) tabs.getTabs().get(index).getComponent();
    }

    public HC_PointView[] getViews() {
        return views;
    }

    protected void resetUnlocked() {
        unlockedList = DC_MathManager.getUnlockedMasteries(hero);
        tabs.getTabs().get(UNLOCKED_INDEX)
         .setComp(new HC_PointView(unlockedList, hero));

    }

    private void initLists() {
        combatList = Arrays.asList(ValuePages.MASTERIES_COMBAT);
        magicList = Arrays.asList(ValuePages.MASTERIES_MAGIC);
        miscList = Arrays.asList(ValuePages.MASTERIES_MISC);
        unlockedList = DC_MathManager.getUnlockedMasteries(hero);
    }

    @Override
    public void refresh() {

        try {
            getCurrentView().setBuffer(CharacterCreator.getHeroPanel()
             .getMiddlePanel().getScc().getBufferType());
        } catch (Exception e) {

        }
        getCurrentView().refresh();
    }

    @Override
    public void init() {

    }

    @Override
    public void activate() {
        tabs.refresh();
    }

    @Override
    public PARAMS getPoolParam() {
        return null;
    }

}
