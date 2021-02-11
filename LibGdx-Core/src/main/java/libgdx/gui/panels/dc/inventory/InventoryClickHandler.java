package libgdx.gui.panels.dc.inventory;

import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.Images;
import main.entity.Entity;

public interface InventoryClickHandler {
    //IDEA: FOR NON-COMBAT, DROP == SELL!
    boolean cellClicked(VisualEnums.CELL_TYPE cell_type, int clickCount, boolean rightClick,
                        boolean altClick, Entity cellContents, boolean ctrlClick);

    boolean itemDragAndDropped(VisualEnums.CELL_TYPE cell_type,
                               Entity cellContents, Entity droppedItem
    );
    void singleClick(VisualEnums.CELL_TYPE cell_type, Entity cellContents);

    void undoClicked();

    void doneClicked();

    void apply();

    void cancelClicked();

    boolean isUndoEnabled();

    boolean isDoneEnabled();

    boolean isCancelEnabled();

    Entity getDragged();

    void setDragged(Entity dragged);

}
