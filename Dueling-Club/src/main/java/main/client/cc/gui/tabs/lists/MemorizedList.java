package main.client.cc.gui.tabs.lists;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.obj.DC_HeroObj;

public class MemorizedList extends SecondaryItemList {

    private static final String TITLE = "Memorized Spells";
    private static final PARAMETER POOL_PARAM = PARAMS.MEMORIZATION_CAP;
    private static final int POOL_WIDTH = 3;
    private static final String POOL_TOOLTIP = "Memorization points (by Spell Difficulty, from Hero's Intelligence)";
    private static int SPELL_SLOTS_COLUMNS = CharacterCreator.STD_COLUMN_NUMBER;

    public MemorizedList(DC_HeroObj hero, ItemListManager itemListManager) {
        super(hero, itemListManager);
        itemListManager.addRemoveList(list);
    }

    @Override
    protected int getColumnCount() {
        return SPELL_SLOTS_COLUMNS;
    }

    @Override
    protected PARAMETER getPoolParam() {
        return POOL_PARAM;
    }

    @Override
    protected String getPoolText() {
        return hero.calculateMemorizationPool() + "/"
                + hero.getParam(getPoolParam());
    }

    @Override
    protected String getPoolTooltip() {
        return POOL_TOOLTIP;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.MEMORIZED_SPELLS;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.SPELLS;
    }

    @Override
    protected HC_LISTS getTemplate() {
        return HC_LISTS.MEMORIZED;
    }

}
