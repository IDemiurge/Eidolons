package libgdx.gui.dungeon.panels.headquarters.tabs.spell;

import eidolons.entity.active.Spell;

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
    protected void click(int button, Spell spell) {

    }
    @Override
    protected String getLabelText() {
        return "Verbatim\n ";
    }
    @Override
    protected boolean isOverlayOn() {
        return false;
    }
    @Override
    protected void doubleClick(int button, Spell spell) {

    }

    protected List<Spell> getSpells() {
        return
         getUserObject().getEntity().getSpells().stream()
          .filter(Spell::isVerbatim).collect(Collectors.toList());
    }
}
