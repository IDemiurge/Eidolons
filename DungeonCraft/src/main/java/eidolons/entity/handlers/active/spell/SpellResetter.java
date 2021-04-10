package eidolons.entity.handlers.active.spell;

import eidolons.content.DC_ContentValsManager;
import eidolons.entity.active.Spell;
import eidolons.entity.handlers.active.ActiveResetter;
import main.content.enums.system.MetaEnums;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 2/26/2017.
 */
public class SpellResetter extends ActiveResetter {
    public SpellResetter(Spell entity, SpellActiveMaster entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    protected void applyPenalties() {
        super.applyPenalties();
    }

    @Override
    public Spell getEntity() {
        return (Spell) super.getEntity();
    }

    @Override
    public void toBase() {
        super.toBase();
        getEntity().setDirty(false);
    }

    @Override
    protected void addCustomMods() {
        if (getEntity().getOwnerUnit().getCustomParamMap() == null) {
            return;
        }
        super.addCustomMods();

        for (PARAMETER param : DC_ContentValsManager.getCostParams()) {
            addCustomMod(
             MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_SPELL_GROUP,
             getEntity().getSpellGroup().toString(), param, false);
            addCustomMod(
             MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_SPELL_POOL,
             getEntity().getSpellPool().toString(), param, false);
            addCustomMod(
             MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_REDUCTION_SPELL_TYPE,
             getEntity().getSpellType().toString(), param, false);

            addCustomMod(MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_MOD_SPELL_GROUP,
             getEntity().getSpellGroup().toString(), param, true);
            addCustomMod(MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_MOD_SPELL_POOL,
             getEntity().getSpellPool().toString(), param, true);
            addCustomMod(MetaEnums.CUSTOM_VALUE_TEMPLATE.COST_MOD_SPELL_TYPE,
             getEntity().getSpellType().toString(), param, true);
        }
    }
}
