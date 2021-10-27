package libgdx.gui.dungeon.menu.selection;

import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.generic.btn.ButtonStyled;

/**
 * Created by JustMe on 7/3/2018.
 */
public class SelectionSubItemsPanel extends TablePanelX {
    private final ItemListPanel panel;

    public SelectionSubItemsPanel(String[] items, ItemListPanel.SelectableItemData item, ItemListPanel itemListPanel) {
        setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        this.panel = itemListPanel;
        for (String sub : items) {
            SmartTextButton line = new SmartTextButton( sub,
             StyleHolder.getHqTextButtonStyle(16), ()-> clicked(item, sub), ButtonStyled.STD_BUTTON.MENU);
            add(line).center().row();
        }
        pack();
    }

    private void clicked(ItemListPanel.SelectableItemData item, String sub) {
        panel.subItemClicked(item, sub);
    }
}
