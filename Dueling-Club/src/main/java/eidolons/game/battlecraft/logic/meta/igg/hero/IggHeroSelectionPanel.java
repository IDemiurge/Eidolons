package eidolons.game.battlecraft.logic.meta.igg.hero;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.hero.HeroListPanel;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import main.entity.Entity;

import java.util.List;
import java.util.function.Supplier;

/**
 * Is this the original choice? Will it be diff with death?
 *
 * logic
 *
 * ui
 *
 */
public class IggHeroSelectionPanel extends HeroSelectionPanel {



    public IggHeroSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new HeroListPanel(){
            @Override
            public boolean isBlocked(SelectableItemData item) {
                //check lives how?
                return super.isBlocked(item);
            }
        };
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new IggHeroInfoPanel();
    }

}
