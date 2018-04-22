package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.entity.active.DC_SpellObj;

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
        int i =0;
        for (DC_SpellObj sub : data) {
            SpellActor actor = actors[i++];
            if (sub==null )
                break;
            actor.setOverlayPath(HqSpellMaster.getOverlay(sub ));
        }
    }
    protected List<DC_SpellObj> getSpells() {
        return getUserObject().getEntity().getSpells();
    }
}
