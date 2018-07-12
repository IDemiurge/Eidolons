package eidolons.libgdx.gui.panels.headquarters.creation.selection;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.tooltips.SmartClickListener;

/**
 * Created by JustMe on 7/3/2018.
 */
public abstract class SelectionImageTable extends SelectionTable<SelectableImageItem> {
    public SelectionImageTable(int wrap, int size, int space) {
        super(wrap, size,space);
    }

    @Override
    protected SelectableImageItem createElement(SelectableItemData datum) {
        SelectableImageItem item = new SelectableImageItem(this, datum);
        item.addListener(getSelectionListener(item, datum));
        return item;
    }
    protected EventListener getSelectionListener(SelectableImageItem item, SelectableItemData data) {
        return new SmartClickListener(item) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
               selectedItem=item;
               selected(data);
            }
        };
    }
    @Override
    protected SelectableImageItem[] initActorArray() {
        return new SelectableImageItem[size];
    }


    public String getDisplayablePath(SelectableItemData data) {
        return data.getImagePath();
    }
}
