package eidolons.libgdx.gui.panels.headquarters.creation.selection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.game.core.EUtils;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;

/**
 * Created by JustMe on 7/3/2018.
 */
public abstract class SelectionTable<A extends Actor> extends ValueTable<SelectableItemData, A> {

    protected A selectedItem;
    protected SelectableItemData selectedData;

    public SelectionTable(int wrap, int size) {
        super(wrap, size);
    }

    public SelectionTable(int wrap, int size, int space) {
        super(wrap, size, space);
    }

    protected EventListener getSelectionListener(A item, SelectableItemData data) {
        return new SmartClickListener(item) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                selected(data);
                selectedItem=item;
            }
        };
    }

    public A getSelectedItem() {
        return selectedItem;
    }

    public SelectableItemData select(int index) {
        SelectableItemData item = data[index];
        selected(item);
        return item;
    }

    protected void selected(SelectableItemData item) {
        if (getProperty() != null)
            HeroCreationMaster.modified(getProperty(), item.getName());
        if (getEvent() != null)
            EUtils.event(getEvent(), item);
    }

    protected abstract GuiEventType getEvent();

    protected abstract PROPERTY getProperty();

    @Override
    protected SelectableItemData[] initDataArray() {
        return new SelectableItemData[size];
    }


}
