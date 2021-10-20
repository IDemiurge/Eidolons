package libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.Spell;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.master.SpellMaster;
import eidolons.game.module.herocreator.HeroManager;
import libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.system.libgdx.datasource.HeroDataModel;

import java.util.ArrayList;
import java.util.List;

import static eidolons.content.consts.VisualEnums.*;

/**
 * Created by JustMe on 4/17/2018.
 */
public class SpellbookContainer extends HqSpellContainer {
    private boolean filtersOn;

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
                HqDataMaster.operation(getUserObject(), HeroDataModel.HERO_OPERATION.SPELL_MEMORIZED, spell);
            else {
                EUtils.showInfoTextStyled(LABEL_STYLE.AVQ_SMALL, "Cannot memorize " + spell.getName());
            }
        }
    }

    @Override
    protected void doubleClick(int button, Spell spell) {
//        if (spell.getOwnerUnit() == null) {
//            EUtils.showInfoTextStyled(GuiStage.LABEL_STYLE.AVQ_SMALL, "Cannot learn " + spell.getName() + ":\n " +
//                    spell.getGame().getRequirementsManager().check(Eidolons.getMainHero(), spell));
//            return; // preview spell
//        }
        if (spell.getSpellPool() == null) {
            if (HqSpellMaster.canLearn(spell)) {
                EUtils.onConfirm("Learn " + spell + " for " + HeroManager.getCost(spell, Core.getMainHero()) + " Experience Points?", true, () ->
                        HqDataMaster.operation(getUserObject(), HeroDataModel.HERO_OPERATION.SPELL_LEARNED, spell));
            } else {
                EUtils.showInfoTextStyled(LABEL_STYLE.AVQ_SMALL, "Cannot learn " + spell.getName() + ":\n " +
                        spell.getGame().getRequirementsManager().check(Core.getMainHero(), spell));
            }
        } else {
//        if (HqSpellMaster.canLearnEnVerbatim(spell))
            if (HqSpellMaster.canLearnEnVerbatim(spell)) {
                EUtils.onConfirm("Learn " + spell + " En Verbatim for " + HeroManager.getCost(spell, Core.getMainHero())
                        + " Experience Points?", true, () ->
                        HqDataMaster.operation(getUserObject(), HeroDataModel.HERO_OPERATION.SPELL_EN_VERBATIM, spell));
            } else
                EUtils.showInfoTextStyled(LABEL_STYLE.AVQ_SMALL, "Cannot learn En Verbatim " + spell.getName());
        }
    }

    protected boolean checkValid(Spell spell) {
        return spell.getSpellPool() != null;
    }

    protected boolean checkAvailable(Spell spell) {
        if (spell.getSpellPool() != null) {
            return false;
        }
        return HqSpellMaster.canLearn(spell);
    }

    protected List<Spell> getSpells() {
        List<Spell> list = new ArrayList<>();
        if (getUserObject().getEntity().getSpellbook() != null) {
            list.addAll(getUserObject().getEntity().getSpellbook());
        }
        if (!filtersOn) {
            List<Spell> potential = SpellMaster.getPotentialSpellsForHero(getUserObject().getEntity());
            List<Spell> finalList = list;
            potential.removeIf(spell -> {
                for (Spell s : finalList) {
                    if (s.getType().equals(spell.getType())) {
                        return true;
                    }
                }
                return false;
            });
            list.addAll(potential);
        }
//        new ListMaster<Spell>().removeDuplicates(list);
        list = list.subList(0, Math.min(list.size(), size));

        return list;
    }

    public void toggleFilters() {
        filtersOn = !filtersOn;
        init();
    }
}
