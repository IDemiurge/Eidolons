package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.DC_SpellObj;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 4/17/2018.
 */
public class VerbatimContainer extends HqSpellContainer {
    public VerbatimContainer() {
        super(2, 10);
    }

    @Override
    protected void click(int button, DC_SpellObj spell) {

    }

    @Override
    protected void doubleClick(int button, DC_SpellObj spell) {

    }

    protected List<DC_SpellObj> getSpells() {
        return
         getUserObject().getEntity().getSpells().stream()
          .filter(s -> s.isVerbatim()).collect(Collectors.toList());
    }
}
