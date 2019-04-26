package eidolons.game.battlecraft.logic.meta.igg.hero;

import eidolons.libgdx.gui.menu.selection.hero.HeroSelectionPanel;
import main.entity.Entity;

import java.util.List;
import java.util.function.Supplier;

public class IggHeroSelectionPanel extends HeroSelectionPanel {
    public IggHeroSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }


}
