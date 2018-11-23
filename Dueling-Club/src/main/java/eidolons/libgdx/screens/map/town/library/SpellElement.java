package eidolons.libgdx.screens.map.town.library;

import eidolons.entity.active.Spell;
import eidolons.libgdx.gui.panels.headquarters.HqSlotActor;

/**
 * Created by JustMe on 11/21/2018.
 */
public class SpellElement extends HqSlotActor<Spell> {
    public SpellElement(Spell model) {
        super(model);
    }

    @Override
    protected String getOverlay(Spell model) {
        return null;
    }

    @Override
    protected String getEmptyImage() {
        return null;
    }
}
