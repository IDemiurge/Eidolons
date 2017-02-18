package main.client.cc.gui.neo.principles;

import main.entity.Entity;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.rules.rpg.IntegrityRule;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.graphics.GuiManager;

import java.util.LinkedList;
import java.util.List;

public class PrincipleItemPagedListPanel extends G_PagePanel<Entity> {

    private static final int PAGE_SIZE = 10;
    private Unit hero;
    private PrincipleTable table;

    public PrincipleItemPagedListPanel(Unit hero, PrincipleTable table) {
        super(PAGE_SIZE, true, 3);
        this.hero = hero;
        this.table = table;
    }

    @Override
    public int getWrap() {
        return 1;
    }

    protected int getItemSize() {
        return GuiManager.getSmallObjSize();
    }

    @Override
    protected List<List<Entity>> getPageData() {
        List<Entity> items = new LinkedList<>();

        if (checkItemDisplayed(hero.getDeity())) {
            items.add(hero.getDeity());
        }
        if (hero.getBackgroundType() != null) // TODO
        {
            if (checkItemDisplayed(hero.getBackgroundType())) {
                items.add(hero.getBackgroundType());
            }
        }
        for (DC_FeatObj feat : hero.getClasses()) {
            if (checkItemDisplayed(feat)) {
                items.add(feat);
            }
        }
        for (DC_FeatObj feat : hero.getSkills()) {
            if (checkItemDisplayed(feat)) {
                items.add(feat);
            }
        }
        return splitList(items);
    }

    @Override
    protected boolean isFillWithNullElements() {
        return true;
    }

    private boolean checkItemDisplayed(Entity item) {
        return IntegrityRule.isAffectingPrinciple(item, hero, table.getSelectedPrinciple());
    }

    @Override
    public void refresh() {
        super.refresh();

        table.setData(getPageData().get(getCurrentIndex()));
        // main.system.auxiliary.LogMaster.log(1, getData() + "***** setData " +
        // getPageData());
        // table.setprinciple
    }

    @Override
    public void flipPage(boolean forward) {
        super.flipPage(forward);
        table.setData(getData());
        // CharacterCreator.getPanel().getPrincipleView();
    }

    @Override
    protected G_Component createPageComponent(List<Entity> list) {
        return new PrincipleItemList(list);
    }

}
