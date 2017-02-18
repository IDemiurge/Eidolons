package main.client.cc.gui.tabs.lists;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.content.OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.entity.obj.unit.Unit;

public class VerbatimList extends SecondaryItemList {

    private static final String TITLE = "Verbatim Spells";
    private static final int SPELL_SLOTS_COLUMNS = CharacterCreator.STD_COLUMN_NUMBER;

    public VerbatimList(Unit hero, ItemListManager itemListManager) {
        super(hero, itemListManager);
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    protected HC_LISTS getTemplate() {
        return HC_LISTS.VERBATIM;
    }

    @Override
    protected boolean isRemovable() {
        return false;
    }

    @Override
    protected int getColumnCount() {
        return SPELL_SLOTS_COLUMNS;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected PARAMETER getPoolParam() {
        return null;
    }

    @Override
    protected int getPoolWidth() {
        return 0;
    }

    @Override
    protected String getPoolTooltip() {
        return null;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.VERBATIM_SPELLS;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.SPELLS;
    }

    @Override
    protected String getPoolText() {
        return null;
    }

}
