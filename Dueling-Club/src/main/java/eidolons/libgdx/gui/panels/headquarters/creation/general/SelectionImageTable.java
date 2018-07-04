package eidolons.libgdx.gui.panels.headquarters.creation.general;

import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;

/**
 * Created by JustMe on 7/3/2018.
 */
public abstract class SelectionImageTable extends SelectionTable<SelectableImageItem> {
    public SelectionImageTable(int wrap, int size) {
        super(wrap, size);
    }

    @Override
    protected SelectableImageItem createElement(SelectableItemData datum) {
        SelectableImageItem item = new SelectableImageItem(datum);
        item.addListener(getSelectionListener(item, datum));
        return item;
    }

    @Override
    protected SelectableImageItem[] initActorArray() {
        return new SelectableImageItem[size];
    }



}
