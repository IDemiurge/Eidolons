package main.swing.components.obj;

import main.entity.obj.DC_QuickItemObj;
import main.swing.generic.components.list.ListItem;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

public class QuickItemListItem extends ListItem<DC_QuickItemObj> {
    public static final String EMPTY_ITEM = ImageManager
            .getDefaultEmptyListIcon();

    public QuickItemListItem(DC_QuickItemObj item, boolean isSelected,
                             boolean cellHasFocus) {
        super(item, isSelected, cellHasFocus, 0);
    }

    @Override
    public String getEmptyIcon() {
        return EMPTY_ITEM;
    }

    protected String getToolTip() {
        return value.getToolTip();
    }

    @Override
    public BORDER getSpecialBorder() {
        if (isHighlighted()) {
            return BORDER.HIGHLIGHTED_96;
        }
        if (!value.canBeActivated()) {
            return BORDER.HIDDEN;
        }
        return super.getSpecialBorder();
    }

    @Override
    protected boolean isHighlighted() {
        return super.isHighlighted();
    }
}
