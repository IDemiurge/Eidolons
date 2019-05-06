package eidolons.game.battlecraft.logic.meta.igg.hero;

import eidolons.game.battlecraft.logic.meta.igg.IGG_Game;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.hero.HeroListPanel;
import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import main.entity.Entity;

import java.util.Collections;
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


    @Override
    protected String getDoneText() {
        return "Onward!";
    }

    @Override
    protected String getTitle() {
        return "Eidolon Avatar";
    }

    @Override
    protected List<ItemListPanel.SelectableItemData> createListData() {
        List<ItemListPanel.SelectableItemData> list = super.createListData();
        if (isShuffleDataList())
            Collections.shuffle(list);
        return list;
    }

    private boolean isShuffleDataList() {
        return true;
    }

    public IggHeroSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected boolean isAutoDoneEnabled() {
        if (Eidolons.getGame().isStarted())
            return false;
        return super.isAutoDoneEnabled();
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new HeroListPanel(){
            @Override
            public boolean isBlocked(SelectableItemData item) {
                //check lives ?
                if (Eidolons.getGame() instanceof IGG_Game){
                    HeroChain chain = ((IGG_Game) Eidolons.getGame()).getMetaMaster().
                            getPartyManager().getHeroChain();
                    if (chain == null) {
                        return false;
                    }
                    return !chain.findHero(item.getName()).hasLives();
                }

                return super.isBlocked(item);
            }
        };
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new IggHeroInfoPanel(null );
    }

}
