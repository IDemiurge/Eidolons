package libgdx.gui.panels.headquarters.creation;

import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.SelectionPanel;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.system.auxiliary.EnumMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/5/2018.
 */
public class HeroCreationPanel extends SelectionPanel {

    private static HeroCreationPanel instance;

    private HeroCreationPanel() {
        super();
        HqHeroDataSource dataSource = HqDataMaster.getHeroDataSource(HeroCreationMaster.getModel().getHero());
        setUserObject(dataSource);
        init();
    }

    @Override
    protected String getTitle() {
        return "Birth of a Hero...";
    }

    @Override
    protected boolean isReadyToBeInitialized() {
        return false;
    }

    public static HeroCreationPanel getInstance() {
        if (instance == null) {
            instance = new HeroCreationPanel();
        }
        return instance;
    }

    public void modelChanged() {
        setUserObject(getUserObject());
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new HeroCreationWorkspace();
    }

    @Override
    protected boolean isDoneDisabled() {
        return true;
    }

    @Override
    protected boolean isDoneSupported() {
        return false;
    }

    @Override
    protected List<SelectableItemData> createListData() {
        List<SelectableItemData> list = new ArrayList<>();
        for (HeroCreationSequence.HERO_CREATION_ITEM sub : HeroCreationSequence.HERO_CREATION_ITEM.values()) {
            SelectableItemData item = new SelectableItemData(sub.toString(), getUserObject().getEntity());
            item.setSubItems(sub.getSubItems());
            list.add(item);
        }

        return list;
    }

    protected boolean isAutoDoneEnabled() {
        return false;
    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new HeroCreationSequence();
    }

    public void setView(HeroCreationSequence.HERO_CREATION_ITEM item, boolean back) {
        getListPanel().clicked(EnumMaster.getEnumConstIndex(HeroCreationSequence.HERO_CREATION_ITEM.class, item),
         back);

    }

    @Override
    public HeroCreationSequence getListPanel() {
        return (HeroCreationSequence) super.getListPanel();
    }

    @Override
    public HeroCreationWorkspace getInfoPanel() {
        return (HeroCreationWorkspace) super.getInfoPanel();
    }
}
