package eidolons.libgdx.gui.panels.headquarters.town;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.macro.entity.town.Shop;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/18/2018.
 */
public class ShopSelectionPanel extends SelectionPanel {

    public ShopSelectionPanel() {
        //set size from parent

    }

    @Override
    public void cancel(boolean manual) {
        if (manual){
            WaitMaster.receiveInput(TownPanel.DONE_OPERATION, false);
            GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, null );
        }
        super.cancel(manual);
    }

    @Override
    public void init() {
        super.init();
        getCell(listPanel). left(). top();
        title.setY(getTitlePosY());
    }

    @Override
    protected float getTitlePosY() {
        return GdxMaster.getHeight() - NINE_PATCH_PADDING.FRAME.top-5;
    }

    @Override
    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        init();
        if (TownPanel.TEST_MODE) {
            debugAll();
        }
    }
    protected String getTitle() {
        return "Available Shops";
    }
    protected boolean isDoneSupported() {
        return false;
    }
    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new ShopPanel( );
    }

    @Override
    protected List<SelectableItemData> createListData() {
        Collection<Shop> shops = (Collection< Shop>) getUserObject();
        return shops.stream().map(shop -> {
            SelectableItemData item =
             new SelectableItemData(shop);
            return item;
        }).collect(Collectors.toList()) ;
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ShopsListPanel();
    }
}
