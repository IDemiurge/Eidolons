package libgdx.gui.dungeon.menu.selection;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by JustMe on 7/2/2018.
 */
public interface SelectableItemDisplayer {
    void setItem(ItemListPanel.SelectableItemData sub);

    default Actor getActor(){
        return (Actor) this;
    }

    default void subItemClicked(ItemListPanel.SelectableItemData item, String sub) {

    }

    void setDoneDisabled(boolean doneDisabled);

    void initStartButton(String doneText, Runnable o);
}
