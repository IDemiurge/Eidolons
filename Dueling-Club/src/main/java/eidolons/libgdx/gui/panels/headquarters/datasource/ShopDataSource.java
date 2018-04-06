package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.headquarters.ShopTabbedPanel;
import eidolons.libgdx.gui.panels.headquarters.ShopValueContainerList;

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
