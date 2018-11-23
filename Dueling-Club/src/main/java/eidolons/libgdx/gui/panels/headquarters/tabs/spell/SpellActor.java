package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.Spell;
import eidolons.libgdx.gui.panels.headquarters.HqSlotActor;
import eidolons.libgdx.texture.Images;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 4/17/2018.
 */
public class SpellActor extends HqSlotActor<Spell> {

    public SpellActor(Spell spellObj) {
        super(spellObj);
    }

    @Override
    protected String getOverlay(Spell model) {
        return HqSpellMaster.getOverlay(model);
    }

    @Override
    protected String getEmptyImage() {
        return Images.EMPTY_SPELL;
    }


    public enum SPELL_OVERLAY {
        VERBATIM,
        MEMORIZED,
        AVAILABLE,
        UNAVAILABLE,
        KNOWN,
        CANNOT_PAY, DIVINED;

        public String imagePath;

        SPELL_OVERLAY() {
            imagePath = StrPathBuilder.build(PathFinder.getComponentsPath(),
             "hq", "spell", "overlay", name() + ".png");
        }

    }
}
