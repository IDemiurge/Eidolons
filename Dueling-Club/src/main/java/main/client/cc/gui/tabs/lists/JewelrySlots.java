package main.client.cc.gui.tabs.lists;

import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class JewelrySlots extends SecondaryItemList {

    public static final int AMULET_INDEX = 2;
    public static final int LIST_SIZE = 5;

    public JewelrySlots(Unit hero, ItemListManager itemListManager) {
        super(hero, itemListManager);
        itemListManager.addRemoveList(list);
    }

    public static List<ObjType> getSortedJewelryData(List<ObjType> data) {

        List<ObjType> sortedData = new ArrayList<>();
        ListMaster.fillWithNullElements(sortedData, LIST_SIZE);
        int rings = 0;
        boolean left = true;
        for (ObjType type : data) {
            if (type.checkProperty(G_PROPS.JEWELRY_TYPE, "" + ItemEnums.JEWELRY_TYPE.AMULET)) {
                sortedData.set(AMULET_INDEX, type);
            } else {
                int index = rings;
                if (!left) {
                    index++;
                } else {
                    rings++;
                }
                if (index >= AMULET_INDEX) {
                    index++;
                }
                if (index >= sortedData.size()) {
                    return sortedData;
                }
                left = !left;
                sortedData.set(index, type);
            }
        }

        return sortedData;
    }

    @Override
    protected boolean isRemovable() {
        return false;
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    protected String getEmptyIcon() {
        return ImageManager.getEmptyItemIconPath(false);
    }

    @SuppressWarnings("serial")
    @Override
    protected HC_PagedListPanel initList() {
        list = new HC_PagedListPanel(getTemplate(), hero, itemListManager, data, getTitle()) {
            protected boolean isAddControlsAlways() {
                return false;
            }

            @Override
            protected boolean isComponentAfterControls() {
                return false;
            }
        };
        return list;
    }

    @Override
    protected List<ObjType> initData() {
        return getSortedJewelryData(super.initData());

    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.JEWELRY;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.JEWELRY;
    }

    @Override
    protected String getTitle() {
        return "Jewelry";
    }

    @Override
    protected HC_LISTS getTemplate() {

        return HC_LISTS.JEWELRY;
    }

    @Override
    protected String getPoolText() {
        return null;
    }
}
