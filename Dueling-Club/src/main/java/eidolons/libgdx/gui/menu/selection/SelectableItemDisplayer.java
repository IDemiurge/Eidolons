package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;

/**
 * Created by JustMe on 7/2/2018.
 */
public interface SelectableItemDisplayer {
    void setItem(SelectableItemData sub);

    Actor getActor();

    default void subItemClicked(SelectableItemData item, String sub) {

    }
}
