package eidolons.client.cc.gui.tabs.lists;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.gui.lists.ItemListManager;
import eidolons.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;

public class QuickItemList extends SecondaryItemList {

    private static final String TITLE = "Quick Items";
    private static final int QUICK_SLOTS_COLUMNS = CharacterCreator.STD_COLUMN_NUMBER;
    private static final String POOL_TOOLTIP = "Remaining quick slots";

    public QuickItemList(Unit hero, ItemListManager itemListManager) {
        super(hero, itemListManager);
        itemListManager.setQuickItemsList(list);
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.QUICK_ITEMS;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return C_OBJ_TYPE.ITEMS;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getPoolText() {

        return (hero.getIntParam(PARAMS.QUICK_SLOTS) - hero.getIntParam(getPoolParam())) + "/"
         + hero.getParam(PARAMS.QUICK_SLOTS);
    }

    @Override
    protected int getColumnCount() {
        return QUICK_SLOTS_COLUMNS;
    }

    @Override
    protected PARAMETER getPoolParam() {
        return PARAMS.C_QUICK_SLOTS;
    }

    @Override
    protected String getPoolTooltip() {
        return POOL_TOOLTIP;
    }

    // private void init() {
    // List<ObjType> data = DataManager.convertToTypeList(hero
    // .getProperty(PROPS.QUICK_ITEMS), C_OBJ_TYPE.ITEMS);
    // // outside simulation?
    // int rowCount = HeroItemTab.DEFAULT_ROW_COUNT;
    // // hero.getIntParam(PARAMS.QUICK_SLOTS);
    // itemListManager.remove(list);
    // list = new HeroItemListPanel("", hero, false, isVertical(), rowCount,
    // GuiManager.getSmallObjSize(), data, QUICK_SLOTS_COLUMNS);
    // itemListManager.add(list);
    // // ???
    // }

    @Override
    protected HC_LISTS getTemplate() {
        return HC_LISTS.QUICK_ITEMS;
    }

}
