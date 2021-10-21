package libgdx.gui.panels.headquarters.creation.selection;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.tooltips.SmartClickListener;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;
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
                for (SelectableImageItem selectableImageItem : getActors()) {
                    selectableImageItem.setSelected(false);
                }
                item.setSelected(true);
            }
        };
    }
    @Override
    protected SelectableImageItem[] initActorArray() {
        return new SelectableImageItem[size];
    }

    @Override
    protected PROPERTY getProperty() {
        return null;
    }

    @Override
    protected GuiEventType getEvent() {
        return null;
    }


    public String getDisplayablePath(SelectableItemData data) {
        return data.getImagePath();
    }
}
