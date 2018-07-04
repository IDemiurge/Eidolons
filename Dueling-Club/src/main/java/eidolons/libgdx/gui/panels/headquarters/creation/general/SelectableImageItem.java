package eidolons.libgdx.gui.panels.headquarters.creation.general;

import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;

/**
 * Created by JustMe on 7/3/2018.
 */
public class SelectableImageItem extends FadeImageContainer{
    SelectableItemData data;

    public SelectableImageItem(SelectableItemData data) {
        super(data.getImagePath());
        this.data = data;
    }

    public SelectableItemData getData() {
        return data;
    }
}



