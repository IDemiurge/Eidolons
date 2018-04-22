package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.DC_SpellObj;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 4/17/2018.
 */
public class MemorizedContainer extends HqSpellContainer {
    public MemorizedContainer() {
        super(2, 10);
    }

    protected List<DC_SpellObj> getSpells() {
        return
         getUserObject().getEntity().getSpells().stream()
          .filter(s -> s.isMemorized()).collect(Collectors.toList());
    }
}
