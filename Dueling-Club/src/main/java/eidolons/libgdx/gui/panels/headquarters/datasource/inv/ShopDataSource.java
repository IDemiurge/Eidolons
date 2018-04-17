package eidolons.libgdx.gui.panels.headquarters.datasource.inv;

import eidolons.libgdx.gui.generic.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/21/2017.
 */
public interface ShopDataSource {

//    ShopTabbedPanel getTabs();

    List<ShopValueContainerList> getGroupLists(String tabName);

    List<ValueContainer> getTextures(String groupList);

    ValueContainer getName();

    ValueContainer getGold();

    ValueContainer getIcon();
}
