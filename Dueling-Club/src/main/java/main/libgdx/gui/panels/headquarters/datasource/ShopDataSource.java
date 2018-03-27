package main.libgdx.gui.panels.headquarters.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.headquarters.ShopValueContainerList;
import main.libgdx.gui.panels.headquarters.ShopTabbedPanel;

import java.util.List;

/**
 * Created by JustMe on 5/21/2017.
 */
public interface ShopDataSource {

    ShopTabbedPanel getTabs();
    List<ShopValueContainerList> getGroupLists(String tabName);
    List<ValueContainer> getTextures(String groupList);
    ValueContainer getName();
    ValueContainer getGold();
    ValueContainer getIcon();
}
