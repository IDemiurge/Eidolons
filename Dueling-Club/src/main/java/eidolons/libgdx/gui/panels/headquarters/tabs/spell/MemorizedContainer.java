package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 4/17/2018.
 */
public class MemorizedContainer extends HqSpellContainer {
    public MemorizedContainer() {
        super(2, 10);
    }

    @Override
    protected void click(int button, DC_SpellObj spell) {
        if (button==1){
            HqDataMaster.operation(getUserObject(), HERO_OPERATION.SPELL_UNMEMORIZED, spell);
        }
    }
    @Override
    protected String getLabelText() {
        return "Memorized "+ getUserObject().getEntity().calculateUsedMemory()+"/"+
         getUserObject().getEntity().getIntParam(PARAMS.MEMORIZATION_CAP);
    }
    @Override
    protected boolean isOverlayOn() {
        return false;
    }
    @Override
    protected void doubleClick(int button, DC_SpellObj spell) {
        if (!HqSpellMaster.canLearnEnVerbatim(spell))
        return;
        HqDataMaster.operation(getUserObject(), HERO_OPERATION.SPELL_UNMEMORIZED, spell);
        HqDataMaster.operation(getUserObject(), HERO_OPERATION.SPELL_EN_VERBATIM, spell);

    }

    protected List<DC_SpellObj> getSpells() {
        return
         getUserObject().getEntity().getSpells().stream()
          .filter(s -> s.isMemorized()).collect(Collectors.toList());
    }
}
