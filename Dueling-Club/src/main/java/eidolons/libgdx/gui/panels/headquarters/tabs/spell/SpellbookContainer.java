package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.Spell;
import eidolons.game.core.master.SpellMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/17/2018.
 */
public class SpellbookContainer extends HqSpellContainer {
    public SpellbookContainer() {
        super(6, 12);
    }

    @Override
    public void init() {
        super.init();
        int i = 0;
        for (Spell sub : data) {
            SpellActor actor = actors[i++];
            if (sub == null)
                break;
            actor.setOverlayPath(HqSpellMaster.getOverlay(sub));
        }
    }

    @Override
    protected String getLabelText() {
        return "Spellbook";
    }

    @Override
    protected boolean isOverlayOn() {
        return true;
    }

    @Override
    protected void click(int button, Spell spell) {
        if (button == 1) {
            if (HqSpellMaster.canMemorize(spell))
                HqDataMaster.operation(getUserObject(), HERO_OPERATION.SPELL_MEMORIZED, spell);
        }
    }

    @Override
    protected void doubleClick(int button, Spell spell) {
        if (spell.getOwnerUnit() == null) {
            return; // preview spell
        }
        if (HqSpellMaster.canLearnEnVerbatim(spell))
            if (HqSpellMaster.canLearnEnVerbatim(spell))
            HqDataMaster.operation(getUserObject(), HERO_OPERATION.SPELL_EN_VERBATIM, spell);
    }

    protected List<Spell> getSpells() {
        List<Spell> list = new ArrayList<>();
        if (getUserObject().getEntity().getSpellbook() != null) {
            list.addAll(getUserObject().getEntity().getSpellbook());
        }
        List<Spell>  potential= SpellMaster.getPotentialSpellsForHero(getUserObject().getEntity());
        list.addAll(potential);
        new ListMaster<Spell>().removeDuplicates(list);
        list= list.subList(0, Math.min(list.size(), size));

        return list;
    }
}
