package main.swing.components.obj;

import main.entity.obj.DC_SpellObj;
import main.swing.generic.components.list.ListItem;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

public class SpellListItem extends ListItem<DC_SpellObj> {
    public static final String EMPTY_SPELL = ImageManager
            .getAltEmptyListIcon();

    public SpellListItem(DC_SpellObj item, boolean isSelected,
                         boolean cellHasFocus) {
        super(item, isSelected, cellHasFocus, 0);
    }

    @Override
    public String getEmptyIcon() {
        return EMPTY_SPELL;
    }

    protected String getToolTip() {
        return value.getToolTip();
    }

    @Override
    public BORDER getSpecialBorder() {
        if (isHighlighted())
            return BORDER.SPELL_HIGHLIGHTED;
        if (!value.canBeActivated())
            return BORDER.HIDDEN;

        return super.getSpecialBorder();
    }

    protected boolean isHighlighted() {
        try {
            return getValue().getGame().getManager().getSelectingSet()
                    .contains(getValue());
        } catch (Exception e) {

        }
        return false;
    }

}
