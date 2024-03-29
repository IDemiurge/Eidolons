package eidolons.entity.item;

import eidolons.entity.unit.UnitModel;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BuffObj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;

public abstract class HeroSlotItem extends HeroItem {

    public HeroSlotItem(ObjType type, Player owner, GenericGame game, Ref ref, PARAMETER[] params
                        // , PROPERTY[] props
    ) {
        super(type, owner, game, ref, params
         // , props
        );
    }

    protected abstract void applyPenaltyReductions();

    @Override
    public void apply() {
        applyBuffEffects();
        applyPenaltyReductions();
        super.apply();
        applyMods();
    }

    private void applyBuffEffects() {
        for (BuffObj b : getBuffs()) {
            b.getEffects().apply(Ref.getSelfTargetingRefCopy(this));
        }
    }

    public MATERIAL getMaterial() {
        return new EnumMaster<MATERIAL>().retrieveEnumConst(MATERIAL.class,
         getProperty(G_PROPS.MATERIAL));
    }

    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, UnitModel target, Ref REF) {
        if (specialEffects == null) {
            return;
        }
        if (specialEffects.get(case_type) == null) {
            return;
        }
        Ref ref = Ref.getCopy(REF);
        ref.setTarget(target.getId());
        ref.setSource(getOwnerObj().getId());
        Effect effect = specialEffects.get(case_type);
        effect.apply(ref);
    }

    protected boolean isActivatePassives() {
        return true;
    }

    @Override
    public String getToolTip() {
        return super.getToolTip();
    }

    @Override
    protected void addDynamicValues() {
        super.addDynamicValues();
    }

    @Override
    public void toBase() {
        getSpecialEffects().clear();
        super.toBase();
    }

    @Override
    public boolean checkSelectHighlighted() {
        return super.checkSelectHighlighted();
    }

    @Override
    public void applyMods() {
        super.applyMods();
    }

    protected KEYS getKey() {
        return KEYS.SLOT_ITEM;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
    }

    public String getBaseTypeName() {
        if (!type.isGenerated())
            return getName();
        return getProperty(G_PROPS.BASE_TYPE);
    }

    public ItemEnums.QUALITY_LEVEL getQuality() {
        return new EnumMaster<ItemEnums.QUALITY_LEVEL>().retrieveEnumConst(ItemEnums.QUALITY_LEVEL.class, getProperty(G_PROPS.QUALITY_LEVEL));
    }
}
